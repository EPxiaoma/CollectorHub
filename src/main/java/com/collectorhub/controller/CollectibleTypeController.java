package com.collectorhub.controller;

import com.collectorhub.dto.Result;
import com.collectorhub.entity.CollectibleType;
import com.collectorhub.service.ICollectibleTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 潮玩分类接口。
 */
@RestController
@RequestMapping("/collectible-types")
public class CollectibleTypeController {
    @Resource
    private ICollectibleTypeService typeService;

    @GetMapping("/list")
    public Result queryTypeList() {
        List<CollectibleType> typeList = typeService.query().orderByAsc("sort").list();
        return Result.ok(typeList);
    }
}