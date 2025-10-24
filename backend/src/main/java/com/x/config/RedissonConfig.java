package com.x.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient(){
        // 配置
        Config config = new Config();
        // 若是集群使用useClusterServers()
        config.useSingleServer().setAddress("redis://localhost:6379");
        // 创建RedissonClient对象
        return Redisson.create(config);
    }
}