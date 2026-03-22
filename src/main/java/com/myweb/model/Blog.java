package com.myweb.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 博客文章实体
 */
@Data
public class Blog {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String cover;
    private String category;
    private String tags;
    private Integer viewCount;
    private Integer status;        // 0-草稿 1-已发布
    private Long authorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime publishTime;
}
