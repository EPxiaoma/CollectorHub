package com.collectorhub.service;

import com.collectorhub.dto.Result;
import com.collectorhub.entity.ReleaseItem;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface IReleaseItemService extends IService<ReleaseItem> {

    Result queryReleaseItemOfCollectible(Long collectibleId);

    void addFlashSaleItem(ReleaseItem releaseItem);
}
