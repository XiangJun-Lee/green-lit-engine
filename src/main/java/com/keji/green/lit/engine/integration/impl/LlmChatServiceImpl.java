package com.keji.green.lit.engine.integration.impl;

import com.alibaba.fastjson.JSON;
import com.keji.green.lit.engine.integration.LlmChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author xiangjun_lee
 */
@Slf4j
@Service
public class LlmChatServiceImpl implements LlmChatService {
    private static final String LLM_URL = "http://47.122.94.215:8100/llm/chat";

    @Override
    public void streamChat(Map<String, Object> param, Consumer<String> onChunk) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(LLM_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(60000);

            String json = JSON.toJSONString(param);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    onChunk.accept(line);
                }
            }
        } catch (Exception e) {
            log.error("调用算法服务失败", e);
            onChunk.accept("[ERROR] " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
} 