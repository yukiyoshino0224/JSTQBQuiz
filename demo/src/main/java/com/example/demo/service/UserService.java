package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.form.RegisterForm;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(RegisterForm form) {
        String encodedPassword = passwordEncoder.encode(form.getPassword());
        User user = new User(form.getName(), form.getEmail(), encodedPassword);
        return userRepository.save(user);
    }
}
