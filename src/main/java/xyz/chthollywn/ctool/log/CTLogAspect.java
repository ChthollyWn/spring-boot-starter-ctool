package xyz.chthollywn.ctool.log;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xyz.chthollywn.ctool.config.CToolProperties;
import xyz.chthollywn.ctool.excepion.CToolException;
import xyz.chthollywn.ctool.log.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Aspect
@Component
@Order(0)
public class CTLogAspect {
    @Resource
    private CTLog ctLog;
    @Resource
    private CToolProperties cToolProperties;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Around("@within(xyz.chthollywn.ctool.log.annotation.ApiOperationLog) || @annotation(xyz.chthollywn.ctool.log.annotation.ApiOperationLog)")
    public Object apiOperationLogAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = null;
        Throwable ex = null;
        try {
             result = joinPoint.proceed();
        } catch (Throwable throwable) {
            ex = throwable;
            throw throwable;
        } finally {
            long endTime = System.currentTimeMillis();
            long spendTime = endTime - startTime;
            String startTimeStr = dateFormat.format(new Date(startTime));
            String spendTimeStr = spendTime + "ms";

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                Method fun = signature.getMethod();
                ApiOperationLog annotation = fun.getAnnotation(ApiOperationLog.class);
                if (annotation == null) {
                    Class<?> declaringClass = fun.getDeclaringClass();
                    annotation = declaringClass.getAnnotation(ApiOperationLog.class);
                }
                String description = StringUtils.isNotBlank(annotation.description()) ? annotation.description() : annotation.value();

                String apiOperationLogMessage = ctLog.getApiOperationLogMessage(request, description, startTimeStr, spendTimeStr, ex, result);

                String level = annotation.level();
                // 日志输出等级 当注解上的level为空时，加载配置文件的level
                if (StringUtils.isBlank(level)) level = cToolProperties.getApiOperationLogLevel();
                if ("info".equalsIgnoreCase(level)) ctLog.INFO(apiOperationLogMessage, CTLog.ANSI_CYAN);
                else if ("error".equalsIgnoreCase(level)) ctLog.ERROR(apiOperationLogMessage, CTLog.ANSI_CYAN);
                else ctLog.DEBUG(apiOperationLogMessage, CTLog.ANSI_CYAN);
            }
        }
        return result;
    }

    @Around("@within(xyz.chthollywn.ctool.log.annotation.GlobalExceptionLog) && @annotation(org.springframework.web.bind.annotation.ExceptionHandler)")
    public Object globalExceptionLogAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method fun = signature.getMethod();
            GlobalExceptionLog annotation = fun.getAnnotation(GlobalExceptionLog.class);
            if (annotation == null) {
                Class<?> declaringClass = fun.getDeclaringClass();
                annotation = declaringClass.getAnnotation(GlobalExceptionLog.class);
            }

            Object[] args = joinPoint.getArgs();

            // 当注解值为打印日志且方法参数中包含Throwable的子类时 进行日志打印操作
            if (annotation.isLog() && annotation.value() && Arrays.stream(joinPoint.getArgs())
                    .anyMatch(arg -> arg instanceof Throwable)) {
                List<Exception> exceptions = Arrays.stream(args)
                        .filter(Objects::nonNull)
                        .filter(f -> f instanceof Throwable)
                        .map(m -> (Exception) m)
                        .collect(Collectors.toList());
                ctLog.doExceptionLog(request, exceptions.get(0));
            }
        }
        return result;
    }

    private static final HashMap<Class<?>, Timer> taskMap = new HashMap<>();

    @Around("@annotation(xyz.chthollywn.ctool.log.annotation.TimerStart)")
    public Object timerStartAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method fun = signature.getMethod();
        TimerStart annotation = fun.getAnnotation(TimerStart.class);
        Class<?> task = annotation.task();
        Timer timer = taskMap.get(task);
        if (timer != null) throw new CToolException("计时器任务重复");

        String taskName = annotation.taskName();
        String stepName = annotation.stepName();
        Timer newInstance = Timer.newInstance();
        newInstance.start(taskName, startTime);
        newInstance.step(stepName, endTime);
        taskMap.put(task, newInstance);

        return result;
    }

    @Around("@annotation(xyz.chthollywn.ctool.log.annotation.TimerStep)")
    public Object timerStepAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method fun = signature.getMethod();
        TimerStep annotation = fun.getAnnotation(TimerStep.class);
        Class<?> task = annotation.task();
        Timer timer = taskMap.get(task);
        if (timer == null) throw new CToolException("计时器任务不存在");

        String stepName = annotation.stepName();
        timer.step(stepName, endTime);
        taskMap.put(task, timer);

        return result;
    }

    @Around("@annotation(xyz.chthollywn.ctool.log.annotation.TimerStop)")
    public Object timerStopAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method fun = signature.getMethod();
        TimerStop annotation = fun.getAnnotation(TimerStop.class);
        Class<?> task = annotation.task();
        Timer timer = taskMap.get(task);
        if (timer == null) throw new CToolException("计时器任务不存在");

        timer.stop(endTime);
        taskMap.remove(task);
        return result;
    }

    @Around("@annotation(xyz.chthollywn.ctool.log.annotation.TimerMethod)")
    public Object timerMethodAspect(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method fun = signature.getMethod();
        TimerMethod annotation = fun.getAnnotation(TimerMethod.class);
        String value = annotation.value();
        Timer timer = Timer.newInstance();
        timer.start(value, startTime);
        timer.stop(endTime);

        return result;
    }

}