package com.keji.green.lit.engine.service.impl;

import com.keji.green.lit.engine.dto.request.ResumeEnhanceRequest;
import com.keji.green.lit.engine.dto.response.ResumeEnhanceResponse;
import com.keji.green.lit.engine.enums.UsageTypeEnum;
import com.keji.green.lit.engine.exception.BusinessException;
import com.keji.green.lit.engine.exception.ErrorCode;
import com.keji.green.lit.engine.mapper.UsageRecordMapper;
import com.keji.green.lit.engine.model.UsageRecord;
import com.keji.green.lit.engine.model.User;
import com.keji.green.lit.engine.service.ResumeService;
import com.keji.green.lit.engine.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
        
        // 模拟扣款，假设每次美化花费10分
        long costInCents = 10L;
        // 模拟算法调用，这里简单地在原始简历前后添加一些标记
        String enhancedResume = "=== 美化后的简历 ===\n" + request.getResumeText() + "\n=== 美化结束 ===";
        
        // 记录使用记录
        UsageRecord usageRecord = UsageRecord.builder()
                .uid(user.getUid())
                .usageType(UsageTypeEnum.RESUME_ENHANCE.getCode())
                .costInCents(costInCents)
                .build();
        // todo 异步重试
        if (usageRecordMapper.insertSelective(usageRecord) <= 0) {

        }

        // 更新用户简历
        User updateUser = new User();
        updateUser.setUid(user.getUid());
        updateUser.setResumeText(enhancedResume);
        userService.updateUserByUid(updateUser);
        
        ResumeEnhanceResponse response = new ResumeEnhanceResponse();
        response.setEnhancedResume(enhancedResume);
        
        return response;
    }
} 