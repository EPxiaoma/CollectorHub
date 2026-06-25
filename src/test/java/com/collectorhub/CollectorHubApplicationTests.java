package com.collectorhub;

import com.collectorhub.entity.Collectible;
import com.collectorhub.service.impl.CollectibleServiceImpl;
import com.collectorhub.utils.CacheClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

import static com.collectorhub.utils.RedisConstants.CACHE_COLLECTIBLE_KEY;

@SpringBootTest
class CollectorHubApplicationTests {

    @Resource
    private CacheClient cacheClient;

    @Resource
    private CollectibleServiceImpl collectibleService;

    @Test
    void testSaveCollectible() {
        Collectible collectible = collectibleService.getById(2L);
        cacheClient.setWithLogicalExpire(CACHE_COLLECTIBLE_KEY + 1L, collectible, 10L, TimeUnit.SECONDS);
    }

}
