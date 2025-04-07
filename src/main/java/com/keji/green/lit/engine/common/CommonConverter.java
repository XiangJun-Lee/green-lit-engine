package com.keji.green.lit.engine.common;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.keji.green.lit.engine.dto.bean.InterviewExtraData;
import com.keji.green.lit.engine.dto.request.CreateInterviewRequest;
import com.keji.green.lit.engine.dto.response.InterviewRecordResponse;
import com.keji.green.lit.engine.dto.response.InterviewSummaryResponse;
import com.keji.green.lit.engine.model.InterviewInfo;
import com.keji.green.lit.engine.model.InterviewRecordWithBLOBs;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;


import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author xiangjun_lee
 * @date 2025/4/6 17:23
 */
@Mapper(componentModel = "spring")
public interface CommonConverter {

    CommonConverter INSTANCE = Mappers.getMapper(CommonConverter.class);

    // todo 默认快捷方式定义
    String SHORTCUT_CONFIG = "";

    @Mappings(value = {
            @Mapping(target = "onlineMode", defaultValue = "false"),
            @Mapping(target = "voiceTrigger", defaultValue = "false"),
            @Mapping(target = "shortcutConfig", expression = "java(updateShortcutConfig(request.getShortcutConfig())")
    })
    InterviewExtraData convert2InterviewExtraData(CreateInterviewRequest request);

    @Mappings({
            @Mapping(target = "extraData", ignore = true),
            @Mapping(target = "interviewLanguage", defaultValue = "中文"),
            @Mapping(target = "status",constant = "1")
    })
    InterviewInfo convert2InterviewInfo(CreateInterviewRequest request);

    @Mappings({})
    InterviewSummaryResponse convert2InterviewSummaryResponse(InterviewInfo interviewInfo);

    @Mappings({})
    List<InterviewRecordResponse> convert2recordResponseList(List<InterviewRecordWithBLOBs> interviewRecordList);

    default String updateShortcutConfig(String shortcutConfig) {
        if (StringUtils.isNoneBlank(shortcutConfig)) {
            try {
                Map<String, String> stringStringMap = JSON.parseObject(shortcutConfig, new TypeReference<Map<String, String>>() {
                });
                if (Objects.nonNull(stringStringMap) && !stringStringMap.isEmpty()) {
                    return shortcutConfig;
                }
            } catch (Exception e) {

            }
        }
        return SHORTCUT_CONFIG;
    }

}
