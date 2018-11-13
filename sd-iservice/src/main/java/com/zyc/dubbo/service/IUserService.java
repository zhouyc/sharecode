package com.zyc.dubbo.service;

import com.zyc.dubbo.model.UserModel;

import java.util.List;

public interface IUserService {

    public String testUser(String name);

    public List<UserModel> findAllModels();
}
