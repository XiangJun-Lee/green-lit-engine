package com.keji.green.lit.engine.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 截图快答请求
 * @author xiangjun_lee
 */
@Data
public class ScreenshotQuestionRequest {
    
    /**
     * 图片base64编码
     */
    @NotBlank(message = "图片不能为空")
    private String imageBase64;
} 