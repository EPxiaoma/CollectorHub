package com.collectorhub.service.impl;

import com.collectorhub.entity.FlashSaleItem;
import com.collectorhub.mapper.FlashSaleItemMapper;
import com.collectorhub.service.IFlashSaleItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 限量抢购发售品表，与发售品是一对一关系 服务实现类
 * </p>
 *
 */
@Service
public class FlashSaleItemServiceImpl extends ServiceImpl<FlashSaleItemMapper, FlashSaleItem> implements IFlashSaleItemService {

}
