package com.chuchen.chuchenaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author chuchen
 * @date 2025/5/2 14:10
 * <br>
 */
@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void doChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是初晨";
        String answer = loveApp.doChat(message, chatId);
        // 第二轮
        message = "我想让我的另一半更爱我应该咋办？他叫jjy";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        message = "我是谁？我刚才已经说过了";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        System.out.println(answer);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是初晨，我想让另一半更佳爱我，但是我不知道应该怎么做";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(loveReport);
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "我已经结婚了，但是婚后关系不太亲密怎么办？";
        String answer = loveApp.doChatWithRag(message, chatId);
        System.out.println(answer);
        Assertions.assertNotNull(answer);
    }
}