package com.cat.service;

import com.cat.feign.UserFeign;
import com.cat.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeignService {

    @Autowired
    private UserFeign feign;

    public User getUser() {
        User user = feign.getUser();
        return user;
    }

}
