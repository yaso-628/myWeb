# PersonalWeb 博客系统

一个基于Spring Boot和Vue的全栈个人博客网站，包含文章管理、评论、音乐播放等完整功能。

## 技术栈

- **后端**: Spring Boot 3, Java 17, MyBatis, MySQL, Redis, JWT
- **前端**: Vue 3, Vite, Axios
- **其他**: Maven, RESTful API

## 功能特性

- 用户注册/登录（JWT认证）
- 博客文章发布与管理
- 评论系统
- 音乐播放器
- 个人资料管理
- 友情链接管理
- 后台管理面板

## 环境要求

- Java 17+
- MySQL 8.0+
- Redis 6.0+
- Node.js 16+

## 快速开始

### 后端启动
```
bash
# 克隆项目
git clone https://github.com/yaso-628/myWeb.git
cd PersonalWeb

# 启动Spring Boot应用
mvn spring-boot:run
```
### 前端启动
```
bash
cd frontend
npm install
npm run dev
```
## 项目结构
```

myWeb/
├── src/main/java/com/myweb/     # 后端源码
├── frontend/                    # 前端源码
├── pom.xml                      # Maven配置
└── application.yml              # 应用配置
```
## 接口文档

API接口采用RESTful风格，基础路径：`/api/v1`

| 模块 | 路径 | 方法 | 描述 |
|------|------|------|------|
| 用户 | /api/v1/auth/login | POST | 用户登录 |
| 用户 | /api/v1/auth/register | POST | 用户注册 |
| 博客 | /api/v1/blogs | GET | 获取博客列表 |
| 博客 | /api/v1/blogs | POST | 发布新博客 |
| 评论 | /api/v1/comments | GET | 获取评论 |
```
