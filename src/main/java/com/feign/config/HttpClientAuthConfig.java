package com.feign.config;

import com.feign.bean_factory_post_processor.HttpPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientAuthConfig {

    @Bean
    public HttpPostProcessor httpConsumerPostProcessor() {
        return new HttpPostProcessor();
    }
}
