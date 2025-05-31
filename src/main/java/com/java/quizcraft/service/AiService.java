package com.java.quizcraft.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.quizcraft.model.Question;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiService{

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Question> generateQuiz(String prompt, String questionsAmount, String quizType){
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer "+apiKey);

            // Build the core system prompt
            StringBuilder systemPromptBuilder = new StringBuilder();
            systemPromptBuilder.append("You are a quiz generator that creates questions in JSON format. ");
            
            // Handle questionsAmount - if "auto", let AI decide, otherwise specify the number
            if ("auto".equalsIgnoreCase(questionsAmount)) {
                systemPromptBuilder.append("Generate an appropriate number of questions based on the content provided. ");
                systemPromptBuilder.append("Use your judgment to determine the optimal number of questions for the given material. ");
                systemPromptBuilder.append("Respond in the same language as the input prompt. ");
            } else {
                systemPromptBuilder.append("Generate ").append(questionsAmount).append(" questions. ");
                systemPromptBuilder.append("Respond in the same language as the input prompt. ");
            }

            switch (quizType.toUpperCase()) {
                case "MULTIPLE_CHOICE":
                    systemPromptBuilder.append("All questions should be multiple-choice. ");
                    systemPromptBuilder.append("Each question must follow this format: {\n")
                                     .append("  \"questionText\": \"Question text\",\n")
                                     .append("  \"questionType\": \"MULTIPLE_CHOICE\",\n")
                                     .append("  \"options\": [\"Option 1\", \"Option 2\", \"Option 3\", \"Option 4\"],\n")
                                     .append("  \"correctAnswerIndex\": 0,\n")
                                     .append("  \"explanation\": \"Explanation of the answer (optional, can be an empty string)\"\n")
                                     .append("}. ");
                    break;
                case "WRITTEN_ANSWER":
                    systemPromptBuilder.append("All questions should be written-answer (open-ended). ");
                    systemPromptBuilder.append("Each question must follow this format: {\n")
                                     .append("  \"questionText\": \"Question text\",\n")
                                     .append("  \"questionType\": \"WRITTEN_ANSWER\",\n")
                                     .append("  \"correctWrittenAnswer\": \"The correct answer text\",\n")
                                     .append("  \"explanation\": \"Explanation of the answer (mandatory)\"\n")
                                     .append("}. ");
                    break;
                case "MIXED":
                default:
                    systemPromptBuilder.append("Questions can be a mix of multiple-choice and written-answer types. ");
                    systemPromptBuilder.append("For multiple-choice, use format: {\n")
                                     .append("  \"questionText\": \"Question text\", \"questionType\": \"MULTIPLE_CHOICE\", \"options\": [\"Opt1\", \"Opt2\", \"Opt3\", \"Opt4\"], \"correctAnswerIndex\": 0, \"explanation\": \"Optional explanation\"\n}");
                    systemPromptBuilder.append(" For written-answer, use format: {\n")
                                     .append("  \"questionText\": \"Question text\", \"questionType\": \"WRITTEN_ANSWER\", \"correctWrittenAnswer\": \"Correct answer text\", \"explanation\": \"Mandatory explanation\"\n}");
                    break;
            }
            
            systemPromptBuilder.append(" Ensure explanations for written-answer questions are always provided. For multiple-choice, explanations are optional. ");
            systemPromptBuilder.append("Return ONLY a valid JSON array of questions with no additional introductory or concluding text outside the JSON structure itself.");
        
            String systemPrompt = systemPromptBuilder.toString();

            Map<String,Object> responseBody = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", prompt)
                )
            );

            HttpEntity<Map<String,Object>> request = new HttpEntity<>(responseBody, headers);
            Map<String,Object> response = restTemplate.postForObject(apiUrl, request, Map.class);

            String content = (String) ((Map<String, Object>) ((List<Map<String, Object>>) response.get("choices")).get(0).get("message")).get("content");
            
            System.out.println("Quiz Type Requested: " + quizType);
            System.out.println("Raw AI Response JSON: " + content);
            
            // Attempt to clean the response if it's not a perfect JSON array
            content = content.trim();
            if (!content.startsWith("[") && content.contains("[")) {
                content = content.substring(content.indexOf("["));
            }
            if (!content.endsWith("]") && content.contains("]")) {
                content = content.substring(0, content.lastIndexOf("]") + 1);
            }

            return objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(List.class, Question.class));
        } catch(Exception e){
            System.err.println("Error during AI quiz generation or parsing: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate a quiz: " + e.getMessage(), e);
        }

    }
}