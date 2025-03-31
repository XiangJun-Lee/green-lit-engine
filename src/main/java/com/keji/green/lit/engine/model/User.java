package com.keji.green.lit.engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

/**
 * 用户实体类
 * 实现UserDetails接口以支持Spring Security认证
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    /**
     * 用户ID，主键
     */
    private Long uid;

    /**
     * 用户手机号，唯一，用作登录账号
     */
    private String phone;

    /**
     * 用户密码，加密存储
     */
    private String password;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 更新时间
     */
    private Date gmtModify;

    /**
     * 最后登录时间
     */
    private Date lastLoginAt;

    /**
     * 用户积分余额
     */
    private Integer creditBalance;

    /**
     * 是否激活，false表示账号已注销
     */
    private Boolean isActive;

    /**
     * 用户简历文本
     */
    private String resumeText;

    /**
     * 用户角色
     */
    private Integer role;
    
    /**
     * 客户端连接信息 (ip:port)
     */
    private String clientConnection;

    /**
     * 获取用户权限信息
     * @return 用户权限集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRole userRole = UserRole.fromCode(role);
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
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
} 