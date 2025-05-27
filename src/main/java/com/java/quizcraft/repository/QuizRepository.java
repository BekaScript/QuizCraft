package com.java.quizcraft.repository;

import com.java.quizcraft.model.Quiz;
import com.java.quizcraft.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    
    List<Quiz> findAllByOrderByCreatedAtDesc();

    List<Quiz> findByCreatedByOrderByCreatedAtDesc(User user);
} 