package com.zyc.dubbo.mapper;

import com.zyc.dubbo.model.UserModel;

import java.util.List;

public interface UserMapper {

    List<UserModel> findAllUsers();
}
