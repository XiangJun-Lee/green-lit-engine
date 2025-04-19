package com.keji.green.lit.engine.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 使用类型枚举
 *
 * @author xiangjun_lee
 */
@Getter
@RequiredArgsConstructor
public enum UsageTypeEnum {

    /**
     * 语音转文字
     */
    STT(1, "语音转文字"),

    /**
     * 语音问答
     */
    AG(2, "语音问答");

    private final int code;
    private final String desc;

    public static UsageTypeEnum getByCode(int code) {
        for (UsageTypeEnum type : UsageTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
} 