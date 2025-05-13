package com.java.quizcraft.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.java.quizcraft.model.Question;
import com.java.quizcraft.service.QuizService;

@Controller
public class HomeController{
    @Autowired
    private QuizService quizService;

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @PostMapping("/submit")
    @ResponseBody
    public void submitQuiz(@RequestBody List<Question> questions, @RequestParam String mode){
        quizService.processQuiz(questions, "learn".equals(mode));
    }

    @GetMapping("/quiz")
    public String showQuizPage(Model model, @RequestParam String mode){
        model.addAttribute("currentQuestion",quizService.getCurrentQuestion());
        model.addAttribute("currentQuestionIndex", quizService.getCurrentQuestionIndex());
        model.addAttribute("totalQuestions", quizService.getQuestionsAmount());
        model.addAttribute("mode", mode);
        return "quiz";
    }
    
}