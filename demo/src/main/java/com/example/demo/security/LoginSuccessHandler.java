package com.example.demo.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // ログインに使ったemail（ユーザー名）を取得
        String email = authentication.getName();

        // DBからユーザーを探す
        User user = userRepository.findByEmail(email);

        // セッションに user_id を格納
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user_id", user.getId());
        }

        // ログイン成功後のリダイレクト先
        response.sendRedirect("/menu");
    }
}
