package com.collectorhub.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileUpdateDTO {
    private String nickName;
    private String icon;
    private String city;
    private String introduce;
    private Boolean gender;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
