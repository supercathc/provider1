package com.feign.json;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonBasicParam {

    /**
     * 字段名
     */
    String name() default "";

    /**
     * 是否必传
     */
    boolean required() default true;

    /**
     * 参数为空提示信息
     */
    String message() default "参数不能为空";

    /**
     * 默认值
     */
    String defaultValue() default "\n\t\t\n\t\t\n\ue000\ue001\ue002\n\t\t\t\t\n";
}