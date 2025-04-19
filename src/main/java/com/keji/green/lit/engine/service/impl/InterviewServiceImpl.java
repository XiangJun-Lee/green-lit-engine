package com.keji.green.lit.engine.service.impl;

import com.alibaba.fastjson.JSON;
import com.keji.green.lit.engine.common.CommonConverter;
import com.keji.green.lit.engine.dto.bean.InterviewExtraData;
import com.keji.green.lit.engine.dto.bean.QuestionAnswerRecordListQueryParam;
import com.keji.green.lit.engine.dto.request.AskQuestionRequest;
import com.keji.green.lit.engine.dto.request.CreateInterviewRequest;
import com.keji.green.lit.engine.dto.request.RecordSttUsageRequest;
import com.keji.green.lit.engine.dto.request.UpdateInterviewRequest;
import com.keji.green.lit.engine.dto.response.*;
import com.keji.green.lit.engine.enums.InterviewStatus;
import com.keji.green.lit.engine.enums.UsageTypeEnum;
import com.keji.green.lit.engine.exception.BusinessException;
import com.keji.green.lit.engine.exception.ErrorCode;
import com.keji.green.lit.engine.mapper.InterviewInfoMapper;
import com.keji.green.lit.engine.mapper.QuestionAnswerRecordMapper;
import com.keji.green.lit.engine.mapper.UsageRecordMapper;
import com.keji.green.lit.engine.model.InterviewInfo;
import com.keji.green.lit.engine.model.QuestionAnswerRecord;
import com.keji.green.lit.engine.model.UsageRecord;
import com.keji.green.lit.engine.model.User;
import com.keji.green.lit.engine.service.InterviewService;
import com.keji.green.lit.engine.service.QuestionAnswerRecordService;
import com.keji.green.lit.engine.service.UserService;
import com.keji.green.lit.engine.utils.DateTimeUtils;
import com.keji.green.lit.engine.utils.RedisUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.keji.green.lit.engine.utils.Constants.*;

/**
 * 面试服务实现类
 *
 * @author xiangjun_lee
 */
@Slf4j
@Service
public class InterviewServiceImpl implements InterviewService {

    @Resource
    private UserService userService;

    @Resource
    private InterviewInfoMapper interviewInfoMapper;

    @Resource
    private QuestionAnswerRecordMapper questionAnswerRecordMapper;

    @Resource
    private UsageRecordMapper usageRecordMapper;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private QuestionAnswerRecordService questionAnswerRecordService;

    // TODO: 注入算法服务客户端

    /**
     * 创建面试会话
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InterviewCreateResponse createInterview(CreateInterviewRequest request) {

        // 生成面试ID (UUID)
        String interviewId = UUID.randomUUID().toString();
        User currentUser = getCurrentUser();

        InterviewInfo interview = CommonConverter.INSTANCE.convert2InterviewInfo(request);
        if (StringUtils.isNotBlank(interview.getJobRequirements())) {
            interview.setJobRequirements(interview.getJobRequirements().trim());
        }
        interview.setUid(currentUser.getUid());
        interview.setInterviewId(interviewId);
        // 构建面试扩展字段
        InterviewExtraData extraData = CommonConverter.INSTANCE.convert2InterviewExtraData(request);
        interview.setExtraData(JSON.toJSONString(extraData));
        Date startTime = new Date();
        interview.setStartTime(startTime);
        interview.setEndTime(DateTimeUtils.plusHours(startTime, INTERVIEW_MAX_HOURS));
        if (interviewInfoMapper.insertSelective(interview) <= 0) {
            throw new BusinessException(ErrorCode.DATABASE_WRITE_ERROR, "创建面试失败");
        }
        if (StringUtils.isNotBlank(request.getResumeText()) && !StringUtils.equals(request.getResumeText(), currentUser.getResumeText())) {
            User user = new User();
            user.setUid(currentUser.getUid());
            user.setVersion(currentUser.getVersion());
            user.setResumeText(request.getResumeText().trim());
            userService.updateUserByUid(user);
        }
        return new InterviewCreateResponse(interviewId);
    }

    /**
     * 提问并获取答案（流式）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SseEmitter askQuestion(String interviewId, AskQuestionRequest request) {
        Long uid = getCurrentUserId();
        // 验证面试所有权
        Optional<InterviewInfo> optionalInterviewInfo = interviewInfoMapper.selectByPrimaryKey(interviewId);
        if (optionalInterviewInfo.isEmpty()) {
            throw new BusinessException(ErrorCode.INTERVIEW_NOT_FOUND);
        }
        InterviewInfo interviewInfo = optionalInterviewInfo.get();
        if (!uid.equals(interviewInfo.getUid())) {
            throw new BusinessException(ErrorCode.INTERVIEW_NOT_OWNED);
        }
        // 检查面试状态，确保面试处于进行中状态
        if (InterviewStatus.isEnd(interviewInfo.getStatus())) {
            throw new BusinessException(ErrorCode.INTERVIEW_ALREADY_ENDED);
        }

        // TODO 检查用户积分是否充足

        // 创建SSE发射器，超时设置为5分钟
        SseEmitter emitter = new SseEmitter(FIVE_MINUTE_MILLISECONDS);

        // 记录面试流水（问题）
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("interviewId", interviewId);
        queryParam.put("limit", 5);
        queryParam.put("orderByDesc", "id");
        List<QuestionAnswerRecord> questionAnswerRecordList = questionAnswerRecordMapper.selectListByInterviewId(queryParam);
        List<String> questionList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(questionAnswerRecordList)) {
            questionList = questionAnswerRecordList.stream().map(QuestionAnswerRecord::getQuestion)
                    .filter(StringUtils::isNotEmpty).toList();
        }
        String currentQuestion = request.getQuestion();
        try {
            // TODO: 保存面试流水到数据库
            QuestionAnswerRecord record = new QuestionAnswerRecord();
            record.setInterviewId(interviewId);
            record.setQuestion(request.getQuestion());
            if (questionAnswerRecordMapper.insertSelective(record) <= 0) {

            }
        } catch (Exception e) {
            log.error("保存面试流水失败", e);
        }

        // 异步调用算法服务
        CompletableFuture.runAsync(() -> {
            try {
                // TODO: 调用算法服务，获取答案
                // String answer = algorithmClient.getAnswer(request.getQuestion());
                StringBuilder answer = new StringBuilder();

                // 模拟流式返回
                for (int i = 0; i < 10; i++) {
                    String chunk = "这是回答的第" + (i + 1) + "部分。";
                    answer.append(chunk);
                    emitter.send(SseEmitter.event().name("message").data(chunk, org.springframework.http.MediaType.TEXT_PLAIN));
                    Thread.sleep(500);
                }

                // 发送完成事件
                emitter.send(SseEmitter.event().name("complete").data("完成", org.springframework.http.MediaType.TEXT_PLAIN));

                // 更新面试流水（答案）
                LocalDateTime answerTime = LocalDateTime.now();
                // TODO: 更新面试流水的答案
//                 questionAnswerRecordMapper.updateByPrimaryKeySelective(recordId, answer.toString(), answerTime);

                emitter.complete();
            } catch (Exception e) {
                log.error("处理面试问题失败: {}", e.getMessage(), e);
                try {
                    emitter.send(SseEmitter.event().name("error").data("处理问题时发生错误: " + e.getMessage(), org.springframework.http.MediaType.TEXT_PLAIN));
                    emitter.complete();
                } catch (IOException ex) {
                    emitter.completeWithError(ex);
                }
            }
        });
        return emitter;
    }

    /**
     * 结束面试
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InterviewInfoResponse endInterview(String interviewId) {
        Long uid = getCurrentUserId();
        // 验证面试所有权
        Optional<InterviewInfo> optionalInterviewInfo = interviewInfoMapper.selectByPrimaryKey(interviewId);
        if (optionalInterviewInfo.isEmpty()) {
            throw new BusinessException(ErrorCode.INTERVIEW_NOT_FOUND);
        }
        InterviewInfo interviewInfo = optionalInterviewInfo.get();
        if (!uid.equals(interviewInfo.getUid())) {
            throw new BusinessException(ErrorCode.INTERVIEW_NOT_OWNED);
        }
        if (InterviewStatus.isEnd(interviewInfo.getStatus())){
            InterviewInfoResponse response = CommonConverter.INSTANCE.convert2InterviewInfoResponse(interviewInfo);
            response.setTotalMinutes(DateTimeUtils.minutesBetween(interviewInfo.getStartTime(), interviewInfo.getEndTime()));
            return response;
        }

        // 记录结束时间
        InterviewInfo updateInterviewInfo = new InterviewInfo();
        updateInterviewInfo.setInterviewId(interviewId);
        updateInterviewInfo.setStatus(InterviewStatus.ENDED_MANUALLY.getCode());
        Date endTime = new Date();
        if (endTime.before(interviewInfo.getEndTime())) {
            updateInterviewInfo.setEndTime(endTime);
        }
        if (interviewInfoMapper.updateByPrimaryKeySelective(updateInterviewInfo)<=0){
            throw new BusinessException(ErrorCode.DATABASE_WRITE_ERROR, "结束面试失败，请稍后重试");
        }

        // 构建返回结果
        InterviewInfoResponse response = new InterviewInfoResponse();
        response.setInterviewId(interviewId);
        response.setInterviewName(interviewInfo.getInterviewName());
        response.setStartTime(interviewInfo.getStartTime());
        response.setAgTotalCost(interviewInfo.getAgTotalCost());
        response.setTotalMinutes(DateTimeUtils.minutesBetween(interviewInfo.getStartTime(), updateInterviewInfo.getEndTime()));
        response.setEndTime(updateInterviewInfo.getEndTime());
        response.setStatus(updateInterviewInfo.getStatus());
        return response;
    }

    /**
     * 获取面试详情
     */
    @Override
    public InterviewDetailResponse getInterviewDetail(String interviewId) {
        // 验证面试所有权
        User currentUser = getCurrentUser();
        // 验证面试所有权
        Optional<InterviewInfo> optionalInterviewInfo = interviewInfoMapper.selectByPrimaryKey(interviewId);
        if (optionalInterviewInfo.isEmpty()) {
            throw new BusinessException(ErrorCode.INTERVIEW_NOT_FOUND);
        }
        InterviewInfo interviewInfo = optionalInterviewInfo.get();
        if (!currentUser.getUid().equals(interviewInfo.getUid())) {
            throw new BusinessException(ErrorCode.INTERVIEW_NOT_OWNED);
        }
        InterviewExtraData interviewExtraData = StringUtils.isNotBlank(interviewInfo.getExtraData()) ? JSON.parseObject(interviewInfo.getExtraData(), InterviewExtraData.class) : null;
        // 获取面试提问信息
        QuestionAnswerRecordListQueryParam queryParam = new QuestionAnswerRecordListQueryParam();
        queryParam.setInterviewId(interviewId);
        queryParam.setLimit(interviewInfo.getAgTotalCount());
        queryParam.setOrderBy("id");
        queryParam.setOrderByDesc(true);
        List<QuestionAnswerRecord> questionAnswerRecordList = questionAnswerRecordService.questionAnswerRecordsList(queryParam);
        // todo mock 数据
        if (CollectionUtils.isEmpty(questionAnswerRecordList)) {
            QuestionAnswerRecord questionAnswerRecord = new QuestionAnswerRecord();
            questionAnswerRecord.setId(1L);
            questionAnswerRecord.setQuestion("请问什么是jvm");
            QuestionAnswerRecord questionAnswerRecord2 = new QuestionAnswerRecord();
            questionAnswerRecord2.setId(2L);
            questionAnswerRecord2.setQuestion("谈一谈你对java的理解");
            questionAnswerRecordList.add(questionAnswerRecord);
            questionAnswerRecordList.add(questionAnswerRecord2);
        }
        // 构建面试详情响应
        InterviewDetailResponse result = CommonConverter.INSTANCE.convert2InterviewDetailResponse(interviewInfo, interviewExtraData, questionAnswerRecordList);
        result.setResumeText(currentUser.getResumeText());

        // todo mock 数据
        result.setBalance(1000L);
        return result;
    }

    /**
     * 分页获取面试列表
     */
    @Override
    public PageResponse<InterviewInfoResponse> getInterviewList(Integer pageNum, Integer pageSize) {
        // 获取当前用户ID
        Long uid = getCurrentUserId();

        // 计算分页查询的偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        params.put("offset", offset);
        params.put("pageSize", pageSize);
        
        // 查询面试列表数据
        List<InterviewInfo> interviewInfoList = interviewInfoMapper.selectPageByUserId(params);
        
        // 统计总记录数
        long total = interviewInfoMapper.countByUserIdAndStatus(params);
        
        // 如果没有数据，直接返回空列表
        if (CollectionUtils.isEmpty(interviewInfoList)) {
            return PageResponse.build(new ArrayList<>(), total, pageNum, pageSize);
        }
        
        // 构建列表响应
        List<InterviewInfoResponse> responses = CommonConverter.INSTANCE.convert2InterviewListResponseList(interviewInfoList);

        // 未正确结束的面试id
        List<String> unEndedInterviewIds = new ArrayList<>();
        for (InterviewInfoResponse response : responses) {
            Date startTime = response.getStartTime();
            Date endTime = response.getEndTime();
            Date now = new Date();
            if (Objects.nonNull(endTime) && DateTimeUtils.hoursBetween(endTime, now) >= INTERVIEW_MAX_HOURS && !InterviewStatus.isEnd(response.getStatus())) {
                unEndedInterviewIds.add(response.getInterviewId());
                response.setStatus(InterviewStatus.ENDED_AUTOMATICALLY.getCode());
            }
            response.setTotalMinutes(DateTimeUtils.minutesBetween(startTime, endTime));
        }
        try {
            if (CollectionUtils.isNotEmpty(unEndedInterviewIds)){
                interviewInfoMapper.forceEndInterviewByInterviewIdList(unEndedInterviewIds);
            }
        } catch (Exception e) {
            log.warn("强制结束面试失败,interviewIdList:{}", unEndedInterviewIds, e);
        }

        return PageResponse.build(responses, total, pageNum, pageSize);
    }

    @Override
    public UpdateInterviewResponse updateInterview(String interviewId, UpdateInterviewRequest request) {
        try {
            Long uid = getCurrentUserId();
            // 验证面试所有权
            Optional<InterviewInfo> optionalInterviewInfo = interviewInfoMapper.selectByPrimaryKey(interviewId);
            if (optionalInterviewInfo.isEmpty()) {
                throw new BusinessException(ErrorCode.INTERVIEW_NOT_FOUND);
            }
            InterviewInfo interviewInfo = optionalInterviewInfo.get();
            if (!uid.equals(interviewInfo.getUid())) {
                throw new BusinessException(ErrorCode.INTERVIEW_NOT_OWNED);
            }
            if (InterviewStatus.isEnd(interviewInfo.getStatus())){
                throw new BusinessException(ErrorCode.INTERVIEW_ALREADY_ENDED);
            }
            InterviewInfo updateInterviewInfo = new InterviewInfo();
            updateInterviewInfo.setInterviewId(interviewId);
            updateInterviewInfo.setVersion(interviewInfo.getVersion());
            CommonConverter.INSTANCE.update2InterviewInfo(updateInterviewInfo,request);
            // 构建面试扩展字段
            InterviewExtraData interviewExtraData = StringUtils.isNotBlank(interviewInfo.getExtraData())
                    ? JSON.parseObject(interviewInfo.getExtraData(), InterviewExtraData.class) : new InterviewExtraData();
            CommonConverter.INSTANCE.update2InterviewExtraData(interviewExtraData, request);
            updateInterviewInfo.setExtraData(JSON.toJSONString(interviewExtraData));
            if (interviewInfoMapper.updateByPrimaryKeySelectiveCAS(updateInterviewInfo) < 0) {
                throw new BusinessException(ErrorCode.DATABASE_WRITE_ERROR);
            }
            return UpdateInterviewResponse.builder().result(true).build();
        } catch (Exception e) {
            log.error("更新面试信息失败,", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordSttUsage(String interviewId, RecordSttUsageRequest request) {
        // 验证面试所有权
        Long uid = getCurrentUserId();
        Optional<InterviewInfo> optionalInterviewInfo = interviewInfoMapper.selectByPrimaryKey(interviewId);
        if (optionalInterviewInfo.isEmpty()) {
            throw new BusinessException(ErrorCode.INTERVIEW_NOT_FOUND);
        }
        InterviewInfo interviewInfo = optionalInterviewInfo.get();
        if (!uid.equals(interviewInfo.getUid())) {
            throw new BusinessException(ErrorCode.INTERVIEW_NOT_OWNED);
        }
        if (InterviewStatus.isEnd(interviewInfo.getStatus())){
            throw new BusinessException(ErrorCode.INTERVIEW_ALREADY_ENDED);
        }

        // 获取分布式锁
        String lockKey = String.format("lock:stt:record-usage:%s", interviewId);
        String threadId = String.valueOf(Thread.currentThread().getId());

        try {
            // 尝试获取锁，设置过期时间为1秒
            boolean locked = redisUtils.lock(lockKey, threadId, NumberUtils.LONG_ONE);
            if (!locked) {
                throw new BusinessException(ErrorCode.CONCURRENT_LOCK_CONFLICT);
            }
            // 更新面试信息
            int updateCount = interviewInfoMapper.updateSttUsageByInterviewId(interviewId, request.getDurationSeconds(), request.getCostInCents());
            if (updateCount <= 0) {
                throw new BusinessException(ErrorCode.DATABASE_WRITE_ERROR, "更新面试信息失败");
            }

            // 记录使用记录
            UsageRecord usageRecord = UsageRecord.builder()
                    .interviewId(interviewId)
                    .uid(uid)
                    .usageType(UsageTypeEnum.STT.getCode())
                    .durationSeconds(request.getDurationSeconds())
                    .costInCents(request.getCostInCents())
                    .build();
            if (usageRecordMapper.insertSelective(usageRecord) <= 0) {
                throw new BusinessException(ErrorCode.DATABASE_WRITE_ERROR, "记录使用记录失败");
            }
        } finally {
            redisUtils.unlock(lockKey, threadId);
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        String phone = authentication.getName();
        User user = userService.queryNormalUserByPhone(phone);
        return user.getUid();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        String phone = authentication.getName();
        return userService.queryNormalUserByPhone(phone);
    }
} 