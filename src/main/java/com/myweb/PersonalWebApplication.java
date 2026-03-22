package com.myweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 个人网站后端启动类
 */
@SpringBootApplication
@MapperScan("com.myweb.mapper")
public class PersonalWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalWebApplication.class, args);
    }
}
