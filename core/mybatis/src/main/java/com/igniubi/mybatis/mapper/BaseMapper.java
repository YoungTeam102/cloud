package com.igniubi.mybatis.mapper;

import java.io.Serializable;

/**
 * 公共mapper
 * <p>
 *
 * @author 徐擂
 * @version 1.0.0
 * @date 2018-12-6
 */
public interface BaseMapper<PK, E> {

    int save(E var1);

    int update(E var1);

    int remove(PK var1);

    E get(PK var1);

}
