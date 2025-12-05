package com.example.chatsys.service;

import com.example.chatsys.entity.Admin;

public interface AdminService {
    Admin findByAdminName(String adminName);

    Admin findByEmail(String email);
}