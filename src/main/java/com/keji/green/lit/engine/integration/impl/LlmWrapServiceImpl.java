package com.keji.green.lit.engine.integration.impl;

import com.alibaba.fastjson.JSON;
import com.keji.green.lit.engine.integration.LlmWrapService;
import com.keji.green.lit.engine.utils.LlmConfig;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author xiangjun_lee
 */
@Slf4j
@Service
public class LlmWrapServiceImpl implements LlmWrapService {

    private static final MediaType MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

    private static final long READ_TIMEOUT = 600;


    @Value("${llm.base-url}")
    private String llmBaseUrl;

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
                    .url(llmBaseUrl + LlmConfig.TEXT_ANSWER_PREFIX)
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

    @Override
    public Pair<Boolean, String> optimizeResume(String resumeText) {

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("resume_info", resumeText);
        requestBody.put("model_name", LlmConfig.DEEP_THINK_MODEL_1);

        try {
            RequestBody body = RequestBody.create(JSON.toJSONString(requestBody), MEDIA_TYPE);
            log.info("调用简历优化服务请求参数: {}", JSON.toJSONString(requestBody));

            Request request = new Request.Builder()
                    .url(llmBaseUrl + LlmConfig.RESUME_OPTIMIZATION_PATH)
                    .post(body)
                    .header("Connection", "keep-alive")
                    .header("Keep-Alive", "timeout=60")
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful() || Objects.isNull(response.body())) {
                    log.error("调用简历优化服务失败，状态码: {}, response: {}", response.code(), response);
                    return Pair.of(Boolean.FALSE, resumeText);
                }
                ResponseBody responseBody = response.body();

                String responseString = responseBody.string();
                log.info("简历优化服务返回内容: {}", responseString);

                Map<String, Object> responseMap = JSON.parseObject(responseString, Map.class);
                if (responseMap != null && responseMap.containsKey("content")) {
                    Map<String, Object> contentMap = (Map<String, Object>) responseMap.get("content");
                    if (contentMap != null && contentMap.containsKey("answer_content")) {
                        String answerContent = (String) contentMap.get("answer_content");
                        return Pair.of(Boolean.TRUE, answerContent);
                    }
                }
            }
        } catch (Exception e) {
            log.error("调用简历优化服务失败", e);
        }
        return Pair.of(Boolean.FALSE, resumeText);
    }
}
