package com.chuchen.chuchenaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author chuchen
 * @date 2025/5/18 15:13
 */
class PDFGenerationToolTest {

    @Test
    void generatePDF() {
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        String fileName = "time_chuchen.pdf";
        String content = "Time 初晨 https://github.com/timechuchen";
        String s = pdfGenerationTool.generatePDF(fileName, content);
        Assertions.assertNotNull(s);
    }
}