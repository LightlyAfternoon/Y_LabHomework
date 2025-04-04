package org.example.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * This aspect is used for logging methods with {@link org.example.annotation.Loggable @Loggable} annotation
 */
@Aspect
public class LoggableAspect {
    long start;
    long end;

    @Pointcut("within(@org.example.annotation.Loggable *) && execution(* * (..))")
    public void annotatedByLoggable() {}

    @Before("annotatedByLoggable()")
    public void beforeCallAtMethod(JoinPoint joinPoint) {
        System.out.println("Вызов метода " + joinPoint.getSignature());

        start = System.currentTimeMillis();
    }

    @After("annotatedByLoggable()")
    public void afterCallAtMethod(JoinPoint joinPoint) {
        end = System.currentTimeMillis() - start;

        System.out.println("Выполнение метода " + joinPoint.getSignature() + " завершено. Время выполнения составило " + end + " ms");
    }
}