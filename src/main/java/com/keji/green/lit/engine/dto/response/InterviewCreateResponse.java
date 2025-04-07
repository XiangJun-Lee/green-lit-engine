package com.keji.green.lit.engine.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 面试创建结果
 * @author xiangjun_lee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewCreateResponse {
    
    /**
     * 面试ID
     */
    private String interviewId;
} 