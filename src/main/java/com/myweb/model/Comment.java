package com.myweb.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 留言/评论实体
 */
@Data
public class Comment {
    private Long id;
    private Long blogId;           // 可为空，留言板留言无 blogId
    private Long parentId;         // 父评论 id，顶级为 0
    private Long userId;
    private String nickname;
    private String email;
    private String content;
    private Integer status;        // 0-待审核 1-通过 2-拒绝
    private String ip;
    private LocalDateTime createTime;
    /** 关联用户头像（从 user 表动态查询，不落库） */
    private String avatar;
}
