package com.example.chatsys.mapper;

import com.example.chatsys.entity.Contact;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ContactMapper {
    // 添加联系人
    int insert(Contact contact);
    
    // 检查是否已添加
    Integer checkExists(Contact contact);
}