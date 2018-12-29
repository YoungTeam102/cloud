package com.igniubi.common.exceptions;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.igniubi.model.enums.common.ResultEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * 服务的全局异常处理
 * 1. 返回200，并且用json作为消息体，并且设置消息头（方便resttemplate处理）
 * 2. 返回500，设置消息头
 */
@Component
public class IGNBGlobalExceptionHandler implements HandlerExceptionResolver {
    private final Logger logger = LoggerFactory.getLogger(getClass());



    //如果服务异常，需要把异常信息加入到头中
    public static String HEADER_ERROR_CODE = "x-service-error-code";
    public static String HEADER_ERROR_MESSAGE = "x-service-error-message";



    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception exception) {


        //publish event

//        if(exception instanceof MethodArgumentNotValidException){
//            logger.error("method argument validate error at {}", httpServletRequest.getRequestURI());
//            logger.error("method argument validate error, e is  {}", exception);
//            ModelAndView mv = getErrorJsonView(400, exception.getMessage());
//            httpServletResponse.addHeader(HEADER_ERROR_CODE, String.valueOf(400));
//            httpServletResponse.addHeader(HEADER_ERROR_MESSAGE, exception.getMessage());
//            return mv;
//        }

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



    /**
     * 使用FastJson提供的FastJsonJsonView视图返回，不需要捕获异常
     */
    public static ModelAndView getErrorJsonView(int code, String message) {
        ModelAndView modelAndView = new ModelAndView();
        FastJsonJsonView jsonView = new FastJsonJsonView();
        Map<String, Object> errorInfoMap = new HashMap<>();
        errorInfoMap.put("code", code);
        errorInfoMap.put("message", message);
        jsonView.setAttributesMap(errorInfoMap);
        modelAndView.setView(jsonView);
        return modelAndView;
    }

    public static void addHeadWithISO( HttpServletResponse httpServletResponse, String message) {
        try {
            httpServletResponse.addHeader(HEADER_ERROR_MESSAGE, new String(
                    message.getBytes("UTF-8"),"ISO8859-1"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }



}
