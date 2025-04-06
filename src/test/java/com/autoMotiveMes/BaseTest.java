package com.autoMotiveMes;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:init-test-data.sql")
public class BaseTest {

    @Autowired
    protected MockMvc mockMvc;

    protected String adminToken;
    protected String operatorToken;

    @BeforeEach
    void setup() throws Exception {
        // 初始化测试用 Token
        adminToken = obtainToken("admin", "123456");
        operatorToken = obtainToken("operator1", "123456");
    }

    String obtainToken(String username, String password) throws Exception {
        // 构建 json 格式的请求体
        String jsonBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);

        return mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)  // 声明请求体为 json格式
                        .content(jsonBody))  // 添加内容
                .andExpect(status().isOk())  // 验证响应状态码
                .andReturn()  // 返回 MvcResult 对象，该对象包含了请求执行后的详细结果，包括响应信息
                .getResponse()  // 从 MvcResult 中获取响应对象。
                .getContentAsString()  // 将响应的内容转换为字符串。
                .split("\"token\":\"")[1].split("\"")[0];  // 通过字符串分割操作从响应内容中提取 token
    }
}
