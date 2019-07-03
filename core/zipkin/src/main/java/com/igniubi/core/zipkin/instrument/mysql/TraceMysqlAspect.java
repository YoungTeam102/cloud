package com.igniubi.core.zipkin.instrument.mysql;

import brave.ScopedSpan;
import brave.Span;
import brave.Tracer;
import com.igniubi.core.zipkin.instrument.TraceKeys;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class TraceMysqlAspect {

    private static final String MYSQL_COMPONENT = "mysql";

    private final Tracer tracer;

    private final TraceKeys traceKeys ;

    public TraceMysqlAspect(Tracer tracer, TraceKeys traceKeys) {
        this.tracer = tracer;
        this.traceKeys = traceKeys;
    }

    @Pointcut("execution (* com.igniubi.*.mapper.*.*(..))")
    private void pointCut() {
    }


    @Around("pointCut()")
    public Object traceMysqlAround(final ProceedingJoinPoint pjp) throws Throwable {

        String className = pjp.getSignature().getDeclaringType().getSimpleName();
        String methodName = pjp.getSignature().getName();

        ScopedSpan span = tracer.startScopedSpan( className + "." + methodName);
        span.tag("service", MYSQL_COMPONENT);
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

    protected Span currentSpan() {
        return this.tracer.currentSpan();
    }

}
