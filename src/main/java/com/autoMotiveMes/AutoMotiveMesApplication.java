package com.autoMotiveMes;

import com.autoMotiveMes.service.simulationService.EquipmentRealTimeDataSimulatorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity() // 以启用方法级安全控制
@EnableScheduling  // 用于开启定时任务功能
@EnableAsync
public class AutoMotiveMesApplication {
    public static void main(String[] args) {
        SpringApplication.run(AutoMotiveMesApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(EquipmentRealTimeDataSimulatorService simulatorService) {
        return args -> {
            simulatorService.startAllSimulators();
        };
    }
}
