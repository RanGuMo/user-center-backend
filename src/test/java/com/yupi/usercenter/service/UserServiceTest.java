package com.yupi.usercenter.service;

import com.yupi.usercenter.Model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService  userService;

    @Test
    public void testAddUser(){

        User user = new User();
        user.setUsername("dogYupi");
        user.setUserAccount("123");
        user.setAvatarUrl("https://img1.baidu.com/it/u=1645832847,2375824523&fm=253&fmt=auto&app=138&f=JPEG?w=480&h=480");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setEmail("123");
        user.setPhone("456");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        //我断言结果为true
        Assertions.assertTrue(result);

    }

    @Test
    void userRegister() {
        //测试密码为空
        String userAccount = "mark";
        String userPassword = "";
        String checkPassword = "12345678";
        String planetCode ="1";
        long result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assert.assertEquals(-1, result);

        //测试账号小于4位
        userAccount = "la";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assert.assertEquals(-1, result);

        //测试密码小于8位
        userAccount = "mark";
        userPassword = "123456";
        checkPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assert.assertEquals(-1, result);

        //测试账号含有特殊字符
        userAccount = "mark@ :;";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assert.assertEquals(-1, result);

        //测试密码和校验密码不相同
        userAccount = "mark";
        userPassword = "12345678";
        checkPassword = "1234568889910";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assert.assertEquals(-1, result);

        //测试账户名重复
        userAccount = "CatMark";
        userPassword = "12345678";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assert.assertEquals(-1, result);

        //测试是否可以注册成功
        userAccount = "mark";
        userPassword = "12345678";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        Assert.assertTrue(result > 0);
    }
}