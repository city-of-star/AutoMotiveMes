package com.autoMotiveMes.service.scheduledTask;

import com.autoMotiveMes.entity.equipment.Equipment;
import com.autoMotiveMes.entity.equipment.EquipmentParameters;
import com.autoMotiveMes.entity.equipment.EquipmentStatus;
import com.autoMotiveMes.mapper.equipment.EquipmentMapper;
import com.autoMotiveMes.mapper.equipment.EquipmentParametersMapper;
import com.autoMotiveMes.mapper.equipment.EquipmentStatusMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

/**
 * 实现功能【设备状态记录定时任务】
 *
 * @author li.hongyu
 * @date 2025-04-17 17:10:29
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EquipmentStatusTask {
    private final EquipmentParametersMapper parametersMapper;
    private final EquipmentStatusMapper statusMapper;
    private final EquipmentMapper equipmentMapper;

    @Scheduled(fixedRate = 60_000)  // 每分钟执行一次
    public void generateStatusRecords() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        LocalDateTime windowStart = now.minusMinutes(1);

        // 获取需要监控的有效设备（正常和待机状态）
        List<Equipment> activeEquipments = equipmentMapper.selectList(
                new QueryWrapper<Equipment>()
                        .select("equipment_id")
                        .in("status", Arrays.asList(1, 2))
        );

        activeEquipments.parallelStream().forEach(equipment -> {
            Long equipmentId = equipment.getEquipmentId();

            // 查询时间窗口内的参数记录（使用现有索引）
            List<EquipmentParameters> params = parametersMapper.selectList(
                    new QueryWrapper<EquipmentParameters>()
                            .eq("equipment_id", equipmentId)
                            .between("collect_time", windowStart, now)
                            .orderByAsc("collect_time")
            );

            processEquipmentStatus(equipmentId, params, windowStart, now);
        });
    }

    private void processEquipmentStatus(Long equipmentId,
                                        List<EquipmentParameters> params,
                                        LocalDateTime windowStart,
                                        LocalDateTime windowEnd) {
        // 状态判断
        int currentStatusCode = judgeStatus(params);
        String statusDetail = getStatusDetail(currentStatusCode);

        // 获取最近状态记录（使用现有索引）
        EquipmentStatus latestStatus = statusMapper.selectOne(
                new QueryWrapper<EquipmentStatus>()
                        .eq("equipment_id", equipmentId)
                        .orderByDesc("start_time")
                        .last("LIMIT 1")
        );

        try {
            if (latestStatus == null) {
                createNewStatus(equipmentId, currentStatusCode,
                        windowStart, windowEnd, statusDetail);
            } else if (isContinuousStatus(latestStatus, currentStatusCode)) {
                updateStatusDuration(latestStatus, windowEnd);
            } else {
                createNewStatus(equipmentId, currentStatusCode,
                        windowStart, windowEnd, statusDetail);
            }
        } catch (Exception e) {
            log.error("设备[{}]状态处理异常: {}", equipmentId, e.getMessage());
        }
    }

    private boolean isContinuousStatus(EquipmentStatus existing, int newCode) {
        return existing.getStatusCode() == newCode &&
                existing.getEndTime() != null &&
                existing.getEndTime().isAfter(LocalDateTime.now().minusMinutes(5));
    }

    private void createNewStatus(Long equipmentId, int statusCode,
                                 LocalDateTime start, LocalDateTime end,
                                 String detail) {
        EquipmentStatus newStatus = new EquipmentStatus();
        newStatus.setEquipmentId(equipmentId);
        newStatus.setStatusCode(statusCode);
        newStatus.setStatusDetail(detail);
        newStatus.setStartTime(start);
        newStatus.setEndTime(end);
        newStatus.setDuration((int) Duration.between(start, end).getSeconds());
        statusMapper.insert(newStatus);
    }

    private void updateStatusDuration(EquipmentStatus status, LocalDateTime newEnd) {
        status.setEndTime(newEnd);
        status.setDuration((int) Duration.between(status.getStartTime(), newEnd).getSeconds());
        statusMapper.updateById(status);
    }

    public static int judgeStatus(List<EquipmentParameters> params) {
        boolean hasAbnormal = params.stream().anyMatch(p -> p.getIsNormal() == 0);
        return hasAbnormal ? 3 : (!params.isEmpty() ? 1 : 2);
    }

    public static String getStatusDetail(int statusCode) {
        return switch (statusCode) {
            case 1 -> "设备运行中";
            case 2 -> "无活动参数";
            case 3 -> "检测到异常参数";
            default -> "未知状态";
        };
    }
}