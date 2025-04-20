package com.keji.green.lit.engine.dto.request;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

/**
 * @author xiangjun_lee
 * @date 2025/4/20 10:28
 */
@Data
public class FastAnswerParam {

    /**
     * 简历文本
     */
    @JSONField(name = "resume_text")
    private String resumeText;

    /**
     * 面试信息
     */
    @JSONField(name = "interview_info")
    private String interviewInfo;

    /**
     * 历史对话
     */
    @JSONField(name = "history_chat")
    private List<String> historyChat;
}
