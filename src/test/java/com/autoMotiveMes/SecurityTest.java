package com.autoMotiveMes;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SecurityTest extends BaseTest {

    // 测试管理员权限接口
    @Test
    void testAdminAccess() throws Exception {
        mockMvc.perform(get("/api/")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    // 测试操作员无权访问管理员接口
    @Test
    void testOperatorForbidden() throws Exception {
        mockMvc.perform(get("/api/user")
                        .header("Authorization", "Bearer " + operatorToken))
                .andExpect(status().isForbidden());
    }

    // 测试未授权访问
    @Test
    void testUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/api/devices"))
                .andExpect(status().isUnauthorized());
    }
}

