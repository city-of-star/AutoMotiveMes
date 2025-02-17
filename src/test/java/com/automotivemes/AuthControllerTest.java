package com.automotivemes;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest extends BaseTest {

    @Test
    void testRegistrationSuccess() throws Exception {
        // 两次密码一致，模拟注册成功
        String jsonBody = "{\"username\":\"newuser\",\"password\":\"newpassword\",\"confirmPassword\":\"newpassword\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testRegistrationFailurePasswordMismatch() throws Exception {
        // 两次密码不一致，模拟注册失败
        String jsonBody = "{\"username\":\"newuser1\",\"password\":\"newpassword\",\"confirmPassword\":\"wrongpassword\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void testLoginSuccess() throws Exception {
        String jsonBody = "{\"username\":\"admin\",\"password\":\"admin123\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.token").exists());
    }

    @Test
    void testLoginFailure() throws Exception {
        String jsonBody = "{\"username\":\"wrong\",\"password\":\"wrong\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isBadRequest());
    }
}
