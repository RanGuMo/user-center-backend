package com.yupi.usercenter.Model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 *  用户登录请求体
 *
 * @author RanGuMo
 */
@Data
public class UserLoginRequest implements Serializable {


    private static final long serialVersionUID = 3319661924440795018L;
    private String userAccount;

    private String userPassword;



}
