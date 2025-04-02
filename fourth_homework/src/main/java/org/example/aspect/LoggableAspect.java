package org.example.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * This aspect is used for logging methods with {@link org.example.annotation.Loggable @Loggable} annotation
 */
@Aspect
public class LoggableAspect {
    @Pointcut("within(@org.example.annotation.Loggable *) && execution(* * (..))")
    public void annotatedByLoggable() {}

    @Around("annotatedByLoggable()")
    public Object logging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("Вызов метода " + proceedingJoinPoint.getSignature());
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long end = System.currentTimeMillis() - start;
        System.out.println("Выполнение метода " + proceedingJoinPoint.getSignature() + " завершено. Время выполнения составило " + end + " ms");
        return result;
    }
}