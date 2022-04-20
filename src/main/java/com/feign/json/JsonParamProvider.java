package com.feign.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
public class JsonParamProvider implements HandlerMethodArgumentResolver {

    /**
     * 请求body格式
     */
    private static final String CONTENT_TYPE = "application/json";

    /**
     * 判断是否是需要我们解析的参数类型
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(JsonBasicParam.class);
    }

    /**
     * 真正解析的方法
     */
    @Override
    public Object resolveArgument(@NonNull MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) throws BaseException {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            return null;
        }
        String contentType = request.getContentType();
        if (StringUtils.isEmpty(contentType) || !contentType.toLowerCase().contains(CONTENT_TYPE)) {
            throw new BaseException("只支持content-type为application/json的请求");
        }
        JSONObject jsonObject;
        try (BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(),
                StandardCharsets.UTF_8))) {
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }
            jsonObject = JSON.parseObject(responseStrBuilder.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BaseException("json格式异常");
        }
        JsonBasicParam jsonParam = methodParameter.getParameterAnnotation(JsonBasicParam.class);
        if (jsonParam == null) {
            return null;
        }
        //获取参数名
        String paramName = jsonParam.name();
        //注解没有给定参数名字，默认取参数名称
        if (StringUtils.isEmpty(paramName)) {
            paramName = methodParameter.getParameter().getName();
        }
        String paramType = methodParameter.getParameter().getType().getSimpleName();
        if (jsonObject != null && jsonObject.containsKey(paramName)) {
            String data = String.valueOf(jsonObject.get(paramName));
            if (jsonParam.required() && StringUtils.isEmpty(data)) {
                throw new BaseException(jsonParam.message());
            }
            return initValue(paramType, data);
        }
        return null;
    }

    /**
     * 给基本类型参数注入值
     *
     * @param type 类型
     * @param data 值
     * @return java.lang.Object
     */
    private Object initValue(String type, String data) throws BaseException {
        try {
            if ("int".equalsIgnoreCase(type) || "integer".equalsIgnoreCase(type)) {
                return Integer.valueOf(data);
            } else if ("double".equalsIgnoreCase(type)) {
                return Double.valueOf(data);
            } else if ("long".equalsIgnoreCase(type)) {
                return Long.valueOf(data);
            }
        } catch (NumberFormatException e) {
            log.error(e.getMessage(), e);
            throw new BaseException("数据类型错误");
        }
        return data;
    }
}