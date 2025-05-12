package com.keji.green.lit.engine.integration;

import java.util.Map;
import java.util.function.Consumer;

public interface LlmChatService {
    /**
     * 调用大模型算法服务，流式返回内容
     * @param param 请求参数
     * @param onChunk 每次收到流式内容的回调
     */
    void streamChat(Map<String, Object> param, Consumer<String> onChunk);
} 