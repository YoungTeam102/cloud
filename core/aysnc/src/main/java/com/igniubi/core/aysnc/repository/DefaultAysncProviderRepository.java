package com.igniubi.core.aysnc.repository;

import com.igniubi.core.aysnc.model.AysncProviderDefinition;

import java.util.concurrent.ConcurrentHashMap;

public class DefaultAysncProviderRepository implements AysncProviderRepository {

    private final ConcurrentHashMap<String, AysncProviderDefinition> providerMap = new ConcurrentHashMap<>();

    @Override
    public void put(AysncProviderDefinition providerDefinition) {
        providerMap.put(providerDefinition.getName(), providerDefinition);
    }

    @Override
    public AysncProviderDefinition get(String name) {
        return providerMap.get(name);
    }

    @Override
    public boolean contains(String name) {
        return providerMap.containsKey(name);
    }
}
