package com.collectorhub.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.collectorhub.dto.Result;
import com.collectorhub.dto.UserDTO;
import com.collectorhub.entity.Review;
import com.collectorhub.service.IReviewService;
import com.collectorhub.utils.SystemConstants;
import com.collectorhub.utils.UserHolder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 玩家开箱测评接口。
 */
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Resource
    private IReviewService reviewService;

    @PostMapping
    public Result saveReview(@RequestBody Review review) {
        UserDTO user = UserHolder.getUser();
        review.setUserId(user.getId());
        reviewService.save(review);
        return Result.ok(review.getId());
    }

    @PutMapping("/like/{id}")
    public Result likeReview(@PathVariable("id") Long id) {
        return reviewService.likeReview(id);
    }

    @GetMapping("/of/me")
    public Result queryMyReview(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        UserDTO user = UserHolder.getUser();
        Page<Review> page = reviewService.query()
                .eq("user_id", user.getId()).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        List<Review> records = page.getRecords();
        return Result.ok(records);
    }

    @GetMapping("/hot")
    public Result queryHotReview(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return reviewService.queryHotReview(current);
    }

    @GetMapping("/{id}")
    public Result queryReviewById(@PathVariable("id") Long id) {
        return reviewService.queryReviewById(id);
    }

    @GetMapping("/likes/{id}")
    public Result queryReviewLikes(@PathVariable("id") Long id) {
        return reviewService.queryReviewLikes(id);
    }
}