package com.example.chatsys.controller;

import com.example.chatsys.entity.User;
import com.example.chatsys.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // 添加登录处理方法
    // 登录处理方法（修改后）
    @PostMapping("/login")
    public String doLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        User user = userService.login(username, password);
        if (user != null) {
            // 关键修复：确保userId存入Session（Long类型）
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getNickname() != null ? user.getNickname() : user.getUsername());
            return "redirect:/chat";
        } else {
            model.addAttribute("error", "用户名或密码错误");
            return "login";
        }
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        if (userService.register(user)) {
            return "redirect:/login?success";
        } else {
            model.addAttribute("error", "用户名或邮箱已存在");
            return "register";
        }
    }

    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }
}