package com.example.chatsys.dto;

import lombok.Data;

@Data
public class SearchResult {
    private Long id;
    private String username;
    private String email;
    private Integer type; // 1-用户，2-管理员

}