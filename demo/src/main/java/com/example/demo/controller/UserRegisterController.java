package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.example.demo.form.RegisterForm;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class UserRegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid RegisterForm registerForm, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }
        model.addAttribute("message", "登録が完了しました");
        return "redirect:/register-success";
    }
    
    /*@PostMapping("/register")
    public String registerUser(@ModelAttribute RegisterForm form) {
        userService.registerUser(form);
        return "redirect:/register-success";
    }*/

    @GetMapping("/register-success")
    public String showRegisterSuccessPage() {
        return "register-success";
    }
}
