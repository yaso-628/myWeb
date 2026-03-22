package com.myweb.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 个人简介实体（单条配置，如关于我、站点说明等）
 */
@Data
public class Profile {
    private Long id;
    private String keyName;        // 配置键，如 about_me, site_desc
    private String value;          // 配置值/富文本内容
    private String type;           // text / html / json
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
