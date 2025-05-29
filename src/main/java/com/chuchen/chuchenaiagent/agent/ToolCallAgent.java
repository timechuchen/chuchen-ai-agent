package com.chuchen.chuchenaiagent.agent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.chuchen.chuchenaiagent.agent.model.AgentState;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chuchen
 * @date 2025/5/28 14:57
 * @description 处理工具调用的基础代理类，具体实现了 think 和 act 方法，可以创建实例的父类
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent{

    // 可用的工具
    private ToolCallback[] availableTools;
    // 保存工具调用信息的响应结果（要调用哪些工具）
    private ChatResponse toolCallChatResponse;

//    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    // 工具调用管理者
    private final ToolCallingManager toolCallingManager;

    // 禁用 SpringAI 内部的工具调用机制，自己维护选项和消息上下文
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] availableTools, ToolCallbackProvider toolCallbackProvider) {
        super();
        this.availableTools = availableTools;
        this.toolCallbackProvider = toolCallbackProvider;
        this.toolCallingManager = ToolCallingManager.builder().build();
        this.chatOptions = DashScopeChatOptions.builder()
                .withProxyToolCalls(true)
                .build();
    }

    /**
     * 处理当前状态并且执行下一步行动
     * @return 是否需要行动
     */
    @Override
    public boolean think() {
        // 校验提示词，拼接用户提示词
        if(StrUtil.isNotBlank(getNextStepPrompt())) {
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
        }
        // 调用 AI 大模型，获取工具调用列表
        List<Message> messageList = getMessageList();
        Prompt prompt = new Prompt(messageList, this.chatOptions);
        try {
            ChatResponse chatResponse = getChatClient()
                    .prompt(prompt)
                    .system(getSystemPrompt())
                    .tools(availableTools)
                    .tools(toolCallbackProvider) // 加入 MCP 工具
                    .call()
                    .chatResponse();
            // 记录响应，用于等下 Act
            this.toolCallChatResponse = chatResponse;
            // 解析工具调用结果，获取要调用的工具
            // 助手消息
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            // 获取要调用的工具列表
            List<AssistantMessage.ToolCall> toolCalls = assistantMessage.getToolCalls();
            // 输出提示信息
            String result = assistantMessage.getText();
            log.info("{} 的思考结果: {}",getName(), result);
            log.info("{} 选择了 {} 个工具", getName(), toolCalls.size());
            String collect = toolCalls.stream()
                    .map(toolCall -> String.format("工具名称: %s, 参数: %s", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info(collect);
            // 如果不需要调用工具，返回 false
            if(toolCalls.isEmpty()) {
                // 只有调用工具的时候才需要记录助手消息
                getMessageList().add(assistantMessage);
                return false;
            } else {
                return true;
            }
        }catch (Exception e) {
            log.error("{} 的思考过程出现问题: {}", getName(), e.getMessage());
            getMessageList().add(new AssistantMessage("处理消息时候出现错误: " + e.getMessage()));
            return false;
        }
    }

    // 执行工具调用并处理结果
    @Override
    public String act() {
        if(!toolCallChatResponse.hasToolCalls()) {
            return "不需要调用工具";
        }
        // 调用工具
        Prompt prompt = new Prompt(getMessageList(), this.chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
        // 记录消息上下文
        setMessageList(toolExecutionResult.conversationHistory());
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
        // 是否调用了终止工具
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> response.name().equals("doTerminate"));
        if(terminateToolCalled) {
            // 任务结束
            setState(AgentState.FINISHED);
        }
        String results = toolResponseMessage.getResponses().stream()
                .map(response -> response.responseData())
                .collect(Collectors.joining("\n"));
        log.info(results);
        return results;
    }
}
