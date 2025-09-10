package com.x;

import com.x.ai.api.MongoChatMemoryStore;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
@TestPropertySource(properties = {
        "DASH_SCOPE_API_KEY=test_api_key"
})

@SpringBootTest
class SocialBackendApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private EmbeddingStore embeddingStore;
    @Autowired
    private EmbeddingModel embeddingModel;
    @Test
    public void testUploadKnowledgeLibrary() {

//        //使用FileSystemDocumentLoader读取指定目录下的知识库文档
//        //并使用默认的文档解析器对文档进行解析
//        Document document1 = FileSystemDocumentLoader.loadDocument("/Users/x/projects/myBlog/x-blog-backend/src/main/resources/测试文档.md");
//        List<Document> documents = Arrays.asList(document1);
//
//        //文本向量化并存入向量数据库：将每个片段进行向量化，得到一个嵌入向量
//        EmbeddingStoreIngestor
//                .builder()
//                .embeddingStore(embeddingStore)
//                .embeddingModel(embeddingModel)
//                .build()
//                .ingest(documents);
        System.out.println("向量数据库初始化完成");
    }
    @Autowired
    private MongoChatMemoryStore mongoChatMemoryStore;


    @Test
    public void testSearchKnowledgeLibrary() {
      List<ChatMessage> messages = mongoChatMemoryStore.getMessages(1L);
      System.out.println(messages);
    }
}
