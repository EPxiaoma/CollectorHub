package com.collectorhub.controller;

import com.collectorhub.dto.Result;
import com.collectorhub.entity.ReleaseItem;
import com.collectorhub.service.IReleaseItemService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 发售品接口。
 */
@RestController
@RequestMapping("/release-items")
public class ReleaseItemController {

    @Resource
    private IReleaseItemService releaseItemService;

    @PostMapping
    public Result addReleaseItem(@RequestBody ReleaseItem releaseItem) {
        releaseItemService.save(releaseItem);
        return Result.ok(releaseItem.getId());
    }

    @PostMapping("/flash-sale")
    public Result addFlashSaleItem(@RequestBody ReleaseItem releaseItem) {
        releaseItemService.addFlashSaleItem(releaseItem);
        return Result.ok(releaseItem.getId());
    }

    @GetMapping("/list/{collectibleId}")
    public Result queryReleaseItemOfCollectible(@PathVariable("collectibleId") Long collectibleId) {
       return releaseItemService.queryReleaseItemOfCollectible(collectibleId);
    }
}