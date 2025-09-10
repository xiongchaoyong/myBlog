package com.x.ai.api;


import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(
        wiringMode = EXPLICIT,
        streamingChatModel = "qwenStreamingChatModel",
        chatMemory = "chatMemory",
        chatMemoryProvider = "chatMemoryProvider",
        tools = {"calculatorTools", "articleTools"},
        contentRetriever = "contentRetriever"
)
public interface Assistant {
    @SystemMessage(fromResource = "ai/systemMessagePrompt.txt")
    @UserMessage("回答问题前请夸我一句，一句就可以。{{message}}")
    Flux<String> chat(@MemoryId Long memoryId,  @V("message") String userMessage);
}
