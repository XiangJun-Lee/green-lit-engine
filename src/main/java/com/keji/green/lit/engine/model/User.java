package com.keji.green.lit.engine.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

/**
 * 用户实体类
 * 实现UserDetails接口以支持Spring Security认证
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_info")
public class User implements UserDetails {

    /**
     * 用户ID，主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long uid;

    /**
     * 用户手机号，唯一，用作登录账号
     */
    @Column(unique = true, nullable = false)
    private String phone;

    /**
     * 用户密码，加密存储
     */
    @Column(nullable = false)
    private String password;

    /**
     * 创建时间
     */
    @Column(name = "gmt_create", nullable = false)
    private LocalDateTime gmtCreate;

    /**
     * 更新时间
     */
    @Column(name = "gmt_modify")
    private LocalDateTime gmtModify;

    /**
     * 最后登录时间
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    /**
     * 用户积分余额
     */
    @Column(name = "credit_balance")
    private Integer creditBalance;

    /**
     * 是否激活，false表示账号已注销
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    /**
     * 用户简历文本
     */
    @Column(name = "resume_text", columnDefinition = "TEXT")
    private String resumeText;

    /**
     * 用户角色
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole role;
    
    /**
     * 客户端连接信息 (ip:port)
     */
    @Column(name = "client_connection")
    private String clientConnection;

    /**
     * 获取用户权限信息
     * @return 用户权限集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * 获取用户名（手机号）
     * @return 用户手机号
     */
    @Override
    public String getUsername() {
        return phone;
    }

    /**
     * 账号是否未过期
     * @return true表示未过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return isActive;
    }

    /**
     * 账号是否未锁定
     * @return true表示未锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return isActive;
    }

    /**
     * 凭证是否未过期
     * @return true表示未过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return isActive;
    }

    /**
     * 账号是否启用
     * @return true表示启用
     */
    @Override
    public boolean isEnabled() {
        return isActive;
    }

    /**
     * 实体创建前的预处理
     * 设置创建时间和默认值
     */
    @PrePersist
    protected void onCreate() {
        gmtCreate = LocalDateTime.now();
        if (creditBalance == null) {
            creditBalance = 0;
        }
        if (isActive == null) {
            isActive = true;
        }
        if (role == null) {
            role = UserRole.USER;
        }
    }

    /**
     * 实体更新前的预处理
     * 设置更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        gmtModify = LocalDateTime.now();
    }
} 