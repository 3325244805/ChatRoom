package com.example.chatsys.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Contact {
    private Long id;
    private Long userId;         // 当前用户ID
    private Long contactId;      // 联系人ID
    private Integer contactType; // 1-普通用户，2-管理员
    private Integer status;      // 1-正常，0-已删除
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}