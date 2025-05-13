package com.java.quizcraft.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.quizcraft.model.Question;
import com.java.quizcraft.service.QuizService;

@RestController
@RequestMapping("/quiz")
public class QuizController {
    @Autowired
    private QuizService quizService;
    
    @GetMapping("/next")
    public Question getNextQuestion(){
        return quizService.getNextQuestion();
    }

    @GetMapping("/previous")
    public Question getPreviousQuestion(){
        return quizService.getPreviousQuestion();
    }

    @PostMapping("/results")
    public Map<String, Integer> getResults(@RequestBody List<Object> userAnswers){
        Integer correctAnswersCount = quizService.calculateCorrectAnswersCount(userAnswers);

        Map<String, Integer> response = new HashMap<>();
        response.put("correctAnswersCount", correctAnswersCount);
        return response;
    }

    @PostMapping("/markIncorrect")
    public void markIncorrect() {quizService.markCurrentQuestionIncorrect();}

}
