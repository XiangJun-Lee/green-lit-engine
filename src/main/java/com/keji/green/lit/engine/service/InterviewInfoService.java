package com.keji.green.lit.engine.service;

import com.keji.green.lit.engine.dto.bean.QueryPageByUIdParam;
import com.keji.green.lit.engine.model.InterviewInfo;

import java.util.List;

/**
 * @author xiangjun_lee
 * @date 2025/5/12 20:01
 */
public interface InterviewInfoService {

    boolean createInterview(InterviewInfo interviewInfo);


    InterviewInfo selectByInterviewId(String interviewId);

    boolean updateByInterviewIdCas(InterviewInfo row);

    boolean updateByInterviewId(InterviewInfo row);

    /**
     * 分页查询面试列表
     * @param params 包含分页参数和查询条件
     * @return 面试信息列表
     */
    List<InterviewInfo> selectPageByUserId(QueryPageByUIdParam params);

    /**
     * 统计用户的面试数量
     * @param params 包含查询条件
     * @return 满足条件的面试数量
     */
    long countByUserIdAndStatus(QueryPageByUIdParam params);

    /**
     * 更新面试的语音识别时长和语音识别花费
     */
    boolean updateSttUsageByInterviewId(String interviewId, Long durationSeconds, Long costInCents);

    /**
     * 更新面试的语音识别时长和语音识别花费
     */
    int updateAgUsageByInterviewId(String interviewId, Long costInCents);

    /**
     * 强制结束面试
     * @param interviewIdList 面试ID列表
     * @return 结束面试的行数
     */
    int forceEndInterviewByInterviewIdList(List<String> interviewIdList);
}
