package com.example.demo.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.entity.User;

public class LoginUserDetails implements UserDetails {

    private final User user;

    public LoginUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Integer getUserId() {
        return user.getId().intValue(); // 明示的にIntegerに変換！
    }
    

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    
    // パスワードを返す（データベースに保存されている暗号化済みのパスワード）
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // ログインIDとして使う値（ここではemailを使ってる）
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // アカウントの有効性の設定（すべてtrueでOK）
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
