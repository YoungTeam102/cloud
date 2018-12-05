package com.igniubi.common.enums;

/**
 * 公共响应码枚举
 * <p>
 *
 * @author 徐擂
 * @version 1.0.0
 * @date 2018-12-5
 */
public enum ResultEnum {

    /**
     * 公共枚举: 200:成功 201:失败
     * 公共发生的枚举控制在: 1000~1999区间
     * 其它响应码分模块, 如用户模块的为 2000~2999
     */
    OK(200, "执行成功!"),
    FAIL(201, "执行失败!"),
    SYSTEM_EXCEPTION(1000, "系统异常!"),
    LACK_REQUEST_PARAMS(1001, "缺少必要的请求参数!"),
    BAD_REQUEST_PARAMS(1002, "参数不合法!"),
    PARSE_JSON_EXCEPTION(1003, " JSON转化异常!");

    private ResultEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 响应码
     */
    private int code;
    /**
     * 响应描述
     */
    private String msg;

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public String getMsg() { return msg; }

    public void setMsg(String msg) { this.msg = msg; }
}
