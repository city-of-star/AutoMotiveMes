package com.autoMotiveMes.service.impl.business;

import com.autoMotiveMes.common.constant.CommonConstant;
import com.autoMotiveMes.common.exception.ServerException;
import com.autoMotiveMes.dto.equipment.*;
import com.autoMotiveMes.entity.equipment.Equipment;
import com.autoMotiveMes.entity.equipment.EquipmentAlarm;
import com.autoMotiveMes.entity.equipment.EquipmentMaintenance;
import com.autoMotiveMes.entity.equipment.EquipmentParameters;
import com.autoMotiveMes.mapper.equipment.EquipmentAlarmMapper;
import com.autoMotiveMes.mapper.equipment.EquipmentMaintenanceMapper;
import com.autoMotiveMes.mapper.equipment.EquipmentMapper;
import com.autoMotiveMes.service.business.AlarmService;
import com.autoMotiveMes.service.simulationService.EquipmentRealTimeDataSimulatorService;
import com.autoMotiveMes.utils.CommonUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实现功能【报警服务类】
 *
 * @author li.hongyu
 * @date 2025-05-05 11:37:51
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {

    private final EquipmentMaintenanceMapper maintenanceMapper;
    private final EquipmentAlarmMapper alarmMapper;
    private final EquipmentMapper equipmentMapper;

    private final RedisTemplate<String, EquipmentParameters> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    // 模拟服务
    private final EquipmentRealTimeDataSimulatorService simulatorService;

    @Scheduled(fixedDelay = 30_000)  // 每30秒检测一次
    public void checkAlarmConditions() {
        Set<String> keys = redisTemplate.keys(CommonConstant.REDIS_KEY_PREFIX + "*");
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
        Map<String, Integer> paramConsecutiveCount = new HashMap<>();

        for (EquipmentParameters param : params) {
            String paramName = param.getParamName();
            if (param.getIsNormal() == 0) {
                int count = paramConsecutiveCount.getOrDefault(paramName, 0) + 1;
                paramConsecutiveCount.put(paramName, count);
                if (count >= 3) {
                    handleAlarm(equipmentId, "参数["+paramName+"]连续3次异常", 2);
                    paramConsecutiveCount.put(paramName, 0); // 重置避免重复报警
                }
            } else {
                paramConsecutiveCount.remove(paramName);
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

    @Override
    @Transactional
    public void handleAlarmMaintenance(HandleAlarmRequestDto dto) {
        try {
            String username = CommonUtils.getCurrentUsername();
            EquipmentAlarm alarm = alarmMapper.selectById(dto.getAlarmId());

            EquipmentMaintenance maintenance = new EquipmentMaintenance();
            maintenance.setEquipmentId(alarm.getEquipmentId());
            maintenance.setPlanDate(LocalDate.now());
            maintenance.setActualDate(LocalDate.now());
            maintenance.setMaintenanceType(3);  // 应急维修
            maintenance.setOperator(username);
            maintenance.setResult(dto.getResult());
            maintenance.setCost(dto.getCost() == null ? BigDecimal.valueOf(0) : dto.getCost());
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

            // 更新设备状态
            Equipment equipment = equipmentMapper.selectById(alarm.getEquipmentId());
            equipment.setStatus(1);  // 运行中
            equipmentMapper.updateById(equipment);

            // 启动该设备的模拟
            simulatorService.startSimulatorForEquipment(equipment.getEquipmentId());
        } catch (ServerException e) {
            throw new ServerException("处理警报并产生维护记录出错出错: " + e.getMessage());
        }
    }

    @Override
    public List<RealTimeAlarmResponseDto> listRealTimeEquipmentAlarm() {
        try {
            return alarmMapper.listRealTimeEquipmentAlarm();
        } catch (ServerException e) {
            throw new ServerException("获取实时设备报警列表出错: " + e.getMessage());
        }
    }

    @Override
    public Page<AlarmHistoryResponseDto> listEquipmentAlarmHistory(AlarmHistoryRequestDto dto) {
        try {
            Page<AlarmHistoryResponseDto> page = new Page<>(dto.getPage() == null ? 1 : dto.getPage(),
                    dto.getSize() == null ? 10 : dto.getSize());
            return alarmMapper.listEquipmentAlarmHistory(page, dto);
        } catch (ServerException e) {
            throw new ServerException("获取设备报警历史列表出错: " + e.getMessage());
        }
    }

    @Override
    public GetEquipmentCountResponseDto getEquipmentCount() {
        try {
            GetEquipmentCountResponseDto dto = new GetEquipmentCountResponseDto();
            dto.setNormalEquipmentCount(equipmentMapper.getNormalEquipmentCount());
            dto.setOnlineEquipmentCount(equipmentMapper.getOnlineEquipmentCount());
            return dto;
        } catch (ServerException e) {
            throw new ServerException("获取设备总数量和在线数量出错: " + e.getMessage());
        }
    }
}