package com.collectorhub.utils;

public class RedisConstants {
    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 2L;
    public static final String LOGIN_USER_KEY = "login:token:";
    public static final Long LOGIN_USER_TTL = 36000L;

    public static final Long CACHE_NULL_TTL = 2L;

    public static final Long CACHE_COLLECTIBLE_TTL = 30L;
    public static final String CACHE_COLLECTIBLE_KEY = "cache:collectible:";

    public static final String LOCK_COLLECTIBLE_KEY = "lock:collectible:";
    public static final Long LOCK_COLLECTIBLE_TTL = 10L;

    public static final String FLASH_SALE_STOCK_KEY = "flash-sale:stock:";
    public static final String REVIEW_LIKED_KEY = "review:liked:";
    public static final String FEED_KEY = "feed:";
    public static final String COLLECTIBLE_GEO_KEY = "collectible:geo:";
    public static final String USER_SIGN_KEY = "sign:";
}
