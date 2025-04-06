package com.autoMotiveMes.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 实现功能【RestTemplate 全局配置】
 *
 * @author li.hongyu
 * @date 2025-03-04 19:25:37
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
