package com.java.quizcraft.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.java.quizcraft.model.User;
import com.java.quizcraft.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController{

    private final UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(HttpSession session){
        if (session.getAttribute("username") != null) return "redirect:/";
        return "register";
    }    

    @PostMapping("/register")
    public String processRegistration(@RequestParam String username,
                 @RequestParam String password, RedirectAttributes redirectAttributes){
        
        if(username == null || password == null || username.trim().isEmpty() || password.isEmpty()){
            redirectAttributes.addFlashAttribute("error", "Username and password cannot be empty.");
            return "redirect:/register";
        }
        boolean success = userService.registerUser(username.trim(), password);
        if(success){
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", "Username already exists.");
            return "redirect:/register";
        }    
    }

    @GetMapping("/login")
    public String showLoginForm(HttpSession session){
        if (session.getAttribute("username") != null) return "redirect:/";
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,  @RequestParam String password,
                               HttpSession session, RedirectAttributes redirectAttributes){
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Username and password cannot be empty.");
            return "redirect:/login";
        } 
        Optional<User> userOptional = userService.loginUser(username.trim(), password);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            session.setAttribute("username", user.getUsername());
            session.setAttribute("userId", user.getId());
            return "redirect:/";
        } else{
            redirectAttributes.addFlashAttribute("error", "Invalid username or password.");
            return "redirect:/login";
        }

    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes){
        if (session!=null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}