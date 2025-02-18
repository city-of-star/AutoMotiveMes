package com.automotivemes;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest extends BaseTest {

    // 封装创建注册请求 JSON 体的方法
    private String createRegistrationJson(String username, String password, String confirmPassword) {
        return String.format("{\"username\":\"%s\",\"password\":\"%s\",\"confirmPassword\":\"%s\"}", username, password, confirmPassword);
    }

    // 封装创建登录请求 JSON 体的方法
    private String createLoginJson(String username, String password) {
        return String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
    }

    // 封装通用的请求执行方法
    private ResultActions performRequest(String url, String jsonBody) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody));
    }

    /**
     * 测试注册成功的情况
     * 该测试方法会构建一个有效的注册请求，包含匹配的密码，
     * 并发送到注册接口，期望返回状态码为 200（OK），表示注册成功
     * @throws Exception 当执行请求或验证结果时出现异常
     */
    @Test
    void testRegistrationSuccess() throws Exception {
        String jsonBody = createRegistrationJson("newuser", "newpassword", "newpassword");
        performRequest("/api/auth/register", jsonBody)
                .andDo(print())
                .andExpect(status().isOk());
    }

    /**
     * 测试注册失败 - 密码不匹配的情况
     * 该测试方法会构建一个注册请求，其中密码和确认密码不匹配，
     * 并发送到注册接口，期望返回状态码为 400（Bad Request），表示请求无效
     * @throws Exception 当执行请求或验证结果时出现异常
     */
    @Test
    void testRegistrationFailurePasswordMismatch() throws Exception {
        String jsonBody = createRegistrationJson("newuser1", "newpassword", "wrongpassword");
        performRequest("/api/auth/register", jsonBody)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试登录成功的情况
     * 该测试方法会构建一个有效的登录请求，包含正确的用户名和密码，
     * 并发送到登录接口，期望返回状态码为 200（OK），且响应体中包含有效的 token 字段
     * @throws Exception 当执行请求或验证结果时出现异常
     */
    @Test
    void testLoginSuccess() throws Exception {
        String jsonBody = createLoginJson("admin", "admin123");
        performRequest("/api/auth/login", jsonBody)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").exists());
    }

    /**
     * 测试登录失败的情况
     * 该测试方法会构建一个无效的登录请求，包含错误的用户名和密码，
     * 并发送到登录接口，期望返回状态码为 400（Bad Request），表示请求无效
     * @throws Exception 当执行请求或验证结果时出现异常
     */
    @Test
    void testLoginFailure() throws Exception {
        String jsonBody = createLoginJson("wrong", "wrong");
        performRequest("/api/auth/login", jsonBody)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}