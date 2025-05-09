package com.autoMotiveMes.config.redis;

import com.autoMotiveMes.entity.equipment.EquipmentParameters;
import com.autoMotiveMes.entity.system.SysUser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.text.SimpleDateFormat;

/**
 * 实现功能【Redis 配置类】
 *
 * @author li.hongyu
 * @date 2025-03-04 19:26:58
 */
@Configuration
public class RedisConfig {

    // 用户缓存配置
    @Bean
    public RedisTemplate<String, SysUser> userRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, SysUser> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());

        // 配置 ObjectMapper 并注册 JavaTimeModule
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 创建 JSON 序列化器
        Jackson2JsonRedisSerializer<SysUser> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, SysUser.class);

        template.setValueSerializer(serializer);
        return template;
    }

    // 设备缓存配置
    @Bean
    public RedisTemplate<String, EquipmentParameters> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, EquipmentParameters> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Key 序列化器
        template.setKeySerializer(new StringRedisSerializer());

        // 配置 ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Value 序列化器：直接通过构造函数传递 ObjectMapper
        Jackson2JsonRedisSerializer<EquipmentParameters> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, EquipmentParameters.class);

        template.setValueSerializer(serializer);

        return template;
    }
}