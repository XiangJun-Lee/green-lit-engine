package com.keji.green.lit.engine.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 创建面试请求
 * @author xiangjun_lee
 * @date 2025/4/6 16:24
 */
@Data
public class CreateInterviewRequest {

    /**
     * 面试名称
     */
    @NotBlank(message = "面试名称不能为空")
    private String interviewName;

    /**
     * 面试语言
     */
    private String interviewLanguage;

    /**
     * 编程语言
     */
    private String programmingLanguage;

    /**
     * 职位信息
     */
    private String positionInfo;

    /**
     * 职位要求
     */
    private String jobRequirements;

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
    private Map<String,String> shortcutConfig;

}
