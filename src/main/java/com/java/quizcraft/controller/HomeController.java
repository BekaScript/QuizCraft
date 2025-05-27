package com.java.quizcraft.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController{
    @GetMapping("/")
    public String index(HttpSession session){
        if (session.getAttribute("username") == null) return "redirect:/login";
        return "index";
    }

    @GetMapping("/ailess")
    public String getAIlessMainPage(){
        return "ailess";
    }

    @GetMapping("/quiz")
    public String showQuizPage() {
        return "quiz";
    }
    
    @GetMapping("/my-quizzes")
    public String showMyQuizzesPage(HttpSession session) {
        if (session.getAttribute("username") == null) return "redirect:/login";
        return "my-quizzes";
    }
    
    @GetMapping("/quiz/{id}")
    public String showSavedQuizPage() {
        return "quiz";
    }
}