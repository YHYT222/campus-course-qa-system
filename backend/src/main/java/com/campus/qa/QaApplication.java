package com.campus.qa;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校园课程互助答疑系统 —— 后端启动类
 */
@SpringBootApplication
@MapperScan("com.campus.qa.mapper")
public class QaApplication {

    public static void main(String[] args) {
        SpringApplication.run(QaApplication.class, args);
    }
}
