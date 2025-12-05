package com.example.chatsys.service.impl;

import com.example.chatsys.dto.Result;
import com.example.chatsys.dto.SearchResult;
import com.example.chatsys.entity.Contact;
import com.example.chatsys.mapper.AdminMapper;
import com.example.chatsys.mapper.ContactMapper;
import com.example.chatsys.mapper.UserMapper;
import com.example.chatsys.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private ContactMapper contactMapper;

    @Override
    public SearchResult searchByEmail(String email) {
        // 先搜索普通用户
        SearchResult user = userMapper.selectByEmail(email);
        if (user != null) {
            return user;
        }
        // 再搜索管理员
        return adminMapper.selectByEmail(email);
    }

    @Override
    @Transactional
    public Result addContact(Contact contact) {
        // 检查是否自己添加自己
        if (contact.getUserId().equals(contact.getContactId())) {
            return Result.fail("不能添加自己为联系人");
        }

        // 检查是否已添加
        Integer count = contactMapper.checkExists(contact);
        if (count != null && count > 0) {
            return Result.fail("该联系人已添加");
        }

        // 执行添加
        contactMapper.insert(contact);
        return Result.success(null);
    }
}

