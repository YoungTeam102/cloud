package com.igniubi.common.dtos;

import com.igniubi.common.enums.ResultEnum;

/**
 * 公共返回响应体
 * <p>
 *
 * @author 徐擂
 * @version 1.0.0
 * @date 2018-12-5
 */
public class ResultDTO<T> extends BaseDTO{

    /**
     * 状态码
     */
    protected Integer code;

    /**
     * 响应描述
     */
    protected String message;

    /**
     * 时间戳
     */
    protected Long timestamp;

    /**
     * 返回数据
     */
    protected T data;

    public ResultDTO() {
        this.code = ResultEnum.OK.getCode();
        this.message = ResultEnum.OK.getMsg();
        this.timestamp = System.currentTimeMillis();
    }

    public ResultDTO(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    @Override
    public String toString() {
        return "响应结果【" + this.code + "】,:" + this.message;
    }

    public Integer getCode() { return code; }

    public void setCode(Integer code) { this.code = code; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public Long getTimestamp() { return timestamp; }

    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }

    public T getData() { return data; }

    public void setData(T data) { this.data = data; }
}
