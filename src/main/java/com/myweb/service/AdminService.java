package com.myweb.service;

import com.myweb.model.*;

import java.util.List;

/**
 * 后台管理业务接口（聚合统计、批量操作等）
 */
public interface AdminService {

    // 统计
    long countBlogs(Integer status);

    long countComments(Integer status);

    long countLinks(Integer status);

    // 列表（后台用，可含未审核）
    List<Blog> listBlogs(Integer page, Integer size, Integer status);

    List<Comment> listComments(Integer page, Integer size, Long blogId, Integer status);

    List<Link> listLinks(Integer page, Integer size, Integer status);

    List<User> listUsers(Integer page, Integer size);
}
