package edu.utn.proyecto.common.audit;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class AuditLogAspect {

    @AfterReturning("@annotation(edu.utn.proyecto.common.audit.Auditable)")
    public void logAfter(JoinPoint jp) {
        String method = jp.getSignature().toShortString();
        String args = Arrays.stream(jp.getArgs())
                .map(arg -> arg == null ? "null" : arg.getClass().getSimpleName())
                .reduce((a, b) -> a + "," + b)
                .orElse("");
        log.info("AUDIT | action={} | args={}", method, args);
    }
}