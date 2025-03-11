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

    // 成功删除设备
    @AfterReturning(
            pointcut = "execution(* com.automotivemes.service.equipment.EquipmentService.deleteEquipmentById(..)) && args(id)",
            argNames = "id"
    )
    public void afterDeleteEquipmentById(Integer id) {
        try {
            String operator = SecurityUtils.maskUsername(SecurityUtils.getCurrentUser());  // 用户名脱敏
            String clientIp = SecurityUtils.getClientIp();

            logger.info("设备删除成功 | 操作员:{} | IP:{} | ID:{}",
                    operator,
                    clientIp,
                    id);
        } catch (Exception e) {
            logger.error("设备日志记录失败: {}", e.getMessage());
        }
    }

    // 成功更新设备的状态
    @AfterReturning(
            pointcut = "execution(* com.automotivemes.service.equipment.EquipmentService.updateEquipmentStatus(..)) && args(id, status)",
            argNames = "id, status"
    )
    public void afterUpdateEquipmentStatus(Integer id, String status) {
        try {
            String operator = SecurityUtils.maskUsername(SecurityUtils.getCurrentUser());  // 用户名脱敏
            String clientIp = SecurityUtils.getClientIp();

            logger.info("更新设备状态成功 | 操作员:{} | IP:{} | ID:{} | Status:{}",
                    operator,
                    clientIp,
                    id,
                    status);
        } catch (Exception e) {
            logger.error("设备日志记录失败: {}", e.getMessage());
        }
    }
}
