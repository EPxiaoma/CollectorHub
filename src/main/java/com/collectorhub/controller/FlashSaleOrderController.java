package com.collectorhub.controller;

import com.collectorhub.dto.Result;
import com.collectorhub.service.IFlashSaleOrderService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 限量发售抢购订单接口。
 */
@RestController
@RequestMapping("/flash-sale-orders")
public class FlashSaleOrderController {

    @Resource
    private IFlashSaleOrderService flashSaleOrderService;

    @PostMapping("/rush/{id}")
    public Result rushBuy(@PathVariable("id") Long releaseItemId) {
        return flashSaleOrderService.rushBuy(releaseItemId);
    }
}