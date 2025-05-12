package com.keji.green.lit.engine.dto.bean;

import lombok.*;

import java.util.Map;

/**
 * @author xiangjun_lee
 * @date 2025/5/12 20:18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryPageByUIdParam extends BaseQueryPageParam{

    private Long uid;


    @Override
    public Map<String, Object> toMap() {
        return super.toMap();
    }
}
