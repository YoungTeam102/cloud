package com.igniubi.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;


public class BeanUtils {

    static Logger log = LoggerFactory.getLogger(BeanUtils.class);

    public static <T> T copyBeans(Class<T> targetClazz, Object... sources) {
        if (null == sources || sources.length == 0) {
            return null;
        }
        T targetObj = null;
        try {
            targetObj = targetClazz.newInstance();
            for (Object sourceObj : sources) {
                BeanCopier copier = BeanCopier.create(sourceObj.getClass(), targetClazz, false);
                copier.copy(sourceObj, targetObj, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return targetObj;
    }


}
