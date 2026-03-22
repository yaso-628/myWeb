package com.myweb.mapper;

import com.myweb.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper {

    User selectById(@Param("id") Long id);

    User selectByUsername(@Param("username") String username);

    List<User> selectList(@Param("offset") Integer offset, @Param("limit") Integer limit);

    long count();

    int insert(User user);

    int updateById(User user);
}
