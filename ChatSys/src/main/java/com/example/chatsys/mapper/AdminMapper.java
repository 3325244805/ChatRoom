package com.example.chatsys.mapper;

import com.example.chatsys.entity.Admin;
import com.example.chatsys.dto.SearchResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminMapper {
    Admin findByAdminName(@Param("adminName") String adminName);

    Admin findById(@Param("id") Long id);

    Admin findByEmail(@Param("email") String email);

    SearchResult selectByEmail(@Param("email") String email);
}