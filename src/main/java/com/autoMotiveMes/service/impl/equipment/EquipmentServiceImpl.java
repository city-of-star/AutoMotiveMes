package com.autoMotiveMes.service.impl.equipment;

import com.autoMotiveMes.entity.equipment.Equipment;
import com.autoMotiveMes.entity.equipment.EquipmentParameters;
import com.autoMotiveMes.mapper.equipment.EquipmentMapper;
import com.autoMotiveMes.service.equipment.EquipmentService;
import com.autoMotiveMes.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
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

    private final EquipmentMapper equipmentMapper;
    private final RedisTemplate<String, EquipmentParameters> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String REDIS_KEY_PREFIX = CommonUtils.REDIS_KEY_PREFIX;
    private static final int DATA_EXPIRE_MINUTES = CommonUtils.DATA_EXPIRE_MINUTES;

    /**
     * 接受设备实时运行参数数据
     * 将其存入 redis 并设置 1 分钟的过期时间
     * 推送 WebSocket 消息
     */
    @Override
    public void acceptEquipmentRealTimeData(EquipmentParameters data) {
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
}