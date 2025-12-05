package com.example.chatsys.service;

import com.example.chatsys.dto.Result;
import com.example.chatsys.dto.SearchResult;
import com.example.chatsys.entity.Contact;

public interface ContactService {
    // 搜索用户或管理员
    SearchResult searchByEmail(String email);
    
    // 添加联系人
    Result addContact(Contact contact);
}