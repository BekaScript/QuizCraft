package com.java.quizcraft.controller;

import com.java.quizcraft.model.Question;
import com.java.quizcraft.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AIQuizController {

    @Autowired
    private AiService aiService;
    
    @PostMapping("/generate-quiz")
    public ResponseEntity<List<Question>> generateQuiz(@RequestBody Map<String, Object> request) {
        String prompt = (String) request.get("prompt");
        String questionsAmount = request.get("questionsAmount").toString();
        String quizType = (String) request.getOrDefault("quizType", "MIXED"); // Default to MIXED

        try {
            List<Question> questions = aiService.generateQuiz(prompt, questionsAmount, quizType);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            e.printStackTrace(); // Keep this for server-side logging
            // Return a more specific error message to the client if possible
            return ResponseEntity.badRequest().body(null); // Or provide an error object: List.of(new QuestionError(e.getMessage()))
        }
    }
} 