package com.myweb.mapper;

import com.myweb.model.Link;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 友链 Mapper
 */
@Mapper
public interface LinkMapper {

    Link selectById(@Param("id") Long id);

    List<Link> selectList(@Param("status") Integer status);

    List<Link> selectListForAdmin(@Param("status") Integer status, @Param("offset") Integer offset, @Param("limit") Integer limit);

    long count(@Param("status") Integer status);

    int insert(Link link);

    int updateById(Link link);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    int deleteById(@Param("id") Long id);
}
