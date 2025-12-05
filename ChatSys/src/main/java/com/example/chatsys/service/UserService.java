package com.example.chatsys.service;

import com.example.chatsys.entity.User;

public interface UserService {
    User findByUsername(String username);
    // 根据邮箱搜索用户
    User searchUserByEmail(String email);
    boolean register(User user);
    User login(String username, String password);
}
