package com.chuchen.chuchenaiagent.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;

/**
 * @author chuchen
 * @date 2025/4/25 18:39
 */
public class LangChainAiInvoke {
    public static void main(String[] args) {
        QwenChatModel qwenChatModel = QwenChatModel.builder()
                .apiKey(TestApiKey.API_KEY)
                .modelName("qwen-plus")
                .build();

        String message = qwenChatModel.chat("我叫初晨");
        System.out.println(message);
    }
}
