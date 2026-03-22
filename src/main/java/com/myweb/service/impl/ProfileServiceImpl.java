package com.myweb.service.impl;

import com.myweb.dto.PersonalProfileDTO;
import com.myweb.mapper.ProfileMapper;
import com.myweb.model.Profile;
import com.myweb.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 个人简介/站点配置业务实现
 */
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private static final String TYPE_TEXT = "text";
    private static final String KEY_AVATAR = "avatar";
    private static final String KEY_BIO = "bio";
    private static final String KEY_CONTACT = "contact";

    private final ProfileMapper profileMapper;

    @Override
    public Profile getByKey(String keyName) {
        return profileMapper.selectByKey(keyName);
    }

    @Override
    public List<Profile> listAll() {
        return profileMapper.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(Profile profile) {
        Profile existing = profileMapper.selectByKey(profile.getKeyName());
        profile.setUpdateTime(LocalDateTime.now());
        if (existing == null) {
            profile.setCreateTime(LocalDateTime.now());
            profileMapper.insert(profile);
        } else {
            profileMapper.updateByKey(profile);
        }
    }

    @Override
    public PersonalProfileDTO getPersonalInfo() {
        PersonalProfileDTO dto = new PersonalProfileDTO();
        dto.setAvatar(getValueByKey(KEY_AVATAR));
        dto.setBio(getValueByKey(KEY_BIO));
        dto.setContact(getValueByKey(KEY_CONTACT));
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePersonalInfo(PersonalProfileDTO dto) {
        if (dto == null) return;
        if (dto.getAvatar() != null) saveOrUpdateKey(KEY_AVATAR, dto.getAvatar());
        if (dto.getBio() != null) saveOrUpdateKey(KEY_BIO, dto.getBio());
        if (dto.getContact() != null) saveOrUpdateKey(KEY_CONTACT, dto.getContact());
    }

    private String getValueByKey(String keyName) {
        Profile p = profileMapper.selectByKey(keyName);
        return p == null ? null : p.getValue();
    }

    private void saveOrUpdateKey(String keyName, String value) {
        Profile profile = new Profile();
        profile.setKeyName(keyName);
        profile.setValue(value);
        profile.setType(TYPE_TEXT);
        saveOrUpdate(profile);
    }
}
