package com.keji.green.lit.engine.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.keji.green.lit.engine.common.CommonConverter;
import com.keji.green.lit.engine.dto.request.FastAnswerParam;
import com.keji.green.lit.engine.dto.bean.InterviewExtraData;
import com.keji.green.lit.engine.dto.response.QuestionAnswerRecordListQueryParam;
import com.keji.green.lit.engine.dto.request.AskQuestionRequest;
import com.keji.green.lit.engine.dto.request.CreateInterviewRequest;
import com.keji.green.lit.engine.dto.request.RecordSttUsageRequest;
import com.keji.green.lit.engine.dto.request.ScreenshotQuestionRequest;
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
import com.keji.green.lit.engine.service.TransactionalService;
import com.keji.green.lit.engine.service.UserService;
import com.keji.green.lit.engine.utils.DateTimeUtils;
import com.keji.green.lit.engine.utils.RedisUtils;
import com.keji.green.lit.engine.integration.LlmChatService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
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

    @Resource
    private TransactionalService transactionalService;

    @Resource
    private LlmChatService llmChatService;

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
            userService.updateUserByUidCAS(user);
        }
        return new InterviewCreateResponse(interviewId);
    }

    /**
     * 提问并获取答案（流式）
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public SseEmitter askQuestion(String interviewId, AskQuestionRequest request) {
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
        // 检查面试状态，确保面试处于进行中状态
        if (InterviewStatus.isEnd(interviewInfo.getStatus())) {
            throw new BusinessException(ErrorCode.INTERVIEW_ALREADY_ENDED);
        }
        InterviewExtraData interviewExtraData = StringUtils.isNotBlank(interviewInfo.getExtraData())
                ? JSON.parseObject(interviewInfo.getExtraData(), InterviewExtraData.class) : new InterviewExtraData();
        UsageTypeEnum usageTypeEnum = UsageTypeEnum.FAST_ANSWER;
        if (Objects.nonNull(interviewExtraData) && Objects.equals(Boolean.TRUE, interviewExtraData.getOnlineMode())){
            usageTypeEnum = UsageTypeEnum.ONLINE_ANSWER;
        }

        // TODO 检查用户积分是否充足

        try {
            transactionalService.fastAnswerCharging(interviewInfo, usageTypeEnum);
        } catch (Exception e) {
            log.error("快速答题扣费失败", e);
            throw new BusinessException(ErrorCode.POINTS_DEDUCTION_FAILED);
        }
        try {
            QuestionAnswerRecord record = new QuestionAnswerRecord();
            record.setInterviewId(interviewId);
            record.setQuestion(request.getQuestion());
            if (questionAnswerRecordMapper.insertSelective(record) <= 0) {
                // todo 异步重试
            }
        } catch (Exception e) {
            log.error("保存面试流水失败", e);
        }

        String resumeText = currentUser.getResumeText();
        FastAnswerParam fastAnswerParam = new FastAnswerParam();
        if (StringUtils.isNotBlank(resumeText)) {
            fastAnswerParam.setResumeText(resumeText);
        }
        fastAnswerParam.setInterviewInfo(JSON.toJSONString(interviewInfo));
        if (StringUtils.isNotBlank(request.getHistoryChat())) {
            List<String> historyChat = JSON.parseArray(request.getHistoryChat(), String.class);
            fastAnswerParam.setHistoryChat(historyChat);
        }
        // 创建SSE发射器，超时设置为10分钟
        SseEmitter emitter = new SseEmitter(TEN_MINUTE_MILLISECONDS);
        // 异步调用算法服务
        CompletableFuture.runAsync(() -> {
            try {
                // 调用算法服务，获取答案（流式）
                Map<String, Object> param = new HashMap<>();
                param.put("messages", List.of(
                        new HashMap<String, Object>() {{
                            put("role", "user");
                            put("content", request.getQuestion());
                        }}
                ));
                param.put("model_name", "qwq-plus-latest");
                param.put("stream", true);
                if (BooleanUtils.isTrue(interviewExtraData.getOnlineMode())){
                    param.put("enable_search", true);
                }
                param.put("enable_reason", false);
                llmChatService.streamChat(param, chunk -> {
                    try {
                        emitter.send(SseEmitter.event().name("message").data(chunk, MediaType.TEXT_PLAIN));
                    } catch (Exception e) {
                        log.error("SSE发送失败", e);
                    }
                });
                // 发送完成事件
                emitter.send(SseEmitter.event().name("complete").data("完成", MediaType.TEXT_PLAIN));
                emitter.complete();
            } catch (Exception e) {
                log.error("处理面试问题失败: {}", e.getMessage(), e);
                try {
                    emitter.complete();
                } catch (Exception ex) {
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
                    .usageType(UsageTypeEnum.SPEECH_TO_TEXT.getCode())
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

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public SseEmitter screenshotQuestion(String interviewId, ScreenshotQuestionRequest request) {
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
        // 检查面试状态，确保面试处于进行中状态
        if (InterviewStatus.isEnd(interviewInfo.getStatus())) {
            throw new BusinessException(ErrorCode.INTERVIEW_ALREADY_ENDED);
        }

        InterviewExtraData interviewExtraData = StringUtils.isNotBlank(interviewInfo.getExtraData())
                ? JSON.parseObject(interviewInfo.getExtraData(), InterviewExtraData.class) : new InterviewExtraData();
        UsageTypeEnum usageTypeEnum = UsageTypeEnum.SCREENSHOT_ANSWER;
        if (Objects.nonNull(interviewExtraData) && Objects.equals(Boolean.TRUE, interviewExtraData.getOnlineMode())){
            usageTypeEnum = UsageTypeEnum.ONLINE_SCREENSHOT_ANSWER;
        }

        try {
            transactionalService.fastAnswerCharging(interviewInfo, usageTypeEnum);
        } catch (Exception e) {
            log.error("快速答题扣费失败", e);
            throw new BusinessException(ErrorCode.POINTS_DEDUCTION_FAILED);
        }

        // 创建初始记录
        QuestionAnswerRecord record = new QuestionAnswerRecord();
        record.setInterviewId(interviewId);
        record.setQuestion("[图片]"); // 初始标记为图片类型的问题
        try {
            if (questionAnswerRecordMapper.insertSelective(record) <= 0) {
                // todo 异步重试
            }
        } catch (Exception e) {
            log.error("保存面试流水失败", e);
        }

        // 创建SSE发射器
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
        
        // 用于存储OCR识别结果
        StringBuilder ocrResult = new StringBuilder();
        
        // 第一个异步线程：处理OCR识别
        CompletableFuture<Void> ocrFuture = CompletableFuture.runAsync(() -> {
            try {
                // TODO: 调用算法服务进行图片识别
                // 这里需要实现与算法服务的交互，将图片base64编码发送给算法服务
                // 并接收算法服务返回的流式OCR结果
                
                // 模拟OCR流式返回
                String[] ocrResults = {"正在识别图片...", "识别完成", "图片内容: 这是一段示例文本"};
                for (String result : ocrResults) {
                    emitter.send(SseEmitter.event().name("ocr").data(result, MediaType.TEXT_PLAIN));
                    ocrResult.append(result).append("\n");
                    Thread.sleep(1000); // 模拟延迟
                }

                // OCR识别完成后，更新问题记录
                QuestionAnswerRecord updateRecord = new QuestionAnswerRecord();
                updateRecord.setId(record.getId()); // 使用之前创建的记录ID
                updateRecord.setQuestion(ocrResult.toString().trim()); // 使用OCR识别结果作为问题
                if (questionAnswerRecordMapper.updateByPrimaryKeySelective(updateRecord) <= 0) {
                    log.error("更新OCR识别结果失败, recordId: {}", record.getId());
                }
            } catch (Exception e) {
                log.error("图片识别处理失败", e);
                try {
                    emitter.send(SseEmitter.event().name("ocr").data("图片识别失败: " + e.getMessage(), MediaType.TEXT_PLAIN));
                } catch (IOException ex) {
                    log.error("发送OCR错误信息失败", ex);
                }
            }
        });
        
        // 第二个异步线程：处理答案生成
        CompletableFuture<Void> answerFuture = CompletableFuture.runAsync(() -> {
            try {
                // 等待OCR识别完成
                ocrFuture.join();
                
                // TODO: 调用算法服务生成答案
                // 这里需要实现与算法服务的交互，将OCR识别结果发送给算法服务
                // 并接收算法服务返回的流式答案
                
                // 模拟答案流式返回
                String[] answerResults = {"正在生成答案...", "根据图片内容分析...", "答案: 这是一个示例答案"};
                for (String result : answerResults) {
                    emitter.send(SseEmitter.event().name("answer").data(result, MediaType.TEXT_PLAIN));
                    Thread.sleep(1000); // 模拟延迟
                }
                emitter.complete();
            } catch (Exception e) {
                log.error("答案生成处理失败", e);
                try {
                    emitter.send(SseEmitter.event().name("answer").data("答案生成失败: " + e.getMessage(), MediaType.TEXT_PLAIN));
                    emitter.complete();
                } catch (IOException ex) {
                    log.error("发送答案错误信息失败", ex);
                }
            }
        });

        return emitter;
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