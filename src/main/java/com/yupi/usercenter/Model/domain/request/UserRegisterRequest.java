package com.yupi.usercenter.Model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 *  用户注册请求体
 *
 * @author RanGuMo
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 1403603928629341883L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
    //星球编号
    private String planetCode;

}
