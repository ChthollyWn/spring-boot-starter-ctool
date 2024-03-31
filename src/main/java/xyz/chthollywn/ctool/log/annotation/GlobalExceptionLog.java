package xyz.chthollywn.ctool.log.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@RestControllerAdvice
public @interface GlobalExceptionLog {
    @AliasFor("value")
    boolean isLog() default true;

    @AliasFor("isLog")
    boolean value() default true;

    String level() default "error";
}
