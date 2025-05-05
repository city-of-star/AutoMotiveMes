package com.autoMotiveMes.service.impl.business;

import com.autoMotiveMes.common.constant.CommonConstant;
import com.autoMotiveMes.entity.equipment.Equipment;
import com.autoMotiveMes.entity.equipment.EquipmentParameters;
import com.autoMotiveMes.mapper.equipment.EquipmentMapper;
import com.autoMotiveMes.service.business.EquipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 实现功能【设备服务实现类】
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

    /**
     * 接受设备实时运行参数数据
     * 将其存入 redis 并设置 5 分钟的过期时间
     * 推送 WebSocket 消息
     */
    @Override
    public void acceptEquipmentRealTimeData(EquipmentParameters data) {
        Long equipmentId = data.getEquipmentId();
        String redisKey = CommonConstant.REDIS_KEY_PREFIX + equipmentId;
        int keepSize = CommonConstant.DATA_EXPIRE_MINUTES * 5 * 60;

        // 1. 将数据插入到列表头部（左侧）
        redisTemplate.opsForList().leftPush(redisKey, data);

        // 2. 修剪列表，仅保留最新的 keepSize 条数据
        redisTemplate.opsForList().trim(redisKey, 0, keepSize - 1);

        // 3. 设置键的过期时间（每次插入都续期）
        redisTemplate.expire(redisKey, CommonConstant.DATA_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 4. 推送WebSocket消息
        messagingTemplate.convertAndSend("/topic/equipment/realtime", data);
    }

    @Override
    public List<Equipment> listEquipment() {
        return equipmentMapper.selectList(null);
    }
}