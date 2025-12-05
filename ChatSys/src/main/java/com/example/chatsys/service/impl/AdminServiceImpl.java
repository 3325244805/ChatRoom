package com.example.chatsys.service.impl;

import com.example.chatsys.entity.Admin;
import com.example.chatsys.mapper.AdminMapper;
import com.example.chatsys.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin findByAdminName(String adminName) {
        return adminMapper.findByAdminName(adminName);
    }

    @Override
    public Admin findByEmail(String email) {
        return adminMapper.findByEmail(email);
    }
}