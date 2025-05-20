package com.keji.green.lit.engine.integration;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author xiangjun_lee
 */
public interface LlmWrapService {
    /**
     * 调用大模型算法服务，流式返回内容
     * @param param 请求参数
     * @param onChunk 每次收到流式内容的回调
     */
    void streamChat(Map<String, Object> param, Consumer<String> onChunk);

    /**
     * 优化简历内容
     * @param resumeText 原始简历文本
     * @return 优化后的简历文本
     */
    Pair<Boolean, String> optimizeResume(String resumeText);
} 