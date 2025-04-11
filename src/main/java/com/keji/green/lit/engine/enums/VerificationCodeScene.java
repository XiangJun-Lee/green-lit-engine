package com.keji.green.lit.engine.enums;

import lombok.Getter;

/**
 * 验证码场景枚举
 * 用于区分不同业务场景下的验证码
 * @author xiangjun_lee
 */
@Getter
public enum VerificationCodeScene {
    
    /**
     * 注册场景
     */
    REGISTER("register", "注册"),
    
    /**
     * 登录场景
     */
    LOGIN("login", "登录"),
    
    /**
     * 修改密码场景
     */
    RESET_PASSWORD("reset_password", "修改密码"),
    
    /**
     * 更换绑定手机号场景
     */
    CHANGE_PHONE("change_phone", "更换绑定手机号");
    
    private final String code;
    private final String desc;
    
    VerificationCodeScene(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
} 