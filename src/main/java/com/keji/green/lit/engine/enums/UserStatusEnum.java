package com.keji.green.lit.engine.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 用户状态枚举
 * 定义系统中用户可能的状态
 * @author xiangjun_lee
 */
@Getter
@RequiredArgsConstructor
public enum UserStatusEnum {
    
    NORMAL(1, "正常"),
    RISK_CONTROL(2, "风控"),
    DISABLED(3, "禁用"),
    CANCELLED(4, "注销");

    private final int code;
    private final String desc;

    /**
     * 根据code获取枚举值
     */
    public static UserStatusEnum fromCode(int code) {
        for (UserStatusEnum status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return NORMAL;
    }

    /**
     * 根据code获取描述
     */
    public static String getDescByCode(int code) {
        return fromCode(code).getDesc();
    }

    /**
     * 判断用户是否处于正常状态
     */
    public static boolean isNormal(int status) {
        return status == NORMAL.getCode();
    }

    /**
     * 判断用户是否处于风控状态
     */
    public static boolean isRiskControl(int status) {
        return status == RISK_CONTROL.getCode();
    }

    /**
     * 判断用户是否处于禁用状态
     */
    public static boolean isDisabled(int status) {
        return status == DISABLED.getCode();
    }

    /**
     * 判断用户是否处于注销状态
     */
    public static boolean isCancelled(int status) {
        return status == CANCELLED.getCode();
    }
}
