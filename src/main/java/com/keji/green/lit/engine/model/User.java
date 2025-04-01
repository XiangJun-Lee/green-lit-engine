package com.keji.green.lit.engine.model;

import com.keji.green.lit.engine.enums.UserRole;
import com.keji.green.lit.engine.enums.UserStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * @author xiangjun_lee
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    /**
     * 用户id
     */
    private Long uid;

    /**
     * 积分余额
     */
    private Integer pointsBalance;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户角色
     */
    private Integer userRole;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 注册时间
     */
    private Date registrationTime;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModify;

    /**
     * 简历文本
     */
    private String resumeText;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRole role = UserRole.fromCode(userRole);
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserStatusEnum.isNormal(status);
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserStatusEnum.isNormal(status);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserStatusEnum.isNormal(status);
    }

    @Override
    public boolean isEnabled() {
        return UserStatusEnum.isNormal(status);
    }
}