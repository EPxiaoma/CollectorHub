package com.collectorhub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.collectorhub.dto.LoginFormDTO;
import com.collectorhub.dto.Result;
import com.collectorhub.entity.User;

/**
 * 用户服务，负责短信验证码登录和 Redis 会话维护。
 */
public interface IUserService extends IService<User> {

    Result sendCode(String phone);

    Result login(LoginFormDTO loginForm);

    Result logout(String token);
}