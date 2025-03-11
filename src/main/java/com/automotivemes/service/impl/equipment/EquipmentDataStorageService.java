package com.automotivemes.service.impl.equipment;

import com.automotivemes.entity.equipment.EquipmentRealtimeData;
import com.automotivemes.mapper.equipment.EquipmentRealtimeDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EquipmentDataStorageService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final EquipmentRealtimeDataMapper equipmentRealtimeDataMapper;
    private final SimpMessagingTemplate messagingTemplate;

    // Redis键格式：equipment:realtime:{equipmentId}
    private static final String REDIS_KEY_PREFIX = "equipment:realtime:";
    private static final long DATA_EXPIRE_MINUTES = 1;

    /**
     * 实现你要求的处理方法
     * @param data 包含设备ID、状态、温度、转速等参数
     */
    public void realTimeDataSimulate(Map<String, String> data) {
        String equipmentId = data.get("equipmentId");
        String redisKey = REDIS_KEY_PREFIX + equipmentId;

        // 存储到Redis（使用List结构存储最新数据）
        redisTemplate.opsForList().rightPush(redisKey, data);

        // 设置双重过期策略：整体key过期 + 保留最新1分钟数据
        redisTemplate.expire(redisKey, DATA_EXPIRE_MINUTES + 1, TimeUnit.MINUTES);
        trimOldData(redisKey);

        // 推送WebSocket消息
        messagingTemplate.convertAndSend("/topic/equipment/realtime", data);
    }

    /**
     * 数据裁剪（保留最近N条）
     */
    private void trimOldData(String redisKey) {
        // 计算保留数量（假设每秒1条，保留60条）
        int keepSize = (int) (DATA_EXPIRE_MINUTES * 60);
        redisTemplate.opsForList().trim(redisKey, 0, keepSize);
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
                    Map<String,String> data = (Map<String,String>) entry;
                    long timestamp = Long.parseLong(data.get("timestamp"));
                    return timestamp > keepAfter;
                });

                if (!expiredData.isEmpty()) {
                    saveToMySQL(expiredData);      // 保存到数据库
                    redisTemplate.opsForList().trim(key, expiredData.size(), -1); // 删除已迁移数据
                }
            });
        }
    }

    /**
     * 批量存储到MySQL
     */
    private void saveToMySQL(List<Object> dataList) {
            for (Object dataObj : dataList) {
                EquipmentRealtimeData equipmentRealtimeData = getEquipmentRealtimeData((Map<String, String>) dataObj);
                equipmentRealtimeDataMapper.insert(equipmentRealtimeData);
            }
    }

    private static EquipmentRealtimeData getEquipmentRealtimeData(Map<String, String> dataObj) {
        Map<String,String> data = dataObj;
        EquipmentRealtimeData equipmentRealtimeData = new EquipmentRealtimeData();
        equipmentRealtimeData.setEquipmentId(Integer.parseInt(data.get("equipmentId")));
        equipmentRealtimeData.setTimestamp(new Date(Long.parseLong(data.get("timestamp"))));
        equipmentRealtimeData.setStatus(data.get("status"));
        equipmentRealtimeData.setTemperature(Double.parseDouble(data.get("temperature")));
        equipmentRealtimeData.setRpm(Integer.parseInt(data.get("rpm")));
        return equipmentRealtimeData;
    }
}
