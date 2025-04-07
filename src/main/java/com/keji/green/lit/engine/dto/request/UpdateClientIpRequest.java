package com.keji.green.lit.engine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新客户端IP请求DTO
 * 用于接收客户端IP更新请求
 * @author xiangjun_lee
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateClientIpRequest {
    
    /**
     * 客户端IP:端口
     */
    @NotBlank(message = "客户端IP不能为空")
    @Pattern(regexp = "^([0-9]{1,3}\\.){3}[0-9]{1,3}:[0-9]{1,5}$", message = "IP:端口格式不正确")
    private String clientIp;
} 