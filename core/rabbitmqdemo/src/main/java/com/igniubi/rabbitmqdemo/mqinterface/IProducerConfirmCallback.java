package com.igniubi.rabbitmqdemo.mqinterface;

/**
 * Created by xueyin on 2018/5/22.
 * 发送消息时的confirm回调接口
 * 配置方法：
 * ...other config
 *  confirm: true
 *  comfirmcallback: com.yoho.xxx.MsgConfirmCallback
 * ...other config
 */
public interface IProducerConfirmCallback {

    /**
     * @param msgId
     * @param ack
     * @param cause
     */
    void confirm(String msgId, boolean ack, String cause);
}
