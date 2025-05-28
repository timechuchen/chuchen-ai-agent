package com.chuchen.chuchenaiagent.agent;

import cn.hutool.core.util.StrUtil;
import com.chuchen.chuchenaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chuchen
 * @date 2025/5/28 14:56
 * @description 抽象代理类，主要是管理了基础属性执行流程等
 */
@Data
@Slf4j
public abstract class BaseAgent {

    // 基础属性
    private String name;

    // 提示词
    private String systemPrompt;
    private String nextStepPrompt;

    // 代理状态
    private AgentState state = AgentState.IDLE;

    // 执行步骤控制
    private int currentStep = 0;
    private int maxSteps = 10;

    // LLM 大模型
    private ChatClient chatClient;

    // Memory 记忆（需要自主维护上下文）
    private List<Message> messageList = new ArrayList<>();

    /**
     * 执行代理
     *
     * @param userPrompt 用户提示词
     * @return 调用结果
     */
    public String run(String userPrompt) {
        if(this.state != AgentState.IDLE) {
            throw new RuntimeException("Cannot run agent in state: " + this.state);
        }
        if(StrUtil.isBlank(userPrompt)) {
            throw new RuntimeException("Cannot run agent with empty user prompt.");
        }

        // 执行
        this.state = AgentState.RUNNING;
        // 记录消息上下文
        messageList.add(new UserMessage(userPrompt));
        // 定义结果列表
        List<String> results = new ArrayList<>();
        // 执行循环
        try{
            for(int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                int stemNumber = i + 1;
                currentStep = stemNumber;
                log.info("Executing step {} / {}", stemNumber, maxSteps);
                // 单步执行，得到结果
                String stepResult = step();
                String result = "Step " + stemNumber + ": " + stepResult;
                results.add(result);
            }

            // 是否超出最大步骤限制
            if(currentStep == maxSteps) {
                state = AgentState.FINISHED;
                results.add("Terminated: Reached max steps (" + maxSteps + ")");
            }
            return String.join("\n", results);
        }catch (Exception e) {
            log.error("Error executing agent", e);
            state = AgentState.ERROR;
            results.add("Error executing agent: " + e.getMessage());
            return "执行错误" + e.getMessage();
        }finally {
            cleanup();
        }
    }

    /**
     * 定义单个步骤
     * @return 执行结果
     */
    public abstract String step();

    /**
     * 清理资源
     */
    protected void cleanup(){
        // 可以重写子类来实现此方法
    }
}
