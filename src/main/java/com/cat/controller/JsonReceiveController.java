package com.cat.controller;

import com.cat.pojo.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/json")
public class JsonReceiveController {

    @RequestMapping("/user")
    public User receiveJson(@RequestBody User user) {
        System.out.println(user);
        return user;
    }
}
