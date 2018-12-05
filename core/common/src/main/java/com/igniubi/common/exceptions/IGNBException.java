package com.igniubi.common.exceptions;


/**
 * 异常公共类
 * <p>
 *
 * @author 徐擂
 * @version 1.0.0
 * @date 2018-12-5
 */
public class IGNBException extends Exception{

    public IGNBException(String message) {
        super(message);
    }

    public IGNBException(String message, Throwable cause) {
        super(message, cause);
    }
}
