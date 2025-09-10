package com.x.ai.api;


import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(
        wiringMode = EXPLICIT,
        chatModel = "qwenChatModel"
)
public interface TitleAssistant {
    @SystemMessage("你是一个生成标题的ai助手，根据用户提出的问题，生成一个简洁的会话标题，标题需不超过 8 个字。标题应该精确反映问题的核心内容，去除不必要的词汇并保持简洁。请确保标题能清晰表达用户关心的问题。例如：\n" +
            "用户问题：'如何在 Vue 中实现状态管理？' → 生成标题：Vue 状态管理" +
            "用户问题：'Java 如何操作文件系统？' → 生成标题：Java 文件操作" +
            "用户问题：'Spring Boot 如何连接 MySQL 数据库？' → 生成标题：'Spring MySQL 连接'" +
            "目标是生成一个简短、有意义且不超过 8 个字的标题。直接返回标题内容即可，不用返回其他的内容。" +
            "一定要记得你要返回的是标题而不是回答用户的问题"
            )
    String chat(@UserMessage String userMessage);
}
