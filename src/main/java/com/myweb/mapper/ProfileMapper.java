package com.myweb.mapper;

import com.myweb.model.Profile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 个人简介/站点配置 Mapper
 */
@Mapper
public interface ProfileMapper {

    Profile selectByKey(@Param("keyName") String keyName);

    List<Profile> selectAll();

    int insert(Profile profile);

    int updateByKey(Profile profile);
}
