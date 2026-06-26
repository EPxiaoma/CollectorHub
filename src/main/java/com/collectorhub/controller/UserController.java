package com.collectorhub.controller;

import com.collectorhub.dto.LoginFormDTO;
import com.collectorhub.dto.Result;
import com.collectorhub.dto.UserDTO;
import com.collectorhub.dto.UserProfileDTO;
import com.collectorhub.dto.UserProfileUpdateDTO;
import com.collectorhub.entity.User;
import com.collectorhub.entity.UserInfo;
import com.collectorhub.service.IUserInfoService;
import com.collectorhub.service.IUserService;
import com.collectorhub.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * User login, logout, and profile APIs.
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

    @GetMapping("/profile")
    public Result profile() {
        UserDTO currentUser = UserHolder.getUser();
        if (currentUser == null) {
            return Result.fail("Not logged in");
        }

        User user = userService.getById(currentUser.getId());
        if (user == null) {
            return Result.fail("User not found");
        }

        UserInfo info = userInfoService.getById(user.getId());
        return Result.ok(buildProfile(user, info));
    }

    @PutMapping("/profile")
    public Result updateProfile(@RequestBody UserProfileUpdateDTO updateDTO) {
        UserDTO currentUser = UserHolder.getUser();
        if (currentUser == null) {
            return Result.fail("Not logged in");
        }

        User user = new User();
        user.setId(currentUser.getId());
        user.setNickName(updateDTO.getNickName());
        user.setIcon(updateDTO.getIcon());
        userService.updateById(user);

        UserInfo info = userInfoService.getById(currentUser.getId());
        if (info == null) {
            info = new UserInfo();
            info.setUserId(currentUser.getId());
        }
        info.setCity(updateDTO.getCity());
        info.setIntroduce(updateDTO.getIntroduce());
        info.setGender(updateDTO.getGender());
        info.setBirthday(updateDTO.getBirthday());
        userInfoService.saveOrUpdate(info);

        User refreshedUser = userService.getById(currentUser.getId());
        return Result.ok(buildProfile(refreshedUser, info));
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

    private UserProfileDTO buildProfile(User user, UserInfo info) {
        UserProfileDTO profile = new UserProfileDTO();
        profile.setId(user.getId());
        profile.setPhone(user.getPhone());
        profile.setNickName(user.getNickName());
        profile.setIcon(user.getIcon());
        profile.setCreateTime(user.getCreateTime());

        if (info != null) {
            profile.setCity(info.getCity());
            profile.setIntroduce(info.getIntroduce());
            profile.setFans(info.getFans());
            profile.setFollowee(info.getFollowee());
            profile.setGender(info.getGender());
            profile.setBirthday(info.getBirthday());
            profile.setCredits(info.getCredits());
            profile.setLevel(info.getLevel());
        }
        return profile;
    }
}
