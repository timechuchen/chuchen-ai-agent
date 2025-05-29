package com.chuchen.chuchenaiagent.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

/**
 * @author chuchen
 * @date 2025/5/17 16:36
 * @description 创建上下文查询器工厂
 */
public class TourismAppContextualQueryAugmenterFactory {

    public static ContextualQueryAugmenter createInstance() {
        PromptTemplate promptTemplate = new PromptTemplate("""
                 你应该输出一下内容：
                 抱歉，我只能回答旅游相关的问题，别的没办法帮助您哦，
                 或者您可以访问客服 https://www.baidu.com/
                """);

        return ContextualQueryAugmenter.builder()
                .allowEmptyContext(false)
                .emptyContextPromptTemplate(promptTemplate)
                .build();
    }
}
