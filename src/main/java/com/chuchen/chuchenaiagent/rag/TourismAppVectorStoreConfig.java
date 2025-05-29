package com.chuchen.chuchenaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author chuchen
 * @date 2025/5/15 14:57
 * @description 向量数据库配置（初始化基于内存向量数据库的的 Bean）
 */
@Configuration
public class TourismAppVectorStoreConfig {

    @Resource
    private TourismAppDocumentLoader tourismAppDocumentLoader;

    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;

    @Resource
    private MyKeyWorldEnricher myKeyWorldEnricher;

    @Bean
    VectorStore tourismAppVectorStore(@Qualifier("dashscopeEmbeddingModel") EmbeddingModel dashScopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashScopeEmbeddingModel).build();
        // 加载文档
        List<Document> documents = tourismAppDocumentLoader.loadMarkers();
        // 自主切分
//        List<Document> splitDocument = myTokenTextSplitter.splitCustomized(documents);

        // 自动补充关键词元信息
        List<Document> enrichDocuments = myKeyWorldEnricher.enrichDocuments(documents);
        simpleVectorStore.add(enrichDocuments);
        return simpleVectorStore;
    }
}
