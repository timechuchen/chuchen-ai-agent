package com.chuchen.chuchenimagesearchmcpserver;

import com.chuchen.chuchenimagesearchmcpserver.tools.ImageSearchTool;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChuchenImageSearchMcpServerApplicationTests {

	@Resource
	private ImageSearchTool imageSearchTool;

	@Test
	void contextLoads() {
		String result = imageSearchTool.searchImage("computer");
		Assertions.assertNotNull(result);
	}

}
