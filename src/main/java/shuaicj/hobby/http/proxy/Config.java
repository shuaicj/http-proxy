package shuaicj.hobby.http.proxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configurations.
 *
 * @author shuaicj 2017/09/15
 */
@Configuration
public class Config {

    @Bean
    public ExecutorService threadPool() {
        return Executors.newCachedThreadPool();
    }
}
