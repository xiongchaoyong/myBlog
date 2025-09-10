package com.x.ai.api;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(
        wiringMode = EXPLICIT,
        chatModel = "qwenChatModel" // 配置文件里应该有这个模型实例
)
public interface ArticleAssistant {

    @SystemMessage("""
                你是一个内容审核助手，负责判断文章是否符合以下规则：
                1. 不得包含暴力、色情、极端政治、违法犯罪、诈骗、歧视等不良内容。
                2. 不得泄露个人隐私信息（如身份证号、电话、住址等）。
                3. 不得包含虚假信息、恶意谣言。
                4.文章字数不能少于50字

                请你根据输入的文章内容，严格输出一个 JSON，格式如下：
                {
                  "approved": true/false,
                  "reason": "如果不合法，请简要说明原因；如果合法，写 OK"
                }

                ⚠️ 注意：只能输出 JSON，不要输出任何解释或附加文字。
                ⚠️ 只能输出 JSON，不要输出任何解释或附加格式（不要加 ```json）。
                      
            """)
    String chat(@UserMessage String articleContent);
}
