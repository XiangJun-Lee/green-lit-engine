package com.keji.green.lit.engine;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
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
@MapperScans({
        @MapperScan("com.keji.green.lit.engine.model"),
        @MapperScan("com.keji.green.lit.engine.mapper")
})
public class GreenLitEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(GreenLitEngineApplication.class, args);
    }

}
