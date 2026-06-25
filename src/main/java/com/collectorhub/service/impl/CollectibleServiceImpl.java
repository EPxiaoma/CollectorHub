package com.collectorhub.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.collectorhub.dto.Result;
import com.collectorhub.entity.Collectible;
import com.collectorhub.mapper.CollectibleMapper;
import com.collectorhub.service.ICollectibleService;
import com.collectorhub.utils.RedisData;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.collectorhub.utils.RedisConstants.*;

/**
 * 潮玩单品服务，提供多级缓存、缓存穿透和缓存击穿防护。
 */
@Service
public class CollectibleServiceImpl extends ServiceImpl<CollectibleMapper, Collectible> implements ICollectibleService {

    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);
    private static final Cache<Long, Collectible> LOCAL_COLLECTIBLE_CACHE = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    private final StringRedisTemplate stringRedisTemplate;

    public CollectibleServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Result queryById(Long id) {
        Collectible localCollectible = LOCAL_COLLECTIBLE_CACHE.getIfPresent(id);
        if (localCollectible != null) {
            return Result.ok(localCollectible);
        }

        Collectible collectible = queryWithMutex(id);
        if (collectible == null) {
            return Result.fail("潮玩单品不存在");
        }
        LOCAL_COLLECTIBLE_CACHE.put(id, collectible);
        return Result.ok(collectible);
    }

    /**
     * 通过逻辑过期时间解决热点单品缓存击穿。
     */
    public Collectible queryWithLogicalExpire(Long id) {
        String collectibleJson = stringRedisTemplate.opsForValue().get(CACHE_COLLECTIBLE_KEY + id);
        if (StrUtil.isBlank(collectibleJson)) {
            return null;
        }

        RedisData redisData = JSONUtil.toBean(collectibleJson, RedisData.class);
        JSONObject data = (JSONObject) redisData.getData();
        Collectible collectible = JSONUtil.toBean(data, Collectible.class);
        LocalDateTime expireTime = redisData.getExpireTime();
        if (expireTime.isAfter(LocalDateTime.now())) {
            return collectible;
        }

        boolean isLock = tryLock(LOCK_COLLECTIBLE_KEY + id);
        if (isLock) {
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    saveCollectible2Redis(id, 20L);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    unlock(LOCK_COLLECTIBLE_KEY + id);
                }
            });
        }
        return collectible;
    }

    /**
     * 预热潮玩单品缓存，并写入逻辑过期时间。
     */
    public void saveCollectible2Redis(Long id, Long expireSeconds) throws InterruptedException {
        Collectible collectible = getById(id);
        Thread.sleep(200);
        RedisData redisData = new RedisData();
        redisData.setData(collectible);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));
        stringRedisTemplate.opsForValue().set(CACHE_COLLECTIBLE_KEY + id, JSONUtil.toJsonStr(redisData));
        if (collectible != null) {
            LOCAL_COLLECTIBLE_CACHE.put(id, collectible);
        }
    }

    /**
     * 通过互斥锁解决缓存击穿，通过空值缓存解决缓存穿透。
     */
    public Collectible queryWithMutex(Long id) {
        String collectibleJson = stringRedisTemplate.opsForValue().get(CACHE_COLLECTIBLE_KEY + id);
        if (StrUtil.isNotBlank(collectibleJson)) {
            return JSONUtil.toBean(collectibleJson, Collectible.class);
        }
        if ("".equals(collectibleJson)) {
            return null;
        }

        Collectible collectible = null;
        try {
            boolean isLock = tryLock(LOCK_COLLECTIBLE_KEY + id);
            if (!isLock) {
                Thread.sleep(50);
                return queryWithMutex(id);
            }

            collectibleJson = stringRedisTemplate.opsForValue().get(CACHE_COLLECTIBLE_KEY + id);
            if (StrUtil.isNotBlank(collectibleJson)) {
                return JSONUtil.toBean(collectibleJson, Collectible.class);
            }

            collectible = getById(id);
            if (collectible == null) {
                stringRedisTemplate.opsForValue().set(CACHE_COLLECTIBLE_KEY + id, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
                return null;
            }
            stringRedisTemplate.opsForValue().set(CACHE_COLLECTIBLE_KEY + id, JSONUtil.toJsonStr(collectible), CACHE_COLLECTIBLE_TTL, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            unlock(LOCK_COLLECTIBLE_KEY + id);
        }
        return collectible;
    }

    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", LOCK_COLLECTIBLE_TTL, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }

    @Transactional
    @Override
    public Result update(Collectible collectible) {
        Long id = collectible.getId();
        if (id == null) {
            return Result.fail("潮玩单品 id 不能为空");
        }
        updateById(collectible);
        stringRedisTemplate.delete(CACHE_COLLECTIBLE_KEY + id);
        LOCAL_COLLECTIBLE_CACHE.invalidate(id);
        return Result.ok();
    }
}