package com.igniubi.mybatis.service;

import com.igniubi.mybatis.entity.BaseEntity;

import java.io.Serializable;

/**
 * 类说明
 * <p>
 *
 * @author 徐擂
 * @version 1.0.0
 * @date 2018-12-6
 */
public interface BaseService<PK extends Serializable, E extends BaseEntity> {

    E get(PK var1);

    int save(E var1);

    int update(E var1);

    int remove(PK var1);
}
