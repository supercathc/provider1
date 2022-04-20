package com.feign.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feign.anno.HttpRequest;
import com.feign.domain.HttpDomain;
import okhttp3.*;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpMethodInterceptor implements MethodInterceptor {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String HTTP_HEAD = "http://";

    private final Class<?> proxyKlass;

    private final HttpDomain httpDomain;

    private final OkHttpClient okHttpClient;

    public HttpMethodInterceptor(Class<?> proxyKlass, HttpDomain httpDomain, OkHttpClient okHttpClient) {
        this.proxyKlass = proxyKlass;
        this.httpDomain = httpDomain;
        this.okHttpClient = okHttpClient;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("接口: " + method.getName() + "即将被代理");
        HttpRequest httpRequest = method.getAnnotation(HttpRequest.class);

        HttpRequest proxyAnnotation = proxyKlass.getAnnotation(HttpRequest.class);
        String namespace = proxyAnnotation.value();
        String url = getUrl(httpRequest.value(), namespace);
        Request request = buildRequest(method, objects, url);
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        ResponseBody body = response.body();
        byte[] bytes = body.bytes();
        String res = new String(bytes, StandardCharsets.UTF_8);
        System.out.println(res);
        return null;
    }

    private Request buildRequest(Method method, Object[] args, String url) throws JsonProcessingException {
        Request.Builder builder = new Request.Builder();
        Map<String, Object> paramMap = new HashMap<>(4);
        Parameter[] parameters = method.getParameters();
//        for (int i=0; i<parameters.length; i++) {
//            Parameter parameter = parameters[i];
//            MultiRequestBody multiRequestBody = parameter.getAnnotation(MultiRequestBody.class);
//            if (Objects.isNull(multiRequestBody)) {
//                throw new IllegalStateException("method[" + method.getName() + "] param must annotated with @MultiRequest!");
//            }
//            paramMap.put(multiRequestBody.value(), args[i]);
//        }
        // 将参数构建为一个大JSON
        String param = OBJECT_MAPPER.writeValueAsString(paramMap);
        RequestBody requestBody = RequestBody.create(JSON, param);
        builder.post(requestBody);
        builder.url(url);
        return builder.build();
    }

    private String getUrl(String value, String namespace) {
        if (Objects.isNull(namespace)) {
            return HTTP_HEAD + httpDomain.getDomain() + ":" +httpDomain.getPort();
        } else {
            return HTTP_HEAD + httpDomain.getDomain() + ":" + httpDomain.getPort() + "/" + namespace + "/" + value;
        }

    }
}
