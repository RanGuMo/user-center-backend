package com.yupi.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.usercenter.Model.domain.User;
import com.yupi.usercenter.common.ErrorCode;
import com.yupi.usercenter.exception.BusinessException;
import com.yupi.usercenter.service.UserService;
import com.yupi.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yupi.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author 86182
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-02-12 12:41:43
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    @Resource
    private UserMapper userMapper;
    /*
       盐值，混淆密码
     */
    private static final String SALT = "mark";


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword,String planetCode) {
         // 1. 校验
        // 1.1 校验是否为空 引入apache common utils ：Apache Commons Lang 使用其中的方法：isAnyBlank 可判断多个字符串是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword,planetCode)) {
            // TODO 修改为自定义异常
//            return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }

        // 1.2 校验账号位数  不小于4位
        if (userAccount.length() < 4) {
//            return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");

        }
        // 1.3 校验密码位数  不小于8位
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
//            return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        if (planetCode.length()>5){
//            return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号过长");
        }
        // 1.5 校验账号不包含特殊字符
        /*  pP和pS匹配特殊符号
            \s+是空格一个或者多个,不管在那个位置都能匹配
            \pP 其中的小写 p 是 property 的意思，表示 Unicode 属性，用于 Unicode 正表达式的前缀。
            大写 P 表示 Unicode 字符集七个字符属性之一：标点字符。
            大写 P 表示 符号（比如数学符号、货币符号等）
        */
        String validPattern = "[\\u00A0\\s\"`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“'。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        // 如果找到了特殊的字符，就返回-1
        if (matcher.find()) {
//            return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不能包含特殊字符");
        }
        // 1.6 校验密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
//            return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入的密码不一致");
        }
        // 1.4 校验账号不能重复 放在最后 当其他校验通过时  再去数据库查询账号是否存在 防止性能浪费
//        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(User::getUserAccount, userAccount);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
//            return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号已存在");
        }
        //校验星球编号是否唯一（星球编号不能重复）
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode",planetCode);
         count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
//            return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号已存在");
        }


        // 2. 密码加密
//        final String SALT = "mark";
        //使用Spring的加密方法，采用MD5加密方式(这里加了盐 mark)
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));

        // 3. 向用户数据库插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if (!saveResult) {
//            return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"数据库已存在该条数据");
        }
        return user.getId();

    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        // 1.1 校验是否为空 引入apache common utils ：Apache Commons Lang 使用其中的方法：isAnyBlank 可判断多个字符串是否为空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            // TODO 修改为自定义异常
//            return null;
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        // 1.2 校验账号位数  不小于4位
        if (userAccount.length() < 4) {
//            return null;
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }
        // 1.3 校验密码位数  不小于8位
        if (userPassword.length() < 8 ) {
//            return null;
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        // 1.5 校验账号不包含特殊字符
        /*  pP和pS匹配特殊符号
            \s+是空格一个或者多个,不管在那个位置都能匹配
            \pP 其中的小写 p 是 property 的意思，表示 Unicode 属性，用于 Unicode 正表达式的前缀。
            大写 P 表示 Unicode 字符集七个字符属性之一：标点字符。
            大写 P 表示 符号（比如数学符号、货币符号等）
        */
        String validPattern = "[\\u00A0\\s\"`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“'。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        // 如果找到了特殊的字符，就返回-1
        if (matcher.find()) {
//            return null;
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不能包含特殊字符");
        }
      //  2、加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
          //查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        //用户不存在
        if(user==null){
            log.info("user login failed,userAccount Cannot match userPassword!");
//            return null;
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户登录失败，该用户不存在");
        }
      //3.用户脱敏
       User safetyUser = getSafetyUser(user);


        //4.记录用户的登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser){
        if (originUser==null){
            return null;
        }
        User safeUser = new User();
        safeUser.setId(originUser.getId());
        safeUser.setUsername(originUser.getUsername());
        safeUser.setUserAccount(originUser.getUserAccount());
        safeUser.setAvatarUrl(originUser.getAvatarUrl());
        safeUser.setGender(originUser.getGender());
        safeUser.setPhone(originUser.getPhone());
        safeUser.setEmail(originUser.getEmail());
        safeUser.setUserStatus(originUser.getUserStatus());
        safeUser.setCreateTime(originUser.getCreateTime());
        safeUser.setUserRole(originUser.getUserRole());
        safeUser.setPlanetCode(originUser.getPlanetCode());
        return safeUser;
    }

    /**
     * 用户注销
     * @param request
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




