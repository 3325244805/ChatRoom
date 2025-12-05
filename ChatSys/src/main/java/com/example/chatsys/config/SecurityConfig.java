package com.example.chatsys.config;

import com.example.chatsys.service.UserService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.config.http.SessionCreationPolicy;

import jakarta.servlet.http.HttpSessionListener;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ObjectProvider<UserService> userServiceProvider;

    public SecurityConfig(ObjectProvider<UserService> userServiceProvider) {
        this.userServiceProvider = userServiceProvider;
    }

    private UserService getUserService() {
        return userServiceProvider.getObject();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/register", "/login", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/chat", "/ws/chat/**", "/api/contact/**").authenticated() // 新增/api/contact/**需要认证
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                // 配置会话管理，允许同一用户多设备登录
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 新增会话创建策略配置
                        .maximumSessions(-1) // -1 表示不限制同一用户的并发会话数
                        .expiredUrl("/login?expired") // 会话过期时跳转的URL
                )
                .formLogin(form -> form
                        .loginPage("/login") // 指定自定义登录页
                        .defaultSuccessUrl("/chat", true) // 登录成功后跳转的页面
                        .failureUrl("/login?error") // 登录失败跳转的页面
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        return http.build();
    }

    // 注册会话事件发布器，用于跟踪会话
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            com.example.chatsys.entity.User user = getUserService().findByUsername(username);
            if (user != null) {
                // 检查用户状态是否正常
                if (user.getStatus() != 1) {
                    throw new UsernameNotFoundException("用户已被禁用");
                }
                return User.withUsername(user.getUsername())
                        .password(user.getPasswordHash())
                        .roles("USER")
                        .build();
            }
            throw new UsernameNotFoundException("用户不存在");
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}