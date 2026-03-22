package com.myweb.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 背景音乐实体
 */
@Data
public class Music {
    private Long id;
    private String title;
    private String artist;
    private String url;
    private Integer sort;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
