package com.feign.config;

import com.feign.anno.HttpConfig;
import com.feign.service.HttpService;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpsConfig {

    @HttpConfig(domain = "localhost", port = "8080")
    public HttpService httpService;
}

