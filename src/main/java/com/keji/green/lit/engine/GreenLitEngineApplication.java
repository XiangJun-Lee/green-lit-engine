package com.keji.green.lit.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * 应用程序入口类
 * 使用@ComponentScan注解明确指定包扫描范围，确保@Resource注解的依赖能正确加载
 */
@SpringBootApplication
@ImportResource("classpath:spring/green-lit-application.xml")
public class GreenLitEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(GreenLitEngineApplication.class, args);
    }

}
