package com.collectorhub.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.collectorhub.dto.Result;
import com.collectorhub.entity.FlashSaleOrder;
import com.collectorhub.mapper.FlashSaleOrderMapper;
import com.collectorhub.service.IFlashSaleItemService;
import com.collectorhub.service.IFlashSaleOrderService;
import com.collectorhub.utils.RedisIdWorker;
import com.collectorhub.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 限量发售抢购订单服务，实现 Lua 资格校验、RocketMQ 异步下单和乐观锁扣减库存。
 */
@Slf4j
@Service
@RocketMQMessageListener(topic = "collectorhub-flash-sale-order", consumerGroup = "collectorhub-flash-sale-consumer")
public class FlashSaleOrderServiceImpl extends ServiceImpl<FlashSaleOrderMapper, FlashSaleOrder>
        implements IFlashSaleOrderService, RocketMQListener<FlashSaleOrder> {

    private static final BlockingQueue<FlashSaleOrder> ORDER_TASKS = new ArrayBlockingQueue<>(1024 * 1024);
    private static final ExecutorService ORDER_EXECUTOR = Executors.newSingleThreadExecutor();
    private static final DefaultRedisScript<Long> FLASH_SALE_SCRIPT;

    static {
        FLASH_SALE_SCRIPT = new DefaultRedisScript<>();
        FLASH_SALE_SCRIPT.setLocation(new ClassPathResource("flash-sale.lua"));
        FLASH_SALE_SCRIPT.setResultType(Long.class);
    }

    @Resource
    private IFlashSaleItemService flashSaleItemService;

    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired(required = false)
    private RocketMQTemplate rocketMQTemplate;

    @Value("${collectorhub.mq.flash-sale-topic:collectorhub-flash-sale-order}")
    private String flashSaleTopic;

    @PostConstruct
    private void initOrderWorker() {
        ORDER_EXECUTOR.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    FlashSaleOrder order = ORDER_TASKS.take();
                    createFlashSaleOrder(order);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    log.error("处理限量发售抢购订单失败", e);
                }
            }
        });
    }

    @PreDestroy
    private void destroyOrderWorker() {
        ORDER_EXECUTOR.shutdownNow();
    }

    @Override
    public Result rushBuy(Long releaseItemId) {
        Long userId = UserHolder.getUser().getId();
        Long result = stringRedisTemplate.execute(
                FLASH_SALE_SCRIPT,
                Collections.emptyList(),
                releaseItemId.toString(), userId.toString()
        );

        int code = result == null ? 1 : result.intValue();
        if (code != 0) {
            return Result.fail(code == 1 ? "库存不足" : "不能重复抢购");
        }

        FlashSaleOrder order = new FlashSaleOrder();
        order.setId(redisIdWorker.nextId("flashSaleOrder"));
        order.setUserId(userId);
        order.setReleaseItemId(releaseItemId);
        order.setStatus(1);
        sendOrderMessage(order);
        return Result.ok(order.getId());
    }

    @Override
    public void onMessage(FlashSaleOrder order) {
        createFlashSaleOrder(order);
    }

    private void sendOrderMessage(FlashSaleOrder order) {
        if (rocketMQTemplate != null) {
            try {
                rocketMQTemplate.convertAndSend(flashSaleTopic, order);
                return;
            } catch (Exception e) {
                log.warn("RocketMQ 发送抢购订单失败，切换为本地队列兜底", e);
            }
        }
        ORDER_TASKS.add(order);
    }

    /**
     * 异步落库时再次校验一人一单，并通过乐观锁扣减库存。
     */
    @Transactional
    public void createFlashSaleOrder(FlashSaleOrder order) {
        Long userId = order.getUserId();
        Long releaseItemId = order.getReleaseItemId();
        int count = query().eq("user_id", userId).eq("release_item_id", releaseItemId).count();
        if (count > 0) {
            log.warn("用户重复抢购同一发售品，userId={}, releaseItemId={}", userId, releaseItemId);
            return;
        }

        boolean success = flashSaleItemService.update()
                .setSql("stock = stock - 1")
                .eq("release_item_id", releaseItemId)
                .gt("stock", 0)
                .update();
        if (!success) {
            log.warn("发售品库存扣减失败，releaseItemId={}", releaseItemId);
            return;
        }
        save(order);
    }
}