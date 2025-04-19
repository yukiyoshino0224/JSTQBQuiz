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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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
    public String LoginUser(@ModelAttribute LoginForm form, Model model) {
        User user = userRepository.findByEmail(form.getEmail());
        
        if (user != null && passwordEncoder.matches(form.getPassword(), user.getPassword())) {
            return "redirect:/menu";
        } else { 
            model.addAttribute("error", "メールアドレスまたはパスワードが間違えています");
            return "login";
        }
    }
}
