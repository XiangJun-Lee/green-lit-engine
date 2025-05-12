package com.keji.green.lit.engine.service.impl;

import com.keji.green.lit.engine.dto.bean.QueryPageByUIdParam;
import com.keji.green.lit.engine.mapper.InterviewInfoMapper;
import com.keji.green.lit.engine.model.InterviewInfo;
import com.keji.green.lit.engine.service.InterviewInfoService;
import com.keji.green.lit.engine.utils.EncryptionUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author xiangjun_lee
 * @date 2025/5/12 20:02
 */
@Slf4j
@Service
public class InterviewInfoServiceImpl implements InterviewInfoService {

    @Resource
    private InterviewInfoMapper interviewInfoMapper;

    @Override
    public boolean createInterview(InterviewInfo interviewInfo) {
        if (StringUtils.isNotBlank(interviewInfo.getJobRequirements())) {
            interviewInfo.setJobRequirements(EncryptionUtils.encrypt(interviewInfo.getJobRequirements().trim()));
        }
        if (StringUtils.isNotBlank(interviewInfo.getPositionInfo())) {
            interviewInfo.setPositionInfo(EncryptionUtils.encrypt(interviewInfo.getPositionInfo().trim()));
        }
        return interviewInfoMapper.insertSelective(interviewInfo) == 1;
    }

    @Override
    public InterviewInfo selectByInterviewId(String interviewId) {
        if (StringUtils.isNotBlank(interviewId)) {
            Optional<InterviewInfo> optionalInterviewInfo = interviewInfoMapper.selectByPrimaryKey(interviewId);
            if (optionalInterviewInfo.isPresent()) {
                InterviewInfo interviewInfo = optionalInterviewInfo.get();
                if (StringUtils.isNotBlank(interviewInfo.getJobRequirements())) {
                    interviewInfo.setJobRequirements(EncryptionUtils.decrypt(interviewInfo.getJobRequirements()));
                }
                if (StringUtils.isNotBlank(interviewInfo.getPositionInfo())) {
                    interviewInfo.setPositionInfo(EncryptionUtils.decrypt(interviewInfo.getPositionInfo()));
                }
                return interviewInfo;
            }
            return null;
        }
        return null;
    }

    @Override
    public boolean updateByInterviewIdCas(InterviewInfo row) {
        if (StringUtils.isNotBlank(row.getJobRequirements())) {
            row.setJobRequirements(EncryptionUtils.encrypt(row.getJobRequirements().trim()));
        }
        if (StringUtils.isNotBlank(row.getPositionInfo())) {
            row.setPositionInfo(EncryptionUtils.encrypt(row.getPositionInfo().trim()));
        }
        return interviewInfoMapper.updateByPrimaryKeySelectiveCAS(row) == 1;
    }

    @Override
    public boolean updateByInterviewId(InterviewInfo row) {
        if (StringUtils.isNotBlank(row.getJobRequirements())) {
            row.setJobRequirements(EncryptionUtils.encrypt(row.getJobRequirements().trim()));
        }
        if (StringUtils.isNotBlank(row.getPositionInfo())) {
            row.setPositionInfo(EncryptionUtils.encrypt(row.getPositionInfo().trim()));
        }
        return interviewInfoMapper.updateByPrimaryKeySelective(row) == 1;
    }

    @Override
    public List<InterviewInfo> selectPageByUserId(QueryPageByUIdParam param) {

        List<InterviewInfo> interviewInfoList = interviewInfoMapper.selectPageByUserId(param.toMap());
        if (CollectionUtils.isNotEmpty(interviewInfoList)){
            for (InterviewInfo interviewInfo : interviewInfoList) {
                if (StringUtils.isNotBlank(interviewInfo.getJobRequirements())) {
                    interviewInfo.setJobRequirements(EncryptionUtils.decrypt(interviewInfo.getJobRequirements()));
                }
                if (StringUtils.isNotBlank(interviewInfo.getPositionInfo())) {
                    interviewInfo.setPositionInfo(EncryptionUtils.decrypt(interviewInfo.getPositionInfo()));
                }
            }
        }
        return interviewInfoList;
    }

    @Override
    public long countByUserIdAndStatus(QueryPageByUIdParam params) {
        return interviewInfoMapper.countByUserIdAndStatus(params.toMap());
    }

    @Override
    public boolean updateSttUsageByInterviewId(String interviewId, Long durationSeconds, Long costInCents) {

        return interviewInfoMapper.updateSttUsageByInterviewId(interviewId, durationSeconds, costInCents) == 1;
    }

    @Override
    public int updateAgUsageByInterviewId(String interviewId, Long costInCents) {
        return 0;
    }

    @Override
    public int forceEndInterviewByInterviewIdList(List<String> interviewIdList) {
        try {
            if (CollectionUtils.isNotEmpty(interviewIdList)) {
                return interviewInfoMapper.forceEndInterviewByInterviewIdList(interviewIdList);
            }
        } catch (Exception e) {
            log.warn("强制结束面试失败,interviewIdList:{}", interviewIdList, e);
        }
        return 0;
    }
}
