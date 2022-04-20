package com.feign.beanpostprocess;

import com.feign.handlerreslove.DefArgumentResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class CustomHandlerMethodArgumentResolverConfig implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RequestMappingHandlerAdapter) {
            final RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter) bean;
            final List<HandlerMethodArgumentResolver> argumentResolvers = Optional.ofNullable(adapter.getArgumentResolvers())
                    .orElseGet(ArrayList::new);
            final List<HandlerMethodArgumentResolver> handlerMethodArgumentResolvers = new ArrayList<>(argumentResolvers);
            // 将我们自己的参数解析器放置到第一位
            handlerMethodArgumentResolvers.add(0, new DefArgumentResolver());
            adapter.setArgumentResolvers(Collections.unmodifiableList(handlerMethodArgumentResolvers));
            return adapter;
        }
        return bean;
    }
}