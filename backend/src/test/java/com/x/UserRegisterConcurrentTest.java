package com.x;

import com.x.pojo.dto.UserRegisterDTO;
import com.x.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
public class UserRegisterConcurrentTest {
    @Autowired
    private UserService userService;

    @Test
    public void testConcurrentRegister() throws InterruptedException {
        int threads = 100; // 并发线程数
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        int totalUsers = 100;
        AtomicInteger failedUsers = new AtomicInteger();
        for (int i = 0; i < threads; i++) {
            int finalI = i;
            executor.submit(() -> {
                try {
                    UserRegisterDTO dto = new UserRegisterDTO();
                    // 使用随机邮箱，避免数据库唯一约束冲突干扰测试账号字段
                    dto.setEmail(UUID.randomUUID().toString() + "@test.com");
                    dto.setPassword("123456");
                    dto.setUsername("test" + finalI);
                    userService.register(dto);
                } catch (Exception e) {
                    failedUsers.getAndIncrement();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 等待所有线程完成
        latch.await();
        executor.shutdown();
        System.out.println("失败的注册用户数：" + failedUsers.get());
        System.out.println("成功率:"+ (totalUsers - failedUsers.get()) * 1.0 / totalUsers);
        System.out.println("并发注册测试完成！");
    }
}
