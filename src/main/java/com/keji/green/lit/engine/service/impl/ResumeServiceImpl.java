package com.keji.green.lit.engine.service.impl;

import com.keji.green.lit.engine.dto.request.ResumeEnhanceRequest;
import com.keji.green.lit.engine.dto.response.ResumeEnhanceResponse;
import com.keji.green.lit.engine.enums.UsageTypeEnum;
import com.keji.green.lit.engine.exception.BusinessException;
import com.keji.green.lit.engine.exception.ErrorCode;
import com.keji.green.lit.engine.integration.LlmWrapService;
import com.keji.green.lit.engine.mapper.UsageRecordMapper;
import com.keji.green.lit.engine.model.UsageRecord;
import com.keji.green.lit.engine.model.User;
import com.keji.green.lit.engine.service.ResumeService;
import com.keji.green.lit.engine.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简历美化服务实现类
 * @author xiangjun_lee
 */
@Slf4j
@Service("resumeService")
public class ResumeServiceImpl implements ResumeService {

    @Resource
    private UserService userService;

    @Resource
    private UsageRecordMapper usageRecordMapper;

    @Resource
    private LlmWrapService llmWrapService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResumeEnhanceResponse enhanceResume(ResumeEnhanceRequest request) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        String phone = authentication.getName();
        User user = userService.queryNormalUserByPhone(phone);

        // TODO: 校验余额是否充足

        // todo 模拟扣款，假设每次美化花费10分
        long costInCents = 10L;

        // 调用LLM服务优化简历
        Pair<Boolean, String> optimizeResumeResult = llmWrapService.optimizeResume(request.getResumeText());

        // 记录积分使用记录
        if (optimizeResumeResult.getLeft()) {
            UsageRecord usageRecord = UsageRecord.builder()
                    .uid(user.getUid())
                    .usageType(UsageTypeEnum.RESUME_ENHANCE.getCode())
                    .costInCents(costInCents)
                    .build();
            // todo 后续可以增加异步重试
            usageRecordMapper.insertSelective(usageRecord);
            // 更新用户简历
            User updateUser = new User();
            updateUser.setUid(user.getUid());
            updateUser.setResumeText(optimizeResumeResult.getRight());
            userService.updateUserByUid(updateUser);
        }

        ResumeEnhanceResponse response = new ResumeEnhanceResponse();
        response.setEnhancedResume(optimizeResumeResult.getRight());
        return response;
    }
} 