package com.cat.feign;

import com.cat.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Component
@FeignClient("providerService")
public interface UserFeign {

    @GetMapping("/feign/user")
    User getUser();
}
