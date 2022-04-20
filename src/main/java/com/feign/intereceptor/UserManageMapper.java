package com.feign.intereceptor;

import org.springframework.stereotype.Repository;

@Repository
public interface UserManageMapper {

    @ParamAnnotation(srcKey = {"phone"}, destKey = {"phone"})
    Integer addOneUser(String userInfoVo);
}