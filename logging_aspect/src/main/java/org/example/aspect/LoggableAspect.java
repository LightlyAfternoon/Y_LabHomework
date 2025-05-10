package org.example.aspect;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

/**
 * This aspect is used for logging methods with @Loggable annotation
 */
@Aspect
@Log4j2
public class LoggableAspect {
    long start;
    long end;

    @Pointcut("within(@org.example.annotation.Loggable *) && execution(* * (..))")
    public void annotatedByLoggable() {}

    @Before("annotatedByLoggable()")
    public void beforeCallAtMethod(JoinPoint joinPoint) {
        log.info("Вызов метода " + joinPoint.getSignature());

        start = System.currentTimeMillis();
    }

    @After("annotatedByLoggable()")
    public void afterCallAtMethod(JoinPoint joinPoint) {
        end = System.currentTimeMillis() - start;

        log.info("Выполнение метода " + joinPoint.getSignature() + " завершено. Время выполнения составило " + end + " ms");
    }
}