package com.hmdp.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisData;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    private final StringRedisTemplate stringRedisTemplate;

    public ShopServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    // 线程池：用于异步重建缓存，固定10个线程，避免频繁创建销毁线程的开销
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    @Override
    public Result queryById(Long id) {
        // 1. 逻辑过期解决缓存击穿
        Shop shop = queryWithLogicalExpire(id);
        if (shop == null) {
            return Result.fail("店铺不存在！");
        }
        // 2. 返回
        return Result.ok(shop);
    }

    // 逻辑过期解决缓存击穿
    public Shop queryWithLogicalExpire(Long id) {
        // 1. 尝试从 Redis 获取缓存
        String shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);

        // 2. 判断缓存是否命中
        if (StrUtil.isBlank(shopJson)) {
            return null;
        }

        // 3. 缓存不存在：说明该数据未预热
        if (StrUtil.isBlank(shopJson)) {
            return null;
        }

        // 4，命中，需要先把 json 反序列化为对象，提取店铺数据与逻辑过期时间
        RedisData redisData = JSONUtil.toBean(shopJson, RedisData.class);
        JSONObject data = (JSONObject) redisData.getData();
        Shop shop = JSONUtil.toBean(data, Shop.class);
        LocalDateTime expireTime = redisData.getExpireTime();

        // 5. 校验过期时间是否为空，未过期则直接返回店铺信息
        if(expireTime.isAfter(LocalDateTime.now())) {
            return shop;
        }

        // 6. 缓存已过期，尝试获取互斥锁以触发异步重建
        // 6.1. 获取互斥锁
        boolean isLock = tryLock(LOCK_SHOP_KEY + id);
        // 6.2. 判断是否获取锁成功
        if (isLock) {
            // 6.3. 成功，开启独立线程，实现缓存重建
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    // 重建缓存
                    this.saveShop2Redis(id, 20L);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }finally {
                    // 释放锁
                    unlock(LOCK_SHOP_KEY + id);
                }
            });
        }
        // 6.4. 返回过期的商铺信息
        return shop;
    }

    // 查询数据库缓存数据
    public void saveShop2Redis(Long id, Long expireSeconds) throws InterruptedException {
        // 1. 查询店铺数据
        Shop shop = getById(id);
        Thread.sleep(200);
        // 2. 封装逻辑过期时间
        RedisData redisData = new RedisData();
        redisData.setData(shop);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds((expireSeconds)));
        // 3. 写入 Redis
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(redisData));
    }

    // 互斥锁解决缓存击穿
    public Shop queryWithMutex(Long id) {
        // 1. 尝试从 Redis 获取缓存
        String shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);

        // 2. 判断缓存是否命中
        if (StrUtil.isNotBlank(shopJson)) {

            return JSONUtil.toBean(shopJson, Shop.class);
        }

        // 3. 缓存命中：判断是否为预留的“空对象”解决缓存穿透
        if ("".equals(shopJson)) {
            return null;
        }

        // 4. 缓存未命中，尝试获取互斥锁解决缓存击穿
        Shop shop = null;
        try {
            boolean isLock = tryLock(LOCK_SHOP_KEY + id);

            // 4.1 获取锁失败：休眠并重试（自旋）
            if (!isLock) {
                Thread.sleep(50);
                return queryWithMutex(id);
            }

            // 4.2 获取锁成功：执行 Double-Check（二次检查缓存是否存在），目的：防止在等待锁期间，前一个线程已经更新了缓存
            shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);
            if (StrUtil.isNotBlank(shopJson)) {
                return JSONUtil.toBean(shopJson, Shop.class);
            }

            // 5. 查询数据库
            shop = getById(id);

            // 6. 数据库数据校验
            if (shop == null) {
                // 7. 数据库未命中：将空值存入 Redis，并设置较短的过期时间（解决缓存穿透）
                stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
                return null;
            }

            /// 7. 数据库命中：将查询结果回写 Redis，并释放锁
            stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            // 8. 释放互斥锁
            unlock(LOCK_SHOP_KEY + id);
        }

        // 9. 返回
        return shop;
    }

    // 获取锁
    private boolean tryLock(String key) {
        Boolean falg = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(falg);
    }

    // 释放锁
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }

    @Transactional
    @Override
    public Result update(Shop shop) {
        Long id = shop.getId();
        if (id == null) {
            return Result.fail("店铺 id 不能为空");
        }
        // 1. 更新数据库
        updateById(shop);
        // 2. 删除缓存
        stringRedisTemplate.delete(CACHE_SHOP_KEY + shop.getId());
        return Result.ok();
    }
}
