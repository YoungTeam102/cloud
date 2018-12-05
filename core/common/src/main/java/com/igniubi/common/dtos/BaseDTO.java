package com.igniubi.common.dtos;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.Serializable;

/**
 * 公共体
 * <p>
 *
 * @author 徐擂
 * @version 1.0.0
 * @date 2018-12-5
 */
public class BaseDTO implements Serializable {

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }
}
