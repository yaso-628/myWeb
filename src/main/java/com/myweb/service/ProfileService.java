package com.myweb.service;

import com.myweb.dto.PersonalProfileDTO;
import com.myweb.model.Profile;

import java.util.List;

/**
 * 个人简介/站点配置业务接口
 */
public interface ProfileService {

    Profile getByKey(String keyName);

    List<Profile> listAll();

    void saveOrUpdate(Profile profile);

    /** 获取个人信息（头像、简介、联系方式） */
    PersonalProfileDTO getPersonalInfo();

    /** 更新个人信息（仅更新非空字段） */
    void updatePersonalInfo(PersonalProfileDTO dto);
}
