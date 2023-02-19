package com.yupi.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.usercenter.Model.domain.User;

import javax.servlet.http.HttpServletRequest;

/** 用户服务
* @author 86182
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-02-12 12:41:43
*/
public interface UserService extends IService<User> {


    /**
     * ⽤户注册
     * @param userAccount ⽤户账户
     * @param userPassword ⽤户密码
     * @param checkPassword 校验密码
     * @return 新⽤户 id
     */
    long userRegister(String userAccount, String userPassword, String
            checkPassword,String planetCode);

    /**
     * 用户登录
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request
     * @return 返回脱敏后的用户数据
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);
}
