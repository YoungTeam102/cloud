package com.igniubi.common.exceptions;


import com.igniubi.model.enums.common.ResultEnum;

/**
 * 异常公共类
 * <p>
 *
 * @author 徐擂
 * @version 1.0.0
 * @date 2018-12-5
 */
public class IGNBException extends RuntimeException {

    private final int code;

    private final String message;

    private final ResultEnum resultEnum;

    public IGNBException(int code, String message) {
        this.code = code;
        this.message = message;
        this.resultEnum = ResultEnum.getResultByCode(code);
    }

    public IGNBException(ResultEnum resultEnum) {
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMsg();
        this.resultEnum = resultEnum;
    }

    public IGNBException(ResultEnum resultEnum, Throwable cause) {
        super(resultEnum.getMsg(), cause);
        this.code = resultEnum.getCode();
        this.message = resultEnum.getMsg();
        this.resultEnum = resultEnum;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public ResultEnum getResultEnum() {
        return resultEnum;
    }
}
