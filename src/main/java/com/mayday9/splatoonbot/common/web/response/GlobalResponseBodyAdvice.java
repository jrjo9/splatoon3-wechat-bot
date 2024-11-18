package com.mayday9.splatoonbot.common.web.response;

import com.mayday9.splatoonbot.common.web.DefaultController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

//统一controller输出格式，和抛出的异常
@RestControllerAdvice
public class GlobalResponseBodyAdvice extends DefaultController implements ResponseBodyAdvice<Object> {

    private static final Logger logger = LoggerFactory.getLogger(GlobalResponseBodyAdvice.class);

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return !ApiResult.class.isAssignableFrom(methodParameter.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        String path = request.getURI().getPath();
        // 跟swagger冲突，跳出response包装
        if (path.contains("swagger") || path.contains("api-docs") || path.contains("/monitor/url-mappings") || path.contains("/Call")) {
            return body;
        }

        //springBootAdmin获取服务信息
        if (path.startsWith("/actuator")) {
            return body;
        }
        if (returnType.getMethod().getAnnotation(RawResponse.class) != null) {
            if (returnType.getParameterType().isAssignableFrom(String.class)) {
                HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
                servletResponse.setContentType("text/html; charset=UTF-8");
                try (ServletOutputStream outputStream = servletResponse.getOutputStream()) {
                    outputStream.write(body.toString().getBytes());
                    outputStream.flush();
                } catch (Exception ex) {

                }
            }
            return body;
        }
        return ApiResult.success(body);
    }

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public Object defaultErrorHandler(HttpServletRequest req, Exception ex) {
        if (ex instanceof ApiException) {
            //自定义错误不记录error和统计exception
            logger.info("{} 错误 ,code:{}, message:{}", req.getRequestURL(), ((ApiException) ex).getCode(), ex.getMessage());
        } else if (ex instanceof NoHandlerFoundException) {
            //404
            logger.info("{} 错误 ,code:{}, message:{}", req.getRequestURL(), 404, ex.getMessage());
        } else {
            logger.error(req.getRequestURL() + "发生异常", ex);
//            String requestUr1 = RequestLoggerManager.get().getRequestUr1();
//            String logFilePath = RequestLoggerManager.get().getLogFilePath();
//            if (!StringUtils.isEmpty(requestUr1)) {
//                RequestLogManager.getInstance().addExceptionTrace(requestUr1, logFilePath, ex);
//            }
        }
        return ApiResult.exception(ex);
    }

    @ResponseBody
    @ExceptionHandler(value = {BindException.class, MethodArgumentNotValidException.class})
    public Object bindError(HttpServletRequest req, Exception ex) {
        String message = ex.getMessage();
        if (ex instanceof BindException) {
            message = buildValidationErrorMessage(((BindException) ex).getBindingResult());
        } else if (ex instanceof MethodArgumentNotValidException) {
            message = buildValidationErrorMessage(((MethodArgumentNotValidException) ex).getBindingResult());
        }
        logger.info("{} 错误 ,code:{}, message:{}", req.getRequestURL(), 500, message);
        return ApiResult.exMessage(ex, message);
    }


    private String buildValidationErrorMessage(BindingResult bindingResult) {
        Locale locale = LocaleContextHolder.getLocale();
        StringBuilder builder = new StringBuilder(120);
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append(messages.getMessage(fieldError, locale)).append('\n');
//            builder.append(fieldError.getObjectName())
//                .append('.')
//                .append(fieldError.getField())
//                .append(" => ")
//                .append(messages.getMessage(fieldError, locale))
//                .append(", rejectedValue=")
//                .append(fieldError.getRejectedValue())
//                .append('\n');
        }
        return builder.toString();
    }
}
