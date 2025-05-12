package com.keji.green.lit.engine.dto.bean;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;

import java.util.Map;

/**
 * @author xiangjun_lee
 * @date 2025/5/12 20:15
 */
@Data
public class BaseQueryPageParam {

    private Integer pageNum;

    private Integer pageSize;

    public Integer transferOffset() {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        return (pageNum - 1) * pageSize;
    }

    protected Map<String, Object> toMap(){
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(this);
        stringObjectMap.remove("pageNum");
        stringObjectMap.put("offset", transferOffset());
        return stringObjectMap;
    }
}
