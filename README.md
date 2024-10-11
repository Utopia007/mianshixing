## 项目介绍🙋
    面试星是一个致力于帮助使用者解决刷题困难的平台，基于 Next.js + Spring Boot + Redis + MySQL + Elasticsearch ，运用 Druid + HotKey + Sa-Token + Sentinel 提高了系统的性能和安全性。管理员可以创建题库、题目和题解；用户可以注册登录、分词检索题目、在线刷题并查看刷题记录日历图。 此外，系统使用数据库连接池、热 Key 探测、缓存、高级数据结构等来提升性能。通过流量控制、熔断、动态 IP 黑白名单过滤、同端登录冲突检测、分级反爬虫策略来提升系统和内容的安全性。

## 需求分析📑
### 基础功能

- 用户模块
  - 用户注册
  - 用户登录
  - 管理用户【admin】
- 题库模块
  - 查看题库列表
  - 查看题库详情
  - 管理题库【admin】
- 题目模块
  - 题目搜索
  - 查看题目详情
  - 管理题目 -（比如按照题库查询题目、修改题目所属题库等）【admin】

### 扩展设计

- 题目批量管理
  - 批量向题库添加题目【admin】
  - 批量从题库移除题目【admin】
  - 批量删除题目【admin】
- 分词题目搜索
- 用户刷题记录日历图
- 自动缓存热门题目
- 网站流量控制和熔断
- 动态 IP 黑白名单过滤
- 同端登录冲突检测
- 分级题目反爬虫策略

## 技术选型 🎯
- Spring Boot 框架
- Maven 多模块构建
- MySQL 数据库 + MyBatis-Plus
- Redis 分布式缓存 + Caffeine 本地缓存
- Redission 分布式锁 + BitMap + BloomFilter
- Elasticsearch 搜索引擎
- Druid 数据库连接池 + 并发编程
- Sa-Token 权限控制
- HotKey 热点探测
- Sentinel 流量控制
- Nacos 配置中心


## 架构设计⭐️
![image](https://github.com/user-attachments/assets/5a19d7f0-5482-40da-a738-359116087088)


## 业务流程⭐️
![image](https://github.com/user-attachments/assets/5600ef64-14b8-47ec-8cde-c4e23bce7440)


## 项目阶段🚀
1. 第一阶段，进行面试星刷题平台的需求分析、库表设计，开发核心业务流程。

2. 第二阶段，对面向用户的功能和面向管理的功能进行扩展。

3. 第三阶段，对项目安全性进行优化，保证最终项目上线后的可用性。





