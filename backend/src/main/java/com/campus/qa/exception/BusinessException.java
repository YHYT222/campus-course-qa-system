package com.campus.qa.exception;

/**
 * 自定义业务异常：用于抛出可预期的业务错误（如参数校验失败、越权操作等）
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
