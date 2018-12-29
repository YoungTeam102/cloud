package com.igniubi.common.exceptions;

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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 异常处理
 * <p>
 *
 * @author 徐擂
 * @version 1.0.0
 * @date 2018-12-5
 */
@ControllerAdvice
@Component
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
    public ModelAndView handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request,HttpServletResponse httpServletResponse) throws UnsupportedEncodingException {
       String errorMessage = null;
        List<ObjectError> errors = ex.getBindingResult().getAllErrors();
        if (errors.size() > 0) {
            errorMessage = ((ObjectError)errors.get(0)).getDefaultMessage();
        }
        //add header
        httpServletResponse.addHeader(IGNBGlobalExceptionHandler.HEADER_ERROR_CODE, String.valueOf(ResultEnum.BAD_REQUEST_PARAMS.getCode()));
        httpServletResponse.addHeader(IGNBGlobalExceptionHandler.HEADER_ERROR_MESSAGE, Optional.ofNullable(errorMessage).orElse(ResultEnum.BAD_REQUEST_PARAMS.getMsg()));
        httpServletResponse.setCharacterEncoding("UTF-8");
        logger.info("handleMethodArgumentNotValidException with method is {}, errorMessage is {} ", request.getRequestURI(), errorMessage);
        ModelAndView mv = IGNBGlobalExceptionHandler.getErrorJsonView(ResultEnum.BAD_REQUEST_PARAMS.getCode(), errorMessage);
        return mv;
    }



}
