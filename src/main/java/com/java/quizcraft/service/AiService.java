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

    public List<Question> generateQuiz(String prompt, String questionsAmount){
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer "+apiKey);

            String systemPrompt = "You are a quiz generator that creates questions in JSON format. " +
                    "Generate " + questionsAmount + " questions" +
                    "Each question should have the following format:\n" +
                    "For multiple-choice: {\n" +
                    "  \"questionText\": \"Question text\",\n" +
                    "  \"questionType\": \"MULTIPLE_CHOICE\",\n" +
                    "  \"options\": [\"Option 1\", \"Option 2\", \"Option 3\", \"Option 4\"],\n" +
                    "  \"correctAnswerIndex\": 0,\n" +
                    "  \"explanation\": \"Explanation of the answer\"\n" +
                    "}\n" +
                    "For written-answer: {\n" +
                    "  \"questionText\": \"Question text\",\n" +
                    "  \"questionType\": \"WRITTEN_ANSWER\",\n" +
                    "  \"correctWrittenAnswer\": \"Correct answer\",\n" +
                    "  \"explanation\": \"Explanation of the answer\"\n" +
                    "}\n" +
                    "The explanation for multiple-choice questions are not mandatory, you may just return space for explanation if you think it is unnecessary" +
                    "But make sure to give explanation for written-answer questions" +
                    "Return ONLY a JSON array of questions with no additional text.";
        
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
            
            System.out.println("Raw AI Response JSON: " + content);
            
            return objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(List.class, Question.class));
        } catch(Exception e){
            System.err.println("Error during AI quiz generation or parsing: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to generate a quiz: " + e.getMessage(), e);
        }

    }
}