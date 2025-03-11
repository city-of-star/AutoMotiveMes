package com.automotivemes.simulator;

import com.automotivemes.entity.equipment.Equipment;
import com.automotivemes.service.impl.equipment.EquipmentServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
            // 每个设备单独一个线程，1秒执行一次
            threadPool.scheduleAtFixedRate(
                    () -> simulateDeviceData(equipment),
                    0, 1, TimeUnit.SECONDS
            );
        });
    }

    /**
     * 单个设备数据模拟方法
     */
    private void simulateDeviceData(Equipment equipment) {
        try {
            // 生成模拟数据
            Map<String, Object> data = new HashMap<>();
            data.put("equipmentId", equipment.getEquipmentId());
            String status = generateStatus(equipment.getType());
            data.put("status", status);
            data.put("temperature", generateTemperature(equipment.getType()));
            data.put("rpm", generateRpm(equipment.getType()));
            data.put("isAlarm", isAlarm(status, (BigDecimal) data.get("temperature"), (Integer) data.get("rpm")));

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
        Random random = new Random();
        int randomNum = random.nextInt(100);
        if (randomNum < 70) {
            return "运行";
        } else if (randomNum < 85) {
            return "待机";
        } else if (randomNum < 95) {
            return "故障";
        } else {
            return "维护";
        }
    }

    private BigDecimal generateTemperature(String type) {
        Random random = new Random();
        switch (type) {
            case "冲压":
                return BigDecimal.valueOf(30 + random.nextDouble() * 20).setScale(2, RoundingMode.HALF_UP);
            case "焊接":
                return BigDecimal.valueOf(80 + random.nextDouble() * 50).setScale(2, RoundingMode.HALF_UP);
            case "涂装":
                return BigDecimal.valueOf(20 + random.nextDouble() * 10).setScale(2, RoundingMode.HALF_UP);
            case "总装":
                return BigDecimal.valueOf(25 + random.nextDouble() * 5).setScale(2, RoundingMode.HALF_UP);
            default:
                return BigDecimal.valueOf(30 + random.nextDouble() * 50).setScale(2, RoundingMode.HALF_UP);
        }
    }

    private int generateRpm(String type) {
        Random random = new Random();
        switch (type) {
            case "冲压":
                return 300 + random.nextInt(500);
            case "焊接":
                return 100 + random.nextInt(300);
            case "涂装":
                return 50 + random.nextInt(100);
            case "总装":
                return 20 + random.nextInt(50);
            default:
                return 500 + random.nextInt(1000);
        }
    }

    private boolean isAlarm(String status, BigDecimal temperature, int rpm) {
        if ("故障".equals(status)) {
            return true;
        }
        if ("运行".equals(status)) {
            if (temperature.compareTo(BigDecimal.valueOf(80)) > 0 || rpm > 1200) {
                return true;
            }
        }
        return false;
    }
}
