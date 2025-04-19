package com.keji.green.lit.engine.dto.bean;

import lombok.Data;

/**
 * @author xiangjun_lee
 * @date 2025/4/6 16:26
 */
@Data
public class InterviewExtraData {

    /**
     * 是否开启联网模式
     */
    private Boolean onlineMode;

    /**
     * 是否开启语音触发
     */
    private Boolean voiceTrigger;

    /**
     * 语音触发词列表
     */
    private String voiceTriggerWords;

    /**
     * 快捷键配置
     * key: 快捷键功能
     * value: 对应快捷键
     */
    private String shortcutConfig;
}
