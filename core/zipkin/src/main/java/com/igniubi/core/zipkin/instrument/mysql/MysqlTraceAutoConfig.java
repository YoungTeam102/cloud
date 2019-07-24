package com.igniubi.core.zipkin.instrument.mysql;

import brave.Tracer;
import com.igniubi.core.zipkin.instrument.TraceEnumKeys;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnBean(Tracer.class)
public class MysqlTraceAutoConfig {

    public static class InnerNested {
        @Bean
        public TraceMysqlAspect traceRedisAspect(Tracer tracer, TraceEnumKeys traceKeys) {
            return new TraceMysqlAspect(tracer, traceKeys);
        }

    }

}
