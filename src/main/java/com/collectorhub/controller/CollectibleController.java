package com.collectorhub.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collectorhub.dto.Result;
import com.collectorhub.entity.Collectible;
import com.collectorhub.service.ICollectibleService;
import com.collectorhub.utils.SystemConstants;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 潮玩单品接口。
 */
@RestController
@RequestMapping("/collectibles")
public class CollectibleController {

    @Resource
    public ICollectibleService collectibleService;

    @GetMapping("/{id}")
    public Result queryCollectibleById(@PathVariable("id") Long id) {
        return collectibleService.queryById(id);
    }

    @PostMapping
    public Result saveCollectible(@RequestBody Collectible collectible) {
        collectibleService.save(collectible);
        return Result.ok(collectible.getId());
    }

    @PutMapping
    public Result updateCollectible(@RequestBody Collectible collectible) {
        return collectibleService.update(collectible);
    }

    @GetMapping("/of/type")
    public Result queryCollectibleByType(
            @RequestParam("typeId") Integer typeId,
            @RequestParam(value = "current", defaultValue = "1") Integer current
    ) {
        Page<Collectible> page = collectibleService.query()
                .eq("type_id", typeId)
                .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
        return Result.ok(page.getRecords());
    }

    @GetMapping("/of/name")
    public Result queryCollectibleByName(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "current", defaultValue = "1") Integer current
    ) {
        Page<Collectible> page = collectibleService.query()
                .like(StrUtil.isNotBlank(name), "name", name)
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        return Result.ok(page.getRecords());
    }
}