package com.java.quizcraft.controller;

import com.java.quizcraft.model.Quiz;
import com.java.quizcraft.model.User;
import com.java.quizcraft.model.Question;
import com.java.quizcraft.service.QuizService;
import com.java.quizcraft.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/saved-quiz")
public class QuizController {
    
    @Autowired
    private QuizService quizService;
    
    @Autowired
    private UserService userService;
    
    private User getAuthenticatedUser(HttpSession session) {
        String username = (String) session.getAttribute("username");
        return username != null ? userService.findByUsername(username) : null;
    }
    
    @PostMapping("/save")
    public ResponseEntity<?> saveQuiz(@RequestBody Map<String, Object> request, HttpSession session) {
        User user = getAuthenticatedUser(session);
        if (user == null) return ResponseEntity.status(401).body("User not authenticated");
        
        String title = (String) request.get("title");
        String description = (String) request.get("description");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> questionsData = (List<Map<String, Object>>) request.get("questions");
        
        List<Question> questions = questionsData.stream().map(this::mapToQuestion).toList();
        
        Quiz savedQuiz = quizService.saveQuiz(title, description, questions, user);
        return ResponseEntity.ok(Map.of("id", savedQuiz.getId(), "message", "Quiz saved successfully"));
    }
    
    
    private Question mapToQuestion(Map<String, Object> questionData) {
        Question question = new Question();
        question.setQuestionText((String) questionData.get("questionText"));
        question.setQuestionType(com.java.quizcraft.model.QuestionType.valueOf((String) questionData.get("questionType")));
        
        if (questionData.get("options") != null) {
            @SuppressWarnings("unchecked")
            List<String> options = (List<String>) questionData.get("options");
            question.setOptions(options);
        }
        
        if (questionData.get("correctAnswerIndex") != null) {
            question.setCorrectAnswerIndex((Integer) questionData.get("correctAnswerIndex"));
        }
        
        if (questionData.get("correctWrittenAnswer") != null) {
            question.setCorrectWrittenAnswer((String) questionData.get("correctWrittenAnswer"));
        }
        
        if (questionData.get("explanation") != null) {
            question.setExplanation((String) questionData.get("explanation"));
        }
        
        return question;
    }
    
    @GetMapping("/my-quizzes")
    public ResponseEntity<?> getUserQuizzes(HttpSession session) {
        User user = getAuthenticatedUser(session);
        if (user == null) return ResponseEntity.status(401).body("User not authenticated");
        
        return ResponseEntity.ok(quizService.getUserQuizzes(user));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuiz(@PathVariable Long id) {
        Optional<Quiz> quiz = quizService.getQuizById(id);
        return quiz.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long id, HttpSession session) {
        User user = getAuthenticatedUser(session);
        if (user == null) return ResponseEntity.status(401).body("User not authenticated");
        
        quizService.deleteQuiz(id, user);
        return ResponseEntity.ok(Map.of("message", "Quiz deleted successfully"));
    }
} 