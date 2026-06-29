# CollectorHub

CollectorHub 是一个面向潮玩单品发售与玩家交流的前后端示例项目。项目后端基于 Spring Boot 构建，覆盖短信验证码登录、潮玩信息管理、发售日历、玩家开箱测评、限量抢购、点赞、关注、共同关注和用户资料维护等场景。

## 技术栈

- Java
- Spring Boot
- MyBatis-Plus
- MySQ
- Redis + Redisson
- RocketMQ
- Caffeine
- Maven
- Nginx

## 项目结构

```text
CollectorHub
├── frontend/nginx-1.18.0/html/collectorhub   # 前端静态页面
├── src/main/java/com/collectorhub
│   ├── config                                # Web、MyBatis、Redisson 等配置
│   ├── controller                            # 接口层
│   ├── dto                                   # 数据传输对象
│   ├── entity                                # 数据库实体
│   ├── mapper                                # MyBatis-Plus Mapper
│   ├── service                               # 业务接口
│   ├── service/impl                          # 业务实现
│   └── utils                                 # Redis、登录拦截、分布式锁等工具
├── src/main/resources
│   ├── application.yaml                      # 应用配置
│   ├── db/collectorhub.sql                   # 数据库初始化脚本
│   ├── flash-sale.lua                        # 抢购库存与一人一单校验脚本
│   └── unlock.lua                            # Redis 锁释放脚本
└── src/test                                  # Java 与前端静态逻辑测试
```

## 环境准备

启动项目前请准备以下服务：

1. JDK 8
2. Maven 3.x
3. MySQL
4. Redis
5. RocketMQ NameServer 与 Broker

数据库初始化脚本位于 `src/main/resources/db/collectorhub.sql`。执行脚本后，根据本机环境修改 `src/main/resources/application.yaml` 中的 MySQL、Redis 和 RocketMQ 地址、端口、用户名与密码。

默认后端端口为 `8081`。

## 后端启动

```bash
mvn clean package
mvn spring-boot:run
```

也可以直接运行主类：

```text
com.collectorhub.CollectorHubApplication
```

## 前端访问

前端资源位于：

```text
frontend/nginx-1.18.0/html/collectorhub
```

在 Windows 环境可进入 `frontend/nginx-1.18.0` 后启动 Nginx：

```bash
nginx.exe
```

然后访问 Nginx 配置中对应的静态页面地址。若只调试页面，也可以直接打开 `frontend/nginx-1.18.0/html/collectorhub/index.html`。

## 常用接口

| 模块 | 接口前缀 | 说明 |
| --- | --- | --- |
| 用户 | `/user` | 验证码、登录、登出、当前用户、资料维护 |
| 潮玩分类 | `/collectible-types` | 分类列表 |
| 潮玩单品 | `/collectibles` | 单品详情、创建、更新、查询 |
| 发售信息 | `/release-items` | 发售记录与抢购配置 |
| 抢购订单 | `/flash-sale-orders` | 限量抢购下单 |
| 测评 | `/reviews` | 发布、点赞、热门列表、详情 |
| 测评评论 | `/review-comments` | 测评评论相关接口 |
| 关注 | `/follow` | 关注、取关、共同关注 |
| 上传 | `/upload` | 图片上传与删除 |

## 测试

运行 Java 测试：

```bash
mvn test
```

运行前端静态逻辑测试：

```bash
node src/test/js/profile-refresh.test.js
```

## 注意事项

- `application.yaml` 中默认服务地址为示例内网地址，首次运行需要替换为本机或实际部署环境地址。
- 抢购链路依赖 Redis、Lua 脚本与 RocketMQ，未启动相关服务时抢购接口无法完整验证。
- 上传文件会写入前端静态资源目录，部署时需要确认目录权限和静态资源映射。