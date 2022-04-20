package com.feign.service;

import com.feign.anno.HttpRequest;


@HttpRequest("/demo")
public interface HttpService {

    @HttpRequest("/val")
    String getValue();
}
