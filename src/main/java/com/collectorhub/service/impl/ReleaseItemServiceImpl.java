package com.collectorhub.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.collectorhub.dto.Result;
import com.collectorhub.entity.FlashSaleItem;
import com.collectorhub.entity.ReleaseItem;
import com.collectorhub.mapper.ReleaseItemMapper;
import com.collectorhub.service.IFlashSaleItemService;
import com.collectorhub.service.IReleaseItemService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.collectorhub.utils.RedisConstants.FLASH_SALE_STOCK_KEY;

/**
 * 发售品服务，负责发售信息查询和限量抢购库存预热。
 */
@Service
public class ReleaseItemServiceImpl extends ServiceImpl<ReleaseItemMapper, ReleaseItem> implements IReleaseItemService {

    @Resource
    private IFlashSaleItemService flashSaleItemService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryReleaseItemOfCollectible(Long collectibleId) {
        List<ReleaseItem> releaseItems = getBaseMapper().queryReleaseItemOfCollectible(collectibleId);
        return Result.ok(releaseItems);
    }

    @Override
    @Transactional
    public void addFlashSaleItem(ReleaseItem releaseItem) {
        save(releaseItem);
        FlashSaleItem flashSaleItem = new FlashSaleItem();
        flashSaleItem.setReleaseItemId(releaseItem.getId());
        flashSaleItem.setStock(releaseItem.getStock());
        flashSaleItem.setBeginTime(releaseItem.getBeginTime());
        flashSaleItem.setEndTime(releaseItem.getEndTime());
        flashSaleItemService.save(flashSaleItem);
        stringRedisTemplate.opsForValue().set(FLASH_SALE_STOCK_KEY + releaseItem.getId(), releaseItem.getStock().toString());
    }
}