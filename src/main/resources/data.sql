-- 默认站点配置（首次启动时插入，若已存在则跳过）
INSERT INTO profile (key_name, value, type, create_time, update_time)
VALUES ('site_name', '我的个人网站', 'text', NOW(), NOW()),
       ('site_desc', '记录技术与生活', 'text', NOW(), NOW())
ON DUPLICATE KEY UPDATE update_time = NOW();
