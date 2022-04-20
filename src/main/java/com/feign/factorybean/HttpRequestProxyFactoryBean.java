package com.feign.factorybean;

import com.feign.domain.HttpDomain;
import com.feign.interceptor.HttpMethodInterceptor;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.Enhancer;

public class HttpRequestProxyFactoryBean implements FactoryBean {
    private final Class<?> proxyKlass;

    private final HttpDomain httpDomain;

    private final OkHttpClient okHttpClient;

    public HttpRequestProxyFactoryBean(Class<?> proxyKlass, HttpDomain httpDomain, OkHttpClient okHttpClient) {
        this.proxyKlass = proxyKlass;
        this.httpDomain = httpDomain;
        this.okHttpClient = okHttpClient;
    }

    @Override
    public Object getObject() {
        Enhancer enhancer = new Enhancer();
        if (proxyKlass.isInterface()) {
            enhancer.setInterfaces(new Class[]{proxyKlass});
        } else {
            enhancer.setSuperclass(proxyKlass);
        }
        HttpMethodInterceptor httpMethodInterceptor = new HttpMethodInterceptor(proxyKlass, httpDomain, okHttpClient);
        enhancer.setCallback(httpMethodInterceptor);
        return enhancer.create();
    }

    @Override
    public Class<?> getObjectType() {
        return proxyKlass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
