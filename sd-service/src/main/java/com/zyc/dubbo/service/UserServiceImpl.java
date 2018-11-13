package com.zyc.dubbo.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.zyc.dubbo.mapper.UserMapper;
import com.zyc.dubbo.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service(version = "1.0.0")
public class UserServiceImpl implements IUserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public List<UserModel> findAllModels() {
        return userMapper.findAllUsers();
    }

    @Override
    public String testUser(String name) {
        System.out.println(System.currentTimeMillis());
        return "hello "+name;
    }
}
