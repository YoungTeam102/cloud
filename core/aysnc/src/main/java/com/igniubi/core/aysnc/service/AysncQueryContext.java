package com.igniubi.core.aysnc.service;

import com.igniubi.core.aysnc.model.AysncProviderDefinition;
import com.igniubi.core.aysnc.model.InvokeSignature;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class AysncQueryContext {

    private Thread rootThread;

    private AysncProviderDefinition rootProviderDefinition;

    private boolean useCache = true;

    private  Map<InvokeSignature, Object> queryCache = new ConcurrentHashMap<>();

    public void putCache(InvokeSignature signature, Object o){
        queryCache.put(signature, o);
    }

    public Object getCache(InvokeSignature signature){
        return queryCache.get(signature);
    }

    public boolean existCache(InvokeSignature signature){
        if(!useCache){
            return false;
        }
        return  queryCache.containsKey(signature);
    }

}
