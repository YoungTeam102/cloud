package com.igniubi.core.zipkin.instrument;

import org.springframework.stereotype.Component;


@Component
public class TraceEnumKeys {

    private Redis redis = new Redis();

    private Mysql mysql = new Mysql();


    public Redis getRedis() {
        return redis;
    }


    public void setRedis(Redis redis) {
        this.redis = redis;
    }

    public Mysql getMysql() {
        return mysql;
    }

    public void setMysql(Mysql mysql) {
        this.mysql = mysql;
    }


    public static class Redis {
        private String classNameKey = "class";

        private String methodNameKey = "method";

        public String getClassNameKey() {
            return classNameKey;
        }

        public String getMethodNameKey() {
            return methodNameKey;
        }

        public void setClassNameKey(String classNameKey) {
            this.classNameKey = classNameKey;
        }

        public void setMethodNameKey(String methodNameKey) {
            this.methodNameKey = methodNameKey;
        }
    }

    public static class Mysql {
        private String classNameKey = "class";

        private String methodNameKey = "method";

        public String getClassNameKey() {
            return classNameKey;
        }

        public String getMethodNameKey() {
            return methodNameKey;
        }

        public void setClassNameKey(String classNameKey) {
            this.classNameKey = classNameKey;
        }

        public void setMethodNameKey(String methodNameKey) {
            this.methodNameKey = methodNameKey;
        }
    }


}
