package com.collectorhub.controller;

import com.collectorhub.dto.LoginFormDTO;
import com.collectorhub.dto.Result;
import com.collectorhub.dto.UserDTO;
import com.collectorhub.entity.UserInfo;
import com.collectorhub.service.IUserInfoService;
import com.collectorhub.service.IUserService;
import com.collectorhub.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户登录、登出和个人信息接口。
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private IUserInfoService userInfoService;

    @PostMapping("/code")
    public Result sendCode(@RequestParam("phone") String phone) {
        return userService.sendCode(phone);
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm) {
        return userService.login(loginForm);
    }

    @PostMapping("/logout")
    public Result logout(HttpServletRequest request) {
        return userService.logout(request.getHeader("authorization"));
    }

    @GetMapping("/me")
    public Result me() {
        UserDTO user = UserHolder.getUser();
        return Result.ok(user);
    }

    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long userId) {
        UserInfo info = userInfoService.getById(userId);
        if (info == null) {
            return Result.ok();
        }
        info.setCreateTime(null);
        info.setUpdateTime(null);
        return Result.ok(info);
    }
}