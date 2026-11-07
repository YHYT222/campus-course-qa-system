package com.campus.qa.exception;

import com.campus.qa.dto.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器：统一捕获异常并以 Result 格式返回，避免前端收到非结构化报错
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常：直接把提示语返回给前端展示 */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e) {
        return Result.fail(e.getMessage());
    }

    /** 其他未知异常：打印日志并返回统一提示 */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        e.printStackTrace();
        return Result.fail("服务器内部错误：" + e.getMessage());
    }
}
