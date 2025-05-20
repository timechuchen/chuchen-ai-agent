package com.chuchen.chuchenaiagent.app;

import com.chuchen.chuchenaiagent.advisor.MyLoggerAdvisor;
import com.chuchen.chuchenaiagent.chatmemory.FileBasedChatMemory;
import com.chuchen.chuchenaiagent.rag.LoveAppRagCustomAdvisorFactory;
import com.chuchen.chuchenaiagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * @author chuchen
 * @date 2025/5/2 13:54
 * @description App 后端，这里以 love（恋爱app） 为例
 */
@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;

    @Resource
    private VectorStore loveAppVectorStore;
    @Resource
    private Advisor loveAppRagCloudAdvisor;
    @Resource
    private VectorStore pgVectorVectorStore;
    @Resource
    private QueryRewriter queryRewriter;
    @Resource
    private ToolCallback[] allTools;

    // 系统预设
    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    /**
     * 初始化 ChatClient 客户端
     * @param dashscopeChatModel 默认使用阿里的模型
     */
    public LoveApp(ChatModel dashscopeChatModel) {
//        // 基于内存的对话记忆
//        ChatMemory chatMemory = new InMemoryChatMemory();
        // 初始化基于文件的对话记忆
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);

        this.chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                     new MessageChatMemoryAdvisor(chatMemory),
                     // 自定义日志拦截器，可以按需开启
                     new MyLoggerAdvisor()
                     // 自定义 Re2 Advisor，可提高大型语言模型的推理能力
                     // new ReReadingAdvisor()
                )
                .build();
    }

    /**
     * AI 基础对话，支持多轮对话记忆
     * @param message 用户发送的消息
     * @param chatId 用户会话 ID
     * @return 返回 AI 的回复
     */
    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();

        String content = null;
        if (response != null) {
            content = response.getResult().getOutput().getText();
        }
        log.info("content: {}", content);
        return content;
    }

    record LoveReport(String title, List<String> suggestions) {
    }

    /**
     * AI 恋爱报告功能（实战结构化输出）
     * @param message 用户发送的消息
     * @param chatId 用户会话 ID
     * @return 返回 AI 的回复
     */
    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话之后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容建议为列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(LoveReport.class);

        log.info("content: {}", loveReport);
        return loveReport;
    }

    /**
     * 和 RAG 知识库进行对话
     * @param message 用户发送的消息
     * @param chatId 会话 ID
     * @return AI 的回复
     */
    public String doChatWithRag(String message, String chatId) {
        // 对用户输入的消息进行重写
        String rewriterMessage = queryRewriter.doQueryRewrite(message);

        ChatResponse response = chatClient
                .prompt()
                .user(rewriterMessage)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new MyLoggerAdvisor()) // 开启日志
//                .advisors(new QuestionAnswerAdvisor(loveAppVectorStore)) // 使用 RAG 知识库问答
//                .advisors(loveAppRagCloudAdvisor) // 应用 RAG 检索增强服务（基于云知识库）
//                .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore)) // 基于 pgVector 向量数据库检索
                .advisors(
                        LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(loveAppVectorStore, "已婚") // 自定义 RAG 检索增强服务（基于本地知识库）
                )
                .call()
                .chatResponse();

        String content = null;
        if (response != null) {
            content = response.getResult().getOutput().getText();
        }
        log.info("content: {}", content);
        return content;
    }

    /**
     * AI 恋爱报告功能（支持调用工具）
     * @param message 用户发送的消息
     * @param chatId 用户会话 ID
     * @return 返回 AI 的回复
     */
    public String doChatWithTools(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new MyLoggerAdvisor()) // 开启日志，便于观察效果
                .tools(allTools)
                .call()
                .chatResponse();

        String content = null;
        if (response != null) {
            content = response.getResult().getOutput().getText();
        }
        log.info("content: {}", content);
        return content;
    }
}
