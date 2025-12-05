package com.example.chatsys.controller;

import com.example.chatsys.dto.Result;
import com.example.chatsys.dto.SearchResult;
import com.example.chatsys.entity.Contact;
import com.example.chatsys.entity.User;
import com.example.chatsys.service.ContactService;
import com.example.chatsys.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    // 搜索用户/管理员（不变）
    @GetMapping("/search")
    public Result searchUser(@RequestParam String email) {
        SearchResult result = contactService.searchByEmail(email);
        return Result.success(result);
    }

    // 添加联系人（修改后）
    @PostMapping("/add")
    public Result addContact(@RequestBody Contact contact, HttpSession session) {
        // 修复：从SecurityContext获取认证用户ID（优先），兼容Session获取
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long currentUserId = null;

        if (principal instanceof UserDetails) {
            // 从Spring Security认证信息中获取用户名，再查询用户ID
            String username = ((UserDetails) principal).getUsername();
            User user = userService.findByUsername(username);
            if (user != null) {
                currentUserId = user.getId();
            }
        }

        // 如果SecurityContext获取失败，尝试从Session获取
        if (currentUserId == null) {
            Object sessionUserId = session.getAttribute("userId");
            // 处理可能的类型转换问题（例如存储时是String类型）
            if (sessionUserId instanceof Number) {
                currentUserId = ((Number) sessionUserId).longValue();
            } else if (sessionUserId instanceof String) {
                try {
                    currentUserId = Long.parseLong((String) sessionUserId);
                } catch (NumberFormatException e) {
                    // 转换失败，视为未登录
                }
            }
        }

        if (currentUserId == null) {
            return Result.fail("请先登录");
        }
        contact.setUserId(currentUserId); // 覆盖前端传递的userId
        return contactService.addContact(contact);
    }
}