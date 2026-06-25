package com.collectorhub.service.impl;

import com.collectorhub.entity.ReviewComments;
import com.collectorhub.mapper.ReviewCommentsMapper;
import com.collectorhub.service.IReviewCommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 */
@Service
public class ReviewCommentsServiceImpl extends ServiceImpl<ReviewCommentsMapper, ReviewComments> implements IReviewCommentsService {

}
