package com.chuchen.chuchenaiagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author chuchen
 * @date 2025/5/28 16:40
 */
@SpringBootTest
class ChuchenManusTest {

    @Resource
    private ChuchenManus chuchenManus;

    @Test
    public void run() {
        String userPrompt = """  
                我想在西安旅游，我喜欢吃美食以及看知名的景点，我明天出发,
                并结合一些网络图片，制定一份详细的旅游计划,
                并以 PDF 格式输出""";
        String answer = chuchenManus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }
}
