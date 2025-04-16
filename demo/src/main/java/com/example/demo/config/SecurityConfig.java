package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/css/**", "/login").permitAll() // 登録画面、CSS、ログインは誰でもOK
                        .requestMatchers("/menu", "/chapter/**", "/quiz/**").authenticated() // メニュー、クイズ画面は認証必須
                        .anyRequest().authenticated() // その他は許可
                )
                .formLogin(form -> form
                        .loginPage("/login") // カスタムログインページ
                        .defaultSuccessUrl("/menu", true) // ログイン成功後の遷移先
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout") // ログアウト後の遷移先
                        .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    

}
