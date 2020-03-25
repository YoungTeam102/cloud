package com.igniubi.core.aysnc.repository;


import com.igniubi.core.aysnc.model.AysncProviderDefinition;

/**
 */
public interface AysncProviderRepository {
    /**
     * 存放provide定义
     * @param aysncProviderDefinition provider定义
     */
    void put(AysncProviderDefinition aysncProviderDefinition);

    /**
     * 获取provider
     *
     * @param name data provider name
     * @return data provider
     */
    AysncProviderDefinition get(String name);

    /**
     * 是否包含指定Provider
     * @param name data provider name
     * @return 是否存在provider
     */
    boolean contains(String name);
}
