package com.collectorhub.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.collectorhub.dto.Result;
import com.collectorhub.entity.FlashSaleOrder;

/**
 * 限量发售抢购订单服务。
 */
public interface IFlashSaleOrderService extends IService<FlashSaleOrder> {

    Result rushBuy(Long releaseItemId);
}