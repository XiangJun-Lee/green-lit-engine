package com.keji.green.lit.engine.exception;

/**
 * 业务异常类
 * 用于表示业务逻辑处理过程中的异常情况
 * 这些异常通常由用户输入错误或业务规则限制导致
 */
public class BusinessException extends RuntimeException {
    
    /**
     * 创建业务异常
     * 
     * @param message 异常信息
     */
    public BusinessException(String message) {
        super(message);
    }
    
    /**
     * 创建业务异常
     * 
     * @param message 异常信息
     * @param cause 原始异常
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
} 