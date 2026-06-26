package com.collectorhub.controller;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.collectorhub.dto.UserDTO;
import com.collectorhub.entity.User;
import com.collectorhub.entity.UserInfo;
import com.collectorhub.service.IUserInfoService;
import com.collectorhub.service.IUserService;
import com.collectorhub.utils.UserHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    @AfterEach
    void tearDown() {
        UserHolder.removeUser();
    }

    @Test
    void userInfoUserIdIsInsertedFromCurrentUserId() throws Exception {
        Field userId = UserInfo.class.getDeclaredField("userId");
        TableId tableId = userId.getAnnotation(TableId.class);

        assertEquals("user_id", tableId.value());
        assertEquals(IdType.INPUT, tableId.type());
    }

    @Test
    void profileReturnsCurrentUserBasicInfoAndUserInfoWithoutPassword() throws Exception {
        IUserService userService = mock(IUserService.class);
        IUserInfoService userInfoService = mock(IUserInfoService.class);
        UserController controller = new UserController();
        ReflectionTestUtils.setField(controller, "userService", userService);
        ReflectionTestUtils.setField(controller, "userInfoService", userInfoService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        UserDTO currentUser = new UserDTO();
        currentUser.setId(7L);
        currentUser.setNickName("Mina");
        currentUser.setIcon("/imgs/icons/mina.png");
        UserHolder.saveUser(currentUser);

        User user = new User();
        user.setId(7L);
        user.setPhone("18800000007");
        user.setPassword("secret-password");
        user.setNickName("Mina");
        user.setIcon("/imgs/icons/mina.png");
        user.setCreateTime(LocalDateTime.of(2026, 3, 5, 9, 30));
        when(userService.getById(7L)).thenReturn(user);

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(7L);
        userInfo.setCity("Shanghai");
        userInfo.setIntroduce("Collector of limited art toys");
        userInfo.setFans(12);
        userInfo.setFollowee(8);
        userInfo.setGender(true);
        userInfo.setBirthday(LocalDate.of(1995, 8, 12));
        userInfo.setCredits(360);
        userInfo.setLevel(true);
        when(userInfoService.getById(7L)).thenReturn(userInfo);

        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(7))
                .andExpect(jsonPath("$.data.phone").value("18800000007"))
                .andExpect(jsonPath("$.data.nickName").value("Mina"))
                .andExpect(jsonPath("$.data.icon").value("/imgs/icons/mina.png"))
                .andExpect(jsonPath("$.data.city").value("Shanghai"))
                .andExpect(jsonPath("$.data.introduce").value("Collector of limited art toys"))
                .andExpect(jsonPath("$.data.fans").value(12))
                .andExpect(jsonPath("$.data.followee").value(8))
                .andExpect(jsonPath("$.data.gender").value(true))
                .andExpect(jsonPath("$.data.birthday").value("1995-08-12"))
                .andExpect(jsonPath("$.data.credits").value(360))
                .andExpect(jsonPath("$.data.level").value(true))
                .andExpect(jsonPath("$.data.createTime").value("2026-03-05 09:30:00"))
                .andExpect(jsonPath("$.data", not(hasKey("password"))));
    }

    @Test
    void updateProfileChangesOnlyEditableFieldsForCurrentUser() throws Exception {
        IUserService userService = mock(IUserService.class);
        IUserInfoService userInfoService = mock(IUserInfoService.class);
        UserController controller = new UserController();
        ReflectionTestUtils.setField(controller, "userService", userService);
        ReflectionTestUtils.setField(controller, "userInfoService", userInfoService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        UserDTO currentUser = new UserDTO();
        currentUser.setId(7L);
        currentUser.setNickName("Old Mina");
        UserHolder.saveUser(currentUser);

        User existingUser = new User();
        existingUser.setId(7L);
        existingUser.setPhone("18800000007");
        existingUser.setNickName("Mina");
        existingUser.setIcon("/imgs/icons/mina-new.png");
        existingUser.setCreateTime(LocalDateTime.of(2026, 3, 5, 9, 30));
        when(userService.getById(7L)).thenReturn(existingUser);

        UserInfo existingInfo = new UserInfo();
        existingInfo.setUserId(7L);
        existingInfo.setFans(12);
        existingInfo.setFollowee(8);
        existingInfo.setCredits(360);
        existingInfo.setLevel(true);
        when(userInfoService.getById(7L)).thenReturn(existingInfo);

        mockMvc.perform(put("/user/profile")
                        .contentType("application/json")
                        .content("{\"nickName\":\"Mina\",\"icon\":\"/imgs/icons/mina-new.png\",\"city\":\"Hangzhou\",\"introduce\":\"Updated collector profile\",\"gender\":false,\"birthday\":\"1996-09-13\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(7))
                .andExpect(jsonPath("$.data.phone").value("18800000007"))
                .andExpect(jsonPath("$.data.nickName").value("Mina"))
                .andExpect(jsonPath("$.data.icon").value("/imgs/icons/mina-new.png"))
                .andExpect(jsonPath("$.data.city").value("Hangzhou"))
                .andExpect(jsonPath("$.data.introduce").value("Updated collector profile"))
                .andExpect(jsonPath("$.data.gender").value(false))
                .andExpect(jsonPath("$.data.birthday").value("1996-09-13"))
                .andExpect(jsonPath("$.data.credits").value(360));

        verify(userService).updateById(argThat(user ->
                Long.valueOf(7L).equals(user.getId())
                        && "Mina".equals(user.getNickName())
                        && "/imgs/icons/mina-new.png".equals(user.getIcon())
                        && user.getPhone() == null
                        && user.getPassword() == null
        ));
        verify(userInfoService).saveOrUpdate(argThat(info ->
                Long.valueOf(7L).equals(info.getUserId())
                        && "Hangzhou".equals(info.getCity())
                        && "Updated collector profile".equals(info.getIntroduce())
                        && Boolean.FALSE.equals(info.getGender())
                        && LocalDate.of(1996, 9, 13).equals(info.getBirthday())
                        && Integer.valueOf(12).equals(info.getFans())
                        && Integer.valueOf(360).equals(info.getCredits())
                        && Boolean.TRUE.equals(info.getLevel())
        ));
    }
}
