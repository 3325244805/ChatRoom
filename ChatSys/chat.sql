-- 创建chatdata数据库（指定字符集和校对规则）
CREATE DATABASE IF NOT EXISTS chatdata
DEFAULT CHARACTER SET utf8mb4
DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用chatdata数据库
USE chatdata;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '用户邮箱',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希值(bcrypt)',
    avatar_url VARCHAR(255) NULL COMMENT '头像URL',
    nickname VARCHAR(50) NULL COMMENT '用户昵称',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    last_login_at DATETIME NULL COMMENT '最后登录时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='普通用户表';

-- 创建管理员表
CREATE TABLE IF NOT EXISTS admins (
                                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      admin_name VARCHAR(50) NOT NULL UNIQUE COMMENT '管理员账号',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '管理员邮箱',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希值(bcrypt)',
    role TINYINT DEFAULT 2 COMMENT '角色：1-超级管理员，2-普通管理员',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    last_login_at DATETIME NULL COMMENT '最后登录时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员表';

-- 创建聊天记录表
CREATE TABLE IF NOT EXISTS chat_records (
                                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                            sender_id BIGINT NOT NULL COMMENT '发送者ID(关联users或admins)',
                                            receiver_id BIGINT NOT NULL COMMENT '接收者ID(关联users或admins)',
                                            message_type TINYINT NOT NULL COMMENT '消息类型：1-文本，2-图片，3-文件',
                                            content TEXT NULL COMMENT '文本消息内容',
                                            file_url VARCHAR(255) NULL COMMENT '图片/文件URL',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    sender_type TINYINT NOT NULL COMMENT '发送者类型：1-用户，2-管理员',
    session_id VARCHAR(64) NULL COMMENT '会话ID(用于关联对话)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    -- 联合索引优化查询
    INDEX idx_sender_receiver (sender_id, receiver_id),
    INDEX idx_session (session_id),
    INDEX idx_created_at (created_at)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天记录表';

-- 创建管理员操作日志表
CREATE TABLE IF NOT EXISTS admin_operation_logs (
                                                    id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                    admin_id BIGINT NOT NULL COMMENT '操作管理员ID',
                                                    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型(如：用户封禁、消息删除、角色变更)',
    target_type VARCHAR(30) NULL COMMENT '操作目标类型(如：user、chat_record)',
    target_id BIGINT NULL COMMENT '操作目标ID',
    operation_detail TEXT NULL COMMENT '操作详情描述',
    ip_address VARCHAR(50) NULL COMMENT '操作IP地址',
    user_agent TEXT NULL COMMENT '客户端信息',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    -- 外键关联管理员表
    CONSTRAINT fk_admin_log_admin_id FOREIGN KEY (admin_id)
    REFERENCES admins(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    -- 索引优化
    INDEX idx_admin_id (admin_id),
    INDEX idx_operation_type (operation_type),
    INDEX idx_created_at (created_at)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员操作日志表';

-- 可选：创建会话表（优化对话管理）
CREATE TABLE IF NOT EXISTS chat_sessions (
                                             id VARCHAR(64) PRIMARY KEY COMMENT '会话ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    admin_id BIGINT NULL COMMENT '管理员ID(客服场景)',
    last_message_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后消息时间',
    status TINYINT DEFAULT 1 COMMENT '会话状态：1-活跃，0-关闭',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_admin (user_id, admin_id),
    CONSTRAINT fk_session_user FOREIGN KEY (user_id)
    REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_session_admin FOREIGN KEY (admin_id)
    REFERENCES admins(id) ON DELETE SET NULL ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天会话表';
-- 创建联系人关系表
CREATE TABLE IF NOT EXISTS contacts (
                                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                        user_id BIGINT NOT NULL COMMENT '当前用户ID',
                                        contact_id BIGINT NOT NULL COMMENT '联系人ID',
                                        contact_type TINYINT NOT NULL COMMENT '联系人类型：1-普通用户，2-管理员',
                                        status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-已删除',
                                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
                                        updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    -- 确保不会重复添加联系人
                                        UNIQUE KEY uk_user_contact (user_id, contact_id, contact_type),
    -- 外键关联
    CONSTRAINT fk_contacts_user FOREIGN KEY (user_id)
    REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系人关系表';