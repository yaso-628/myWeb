package com.myweb.dto;

import lombok.Data;

/**
 * 用户个人信息 DTO（用于前台展示与编辑）
 */
@Data
public class UserProfileDTO {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String avatar;
    /** 角色：0-普通用户 1-管理员 */
    private Integer role;
}

