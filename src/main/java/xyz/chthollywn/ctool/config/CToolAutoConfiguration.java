package xyz.chthollywn.ctool.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.chthollywn.ctool.api.CToolUserInterface;
import xyz.chthollywn.ctool.api.DefaultUserImpl;
import xyz.chthollywn.ctool.log.CTLog;
import xyz.chthollywn.ctool.log.CTLogAspect;
import xyz.chthollywn.ctool.log.Timer;

@Configuration
@EnableConfigurationProperties(value = CToolProperties.class)
public class CToolAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public ApplicationContextProvider applicationContextProvider() {
        return new ApplicationContextProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public CToolUserInterface cToolUserInterface() {
        return new DefaultUserImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public CToolProperties cToolProperties() {
        return new CToolProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public CTLog ctLog() {
        return new CTLog();
    }

    @Bean
    @ConditionalOnMissingBean
    public CTLogAspect ctLogAspect() {
        return new CTLogAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public Timer timer() {
        return new Timer();
    }
}
