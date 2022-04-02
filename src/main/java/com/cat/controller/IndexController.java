package com.cat.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gate")
public class IndexController {
    @RequestMapping("/index")
    public String index() {
        return "2333";
    }
}

