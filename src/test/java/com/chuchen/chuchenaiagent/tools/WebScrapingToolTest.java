package com.chuchen.chuchenaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author chuchen
 * @date 2025/5/18 14:49
 */
class WebScrapingToolTest {

    @Test
    void scrapeWebPage() {
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        String result = webScrapingTool.scrapeWebPage("https://omicx.online");
        Assertions.assertNotNull(result);
    }
}