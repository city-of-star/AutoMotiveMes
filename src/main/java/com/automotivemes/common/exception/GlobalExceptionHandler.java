package com.automotivemes.common.exception;

import com.automotivemes.common.response.CommonResponse;
import com.automotivemes.common.response.ResponseUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.automotivemes.common.log.GlobalLoggingAspect.logger;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<CommonResponse<Object>> handleAuthException(AuthException e) {
        return ResponseUtils.badRequest(e.getMessage());
    }

    @ExceptionHandler(EquipmentException.class)
    public ResponseEntity<CommonResponse<Object>> handleEquipmentException(EquipmentException e) {
        // 根据异常类型动态调整日志级别
        if (e.getMessage().contains("冲突") || e.getMessage().contains("已存在")) {
            logger.warn("设备业务规则冲突: {}", e.getMessage());
        } else {
            logger.error("设备操作异常: {}", e.getMessage(), e); // 记录堆栈
        }
        return ResponseUtils.badRequest(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonResponse<Object>> handleRuntimeException(RuntimeException e) {
        return ResponseUtils.internalServerError("系统内部错误：" + e.getMessage());
    }
}