package xyz.chthollywn.ctool.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "ctool")
public class CToolProperties {
    /**
     * api操作日志输出级别
     */
    private String apiOperationLogLevel = "debug";
    /**
     * 计时器的日志输出级别
     */
    private String timerLogLevel = "debug";
}
