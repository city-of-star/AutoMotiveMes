package com.automotivemes.common.exception;

import lombok.Getter;

@Getter
public class EquipmentException extends RuntimeException{
    private final ExceptionTypeEnum exceptionTypeEnum;

    public EquipmentException(String message, ExceptionTypeEnum exceptionTypeEnum) {
        super(message);
        this.exceptionTypeEnum = exceptionTypeEnum;
    }

}
