package com.keji.green.lit.engine.integration.impl;

import com.alibaba.fastjson.JSON;
import com.keji.green.lit.engine.integration.LlmChatService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author xiangjun_lee
 */
@Slf4j
@Service
public class LlmChatServiceImpl implements LlmChatService {
    private static final String LLM_URL = "http://47.122.94.215:8100/llm/chat";
    private static final MediaType MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");
    private static final long READ_TIMEOUT = 600;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .build();

    @Override
    public void streamChat(Map<String, Object> param, Consumer<String> onChunk) {
        try {
            RequestBody body = RequestBody.create(JSON.toJSONString(param), MEDIA_TYPE);
            log.info("调用算法服务请求参数: {}", JSON.toJSONString(param));

            Request request = new Request.Builder()
                    .url(LLM_URL)
                    .post(body)
                    .header("Connection", "keep-alive")
                    .header("Keep-Alive", "timeout=600")
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    log.error("调用算法服务失败，状态码: {}, 错误信息: {}", response.code(), errorBody);
                    throw new IOException("调用失败，状态码: " + response.code() + ", 错误信息: " + errorBody);
                }

                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    throw new IOException("Empty response body");
                }

                // 处理响应流
                BufferedSource source = responseBody.source();
                while (!source.exhausted()) {
                    String line = source.readUtf8LineStrict();
                    if (!line.trim().isEmpty()) {
                        log.info("算法服务返回内容: {}", line);
                        onChunk.accept(line);
                    }
                }
            }
        } catch (Exception e) {
            log.error("调用算法服务失败", e);
            onChunk.accept("[ERROR] " + e.getMessage());
        }
    }
}
