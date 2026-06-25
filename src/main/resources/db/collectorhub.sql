-- CollectorHub 数据库初始化脚本
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `tb_review`;
CREATE TABLE `tb_review` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `collectible_id` bigint(20) NOT NULL COMMENT '关联潮玩单品id',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
  `title` varchar(255) NOT NULL COMMENT '测评标题',
  `images` varchar(2048) NOT NULL COMMENT '开箱测评图片，最多9张，多张以逗号隔开',
  `content` varchar(2048) NOT NULL COMMENT '开箱测评正文',
  `liked` int(8) UNSIGNED DEFAULT 0 COMMENT '点赞数量',
  `comments` int(8) UNSIGNED DEFAULT 0 COMMENT '评论数量',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家开箱测评表';

DROP TABLE IF EXISTS `tb_review_comments`;
CREATE TABLE `tb_review_comments` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
  `review_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联开箱测评id',
  `parent_id` bigint(20) UNSIGNED DEFAULT NULL COMMENT '父评论id',
  `answer_id` bigint(20) UNSIGNED DEFAULT NULL COMMENT '回复评论id',
  `content` varchar(255) NOT NULL COMMENT '评论内容',
  `liked` int(8) UNSIGNED DEFAULT 0 COMMENT '点赞数量',
  `status` tinyint(1) UNSIGNED DEFAULT 0 COMMENT '状态，0正常，1隐藏',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='开箱测评评论表';

DROP TABLE IF EXISTS `tb_follow`;
CREATE TABLE `tb_follow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
  `follow_user_id` bigint(20) UNSIGNED NOT NULL COMMENT '被关注用户id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_follow` (`user_id`, `follow_user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注表';

DROP TABLE IF EXISTS `tb_flash_sale_item`;
CREATE TABLE `tb_flash_sale_item` (
  `release_item_id` bigint(20) UNSIGNED NOT NULL COMMENT '关联发售品id',
  `stock` int(8) NOT NULL COMMENT '抢购库存',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `begin_time` timestamp NOT NULL COMMENT '抢购开始时间',
  `end_time` timestamp NOT NULL COMMENT '抢购结束时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`release_item_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='限量发售配置表';

DROP TABLE IF EXISTS `tb_collectible`;
CREATE TABLE `tb_collectible` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(128) NOT NULL COMMENT '潮玩单品名称',
  `type_id` bigint(20) UNSIGNED NOT NULL COMMENT '潮玩分类id',
  `images` varchar(1024) NOT NULL COMMENT '潮玩单品图片，多个图片以逗号隔开',
  `area` varchar(128) DEFAULT NULL COMMENT '品牌或系列',
  `address` varchar(255) DEFAULT NULL COMMENT '发售渠道',
  `x` double DEFAULT NULL COMMENT '经度，线上发售可为空',
  `y` double DEFAULT NULL COMMENT '纬度，线上发售可为空',
  `avg_price` bigint(10) UNSIGNED DEFAULT 0 COMMENT '参考价格，单位分',
  `sold` int(8) UNSIGNED DEFAULT 0 COMMENT '售出数量',
  `comments` int(8) UNSIGNED DEFAULT 0 COMMENT '测评数量',
  `score` int(2) UNSIGNED DEFAULT 0 COMMENT '评分，0到50',
  `open_hours` varchar(32) DEFAULT NULL COMMENT '发售时间说明',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_type_id` (`type_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='潮玩单品表';

DROP TABLE IF EXISTS `tb_collectible_type`;
CREATE TABLE `tb_collectible_type` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) NOT NULL COMMENT '潮玩分类名称',
  `icon` varchar(255) DEFAULT NULL COMMENT '分类图标',
  `sort` int(4) UNSIGNED DEFAULT 0 COMMENT '排序',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='潮玩分类表';

DROP TABLE IF EXISTS `tb_sign`;
CREATE TABLE `tb_sign` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
  `year` year NOT NULL COMMENT '签到年份',
  `month` tinyint(2) NOT NULL COMMENT '签到月份',
  `date` date NOT NULL COMMENT '签到日期',
  `is_backup` tinyint(1) UNSIGNED DEFAULT 0 COMMENT '是否补签',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户签到表';

DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `phone` varchar(11) NOT NULL COMMENT '手机号',
  `password` varchar(128) DEFAULT '' COMMENT '密码',
  `nick_name` varchar(32) DEFAULT '' COMMENT '昵称',
  `icon` varchar(255) DEFAULT '' COMMENT '头像',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_phone` (`phone`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

DROP TABLE IF EXISTS `tb_user_info`;
CREATE TABLE `tb_user_info` (
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
  `city` varchar(64) DEFAULT NULL COMMENT '城市',
  `introduce` varchar(128) DEFAULT NULL COMMENT '个人介绍',
  `fans` int(8) UNSIGNED DEFAULT 0 COMMENT '粉丝数量',
  `followee` int(8) UNSIGNED DEFAULT 0 COMMENT '关注数量',
  `gender` tinyint(1) UNSIGNED DEFAULT 0 COMMENT '性别，0男，1女',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `credits` int(8) UNSIGNED DEFAULT 0 COMMENT '积分',
  `level` tinyint(1) UNSIGNED DEFAULT 0 COMMENT '会员等级',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户资料表';

DROP TABLE IF EXISTS `tb_release_item`;
CREATE TABLE `tb_release_item` (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `collectible_id` bigint(20) UNSIGNED DEFAULT NULL COMMENT '关联潮玩单品id',
  `title` varchar(255) NOT NULL COMMENT '发售品标题',
  `sub_title` varchar(255) DEFAULT NULL COMMENT '发售副标题',
  `rules` varchar(1024) DEFAULT NULL COMMENT '发售规则',
  `pay_value` bigint(10) UNSIGNED NOT NULL COMMENT '支付金额，单位分',
  `actual_value` bigint(10) NOT NULL COMMENT '标价金额，单位分',
  `type` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '类型，0普通发售，1限量抢购',
  `status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态，1上架，2下架，3过期',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_collectible_id` (`collectible_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发售品表';

DROP TABLE IF EXISTS `tb_flash_sale_order`;
CREATE TABLE `tb_flash_sale_order` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '下单用户id',
  `release_item_id` bigint(20) UNSIGNED NOT NULL COMMENT '购买的发售品id',
  `pay_type` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '支付方式，1余额，2支付宝，3微信',
  `status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '订单状态，1未支付，2已支付，3已核销，4已取消，5退款中，6已退款',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `pay_time` timestamp NULL DEFAULT NULL COMMENT '支付时间',
  `use_time` timestamp NULL DEFAULT NULL COMMENT '核销时间',
  `refund_time` timestamp NULL DEFAULT NULL COMMENT '退款时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_release_item` (`user_id`, `release_item_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='抢购订单表';

-- CollectorHub 示例数据
INSERT INTO `tb_collectible_type` VALUES (1, '设计师潮玩', '/types/designer-toy.png', 1, '2026-03-01 10:00:00', '2026-03-01 10:00:00');
INSERT INTO `tb_collectible_type` VALUES (2, '盲盒系列', '/types/blind-box.png', 2, '2026-03-01 10:00:00', '2026-03-01 10:00:00');
INSERT INTO `tb_collectible` VALUES (1, '星际小熊限定款', 1, '/imgs/collectibles/star-bear.png', 'CollectorHub', '线上发售', 0, 0, 12900, 0, 0, 50, '2026-03-01 10:00-22:00', '2026-03-01 10:00:00', '2026-03-01 10:00:00');
INSERT INTO `tb_user` VALUES (1, '18800000000', '', '潮玩玩家001', '/imgs/icons/default-icon.png', '2026-03-01 10:00:00', '2026-03-01 10:00:00');
INSERT INTO `tb_review` VALUES (1, 1, 1, '星际小熊开箱测评', '/imgs/reviews/d764fce6-a5d8-435f-ad81-17e65520f4e7', '做工扎实，涂装细节清晰，适合放入限量系列收藏。', 0, 0, '2026-03-01 10:00:00', '2026-03-01 10:00:00');
INSERT INTO `tb_release_item` VALUES (1, 1, '星际小熊首发抢购', '限量首发', '每位用户限购一件，库存售完即止。', 12900, 12900, 1, 1, '2026-03-01 10:00:00', '2026-03-01 10:00:00');
INSERT INTO `tb_flash_sale_item` VALUES (1, 100, '2026-03-01 10:00:00', '2026-03-01 10:00:00', '2026-03-31 22:00:00', '2026-03-01 10:00:00');

SET FOREIGN_KEY_CHECKS = 1;