package com.keji.green.lit.engine.dto.response;

import lombok.Data;

/**
 * 面试记录响应
 * @author xiangjun_lee
 */
@Data
public class InterviewRecordResponse {
    
    /**
     * 记录ID
     */
    private Long id;
    
    /**
     * 问题内容
     */
    private String question;
} 