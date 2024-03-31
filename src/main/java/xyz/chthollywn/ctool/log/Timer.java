package xyz.chthollywn.ctool.log;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import xyz.chthollywn.ctool.config.ApplicationContextProvider;
import xyz.chthollywn.ctool.config.CToolProperties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@NoArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Timer {
    @Resource
    private CTLog ctLog;
    @Resource
    private CToolProperties cToolProperties;
    private long startTime;
    private long stepTime;
    private String name;
    private String level;
    private final List<Step> steps = Lists.newArrayList();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * 依赖初始化方法 防止依赖注入滞后抛出空指针
     */
    @PostConstruct
    private void init() {
        this.level = cToolProperties.getTimerLogLevel();
    }

    /**
     * 新建timer方法
     */
    public static Timer newInstance() {
        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
        return context.getBean(Timer.class);
    }

    // 计时开始
    public void start(String name, long startTime) {
        this.startTime = startTime;
        this.stepTime = startTime;
        logWithLevel(String.format("[%s]-计时开始", name), CTLog.ANSI_BLUE);
        this.name = name;
        scheduler.scheduleWithFixedDelay(this::logElapsedTime, 1, 1, TimeUnit.MINUTES);
    }
    public void start(String name) {
        start(name, System.currentTimeMillis());
    }

    // 步骤计时
    public void step(String name, long stepTime) {
        String time = getElapsedTime(stepTime, this.stepTime);
        logWithLevel(String.format("[%s]-步骤耗时：[%s]-当前总耗时：[%s]",
                        name, time, getElapsedTime(System.currentTimeMillis(), startTime)),
                CTLog.ANSI_GREEN);
        addStep(name, time);
        this.stepTime = System.currentTimeMillis();
    }
    public void step(String name) {
        step(name, System.currentTimeMillis());
    }

    // 计时结束
    public void stop(long stopTime) {
        String allTime = getElapsedTime(stopTime, startTime);
        logWithLevel("计时结束", CTLog.ANSI_BLUE);
        logStep();
        logWithLevel(String.format("[%s]-总耗时：[%s]", this.name, allTime), CTLog.ANSI_PURPLE);
    }
    public void stop() {
        stop(System.currentTimeMillis());
    }

    // 心跳日志
    private void logElapsedTime() {
        logWithLevel(String.format("[心跳日志]-当前步骤耗时：[%s]-当前总耗时：[%s]",
                getElapsedTime(System.currentTimeMillis(), this.stepTime),
                getElapsedTime(System.currentTimeMillis(), this.startTime)), CTLog.ANSI_CYAN);
    }

    // 步骤日志
    private void logStep() {
        if (!CollectionUtils.isEmpty(this.steps)) {
            for (Step step : steps) {
                logWithLevel(String.format("[%s]-步骤耗时：[%s]", step.getName(), step.getTime()), CTLog.ANSI_PURPLE);
            }
        }
    }

    private void addStep(String name, String time) {
        this.steps.add(new Step(name, time));
    }

    private String getElapsedTime(long t1, long t2) {
        long elapsedTime = t1 - t2;

        long milliseconds = elapsedTime % 1000;
        long seconds = (elapsedTime / 1000) % 60;
        long minutes = (elapsedTime / (1000 * 60)) % 60;
        long hours = elapsedTime / (1000 * 60 * 60);

        StringBuilder formattedTime = new StringBuilder();

        if (hours > 0) {
            formattedTime.append(hours).append(" 小时 ");
        }

        if (minutes > 0 || hours > 0) {
            formattedTime.append(minutes).append(" 分钟 ");
        }

        if (seconds > 0 || minutes > 0 || hours > 0) {
            formattedTime.append(seconds).append(" 秒 ");
        }

        formattedTime.append(milliseconds).append(" 毫秒");

        return formattedTime.toString();
    }

    private void logWithLevel(String message, String color) {
        if ("info".equalsIgnoreCase(this.level)) ctLog.INFO(message, color);
        else if ("error".equalsIgnoreCase(this.level)) ctLog.ERROR(message, color);
        else ctLog.DEBUG(message, color);
    }

    @Data
    @AllArgsConstructor
    private static class Step {
        private String name;
        private String time;
    }
}
