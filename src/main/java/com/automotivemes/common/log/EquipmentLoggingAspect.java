package com.automotivemes.common.log;

import com.automotivemes.entity.equipment.Equipment;
import com.automotivemes.utils.SecurityUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class EquipmentLoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(EquipmentLoggingAspect.class);

    // 成功添加设备
    @AfterReturning(
            pointcut = "execution(* com.automotivemes.service.equipment.EquipmentService.addEquipment(..))",
            returning = "result"
    )
    public void afterAddEquipment(Equipment result) {
        try {
            String operator = SecurityUtils.maskUsername(SecurityUtils.getCurrentUser());  // 用户名脱敏
            String clientIp = SecurityUtils.getClientIp();

            logger.info("设备添加成功 | 操作员:{} | IP:{} | ID:{} | 名称:{} | 类型:{}",
                    operator,
                    clientIp,
                    result.getEquipmentId(),
                    result.getEquipmentName(),
                    result.getType());
        } catch (Exception e) {
            logger.error("设备日志记录失败: {}", e.getMessage());
        }
    }
}
