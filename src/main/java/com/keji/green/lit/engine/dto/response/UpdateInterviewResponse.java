package com.keji.green.lit.engine.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新面试信息响应
 * @author xiangjun_lee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInterviewResponse {
    
    /**
     * 是否更新成功
     */
    private boolean result;
} 