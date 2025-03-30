package com.automotivemes.common.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalLogger {

    public static Logger getLogger() {
        Class<?> clazz = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .getCallerClass();
        return LoggerFactory.getLogger(clazz);
    }

    // 基础日志方法
    public static void info(String message) {
        getLogger().info(message);
    }

    public static void error(String message) {
        getLogger().error(message);
    }

    public static void debug(String message) {
        getLogger().debug(message);
    }

    public static void warn(String message) {
        getLogger().warn(message);
    }
}
