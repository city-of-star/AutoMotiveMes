package com.autoMotiveMes.service.impl.equipment;

import com.autoMotiveMes.dto.equipment.*;
import com.autoMotiveMes.entity.equipment.Equipment;
import com.autoMotiveMes.entity.equipment.EquipmentAlarm;
import com.autoMotiveMes.entity.equipment.EquipmentMaintenance;
import com.autoMotiveMes.entity.equipment.EquipmentParameters;
import com.autoMotiveMes.mapper.equipment.EquipmentAlarmMapper;
import com.autoMotiveMes.mapper.equipment.EquipmentMaintenanceMapper;
import com.autoMotiveMes.mapper.equipment.EquipmentMapper;
import com.autoMotiveMes.service.equipment.EquipmentService;
import com.autoMotiveMes.utils.CommonUtils;
import com.autoMotiveMes.utils.EquipmentRealTimeDataSimulatorService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 实现功能【设备实现类】
 *
 * @author li.hongyu
 * @date 2025-04-16 16:39:45
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentMaintenanceMapper maintenanceMapper;
    private final EquipmentAlarmMapper alarmMapper;
    private final EquipmentMapper equipmentMapper;

    // 报警规则缓存（设备类型ID -> 规则配置）
    private final Map<Integer, AlarmRuleConfig> alarmRules = new ConcurrentHashMap<>();

    private final RedisTemplate<String, EquipmentParameters> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    // 模拟服务
    private final EquipmentRealTimeDataSimulatorService simulatorService;

    // redis 存入的设备实时参数数据的公共key
    private static final String REDIS_KEY_PREFIX = CommonUtils.REDIS_KEY_PREFIX;
    // redis 存入的设备实时参数数据的过期时间
    private static final int DATA_EXPIRE_MINUTES = CommonUtils.DATA_EXPIRE_MINUTES;

    /**
     * 接受设备实时运行参数数据
     * 将其存入 redis 并设置 5 分钟的过期时间
     * 推送 WebSocket 消息
     */
    @Override
    public void acceptEquipmentRealTimeData(EquipmentParameters data) {
        Long equipmentId = data.getEquipmentId();
        String redisKey = REDIS_KEY_PREFIX + equipmentId;
        int keepSize = DATA_EXPIRE_MINUTES * 5 * 60;

        // 1. 将数据插入到列表头部（左侧）
        redisTemplate.opsForList().leftPush(redisKey, data);

        // 2. 修剪列表，仅保留最新的 keepSize 条数据
        redisTemplate.opsForList().trim(redisKey, 0, keepSize - 1);

        // 3. 设置键的过期时间（每次插入都续期）
        redisTemplate.expire(redisKey, DATA_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 4. 推送WebSocket消息
        messagingTemplate.convertAndSend("/topic/equipment/realtime", data);
    }

    @Override
    public List<Equipment> listEquipment() {
        return equipmentMapper.selectList(null);
    }

    @Scheduled(fixedDelay = 30_000) // 每30秒检测一次
    public void checkAlarmConditions() {
        Set<String> keys = redisTemplate.keys(CommonUtils.REDIS_KEY_PREFIX + "*");
        keys.forEach(key -> {
            Long equipmentId = Long.parseLong(key.substring(10));
            List<EquipmentParameters> params = redisTemplate.opsForList().range(key, 0, 49);  // 取最近50条

            if (params != null) {
                checkContinuousAbnormal(equipmentId, params);
            }
            if (params != null) {
                checkMultiParamAbnormal(equipmentId, params);
            }
        });
    }

    private void checkContinuousAbnormal(Long equipmentId, List<EquipmentParameters> params) {
        int consecutiveCount = 0;
        for (EquipmentParameters param : params) {
            if (param.getIsNormal() == 0) {
                consecutiveCount++;
                if (consecutiveCount >= 3) {
                    handleAlarm(equipmentId, "连续3次异常参数", 2);
                    break;
                }
            } else {
                consecutiveCount = 0;
            }
        }
    }

    private void checkMultiParamAbnormal(Long equipmentId, List<EquipmentParameters> params) {
        Map<String, Integer> abnormalCounts = new HashMap<>();
        params.stream()
                .filter(p -> p.getIsNormal() == 0)
                .forEach(p -> abnormalCounts.merge(p.getParamName(), 1, Integer::sum));

        if (abnormalCounts.size() >= 2) {
            handleAlarm(equipmentId, "多参数异常:" + abnormalCounts.keySet(), 3);
        }
    }

    private void handleAlarm(Long equipmentId, String detail, int level) {
        EquipmentAlarm alarm = new EquipmentAlarm();
        alarm.setEquipmentId(equipmentId);
        alarm.setAlarmCode(generateAlarmCode(level));
        alarm.setAlarmReason(detail);
        alarm.setAlarmLevel(level);
        alarm.setStartTime(LocalDateTime.now());
        alarm.setStatus(0);  // 未处理

        alarmMapper.insert(alarm);

        Equipment equipment = equipmentMapper.selectById(equipmentId);
        equipment.setStatus(2);  // 待机
        equipmentMapper.updateById(equipment);

        // 停止该设备的模拟任务
        simulatorService.stopSimulatorForEquipment(equipmentId);

        // WebSocket通知
        messagingTemplate.convertAndSend("/topic/equipment/alarm", alarm);
    }

    // 报警编码生成规则（示例：AL3-202311021030）
    private String generateAlarmCode(int level) {
        return String.format("AL%d-%s", level,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));
    }

    @Data
    private static class AlarmRuleConfig {
        private int continuousThreshold = 3;
        private int multiParamThreshold = 2;
    }

    @Scheduled(cron = "0 0 8 * * ?") // 每天8点生成计划
    public void generatePreventiveMaintenance() {
        List<Equipment> equipments = equipmentMapper.selectList(
                new QueryWrapper<Equipment>().isNotNull("maintenance_cycle"));

        equipments.forEach(equip -> {
            EquipmentMaintenance maintenance = new EquipmentMaintenance();
            maintenance.setEquipmentId(equip.getEquipmentId());
            maintenance.setPlanDate(LocalDate.now().plusDays(equip.getMaintenanceCycle()));
            maintenance.setMaintenanceType(1);  // 预防性
            maintenance.setMaintenanceContent("定期保养");

            maintenanceMapper.insert(maintenance);
        });
    }

    @Transactional
    public void handleAlarmMaintenance(HandleAlarmRequestDto dto) {
        String username = CommonUtils.getCurrentUsername();
        EquipmentAlarm alarm = alarmMapper.selectById(dto.getAlarmId());

        EquipmentMaintenance maintenance = new EquipmentMaintenance();
        maintenance.setEquipmentId(alarm.getEquipmentId());
        maintenance.setPlanDate(LocalDate.now());
        maintenance.setActualDate(LocalDate.now());
        maintenance.setMaintenanceType(3);  // 应急维修
        maintenance.setOperator(username);
        maintenance.setResult(dto.getResult());
        maintenance.setCost(dto.getCost());
        maintenance.setMaintenanceContent("(处理报警：" + alarm.getAlarmCode() + ") " + dto.getMaintenanceContent());

        maintenanceMapper.insert(maintenance);

        // 更新报警状态
        alarm.setStatus(2);  // 已处理
        alarm.setHandler(username);
        alarm.setEndTime(LocalDateTime.now());
        Duration duration = Duration.between(alarm.getStartTime(), alarm.getEndTime());
        alarm.setDuration((int) duration.getSeconds());
        alarm.setSolution("维护内容: " + dto.getMaintenanceContent() + "\n处理结果: " + dto.getResult());
        alarmMapper.updateById(alarm);

        // 更新涉笔状态
        Equipment equipment = equipmentMapper.selectById(alarm.getEquipmentId());
        equipment.setStatus(1);  // 运行中
        equipmentMapper.updateById(equipment);

        // 启动该设备的模拟
        simulatorService.startSimulatorForEquipment(equipment.getEquipmentId());
    }

    @Override
    public List<RealTimeAlarmResponseDto> listRealTimeEquipmentAlarm() {
        return alarmMapper.listRealTimeEquipmentAlarm();
    }

    @Override
    public Page<AlarmHistoryResponseDto> listEquipmentAlarmHistory(AlarmHistoryRequestDto dto) {
        Page<AlarmHistoryResponseDto> page = new Page<>(dto.getPage() == null ? 1 : dto.getPage(),
                dto.getSize() == null ? 10 : dto.getSize());
        return alarmMapper.listEquipmentAlarmHistory(page, dto);
    }

    @Override
    public GetEquipmentCountResponseDto getEquipmentCount() {
        GetEquipmentCountResponseDto dto = new GetEquipmentCountResponseDto();
        dto.setNormalEquipmentCount(equipmentMapper.getNormalEquipmentCount());
        dto.setOnlineEquipmentCount(equipmentMapper.getOnlineEquipmentCount());
        return dto;
    }

    @Override
    public Page<MaintenanceRecordListResponseDto> listMaintenanceRecord(MaintenanceRecordListRequestDto dto) {
        Page<MaintenanceRecordListResponseDto> page = new Page<>(dto.getPage(), dto.getSize());
        return maintenanceMapper.listMaintenanceRecord(page, dto);
    }
}