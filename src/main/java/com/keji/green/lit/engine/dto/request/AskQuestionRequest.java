package com.keji.green.lit.engine.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 面试提问请求
 * @author xiangjun_lee
 */
@Data
public class AskQuestionRequest {
    
    /**
     * 问题内容(当前文本)
     */
    @NotBlank(message = "问题内容不能为空")
    private String question;

    /**
     * 历史对话
     */
    private String historyChat;
} 