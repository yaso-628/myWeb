package com.myweb.mapper;

import com.myweb.model.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 留言/评论 Mapper
 */
@Mapper
public interface CommentMapper {

    Comment selectById(@Param("id") Long id);

    List<Comment> selectByBlogId(@Param("blogId") Long blogId, @Param("status") Integer status);

    List<Comment> selectGuestbook(@Param("status") Integer status, @Param("offset") Integer offset, @Param("limit") Integer limit);

    List<Comment> selectList(@Param("blogId") Long blogId, @Param("status") Integer status, @Param("offset") Integer offset, @Param("limit") Integer limit);

    long countByBlogId(@Param("blogId") Long blogId, @Param("status") Integer status);

    long countList(@Param("blogId") Long blogId, @Param("status") Integer status);

    long countGuestbook(@Param("status") Integer status);

    int insert(Comment comment);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    int deleteById(@Param("id") Long id);
}
