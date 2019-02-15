package com.igniubi.common.exceptions;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.igniubi.model.enums.common.ResultEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * 服务的全局异常处理
 * 1. 返回200，并且用json作为消息体，并且设置消息头（方便resttemplate处理）
 * 2. 返回500，设置消息头
 *  服务的全局异常处理（webclient）
 *  1. ignbexception 返回500，设置消息头(x-service-error-code). webclient 处理http500时，先判断是否有该消息头
 *  2. 非ignbexception，不设置头
 *  3. 验参异常，同1
 */
@Component
@ControllerAdvice
public class IGNBGlobalExceptionHandler implements HandlerExceptionResolver {
    private final Logger logger = LoggerFactory.getLogger(getClass());



    //如果服务异常，需要把异常信息加入到头中
    public static String HEADER_ERROR_CODE = "x-service-error-code";
    public static String HEADER_ERROR_MESSAGE = "x-service-error-message";



    @Override
    public ModelAndView resolveException(@NonNull HttpServletRequest httpServletRequest, @NonNull HttpServletResponse httpServletResponse, Object handler, @NonNull Exception exception) {


        //如果不是服务异常，直接返回500，并且打印异常
        if (!(exception instanceof IGNBException)) {
            logger.error("unknown exception happened at:{}", httpServletRequest.getRequestURI());
            logger.error("unknown exception is ", exception);
            httpServletResponse.setStatus(500);
            addHeadWithISO(httpServletResponse,exception.getMessage() );

            return new ModelAndView();
        }
        //处理服务异常。 需要添加异常信息到头中，并且返回json
        IGNBException se =  (IGNBException) exception;
        int code = se.getCode();
        String message = se.getMessage();
        //add header
        httpServletResponse.addHeader(HEADER_ERROR_CODE, String.valueOf(code));
        httpServletResponse.setStatus(500);
        addHeadWithISO(httpServletResponse, message);

        //如果是服务不可用，直接返回500，并且打印异常
        if ( code == ResultEnum.SERVICE_NOT_AVAILABLE.getCode()) {
            logger.error("service not available exception happened at:{}", httpServletRequest.getRequestURI());
            httpServletResponse.setStatus(500);
            addHeadWithISO(httpServletResponse, message);
            return new ModelAndView();
        }
        ModelAndView mv = getErrorJsonView(code, message);
        return mv;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MissingServletRequestParameterException.class})
    @ResponseBody
    public ModelAndView handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request,HttpServletResponse httpServletResponse) throws UnsupportedEncodingException {
        String errorMessage = null;
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        if (errors.size() > 0) {
            errorMessage = ((ObjectError)errors.get(0)).getDefaultMessage();
        }
        //add header
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        httpServletResponse.addHeader(IGNBGlobalExceptionHandler.HEADER_ERROR_CODE, String.valueOf(ResultEnum.BAD_REQUEST_PARAMS.getCode()));
        httpServletResponse.setStatus(500);
        addHeadWithISO(httpServletResponse, Optional.ofNullable(errorMessage).orElse(ResultEnum.BAD_REQUEST_PARAMS.getMsg()));
        logger.info("handleMethodArgumentNotValidException with method is {}, errorMessage is {} ", request.getRequestURI(), errorMessage);
        ModelAndView mv = getErrorJsonView(ResultEnum.BAD_REQUEST_PARAMS.getCode(), errorMessage);
        return mv;
    }

    /**
     * 使用FastJson提供的FastJsonJsonView视图返回，不需要捕获异常
     */
    private static ModelAndView getErrorJsonView(int code, String message) {
        ModelAndView modelAndView = new ModelAndView();
        FastJsonJsonView jsonView = new FastJsonJsonView();
        Map<String, Object> errorInfoMap = new HashMap<>();
        errorInfoMap.put("code", code);
        errorInfoMap.put("message", message);
        jsonView.setAttributesMap(errorInfoMap);
        modelAndView.setView(jsonView);
        return modelAndView;
    }

    private static void addHeadWithISO(HttpServletResponse httpServletResponse, String message) {
        try {
            String msg = Optional.ofNullable(message).orElse("");
            httpServletResponse.addHeader(HEADER_ERROR_MESSAGE, new String(
                    msg.getBytes("UTF-8"),"ISO8859-1"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }



}
