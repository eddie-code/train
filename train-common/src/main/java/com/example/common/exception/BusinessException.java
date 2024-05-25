package com.example.common.exception;

public class BusinessException extends RuntimeException {

    private BusinessExceptionEnum businessExceptionEnum;

    public BusinessException(BusinessExceptionEnum businessExceptionEnum) {
        this.businessExceptionEnum = businessExceptionEnum;
    }

    public BusinessExceptionEnum getBusinessExceptionEnum() {
        return businessExceptionEnum;
    }

    public void setBusinessExceptionEnum(BusinessExceptionEnum businessExceptionEnum) {
        this.businessExceptionEnum = businessExceptionEnum;
    }

    /**
     * 不写入堆栈信息，提高性能
     * 业务异常就不打印, 仍然会打印系统异常
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
