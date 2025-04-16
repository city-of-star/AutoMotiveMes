package com.autoMotiveMes.service.impl.equipment;

import com.autoMotiveMes.entity.equipment.EquipmentParameters;
import com.autoMotiveMes.service.equipment.EquipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
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

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    // Redis键格式：equipment:{equipmentId}
    private static final String REDIS_KEY_PREFIX = "equipment:";
    private static final long DATA_EXPIRE_MINUTES = 1;

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

    @Override
    public void acceptEquipmentRealTimeData(EquipmentParameters data) {
//        printData(data);
        Long equipmentId = data.getEquipmentId();
        String redisKey = REDIS_KEY_PREFIX + equipmentId;

        // 存储到Redis（使用List结构存储最新数据）
        redisTemplate.opsForList().rightPush(redisKey, data);

        // 设置双重过期策略：整体key过期 + 保留最新1分钟数据
        redisTemplate.expire(redisKey, DATA_EXPIRE_MINUTES + 1, TimeUnit.MINUTES);

        // 计算保留数量（假设每秒1条，保留60条）
        int keepSize = (int) (DATA_EXPIRE_MINUTES * 60);
        redisTemplate.opsForList().trim(redisKey, 0, keepSize);

        // 推送WebSocket消息
        messagingTemplate.convertAndSend("/topic/equipment/realtime", data);
    }

    /**
     * 定时任务：每分钟将过期数据迁移到MySQL
     */
    @Scheduled(fixedRate = 60_000)
    public void dataMigrationTask() {
        // 获取所有设备键模式
        Set<String> keys = redisTemplate.keys(REDIS_KEY_PREFIX + "*");

        if (keys != null) {
            keys.forEach(key -> {
                // 获取超时数据范围（保留最后1分钟）
                long keepAfter = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(DATA_EXPIRE_MINUTES);
                List<Object> expiredData = redisTemplate.opsForList().range(key, 0, -1);

                // 筛选需要迁移的数据
                assert expiredData != null;
                expiredData.removeIf(entry -> {
                    EquipmentParameters data = (EquipmentParameters) entry;
                    long timestamp = data.getCollectTime().toInstant(ZoneOffset.UTC).toEpochMilli();
                    return timestamp > keepAfter;
                });

                if (!expiredData.isEmpty()) {
//                    saveToMySQL(expiredData);      // 保存到数据库
                    redisTemplate.opsForList().trim(key, expiredData.size(), -1); // 删除已迁移数据
                }
            });
        }
    }
}