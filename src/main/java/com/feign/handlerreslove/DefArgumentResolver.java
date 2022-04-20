package com.feign.handlerreslove;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import javax.servlet.http.HttpServletRequest;

public class DefArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        System.out.println(methodParameter);
        return String.class.isAssignableFrom(methodParameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) {
        // 获取请求
        HttpServletRequest servletRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        String info = (String) nativeWebRequest.getAttribute("params", NativeWebRequest.SCOPE_REQUEST);
        //获取消息头认证信息，没有后续操作了，根据业务实际来解析校验该token
        String token = servletRequest.getHeader("Authorization");
        //自己塞数据进去，也可以从配置文件获取数据
        System.out.println("启动了！~~~~~~~~~~~~~~~~");
        return "abcd";
    }
}
