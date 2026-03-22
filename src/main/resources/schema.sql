-- 个人网站数据库表结构（MySQL）
CREATE DATABASE IF NOT EXISTS myweb DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE myweb;

CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    nickname VARCHAR(64),
    email VARCHAR(128),
    avatar VARCHAR(255),
    role TINYINT DEFAULT 0 COMMENT '0普通 1管理员',
    status TINYINT DEFAULT 1 COMMENT '0禁用 1正常',
    create_time DATETIME,
    update_time DATETIME
);

CREATE TABLE IF NOT EXISTS blog (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    summary VARCHAR(500),
    content TEXT,
    cover VARCHAR(255),
    category VARCHAR(64),
    tags VARCHAR(255),
    view_count INT DEFAULT 0,
    status TINYINT DEFAULT 0 COMMENT '0草稿 1已发布',
    author_id BIGINT,
    create_time DATETIME,
    update_time DATETIME,
    publish_time DATETIME
);

CREATE TABLE IF NOT EXISTS comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    blog_id BIGINT COMMENT '文章评论有值，留言板为NULL或0',
    parent_id BIGINT DEFAULT 0,
    user_id BIGINT,
    nickname VARCHAR(64),
    email VARCHAR(128),
    content TEXT NOT NULL,
    status TINYINT DEFAULT 0 COMMENT '0待审核 1通过 2拒绝',
    ip VARCHAR(64),
    create_time DATETIME
);

CREATE TABLE IF NOT EXISTS link (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    url VARCHAR(255) NOT NULL,
    description VARCHAR(500),
    logo VARCHAR(255),
    sort INT DEFAULT 0,
    status TINYINT DEFAULT 0 COMMENT '0待审核 1通过 2拒绝',
    create_time DATETIME,
    update_time DATETIME
);

CREATE TABLE IF NOT EXISTS profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    key_name VARCHAR(64) NOT NULL UNIQUE,
    value TEXT,
    type VARCHAR(32) DEFAULT 'text',
    create_time DATETIME,
    update_time DATETIME
);

CREATE TABLE IF NOT EXISTS music (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL COMMENT '歌名',
    artist VARCHAR(200) DEFAULT '' COMMENT '艺术家',
    url VARCHAR(500) NOT NULL COMMENT '音频URL',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME,
    update_time DATETIME
);
