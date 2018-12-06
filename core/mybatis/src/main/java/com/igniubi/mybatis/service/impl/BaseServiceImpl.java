package com.igniubi.mybatis.service.impl;

import com.igniubi.mybatis.entity.BaseEntity;
import com.igniubi.mybatis.mapper.BaseMapper;
import com.igniubi.mybatis.service.BaseService;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * 类说明
 * <p>
 *
 * @author 徐擂
 * @version 1.0.0
 * @date 2018-12-6
 */
@Repository("baseService")
public abstract class BaseServiceImpl<PK extends Serializable, E extends BaseEntity> implements BaseService<PK, E> {
    private BaseMapper<PK, E> baseMapper;

    public BaseServiceImpl() {
    }

    public void setBaseMapper(BaseMapper<PK, E> baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public int save(E entity) { return this.baseMapper.save(entity); }

    @Override
    public int update(E entity) { return this.baseMapper.update(entity); }

    @Override
    public int remove(PK id) { return this.baseMapper.remove(id); }

    @Override
    public E get(PK id) { return this.baseMapper.get(id); }
}
