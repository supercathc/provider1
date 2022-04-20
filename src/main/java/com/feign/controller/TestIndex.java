package com.feign.controller;

import com.feign.service.HttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/23")
public class TestIndex {
    @Autowired
    private HttpService service;

    @RequestMapping("/32")
    public void test(String a) {
//        service.getValue();
        System.out.println(a);
    }
}
