package com.feign.intereceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 通过注解来表明，我们需要对那个字段进行加密
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamAnnotation {
    String[] srcKey() default {};

    String[] destKey() default {};
}