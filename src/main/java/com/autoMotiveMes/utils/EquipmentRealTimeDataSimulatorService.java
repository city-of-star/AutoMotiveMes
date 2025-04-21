package com.autoMotiveMes.utils;

import com.autoMotiveMes.entity.equipment.Equipment;
import com.autoMotiveMes.entity.equipment.EquipmentType;
import com.autoMotiveMes.mapper.equipment.EquipmentMapper;
import com.autoMotiveMes.mapper.equipment.EquipmentTypeMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

/**
 * 实现功能【设备实时数据模拟服务】
 *
 * @author li.hongyu
 * @date 2025-04-16 16:39:45
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentRealTimeDataSimulatorService {

    // 数据库访问组件
    private final EquipmentMapper equipmentMapper;
    private final EquipmentTypeMapper equipmentTypeMapper;

    // REST请求组件
    private final RestTemplate restTemplate;

    // 线程池（5个线程处理定时任务）
    private final ScheduledExecutorService threadPool;

    // 存储设备ID对应的定时任务
    private final Map<Long, ScheduledFuture<?>> equipmentTasks = new ConcurrentHashMap<>();

    // 时间格式化和数值格式化工具
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.####");

    // 设备类型参数配置缓存（Key: 设备类型ID）
    private final Map<Integer, List<ParameterConfig>> equipmentTypeParamConfigs = new HashMap<>();

    // 当前加载的设备列表
    private List<Equipment> equipmentList;

    /**
     * 服务初始化方法
     * 1. 加载所有设备信息
     * 2. 初始化设备类型参数配置
     */
    @PostConstruct
    public void init() {
        long startTime = System.currentTimeMillis();
        log.info("设备实时参数数据模拟服务--初始化开始");
        loadAllEquipment();
        initParamConfigs();
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        log.info("设备实时参数数据模拟服务--初始化成功，耗时: {} 毫秒", elapsedTime);
    }

    /**
     * 从数据库加载所有设备信息
     */
    private void loadAllEquipment() {
        this.equipmentList = equipmentMapper.selectByEquipmentStatus();
        log.info("成功加载设备数量：{}", equipmentList.size());
    }

    /**
     * 初始化设备类型参数配置
     * 1. 加载所有设备类型
     * 2. 解析每个类型的参数配置JSON
     */
    private void initParamConfigs() {
        List<EquipmentType> equipmentTypes = equipmentTypeMapper.selectByEquipmentStatus();
        ObjectMapper objectMapper = new ObjectMapper();

        for (EquipmentType type : equipmentTypes) {
            String json = type.getParametersConfig();
            List<ParameterConfig> configs;

            if (StringUtils.isBlank(json)) {
                log.warn("设备类型[{} - {}]遇到空参数配置", type.getTypeId(), type.getTypeName());
                configs = Collections.emptyList();
            } else {
                try {
                    configs = objectMapper.readValue(json, new TypeReference<>() {
                    });
                } catch (JsonProcessingException e) {
                    log.error("设备类型[{} - {}]参数配置解析失败 - 无效JSON: {}",
                            type.getTypeId(), type.getTypeName(), json, e);
                    configs = Collections.emptyList();
                }
            }

            equipmentTypeParamConfigs.put(type.getTypeId(), configs);
            log.info("加载设备类型[{} - {}]参数配置: {} 项",
                    type.getTypeId(), type.getTypeName(), configs.size());
        }
        log.info("已加载 {} 种设备类型的参数配置", equipmentTypes.size());
    }

    /**
     * 启动所有设备模拟器（异步方法）
     * 为每个设备创建定时任务，每秒生成并发送数据
     */
    @Async
    public void startAllSimulators() {
        equipmentList.forEach(equipment -> {
            // 将定时任务Future存入Map
            ScheduledFuture<?> future = threadPool.scheduleAtFixedRate(
                    () -> simulateDeviceData(equipment),
                    0,
                    1,
                    TimeUnit.SECONDS
            );
            equipmentTasks.put(equipment.getEquipmentId(), future);
        });
        log.info("设备实时参数数据模拟服务--启动成功，已启动 {} 个设备的模拟任务，数据生成间隔 1 秒", equipmentList.size());
    }

    /**
     * 停止指定设备的模拟任务
     */
    public void stopSimulatorForEquipment(Long equipmentId) {
        ScheduledFuture<?> future = equipmentTasks.get(equipmentId);
        if (future != null) {
            future.cancel(true);  // 中断正在执行的任务
            equipmentTasks.remove(equipmentId);
            log.info("设备[{}]的模拟任务已停止", equipmentId);
        }
    }

    /**
     * 开始指定设备的模拟任务
     */
    public void startSimulatorForEquipment(Long equipmentId) {
        // 检查设备是否存在且状态正常
        Equipment equipment = equipmentMapper.selectById(equipmentId);

        if (equipment == null || equipment.getStatus() != 1) {
            log.warn("设备[{}]状态不可用或不存在，无法启动模拟", equipmentId);
            return;
        }

        // 如果已有任务，无需启动
        ScheduledFuture<?> existingTask = equipmentTasks.get(equipmentId);
        if (existingTask != null) {
            return;
        }

        // 创建新定时任务
        ScheduledFuture<?> newTask = threadPool.scheduleAtFixedRate(
                () -> simulateDeviceData(equipment),
                0,
                1,
                TimeUnit.SECONDS
        );

        equipmentTasks.put(equipmentId, newTask);
        log.info("设备[{}]模拟任务已启动", equipmentId);
    }

    /**
     * 模拟单个设备数据
     * @param equipment 目标设备实体
     */
    private void simulateDeviceData(Equipment equipment) {

        List<Map<String, Object>> parameters = generateParameters(equipment);
        sendParametersToAPI(equipment, parameters);
    }

    /**
     * 将生成的参数发送到API接口
     * @param equipment  设备实体
     * @param parameters 参数列表
     */
    private void sendParametersToAPI(Equipment equipment, List<Map<String, Object>> parameters) {
        final String API_URL = "http://localhost:3000/api/equipment/acceptData";

        parameters.forEach(param -> {
            Map<String, Object> payload = new HashMap<>();
            payload.put("equipmentId", equipment.getEquipmentId());
            payload.put("paramName", param.get("name"));
            payload.put("paramValue", param.get("value"));
            payload.put("unit", param.get("unit"));
            payload.put("collectTime", LocalDateTime.now().format(TIME_FORMATTER));
            payload.put("isNormal", param.get("isNormal"));

            restTemplate.postForObject(API_URL, payload, String.class);
        });
    }

    /**
     * 生成设备参数列表
     * @param equipment 目标设备
     * @return 参数列表（包含参数名、值、单位、状态）
     */
    private List<Map<String, Object>> generateParameters(Equipment equipment) {
        List<ParameterConfig> configs = equipmentTypeParamConfigs.get(equipment.getEquipmentType());
        if (configs == null || configs.isEmpty()) {
            log.warn("设备[{}]类型[{}]无有效参数配置", equipment.getEquipmentId(), equipment.getEquipmentType());
            return Collections.emptyList();
        }

        List<Map<String, Object>> parameters = new ArrayList<>();
        configs.forEach(config -> parameters.add(createParameter(config)));
        return parameters;
    }

    /**
     * 创建单个参数项
     * @param config 参数配置
     */
    private Map<String, Object> createParameter(ParameterConfig config) {
        boolean isAbnormal = Math.random() < config.abnormalProbability;
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double value = isAbnormal ?
                random.nextDouble(config.abnormalMin, config.abnormalMax) :
                random.nextDouble(config.normalMin, config.normalMax);
        double formattedValue = Double.parseDouble(DECIMAL_FORMAT.format(value));

        Map<String, Object> param = new HashMap<>();
        param.put("name", config.name);
        param.put("value", formattedValue);
        param.put("unit", config.unit);
        param.put("isNormal", isAbnormal ? 0 : 1);
        return param;
    }

    /**
     * 参数配置模型
     * 字段说明：
     * - name: 参数名称
     * - unit: 计量单位
     * - normalMin: 正常范围最小值
     * - normalMax: 正常范围最大值
     * - abnormalMin: 异常范围最小值
     * - abnormalMax: 异常范围最大值
     * - abnormalProbability: 异常值发生概率（0~1）
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class ParameterConfig {
        private String name;
        private String unit;
        private double normalMin;
        private double normalMax;
        private double abnormalMin;
        private double abnormalMax;
        private double abnormalProbability;
    }
}