package com.myweb.dto;

import lombok.Data;

/**
 * 个人简介 DTO：头像、简介、联系方式
 */
@Data
public class PersonalProfileDTO {
    /** 头像 URL */
    private String avatar;
    /** 个人简介/关于我 */
    private String bio;
    /** 联系方式（如邮箱、微信、GitHub 等，可存 JSON 或纯文本） */
    private String contact;
}
