package com.autoMotiveMes.utils;

import com.autoMotiveMes.entity.equipment.Equipment;
import com.autoMotiveMes.mapper.equipment.EquipmentMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EquipmentRealTimeDataSimulatorService {

    private final EquipmentMapper equipmentMapper;
    private final RestTemplate restTemplate;
    private final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(10);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 设备类型参数配置
    private final Map<Integer, List<ParameterConfig>> equipmentTypeParamConfigs = new HashMap<>();
    private List<Equipment> equipmentList;

    @PostConstruct
    public void init() {
        this.equipmentList = equipmentMapper.selectList(null);
        System.out.println("已加载设备数量：" + equipmentList.size());

        // 初始化设备类型参数配置
        initParamConfigs();
    }

    private void initParamConfigs() {
        // 冲压机（类型1）
        equipmentTypeParamConfigs.put(1, Arrays.asList(
                new ParameterConfig("主电机电流", "A", 80.0, 120.0, 120.0, 150.0, 0.05),
                new ParameterConfig("滑块压力", "MPa", 18.0, 22.0, 22.0, 25.0, 0.03),
                new ParameterConfig("工作温度", "℃", 50.0, 80.0, 80.0, 100.0, 0.05),
                new ParameterConfig("振动幅度", "mm", 0.1, 0.5, 0.5, 1.0, 0.02)
        ));

        // 焊接机器人（类型2）
        equipmentTypeParamConfigs.put(2, Arrays.asList(
                new ParameterConfig("焊接电流", "A", 200.0, 250.0, 250.0, 300.0, 0.05),
                new ParameterConfig("焊接电压", "V", 22.0, 28.0, 28.0, 35.0, 0.03),
                new ParameterConfig("气体流量", "L/min", 15.0, 20.0, 20.0, 25.0, 0.04),
                new ParameterConfig("臂架负载率", "%", 60.0, 90.0, 90.0, 100.0, 0.05)
        ));

        // CNC加工中心（类型3）
        equipmentTypeParamConfigs.put(3, Arrays.asList(
                new ParameterConfig("主轴转速", "rpm", 18000.0, 22000.0, 22000.0, 25000.0, 0.03),
                new ParameterConfig("进给速率", "mm/min", 2000.0, 5000.0, 5000.0, 6000.0, 0.02),
                new ParameterConfig("刀具温度", "℃", 30.0, 50.0, 50.0, 70.0, 0.04),
                new ParameterConfig("切削力", "kN", 5.0, 15.0, 15.0, 25.0, 0.03)
        ));

        // AGV小车（类型4）
        equipmentTypeParamConfigs.put(4, Arrays.asList(
                new ParameterConfig("电池电压", "V", 48.0, 52.0, 52.0, 60.0, 0.05),
                new ParameterConfig("行驶速度", "m/s", 1.0, 1.5, 1.5, 2.0, 0.05),
                new ParameterConfig("载重负荷", "kg", 0.0, 800.0, 800.0, 1000.0, 0.03),
                new ParameterConfig("导航信号强度", "%", 80.0, 100.0, 0.0, 80.0, 0.05)
        ));

        // 注塑机（类型5）
        equipmentTypeParamConfigs.put(5, Arrays.asList(
                new ParameterConfig("注射压力", "bar", 800.0, 1200.0, 1200.0, 1500.0, 0.04),
                new ParameterConfig("料筒温度", "℃", 200.0, 250.0, 250.0, 300.0, 0.03),
                new ParameterConfig("锁模力", "ton", 800.0, 850.0, 850.0, 900.0, 0.02),
                new ParameterConfig("循环时间", "s", 30.0, 40.0, 40.0, 60.0, 0.05)
        ));
    }

    @Async
    public void startAllSimulators() {
        equipmentList.forEach(equipment -> {
            threadPool.scheduleAtFixedRate(
                    () -> simulateDeviceData(equipment),
                    0, 1, TimeUnit.SECONDS
            );
        });
    }

    private void simulateDeviceData(Equipment equipment) {
        List<Map<String, Object>> parameters = generateParameters(equipment);
        String apiUrl = "http://localhost:3000/api/equipment/acceptData";

        parameters.forEach(param -> {
            Map<String, Object> data = new HashMap<>();
            data.put("equipmentId", equipment.getEquipmentId());
            data.put("paramName", param.get("name"));
            data.put("paramValue", param.get("value"));
            data.put("unit", param.get("unit"));
            data.put("collectTime", LocalDateTime.now().format(formatter));
            data.put("isNormal", param.get("isNormal"));
            restTemplate.postForObject(apiUrl, data, String.class);
        });
    }

    private List<Map<String, Object>> generateParameters(Equipment equipment) {
        List<ParameterConfig> configs = equipmentTypeParamConfigs.get(equipment.getEquipmentType());
        if (configs == null) return Collections.emptyList();

        List<Map<String, Object>> parameters = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("#.####");

        for (ParameterConfig config : configs) {
            // 生成随机值
            boolean isAbnormal = Math.random() < config.abnormalProbability;
            double value = generateValue(config, isAbnormal);

            // 构建参数
            Map<String, Object> param = new HashMap<>();
            param.put("name", config.name);
            param.put("value", Double.parseDouble(df.format(value)));
            param.put("unit", config.unit);
            param.put("isNormal", isAbnormal ? 0 : 1);
            parameters.add(param);
        }
        return parameters;
    }

    private double generateValue(ParameterConfig config, boolean isAbnormal) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (isAbnormal) {
            return random.nextDouble(config.abnormalMin, config.abnormalMax);
        }
        return random.nextDouble(config.normalMin, config.normalMax);
    }

    // 参数配置内部类
    private static class ParameterConfig {
        String name;
        String unit;
        double normalMin;
        double normalMax;
        double abnormalMin;
        double abnormalMax;
        double abnormalProbability;

        public ParameterConfig(String name, String unit,
                               double normalMin, double normalMax,
                               double abnormalMin, double abnormalMax,
                               double abnormalProbability) {
            this.name = name;
            this.unit = unit;
            this.normalMin = normalMin;
            this.normalMax = normalMax;
            this.abnormalMin = abnormalMin;
            this.abnormalMax = abnormalMax;
            this.abnormalProbability = abnormalProbability;
        }
    }
}