package com.automotivemes.simulator;

import com.automotivemes.entity.equipment.Equipment;
import com.automotivemes.service.impl.equipment.EquipmentServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EquipmentRealTimeDataSimulatorService {

    private final EquipmentServiceImpl equipmentService;
    private final RestTemplate restTemplate;
    private final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(10);

    // 设备列表缓存
    private List<Equipment> equipmentList;

    /**
     * 服务启动后初始化设备列表
     */
    @PostConstruct
    public void init() {
        this.equipmentList = equipmentService.getAllEquipment();
        System.out.println("已加载设备数量：" + equipmentList.size());
    }

    /**
     * 启动所有设备的模拟线程
     */
    @Async
    public void startAllSimulators() {
        equipmentList.forEach(equipment -> {
            // 每个设备单独一个线程，5秒执行一次
            threadPool.scheduleAtFixedRate(
                    () -> simulateDeviceData(equipment),
                    0, 5, TimeUnit.SECONDS
            );
        });
    }

    /**
     * 单个设备数据模拟方法
     */
    private void simulateDeviceData(Equipment equipment) {
        try {
            // 生成模拟数据
            Map<String, String> data = new HashMap<>();
            data.put("equipmentId", String.valueOf(equipment.getEquipmentId()));
            data.put("status", generateStatus(equipment.getType()));
            data.put("temperature", generateTemperature(equipment.getType()));
            data.put("rpm", generateRpm(equipment.getType()));

            // 发送到API接口
            String apiUrl = "http://localhost:3000/api/equipment/real-time-data/simulate-create";
            restTemplate.postForObject(apiUrl, data, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("HTTP错误: " + e.getStatusCode() + " 设备ID: " + equipment.getEquipmentId());
        } catch (Exception e) {
            System.err.println("模拟数据异常 设备ID: " + equipment.getEquipmentId());
        }
    }

    // 以下为数据生成方法（示例实现）
    private String generateStatus(String type) {
        // 实际实现需要根据设备类型生成不同状态
        return "运行";
    }

    private String generateTemperature(String type) {
        return String.valueOf(30 + (Math.random() * 50));
    }

    private String generateRpm(String type) {
        return String.valueOf(500 + (int)(Math.random() * 1000));
    }
}
