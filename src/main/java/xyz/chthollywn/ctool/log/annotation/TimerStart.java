package xyz.chthollywn.ctool.log.annotation;

import org.springframework.core.annotation.AliasFor;
import xyz.chthollywn.ctool.log.task.DefaultTimerTask;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用Timer注解计算任务流程各阶段耗时，因为aop在相同bean内调用方法会失效的机制，
 * 要注意调用方法和被调用任务不能在同一个bean
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface TimerStart {
    String taskName() default "default task";
    String stepName() default "first step";
    @AliasFor("value")
    Class<?> task() default DefaultTimerTask.class;
    @AliasFor("task")
    Class<?> value() default DefaultTimerTask.class;
}
