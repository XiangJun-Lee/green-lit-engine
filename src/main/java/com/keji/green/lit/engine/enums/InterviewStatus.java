package com.keji.green.lit.engine.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 面试状态枚举
 *
 * @author xiangjun_lee
 */
@Getter
@RequiredArgsConstructor
public enum InterviewStatus {

    /**
     * 待开始
     */
    NOT_STARTED(0, "待开始"),

    /**
     * 进行中
     */
    ONGOING(1, "进行中"),

    /**
     * 手动结束
     */
    ENDED_MANUALLY(2, "手动结束"),

    /**
     * 自动结束
     */
    ENDED_AUTOMATICALLY(3, "自动结束");

    private final int code;

    private final String desc;

    public static InterviewStatus getByCode(int code) {
        for (InterviewStatus status : InterviewStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    public static boolean isEnd(int code) {
        return ENDED_MANUALLY.getCode() == code || ENDED_AUTOMATICALLY.getCode() == code;
    }

    public static boolean isOngoing(int code) {
        return ONGOING.getCode() == code;
    }
}