package com.java.quizcraft.service;

import com.java.quizcraft.model.Quiz;
import com.java.quizcraft.model.User;
import com.java.quizcraft.model.Question;
import com.java.quizcraft.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QuizService {
    
    @Autowired
    private QuizRepository quizRepository;
    
    public Quiz saveQuiz(String title, String description, List<Question> questions, User user) {
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setDescription(description);
        quiz.setQuestions(questions);
        quiz.setCreatedBy(user);
        
        return quizRepository.save(quiz);
    }
    
    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }
    
    public List<Quiz> getUserQuizzes(User user) {
        return quizRepository.findByCreatedByOrderByCreatedAtDesc(user);
    }
    
    public void deleteQuiz(Long quizId, User user) {
        Optional<Quiz> quiz = quizRepository.findById(quizId);
        if (quiz.isPresent() && quiz.get().getCreatedBy().getId().equals(user.getId())) {
            quizRepository.deleteById(quizId);
        }
    }
} 