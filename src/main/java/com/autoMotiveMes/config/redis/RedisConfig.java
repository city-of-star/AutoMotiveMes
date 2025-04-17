package com.autoMotiveMes.config.redis;

import com.autoMotiveMes.entity.equipment.EquipmentParameters;
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