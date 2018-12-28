package com.igniubi.common.exceptions;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.igniubi.model.dtos.common.ResultDTO;
import com.igniubi.model.enums.common.ResultEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 异常处理
 * <p>
 *
 * @author 徐擂
 * @version 1.0.0
 * @date 2018-12-5
 */
@ControllerAdvice
public class IGNBExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(IGNBExceptionHandler.class);

    /**
     * 校验异常处理
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, MissingServletRequestParameterException.class})
    @ResponseBody
    public ResultDTO handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = "请求参数错误！";
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        if (errors.size() > 0) {
            errorMessage = ((ObjectError)errors.get(0)).getDefaultMessage();
        }
        ResultDTO baseResult = new ResultDTO();
        baseResult.setCode(ResultEnum.BAD_REQUEST_PARAMS.getCode());
        baseResult.setMessage(errorMessage);
        return baseResult;
    }



}
