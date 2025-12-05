package com.example.chatsys.service.impl;

import com.example.chatsys.entity.User;
import com.example.chatsys.mapper.UserMapper;
import com.example.chatsys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User searchUserByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    public boolean register(User user) {
        // 检查用户名和邮箱是否已存在
        if (findByUsername(user.getUsername()) != null) {
            return false;
        }
        if (searchUserByEmail(user.getEmail()) != null) {
            return false;
        }
        // 加密密码
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        // 设置默认昵称
        if (user.getNickname() == null || user.getNickname().isEmpty()) {
            user.setNickname(user.getUsername());
        }
        return userMapper.insert(user) > 0;
    }

    // 实现登录方法
    @Override
    public User login(String username, String password) {
        User user = findByUsername(username);
        if (user == null) {
            return null;
        }
        // 检查用户状态
        if (user.getStatus() != 1) {
            return null;
        }
        if (passwordEncoder.matches(password, user.getPasswordHash())) {
            // 更新最后登录时间
            user.setLastLoginAt(LocalDateTime.now());
            // 注意：需要在UserMapper中添加update方法并实现
            userMapper.updateLastLogin(user);
            return user;
        }
        return null;
    }
}