package com.collectorhub.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.collectorhub.dto.Result;
import com.collectorhub.dto.UserDTO;
import com.collectorhub.entity.Review;
import com.collectorhub.entity.User;
import com.collectorhub.mapper.ReviewMapper;
import com.collectorhub.service.IReviewService;
import com.collectorhub.service.IUserService;
import com.collectorhub.utils.SystemConstants;
import com.collectorhub.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.collectorhub.utils.RedisConstants.REVIEW_LIKED_KEY;

/**
 * 开箱测评服务，使用 Redis ZSet 维护点赞记录和点赞排行榜。
 */
@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements IReviewService {

    @Resource
    private IUserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryHotReview(Integer current) {
        Page<Review> page = query()
                .orderByDesc("liked")
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        List<Review> records = page.getRecords();
        records.forEach(review -> {
            queryReviewUser(review);
            isReviewLiked(review);
        });
        return Result.ok(records);
    }

    @Override
    public Result queryReviewById(Long id) {
        Review review = getById(id);
        if (review == null) {
            return Result.fail("开箱测评不存在");
        }
        queryReviewUser(review);
        isReviewLiked(review);
        return Result.ok(review);
    }

    private void queryReviewUser(Review review) {
        User user = userService.getById(review.getUserId());
        review.setName(user.getNickName());
        review.setIcon(user.getIcon());
    }

    private void isReviewLiked(Review review) {
        UserDTO user = UserHolder.getUser();
        if (user == null) {
            return;
        }
        String key = REVIEW_LIKED_KEY + review.getId();
        Double score = stringRedisTemplate.opsForZSet().score(key, user.getId().toString());
        review.setIsLike(score != null);
    }

    @Override
    public Result likeReview(Long id) {
        Long userId = UserHolder.getUser().getId();
        String key = REVIEW_LIKED_KEY + id;
        Double score;
        try {
            score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        } catch (Exception e) {
            stringRedisTemplate.delete(key);
            score = null;
        }

        if (score == null) {
            boolean isSuccess = update().setSql("liked = liked + 1").eq("id", id).update();
            if (isSuccess) {
                stringRedisTemplate.opsForZSet().add(key, userId.toString(), System.currentTimeMillis());
            }
        } else {
            boolean isSuccess = update().setSql("liked = liked - 1").eq("id", id).update();
            if (isSuccess) {
                stringRedisTemplate.opsForZSet().remove(key, userId.toString());
            }
        }
        return Result.ok();
    }

    @Override
    public Result queryReviewLikes(Long id) {
        String key = REVIEW_LIKED_KEY + id;
        Set<String> top5 = stringRedisTemplate.opsForZSet().range(key, 0, 4);
        if (top5 == null || top5.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }
        List<Long> ids = top5.stream().map(Long::valueOf).collect(Collectors.toList());
        String idStr = StrUtil.join(",", ids);
        List<UserDTO> users = userService.query()
                .in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list()
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
        return Result.ok(users);
    }
}