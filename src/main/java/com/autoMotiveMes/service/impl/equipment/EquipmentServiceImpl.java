package com.autoMotiveMes.service.impl.equipment;

import com.autoMotiveMes.entity.equipment.Equipment;
import com.autoMotiveMes.entity.equipment.EquipmentParameters;
import com.autoMotiveMes.mapper.equipment.EquipmentMapper;
import com.autoMotiveMes.mapper.equipment.EquipmentParametersMapper;
import com.autoMotiveMes.mapper.equipment.EquipmentStatusMapper;
import com.autoMotiveMes.service.equipment.EquipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 实现功能【设备实现类】
 *
 * @author hu.hongdou
 * @date 2025-04-16 16:39:45
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentStatusMapper equipmentStatusMapper;
    private final EquipmentParametersMapper equipmentParametersMapper;
    private final EquipmentMapper equipmentMapper;
    private final RedisTemplate<String, EquipmentParameters> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String REDIS_KEY_PREFIX = "equipment:";
    private static final int DATA_EXPIRE_MINUTES = 2;

    private void printData(EquipmentParameters data){
        synchronized (this) {
            System.out.println("设备 " + data.getEquipmentId() + " 数据：");
            System.out.println("参数名称: " + data.getParamName());
            System.out.println("参数数值: " + data.getParamValue());
            System.out.println("计量单位: " + data.getUnit());
            System.out.println("采集时间: " + data.getCollectTime());
            System.out.println("是否正常: " + data.getIsNormal());
            System.out.println();
        }
    }

    /**
     * 接受设备实时运行参数数据
     * 将其存入 redis 并设置 1 分钟的过期时间
     * 推送 WebSocket 消息
     */
    @Override
    public void acceptEquipmentRealTimeData(EquipmentParameters data) {
        // 添加调试日志
        log.debug("接收数据: equipmentId={}, collectTime={} (UTC时间: {})",
                data.getEquipmentId(),
                data.getCollectTime(),
                data.getCollectTime().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC));

        Long equipmentId = data.getEquipmentId();
        String redisKey = REDIS_KEY_PREFIX + equipmentId;
        int keepSize = DATA_EXPIRE_MINUTES * 60; // 保留最新120条数据

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

    /**
     * 定时任务：每分钟将过期数据迁移到 equipment_parameters 表
     */
    @Scheduled(fixedRate = 60_000)
    public void dataMigrationTask() {
        long startTime = System.currentTimeMillis();
        log.info("过期数据迁移任务--开始");

        Set<String> keys = redisTemplate.keys(REDIS_KEY_PREFIX + "*");

        if (keys == null || keys.isEmpty()) {
            log.info("未找到需要保存的设备实时参数数据");
            return;
        }

        keys.forEach(key -> {
            try {
                long cutoffTime = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(DATA_EXPIRE_MINUTES);

                List<EquipmentParameters> allData = redisTemplate.opsForList().range(key, 0, -1);
                if (allData == null || allData.isEmpty()) {
                    log.info("{} 无数据，跳过处理", key);
                    return;
                }

                List<EquipmentParameters> expiredData = allData.stream()
                        .filter(entry -> entry.getCollectTime() != null)
                        .filter(entry -> {
                            long timestamp = entry.getCollectTime()
                                    .atZone(ZoneId.systemDefault())
                                    .toInstant()
                                    .toEpochMilli();
                            return timestamp > cutoffTime;
                        })
                        .toList();

                if (!expiredData.isEmpty()) {
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