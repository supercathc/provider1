package com.feign.domain;


import com.feign.anno.HttpConfig;

/**
 * @author kun
 * @data 2022/1/15 19:54
 */
public class HttpDomain {
    private String domain;

    private String port;

    public static HttpDomain from(HttpConfig httpConfig) {
        HttpDomain httpDomain = new HttpDomain();
        httpDomain.setDomain(httpConfig.domain());
        httpDomain.setPort(httpConfig.port());
        return httpDomain;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
