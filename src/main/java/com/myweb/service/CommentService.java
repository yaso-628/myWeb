package com.myweb.service;

import com.myweb.model.Comment;

import java.util.List;

/**
 * 留言/评论业务接口
 */
public interface CommentService {

    Comment getById(Long id);

    List<Comment> listByBlogId(Long blogId, Integer status);

    List<Comment> listGuestbook(Integer page, Integer size, Integer status);

    long countByBlogId(Long blogId, Integer status);

    long countGuestbook(Integer status);

    long countList(Long blogId, Integer status);

    Long add(Comment comment);

    void updateStatus(Long id, Integer status);

    void deleteById(Long id);
}
