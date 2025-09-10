package com.x;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement  // 启动事务

public class SocialBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialBackendApplication.class, args);
    }

}
