package com.example.demo.repository;

import com.example.demo.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // ここに必要なクエリを書くこともできる（例：findByUsername など）
    //User findByUsername(String username);
    Optional<User> findByUsername(String username);
}
