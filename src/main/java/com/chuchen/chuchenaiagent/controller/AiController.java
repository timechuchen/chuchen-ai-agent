package com.chuchen.chuchenaiagent.controller;

import com.chuchen.chuchenaiagent.agent.ChuchenManus;
import com.chuchen.chuchenaiagent.app.TourismApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

/**
 * @author chuchen
 * @date 2025/5/28 17:43
 * @description 这里为了方便测试，都使用的 GET 请求，真实情况这些接口基本上都是 POST 请求，以及返回结果还要进行一次封装
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private TourismApp tourismApp;
    @Resource
    private ToolCallback[] allTools;
    @Resource
    private ChatModel dashscopeChatModel;
    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    /**
     * 同步调用 AI 大师应用
     * @param message 用户消息
     * @param chatId 会话 ID
     * @return 返回的的 AI 结果
     */
    @GetMapping("/tourism_app/chat/sync")
    public String doChatWithTourismAppSync(String message, String chatId) {
        return tourismApp.doChat(message, chatId);
    }

    /**
     * SSE 流式调用 AI 应用 方法一
     * @param message 用户消息
     * @param chatId 会话 ID
     * @return 返回的的 AI 结果
     */
    @GetMapping(value = "/tourism_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithTourismAppSSE(String message, String chatId) {
        return tourismApp.doChatByStream(message, chatId);
    }

    /**
     * SSE 流式调用 AI 应用 方法二
     * @param message 用户消息
     * @param chatId 会话 ID
     * @return 返回的的 AI 结果
     */
    @GetMapping(value = "/tourism_app/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithTourismAppSseServerSentEvent(String message, String chatId) {
        return tourismApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    /**
     * SSE 流式调用 AI 应用 方法三
     * @param message 用户消息
     * @param chatId 会话 ID
     * @return 返回的的 AI 结果
     */
    @GetMapping(value = "/tourism_app/chat/sse_emitter")
    public SseEmitter doChatWithTourismAppServerSseEmitter(String message, String chatId) {
        // 创建一个时间较长的 SSE 发射器
        SseEmitter emitter = new SseEmitter(360000L);
        // 获取 Flux 响应式数据流，并且直接通过订阅推送给 SSE 发射器
        tourismApp.doChatByStream(message, chatId)
                .subscribe(chunk -> {
                    try {
                        emitter.send(chunk);
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                }, emitter::completeWithError, emitter::complete);
        return emitter;
    }

    /**
     * 流式调用 manus 智能体
     * @param message 用户消息
     * @return 返回的的 AI 结果
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        ChuchenManus chuchenManus = new ChuchenManus(allTools, toolCallbackProvider, dashscopeChatModel);
        return chuchenManus.runStream(message);
    }
}
