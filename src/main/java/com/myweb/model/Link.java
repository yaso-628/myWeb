package com.myweb.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 友链实体
 */
@Data
public class Link {
    private Long id;
    private String name;
    private String url;
    private String description;
    private String logo;
    private Integer sort;
    private Integer status;        // 0-待审核 1-通过 2-拒绝
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
