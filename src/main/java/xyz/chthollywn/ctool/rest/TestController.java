package xyz.chthollywn.ctool.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.chthollywn.ctool.config.CToolProperties;
import xyz.chthollywn.ctool.log.CTLog;
import xyz.chthollywn.ctool.log.annotation.ApiOperationLog;
import xyz.chthollywn.ctool.log.annotation.TimerMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/demo")
public class TestController {
    @Resource
    private CTLog ctLog;
    @Resource
    private CToolProperties cToolProperties;
    @Resource
    private TestService testService;

    @GetMapping("/log")
    @ApiOperationLog(value = "log", level = "info")
    @TimerMethod
    public String logTest(HttpServletRequest request) throws InterruptedException {
        testService.timerTest();
        testService.timerTest2();
        testService.timerTest3();
        testService.timerTest4();
        return "ok";
    }
}

