package com.keji.green.lit.engine.exception;

import lombok.Getter;

/**
 * 业务异常类
 * 用于表示业务逻辑处理过程中的异常情况
 * 这些异常通常由用户输入错误或业务规则限制导致
 * @author xiangjun_lee
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * -- GETTER --
     *  获取错误码
     *
     * @return 错误码
     */
    private final int code;
    
    /**
     * 创建业务异常
     * 
     * @param errorCode 错误码枚举
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
    
    /**
     * 创建业务异常
     * 
     * @param errorCode 错误码枚举
     * @param message 自定义错误信息
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
    
    /**
     * 创建业务异常
     * 
     * @param errorCode 错误码枚举
     * @param cause 原始异常
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
    }
    
    /**
     * 创建业务异常
     * 
     * @param errorCode 错误码枚举
     * @param message 自定义错误信息
     * @param cause 原始异常
     */
    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s", code, getMessage());
    }
} 