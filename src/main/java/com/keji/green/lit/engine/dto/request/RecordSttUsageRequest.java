package com.keji.green.lit.engine.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 记录 STT 使用情况请求
 *
 * @author xiangjun_lee
 */
@Data
public class RecordSttUsageRequest {

    /**
     * 使用时长（秒）
     */
    @NotNull(message = "使用时长不能为空")
    @Min(value = 1, message = "使用时长必须大于0")
    private Long durationSeconds;

    /**
     * 费用（分）
     */
    @NotNull(message = "费用不能为空")
    @Min(value = 0, message = "费用不能为负数")
    private Long costInCents;
} 