package com.mayday9.splatoonbot.common.web.response;

//异常代码每句
public enum ExceptionCode {

    ParamIllegal(100001, "参数不合法"),

    IpRequestExceedLimit(100002, "当前ip请求超出限制"),

    TokenError(100003, "token不合法或已过期"),

    InternalError(100004, "内部错误"),

    FailConfirm(100005, "失败并确认"),

    Unregistered(100006, "未注册"),

    Voidance(100007, "许可失效"),

    TimeError(100008, "主机时间错误"),
    ;


    ExceptionCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
