package com.collectorhub.service;

import com.collectorhub.dto.Result;
import com.collectorhub.entity.Review;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface IReviewService extends IService<Review> {

    Result queryReviewById(Long id);

    Result queryHotReview(Integer current);

    Result likeReview(Long id);

    Result queryReviewLikes(Long id);
}
