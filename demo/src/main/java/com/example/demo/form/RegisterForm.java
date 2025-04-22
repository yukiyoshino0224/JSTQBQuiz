package com.example.demo.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterForm {

    @NotBlank(message = "名前を入力してください")
    @Size(max = 20, message = "名前は20字以内で入力してください")
    private String name;

    @NotBlank(message = "メールアドレスを入力してください")
    @Email(message = "メールアドレスが不正です")
    @Size(max = 100, message = "メールアドレスは100字以内で入力してください")
    private String email;

    @NotBlank(message = "パスワードを入力してください")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "半角英数で入力してください")
    @Size(min = 4, max = 20, message = "パスワードは4文字以上20字以内、半角英数で入力してください")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public String toString() {
        return "RegisterForm[name = " + name + ", email = " + email + ", password = " + password + "]";
    }
}
