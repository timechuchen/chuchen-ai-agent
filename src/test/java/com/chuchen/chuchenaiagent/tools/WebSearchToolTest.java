package com.chuchen.chuchenaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author chuchen
 * @date 2025/5/18 14:37
 */
@SpringBootTest
class WebSearchToolTest {


    @Value("${search-api.api-key}")
    private String apiKey;

    @Test
    void searchWeb() {
        WebSearchTool webSearchTool = new WebSearchTool(apiKey);
        String query = "time 初晨";
        String result = webSearchTool.searchWeb(query);
        Assertions.assertNotNull(result);
    }
}