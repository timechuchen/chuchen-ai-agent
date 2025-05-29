package com.chuchen.chuchenaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

/**
 * @author chuchen
 * @date 2025/5/2 14:10
 */
@SpringBootTest
class TourismAppTest {

    @Resource
    private TourismApp tourismApp;

    @Test
    void doChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是初晨";
        String answer = tourismApp.doChat(message, chatId);
        // 第二轮
        message = "我想在陕西旅游，有啥建议？";
        answer = tourismApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        // 第三轮
        message = "我是谁？我刚才已经说过了";
        answer = tourismApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
        System.out.println(answer);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是初晨，我现在在西安，想找个地方玩，有啥建议？";
        TourismApp.TourismReport tourismReport = tourismApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(tourismReport);
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮
        String message = "你好，我是初晨，我现在在西安，想找个地方玩，有啥建议？";
        String answer = tourismApp.doChatWithRag(message, chatId);
        System.out.println(answer);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithTools() {
//        // 测试联网搜索问题的答案
//        testMessage("周末想带女朋友去上海约会，推荐几个适合情侣的小众打卡地？");
//
//        // 测试网页抓取：恋爱案例分析
//        testMessage("最近和对象吵架了，看看编程导航网站（codefather.cn）的其他情侣是怎么解决矛盾的？");
//
//        // 测试资源下载：图片下载
//        testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");
//
//        // 测试终端操作：执行代码
//        testMessage("执行 Python3 脚本来生成数据分析报告");
//
//        // 测试文件操作：保存用户档案
//        testMessage("保存我的恋爱档案为文件");
//
//        // 测试 PDF 生成
//        testMessage("生成一份‘七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");

        String res = testMessage("帮我搜索并下载两张张喜羊羊和懒羊羊的两张照片");
        System.out.println(res);
    }

    private String testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = tourismApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
        return answer;
    }

    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();
        // 测试地图 MCP
        String message = "我的另一半居住在西安市雁塔区，请帮我找到 5 公里内合适的约会地点，要最新的搜索消息，并且帮我搜索周边的照片";
        String answer =  tourismApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMcp1() {
        String chatId = UUID.randomUUID().toString();
        // 测试图片搜索 MCP
        String message = "请帮我搜索一些懒羊羊的图片";
        String answer =  tourismApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(answer);
    }
}