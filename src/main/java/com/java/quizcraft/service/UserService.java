package com.java.quizcraft.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.java.quizcraft.model.User;
import com.java.quizcraft.repository.UserRepository;
import com.java.quizcraft.utils.PasswordUtil;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public boolean registerUser(String username, String password){
        if(userRepository.findByUsername(username).isPresent()) return false;
        String hashedPassword = PasswordUtil.hashPassword(password);
        User newUser = new User(username, hashedPassword);
        userRepository.save(newUser);
        return true;
    }
    
    public Optional<User> loginUser(String username, String password){
        Optional<User> userOptional = userRepository.findByUsername(username);
        if(userOptional.isPresent()){
            User user = userOptional.get();
            if(PasswordUtil.verifyPassword(password, user.getPassword())){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
