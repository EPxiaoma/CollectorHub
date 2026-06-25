package com.collectorhub.service;

import com.collectorhub.dto.Result;
import com.collectorhub.entity.Collectible;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface ICollectibleService extends IService<Collectible> {

    Result queryById(Long id);

    @Transactional
    Result update(Collectible collectible);
}
