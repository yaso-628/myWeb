package com.myweb.mapper;

import com.myweb.model.Blog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 博客 Mapper
 */
@Mapper
public interface BlogMapper {

    Blog selectById(@Param("id") Long id);

    List<Blog> selectList(@Param("offset") Integer offset, @Param("limit") Integer limit,
                          @Param("status") Integer status, @Param("category") String category);

    long count(@Param("status") Integer status, @Param("category") String category);

    int insert(Blog blog);

    int updateById(Blog blog);

    int deleteById(@Param("id") Long id);

    int incrementViewCount(@Param("id") Long id);
}
