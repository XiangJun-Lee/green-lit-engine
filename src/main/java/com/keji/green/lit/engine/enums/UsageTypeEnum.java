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
    SPEECH_TO_TEXT(1, "语音转文字"),

    /**
     * 快速问答
     */
    FAST_ANSWER(2, "快速问答"),
    
    /**
     * 联网问答
     */
    ONLINE_ANSWER(3, "联网问答"),
    
    /**
     * 截图问答
     */
    SCREENSHOT_ANSWER(4, "截图问答"),

    /**
     * 联网截图问答
     */
    ONLINE_SCREENSHOT_ANSWER(5, "联网截图问答"),

    /**
     * 简历美化
     */
    RESUME_ENHANCE(6, "简历美化");

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