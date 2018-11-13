package com.zyc.dubbo;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication
@MapperScan("com.zyc.dubbo.mapper")
public class SdServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SdServiceApplication.class, args);
    }
}
