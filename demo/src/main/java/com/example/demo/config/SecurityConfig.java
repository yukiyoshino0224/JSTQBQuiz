package com.example.demo.config;

import com.example.demo.security.LoginUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final LoginUserDetailsService userDetailsService;

    public SecurityConfig(LoginUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 認証マネージャーの設定
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll() // ログイン・登録は許可
                .anyRequest().authenticated() // その他は認証が必要
            )
            .formLogin(login -> login
                .loginPage("/login") // ログインページのURL
                .loginProcessingUrl("/login") // フォームのactionに一致するURL
                .defaultSuccessUrl("/menu", true) // 成功時の遷移先
                .failureUrl("/login?error=true") // 失敗時のURL
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout") // ログアウト成功時
                .permitAll()
            )
            .csrf().disable(); // 開発時のみ無効

        return http.build();
    }
}
