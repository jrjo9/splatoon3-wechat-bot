package com.mayday9.splatoonbot.common.web.response;

public class ApiException extends RuntimeException {

    private Integer code;

    private Object result;

    public ApiException(Integer code) {
        this.code = code;
    }

    public ApiException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ApiException(Integer code, String message, Object result) {
        super(message);
        this.code = code;
        this.result = result;
    }

    public ApiException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.code = exceptionCode.getCode();
    }

    public Integer getCode() {
        return code;
    }

    public Object getResult() {
        return result;
    }
}
