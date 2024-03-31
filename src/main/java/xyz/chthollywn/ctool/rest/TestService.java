package xyz.chthollywn.ctool.rest;

import org.springframework.stereotype.Service;
import xyz.chthollywn.ctool.log.annotation.TimerStart;
import xyz.chthollywn.ctool.log.annotation.TimerStep;
import xyz.chthollywn.ctool.log.annotation.TimerStop;

@Service
public class TestService {
    @TimerStart(taskName = "测试计时器任务", stepName = "步骤一")
    public void timerTest() throws InterruptedException {
        Thread.sleep(1000);
    }
    @TimerStep(stepName = "步骤二")
    public void timerTest2() throws InterruptedException {
        Thread.sleep(1000);
    }
    @TimerStep(stepName = "步骤三")
    public void timerTest3() throws InterruptedException {
        Thread.sleep(1000);
    }
    @TimerStop
    public void timerTest4() throws InterruptedException {
        Thread.sleep(1000);
    }
}
