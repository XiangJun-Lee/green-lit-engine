package com.keji.green.lit.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * 应用程序启动类
 *
 * @author xiangjun_lee
 * @since 2024-04-11
 */
@SpringBootApplication
@ImportResource("classpath:spring/green-lit-application.xml")
public class GreenLitEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(GreenLitEngineApplication.class, args);
    }

}
