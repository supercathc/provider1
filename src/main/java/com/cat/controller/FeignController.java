package com.cat.controller;

import com.cat.pojo.User;
import com.cat.service.FeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feign")
public class FeignController {

    @Autowired
    private FeignService feignService;

    @RequestMapping("/provider2")
    public User feignProvider2() {
        User user = feignService.getUser();
        return user;
    }
}
