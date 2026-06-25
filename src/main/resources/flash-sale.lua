-- 参数 1：发售品 id
local releaseItemId = ARGV[1]
-- 参数 2：用户 id
local userId = ARGV[2]

-- Redis 库存和下单记录 key
local stockKey = 'flash-sale:stock:' .. releaseItemId
local orderKey = 'flash-sale:order:' .. releaseItemId

-- 库存不足时直接返回 1
if (tonumber(redis.call('get', stockKey)) <= 0) then
    return 1
end

-- 同一用户重复抢购时返回 2
if (redis.call('sismember', orderKey, userId) == 1) then
    return 2
end

-- 扣减 Redis 预库存，并记录用户已抢购
redis.call('incrby', stockKey, -1)
redis.call('sadd', orderKey, userId)
return 0