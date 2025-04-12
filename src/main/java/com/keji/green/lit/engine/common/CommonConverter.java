package com.keji.green.lit.engine.common;

import com.alibaba.fastjson2.JSON;
import com.keji.green.lit.engine.dto.bean.InterviewExtraData;
import com.keji.green.lit.engine.dto.request.CreateInterviewRequest;
import com.keji.green.lit.engine.dto.request.UpdateInterviewRequest;
import com.keji.green.lit.engine.dto.response.InterviewDetailResponse;
import com.keji.green.lit.engine.dto.response.InterviewRecordResponse;
import com.keji.green.lit.engine.dto.response.InterviewInfoResponse;
import com.keji.green.lit.engine.model.InterviewInfo;
import com.keji.green.lit.engine.model.InterviewRecordWithBLOBs;
import org.apache.commons.collections4.MapUtils;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;


import java.util.List;
import java.util.Map;

/**
 * @author xiangjun_lee
 * @date 2025/4/6 17:23
 */
@Mapper(componentModel = "spring")
public interface CommonConverter {

    CommonConverter INSTANCE = Mappers.getMapper(CommonConverter.class);

    @Mappings(value = {
            @Mapping(target = "onlineMode", defaultValue = "false"),
            @Mapping(target = "voiceTrigger", defaultValue = "false"),
            @Mapping(target = "shortcutConfig", defaultValue = "{\\\"cut\\\":\\\"Ctrl+a\\\",\\\"fastAnswer\\\":\\\"Ctrl+b\\\"}")
    })
    InterviewExtraData convert2InterviewExtraData(CreateInterviewRequest request);

    @Mappings({
            @Mapping(target = "extraData", ignore = true),
            @Mapping(target = "interviewLanguage", defaultValue = "中文"),
            @Mapping(target = "status", constant = "1")
    })
    InterviewInfo convert2InterviewInfo(CreateInterviewRequest request);

    @Mappings({
            @Mapping(target = "createTime", source = "gmtCreate"),
    })
    InterviewInfoResponse convert2InterviewInfoResponse(InterviewInfo interviewInfo);

    @Mappings({})
    List<InterviewRecordResponse> convert2RecordResponseList(List<InterviewRecordWithBLOBs> interviewRecordList);

    @Mappings({
            @Mapping(target = "createTime", source = "gmtCreate"),
    })
    List<InterviewInfoResponse> convert2InterviewListResponseList(List<InterviewInfo> interviewInfoList);

    @Mappings({
            @Mapping(target = "createTime", source = "interviewInfo.gmtCreate"),
            @Mapping(target = "records", expression = "java(convert2RecordResponseList(interviewRecordList))")
    })
    InterviewDetailResponse convert2InterviewDetailResponse(InterviewInfo interviewInfo, List<InterviewRecordWithBLOBs> interviewRecordList);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update2InterviewInfo(@MappingTarget InterviewInfo updateInterviewInfo, UpdateInterviewRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update2InterviewExtraData(@MappingTarget InterviewExtraData interviewExtraData, UpdateInterviewRequest request);
}
