package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginViewController {

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }
}


















/*package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CustomUserDetailsService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class LoginController {

    @Autowired
    private HttpSession session;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final CustomUserDetailsService userService;

    public LoginController(CustomUserDetailsService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        userService.register(user);
        return "redirect:/login"; // 成功画面へ
    }

    // ログインページは Spring Security に任せる
     @GetMapping("/login")
     public String showLoginForm() {
     return "login"; // Spring Security のログインページを表示
     }

    // ログアウト処理（Spring Security が管理する）
    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login?logout"; // ログアウト後、ログイン画面にリダイレクト
    }

    // ログイン後の遷移ページ（Welcomeページ）
    //@GetMapping("/welcome")
    //public String showWelcomePage() {
       // return "welcome"; // ログイン済みのユーザーに表示するページ
    //}
}*/
