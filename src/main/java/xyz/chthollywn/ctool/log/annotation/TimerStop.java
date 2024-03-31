package xyz.chthollywn.ctool.log.annotation;

import org.springframework.core.annotation.AliasFor;
import xyz.chthollywn.ctool.log.task.DefaultTimerTask;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TimerStop {
    Class<?> task() default DefaultTimerTask.class;
}
