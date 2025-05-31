package com.java.quizcraft.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController{
    @GetMapping("/")
    public String index(HttpSession session, Model model){
        boolean isAuthenticated = session.getAttribute("username") != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        return "index";
    }

    @GetMapping("quiz-generator")
    public String getQuizGeneratorPage(HttpSession session, Model model){
        if (session.getAttribute("username") == null) return "redirect:/login";
        model.addAttribute("isAuthenticated", true);
        return "quiz-generator";
    }

    @GetMapping("/ailess")
    public String getAIlessMainPage(){
        return "ailess";
    }

    @GetMapping("/quiz")
    public String showQuizPage(HttpSession session, Model model) {
        if (session.getAttribute("username") == null) return "redirect:/login";
        model.addAttribute("isAuthenticated", true);
        return "quiz";
    }
    
    @GetMapping("/my-quizzes")
    public String showMyQuizzesPage(HttpSession session, Model model) {
        if (session.getAttribute("username") == null) return "redirect:/login";
        model.addAttribute("isAuthenticated", true);
        return "my-quizzes";
    }
    
    @GetMapping("/quiz/{id}")
    public String showSavedQuizPage(HttpSession session, Model model) {
        if (session.getAttribute("username") == null) return "redirect:/login";
        model.addAttribute("isAuthenticated", true);
        return "quiz";
    }
}