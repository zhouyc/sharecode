package com.zyc.dubbo.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.zyc.dubbo.service.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @Reference(version = "1.0.0")
    IUserService userService;

    @RequestMapping("/index")
    public String index(){
        System.out.println("test user index");
        System.out.println(userService.testUser("zyc"));

        System.out.println(userService.findAllModels());

        return "user/index";
    }
}
