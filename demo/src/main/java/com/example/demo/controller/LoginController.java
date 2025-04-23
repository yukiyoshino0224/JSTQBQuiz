package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.demo.entity.User;
import com.example.demo.form.LoginForm;
import com.example.demo.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;



@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "/login";
    }

    @PostMapping("/login")
    public String LoginUser(@ModelAttribute LoginForm form, Model model, HttpSession session) {
        User user = userRepository.findByEmail(form.getEmail());
        
        if (user != null && passwordEncoder.matches(form.getPassword(), user.getPassword())) {
            session.setAttribute("userName", user.getName());
            return "redirect:/menu";
        } else { 
            model.addAttribute("error", "メールアドレスまたはパスワードが間違えています");
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}