package com.chuchen.chuchenaiagent.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

/**
 * @author chuchen
 * @date 2025/5/18 14:45
 * @description 网页抓取工具
 */
public class WebScrapingTool {

    @Tool(description = "scrape the content of a web page")
    public String scrapeWebPage(@ToolParam(description = "URL of the web page to scrape") String url) {
        try {
            Document elements = Jsoup.connect(url).get();
            return elements.html();
        } catch (Exception e) {
            return "Error scraping web page: " + e.getMessage();
        }
    }
}
