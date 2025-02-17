package com.automotivemes;

import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PermissionTest extends BaseTest {

    @Test
    void testDeviceControlPermission() throws Exception {
        // 设备操作员应该可以访问设备控制接口
        mockMvc.perform(post("/api/devices/123/control")
                        .header("Authorization", "Bearer " + operatorToken)
                        .content("{\"command\":\"start\"}"))
                .andExpect(status().isOk());
    }

//    @Test
//    void testCreateOrderPermission() throws Exception {
//        // 生产主管应该可以创建工单
//        String managerToken = obtainToken("prod_manager", "manager123");
//
//        mockMvc.perform(post("/api/production/orders")
//                        .header("Authorization", "Bearer " + managerToken)
//                        .content("{\"orderNo\":\"PO-20240501\"}"))
//                .andExpect(status().isCreated());
//    }
}
