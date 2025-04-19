package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Securityがログイン時に呼び出すサービス
@Service
public class LoginUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // email を元にユーザーを検索
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            // ユーザーが見つからない場合 → Spring Securityがログイン失敗とみなす
            throw new UsernameNotFoundException("該当するユーザーが見つかりません");
        }

        // ユーザーが見つかった場合 → Spring Securityがこの情報で認証する
        return new LoginUserDetails(user);
    }
}

