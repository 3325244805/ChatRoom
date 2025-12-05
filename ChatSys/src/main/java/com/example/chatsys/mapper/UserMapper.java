package com.example.chatsys.mapper;

import com.example.chatsys.dto.SearchResult;
import com.example.chatsys.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);

    int insert(User user);

    // 根据邮箱查询用户
    User findByEmail(@Param("email") String email);

    SearchResult selectByEmail(@Param("email") String email);

    // 根据ID查询用户
    User selectById(@Param("id") Long id);


    int updateLastLogin(User user);


}