package com.keji.green.lit.engine.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 用户角色枚举
 * 定义系统中可用的用户角色类型
 */
@Getter
@RequiredArgsConstructor
public enum UserRole {
    
    USER(1, "普通用户"),
    ADMIN(2, "管理员");

    private final int code;
    private final String desc;

    /**
     * 根据code获取枚举值
     */
    public static UserRole fromCode(int code) {
        for (UserRole role : values()) {
            if (role.getCode() == code) {
                return role;
            }
        }
        return USER;
    }

    /**
     * 根据code获取描述
     */
    public static String getDescByCode(int code) {
        return fromCode(code).getDesc();
    }
} 