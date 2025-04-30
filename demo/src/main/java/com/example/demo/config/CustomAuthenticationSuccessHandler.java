package com.example.demo.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public CustomAuthenticationSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName(); // ログインID（メールとか）
        User user = userRepository.findByEmail(username); // ユーザー取得（メソッド名は適宜変更！）

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("userId", user.getId());
            System.out.println("ログイン成功！userId: " + user.getId());
        }

        // 元々指定してた遷移先へリダイレクト
        response.sendRedirect("/menu");
    }
}
