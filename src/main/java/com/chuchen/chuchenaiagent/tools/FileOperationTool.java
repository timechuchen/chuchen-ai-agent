package com.chuchen.chuchenaiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.chuchen.chuchenaiagent.constan.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author chuchen
 * @date 2025/5/18 10:50
 * @description 文件操作工具类（提供文件读写功能）
 */
public class FileOperationTool {

    private final String DILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";

    /**
     * 读文件
     */
    @Tool(description = "Read content from a file")
    public String readFile(@ToolParam(description = "Name of a file to read") String fileName) {
        String filePath = DILE_DIR + "/" + fileName;
        try {
            return FileUtil.readUtf8String(filePath);
        }catch (Exception e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    /**
     * 写文件
     */
    @Tool(description = "Write content to a file")
    public String writeFile(@ToolParam(description = "Name of a file to write") String fileName,
                            @ToolParam(description = "Content to write to the file") String content) {
        String filePath = DILE_DIR + "/" + fileName;
        // 创建目录
        FileUtil.mkdir(DILE_DIR);
        try {
            FileUtil.writeUtf8String(content, filePath);
            return "Write file success to " + filePath;
        }catch (Exception e) {
            return "Error writing to file: " + e.getMessage();
        }
    }
}
