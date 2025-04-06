package com.keji.green.lit.engine.exception;

import lombok.Getter;

/**
 * 错误码枚举
 * 用于定义系统中的错误码和对应的错误信息
 */
@Getter
public enum ErrorCode {
    
    // 系统级错误码 (1000-1999)
    SYSTEM_ERROR(1000, "系统错误"),
    PARAM_ERROR(1001, "参数错误"),
    
    // 业务级错误码 (2000-2999)
    RATE_LIMIT_EXCEEDED(2000, "访问频率超限"),
    VERIFICATION_CODE_ERROR(2001, "验证码错误"),
    VERIFICATION_CODE_EXPIRED(2002, "验证码过期"),
    
    // 权限相关错误码 (3000-3999)
    UNAUTHORIZED(3000, "未授权访问"),
    FORBIDDEN(3001, "禁止访问"),
    AUTH_ERROR(3002, "认证失败"),
    SAME_PASSWORD(3003, "新旧密码相同"),
    
    // 数据相关错误码 (4000-4999)
    DATA_NOT_FOUND(4000, "数据不存在"),
    DATA_ALREADY_EXISTS(4001, "数据已存在"),
    USER_NOT_EXIST(4002, "用户不存在"),
    USER_ALREADY_EXISTS(4003, "用户已存在"),
    DATABASE_WRITE_ERROR(4004, "数据库写入失败"),


    // 外部服务错误码 (5000-5999)
    EXTERNAL_SERVICE_ERROR(5000, "外部服务调用失败");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s", code, message);
    }
} 