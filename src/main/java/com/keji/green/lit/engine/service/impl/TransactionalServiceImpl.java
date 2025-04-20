package com.keji.green.lit.engine.service.impl;

import com.keji.green.lit.engine.exception.BusinessException;
import com.keji.green.lit.engine.exception.ErrorCode;
import com.keji.green.lit.engine.mapper.InterviewInfoMapper;
import com.keji.green.lit.engine.model.InterviewInfo;
import com.keji.green.lit.engine.service.TransactionalService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author xiangjun_lee
 * @date 2025/4/20 10:46
 */
@Service("transactionalService")
public class TransactionalServiceImpl implements TransactionalService {

    @Resource
    private InterviewInfoMapper interviewInfoMapper;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void fastAnswerCharging(InterviewInfo interviewInfo) {
        // todo 查询计费规则
        InterviewInfo updateInterviewInfo = new InterviewInfo();
        updateInterviewInfo.setInterviewId(interviewInfo.getInterviewId());
        updateInterviewInfo.setAgTotalCost(interviewInfo.getAgTotalCost());
        updateInterviewInfo.setAgTotalCount(interviewInfo.getAgTotalCount());
        // todo 根据计费规则扣费，先写死50
        if (interviewInfoMapper.updateAgUsageByInterviewId(interviewInfo.getInterviewId(), 50L) <= 0) {
            throw new BusinessException(ErrorCode.DATABASE_WRITE_ERROR, "面试记录更新费用失败");
        }
        // todo 真正扣款逻辑
    }
}
