package com.chuchen.chuchenaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author chuchen
 * @date 2025/5/15 14:50
 */
@SpringBootTest
class TourismAppDocumentLoaderTest {

    @Resource
    private TourismAppDocumentLoader tourismAppDocumentLoader;

    @Test
    void loadMarkers() {
        tourismAppDocumentLoader.loadMarkers();
    }
}