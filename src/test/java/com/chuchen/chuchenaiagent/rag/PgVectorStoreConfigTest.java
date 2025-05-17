package com.chuchen.chuchenaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author chuchen
 * @date 2025/5/17 13:09
 */
@SpringBootTest
class PgVectorStoreConfigTest {

    @Resource
    private VectorStore pgVectorVectorStore;

    @Test
    void pgVectorVectorStore() {
        List<Document> documents = List.of(
                new Document("你是谁？我叫初晨，是一个程序员，拥有很强的开发能力，还会 PS 等自媒体知识", Map.of("meta1", "meta1")),
                new Document("这是初晨的个人项目，是一个 AI 项目"),
                new Document("初晨有很强的学习能力，可以快速的学习很多知识", Map.of("meta2", "meta2")));
        // 添加文档
        pgVectorVectorStore.add(documents);
        // 相似度查询
        List<Document> results = pgVectorVectorStore.similaritySearch(SearchRequest.builder().query("初晨是谁？能力咋样").topK(2).build());
        Assertions.assertNotNull(results);
    }
}