package xyz.chthollywn.ctool.log;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import xyz.chthollywn.ctool.api.CToolUserInterface;
import xyz.chthollywn.ctool.util.IpUtil;
import xyz.chthollywn.ctool.util.JsonUtil;
import xyz.chthollywn.ctool.util.RequestUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class CTLog {
    // TODO 日志的不同输出策略
    // TODO 订阅发布模式的日志监控
    // TODO logback注入
    @Resource
    private CToolUserInterface cToolUserInterface;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public void doExceptionLog(HttpServletRequest request, Exception e) {
        ERROR(getExceptionLogMessage(request, e), e);
    }

    public void doExceptionLog(HttpServletRequest request, Exception e, String color) {
        ERROR(getExceptionLogMessage(request, e), color, e);
    }

    private String getExceptionLogMessage(HttpServletRequest request, Exception e) {
        return String.format(
                request.getMethod() + " " + request.getRequestURI() + "\n" +
                        "Error-[%s]\n" +
                        "UserId-[%s]\n" +
                        "IP-[%s]\n" +
                        "Parameters-[%s]\n" +
                        "Body-[%s]",
                e.getMessage(), cToolUserInterface.isLogin() ? cToolUserInterface.getUserId() : "not login",
                IpUtil.getIpAddr(request), RequestUtil.getRequestParams(request),
                StringUtils.isNotBlank(RequestUtil.getRequestBody(request)) ? RequestUtil.getRequestBody(request) : "{}");
    }

    public String getApiOperationLogMessage(String description, String userId, String startTimeStr, String spendTimeStr,
                                            String basePath, String uri, String url, String method, String ip, String parameters,
                                            String body, Throwable ex, Object result) {
        return String.format("\n" +
                        "Description: %s\n" +
                        "UserId: %s\n" +
                        "StartTime: %s\n" +
                        "SpendTime: %s\n" +
                        "BasePath: %s\n" +
                        "URI: %s\n" +
                        "URL: %s\n" +
                        "Method: %s\n" +
                        "IP: %s\n" +
                        "Parameters: %s\n" +
                        "Body: %s\n" +
                        "Error: %s\n" +
                        "Result: %s",
                description, userId, startTimeStr, spendTimeStr, basePath, uri, url, method,
                ip, parameters, StringUtils.isNotBlank(body) ? body : "{}",
                ex != null ? ex.getMessage() : null, JsonUtil.toJson(result));
    }

    public String getApiOperationLogMessage(HttpServletRequest request, String description,
                                            String startTimeStr, String spendTimeStr,
                                            Throwable exception, Object result) {
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        String uri = request.getRequestURI();
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        String ip = IpUtil.getIpAddr(request);
        String parameters = RequestUtil.getRequestParams(request).toString();
        String body = RequestUtil.getRequestBody(request);
        String userId = (String) cToolUserInterface.getUserId();

        return getApiOperationLogMessage(description, userId, startTimeStr, spendTimeStr,
                basePath, uri, url, method, ip, parameters, body, exception, result);
    }

    public void DEBUG(String message, String color, Object... objects) {
        log.debug(color + message + ANSI_RESET, objects);
    }

    public void DEBUG(String message, Object... objects) {
        log.debug(message, objects);
    }

    public void INFO(String message, String color, Object... objects) {
        log.info(color + message + ANSI_RESET, objects);
    }

    public void INFO(String message, Object... objects) {
        log.info(message, objects);
    }

    public void ERROR(String message, String color, Object... objects) {
        log.error(color + message + ANSI_RESET, objects);
    }

    public void ERROR(String message, Object... objects) {
        log.error(message, objects);
    }

}
