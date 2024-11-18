package com.mayday9.splatoonbot.common.web.response;

import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

//api 统一返回类型
public class ApiResult {

    private Integer code;
    private String message;
    private Object result;

    public static ApiResult success() {
        ApiResult apiResult = new ApiResult();
        apiResult.setCode(0);
        apiResult.setMessage("success");
        apiResult.setResult(null);
        return apiResult;
    }

    public static ApiResult success(Object result) {
        ApiResult apiResult = new ApiResult();
        apiResult.setCode(0);
        apiResult.setMessage("success");
        apiResult.setResult(result);
        return apiResult;
    }

    public static ApiResult exception(Exception ex) {
        ApiResult apiResult = new ApiResult();
        if (ex instanceof ApiException) {
            apiResult.setCode(((ApiException) ex).getCode());
            apiResult.setMessage(ex.getMessage());
            apiResult.setResult(((ApiException) ex).getResult());
        } else if (ex instanceof NoHandlerFoundException) {
            apiResult.setCode(404);
            apiResult.setMessage(ex.getMessage());
        } else if (ex instanceof MaxUploadSizeExceededException) {
            apiResult.setCode(500);
            apiResult.setMessage(" 上传附件过大 !");
        } else if (ex instanceof InvocationTargetException) {
            apiTargetException(apiResult, ((InvocationTargetException) ex).getTargetException());
        } else {
            apiResult.setCode(500);
            if (ex instanceof NullPointerException) {
                apiResult.setMessage("NullPointerException");
            } else if (isSQLException(ex)) {
                apiResult.setMessage("SQLException");
            } else {
                String message = ex.getMessage();
                if (message != null && message.length() > 500) {
                    message = message.substring(0, 500) + "...";
                }
                apiResult.setMessage(message);
            }
        }
//        apiResult.setResult(null);
        return apiResult;
    }

    private static void apiTargetException(ApiResult apiResult, Throwable ex) {
        if (ex instanceof ApiException) {
            apiResult.setCode(((ApiException) ex).getCode());
            apiResult.setMessage(ex.getMessage());
        }
    }

    public static ApiResult exMessage(Exception ex, String message) {
        ApiResult apiResult = new ApiResult();
        if (ex instanceof BindException) {
            apiResult.setCode(ExceptionCode.ParamIllegal.getCode());
            apiResult.setMessage(message);
        } else if (ex instanceof MethodArgumentNotValidException) {
            apiResult.setCode(ExceptionCode.ParamIllegal.getCode());
            apiResult.setMessage(message);
        }
        apiResult.setResult(null);
        return apiResult;
    }

    //是否数据库异常
    private static boolean isSQLException(Throwable ex) {
        if (ex instanceof SQLException) {
            return true;
        }
        if (ex.getCause() != null) {
            return isSQLException(ex.getCause());
        }
        return false;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
