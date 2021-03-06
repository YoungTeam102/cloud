package com.igniubi.core.zipkin.instrument.redis;

import brave.ScopedSpan;
import brave.Tracer;
import com.igniubi.core.zipkin.instrument.TraceEnumKeys;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class TraceRedisAspect {

    private Logger logger = LoggerFactory.getLogger(TraceRedisAspect.class);

    private static final String REDIS_COMPONENT = "redis";

    private final Tracer tracer;

    private final TraceEnumKeys traceKeys ;

    public TraceRedisAspect(Tracer tracer, TraceEnumKeys traceKeys) {
        this.tracer = tracer;
        this.traceKeys = traceKeys;
    }

    @Pointcut("execution (* com.igniubi.redis.operations.*.*(..))")
    private void pointCut() {
    }


    @Around("pointCut()")
    public Object traceRedisAround(final ProceedingJoinPoint pjp) throws Throwable {

        String className = pjp.getTarget().getClass().getSimpleName();
        String methodName = pjp.getSignature().getName();
        ScopedSpan span = tracer.startScopedSpan( className + "." + methodName);

        span.tag("service", REDIS_COMPONENT);
        span.tag(traceKeys.getRedis().getClassNameKey(), className);
        span.tag(traceKeys.getRedis().getMethodNameKey(), methodName);
        try {
            return pjp.proceed();
        } catch (Exception e){
            span.tag("error", e.getMessage() != null ? e.getMessage() : e.toString());
            throw e;
        }finally {
            span.finish();
        }
    }

}
