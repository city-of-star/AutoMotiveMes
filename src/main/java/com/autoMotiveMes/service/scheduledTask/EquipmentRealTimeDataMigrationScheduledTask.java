package com.autoMotiveMes.service.scheduledTask;

import com.autoMotiveMes.common.constant.CommonConstant;
import com.autoMotiveMes.entity.equipment.EquipmentParameters;
import com.autoMotiveMes.mapper.equipment.EquipmentParametersMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 实现功能【设备实时参数数据迁移定时任务服务】
 *
 * @author li.hongyu
 * @date 2025-04-17 16:43:26
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class EquipmentRealTimeDataMigrationScheduledTask {

    private final EquipmentParametersMapper equipmentParametersMapper;
    private final RedisTemplate<String, EquipmentParameters> redisTemplate;

    /**
     * 定时任务：每 3 分钟将过期数据迁移到 equipment_parameters 表
     */
    @Scheduled(fixedRate = 180_000)
    public void dataMigrationTask() {
        long startTime = System.currentTimeMillis();
        log.info("过期数据迁移任务--开始");

        Set<String> keys = redisTemplate.keys(CommonConstant.REDIS_KEY_PREFIX + "*");

        if (keys.isEmpty()) {
            log.info("未找到需要保存的设备实时参数数据");
            return;
        }

        keys.forEach(key -> {
            try {
                List<EquipmentParameters> allData = redisTemplate.opsForList().range(key, 0, -1);
                if (allData == null || allData.isEmpty()) {
                    log.info("{} 无数据，跳过处理", key);
                    return;
                }

                LocalDateTime currentTime = LocalDateTime.now();

                List<EquipmentParameters> expiredData = allData.stream()
                        .filter(entry -> entry.getCollectTime() != null)
                        .filter(entry ->
                                currentTime.isAfter(entry.getCollectTime().
                                        plusMinutes(CommonConstant.DATA_EXPIRE_MINUTES)))
                        .toList();

                if (expiredData.isEmpty()) {
                    log.info("{} 暂无过期数据", key);
                } else {
                    equipmentParametersMapper.insertBatch(expiredData);  // 保存到数据库

                    int remainingSize = allData.size() - expiredData.size();
                    if (remainingSize > 0) {
                        redisTemplate.opsForList().trim(key, -remainingSize, -1);
                    } else {
                        redisTemplate.delete(key);
                    }
                    log.info("{} 成功: {迁移 {} 条数据，保留 {} 条新数据}", key, expiredData.size(), remainingSize);
                }
            } catch (Exception e) {
                log.error("处理 {} 异常: ", key, e);
            }
        });
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        log.info("过期数据迁移任务--成功，耗时: {} 毫秒", elapsedTime);
    }
}