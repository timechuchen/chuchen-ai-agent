package com.chuchen.chuchenaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chuchen
 * @date 2025/5/15 14:57
 * @description 向量数据库配置（初始化基于内存向量数据库的的 Bean）
 */
@Configuration
public class LoveAppVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Bean
    VectorStore loveAppVectorStore(@Qualifier("dashscopeEmbeddingModel") EmbeddingModel dashScopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashScopeEmbeddingModel).build();
        simpleVectorStore.add(loveAppDocumentLoader.loadMarkers());
        return simpleVectorStore;
    }
}
