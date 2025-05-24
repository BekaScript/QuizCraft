package com.java.quizcraft.controller;

import com.java.quizcraft.model.Question;
import com.java.quizcraft.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
public class AIQuizController {

    @Autowired
    private AiService aiService;
    
    @PostMapping("/generate")
    public ResponseEntity<List<Question>> generateQuiz(@RequestBody Map<String, Object> request) {
        String prompt = (String) request.get("prompt");
        String questionsAmount = request.get("questionsAmount").toString();
        try {
            List<Question> questions = aiService.generateQuiz(prompt, questionsAmount);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
} 