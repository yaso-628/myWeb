package com.myweb.service;

import com.myweb.model.Blog;

import java.util.List;

/**
 * 博客业务接口
 */
public interface BlogService {

    Blog getById(Long id);

    List<Blog> list(Integer page, Integer size, Integer status, String category);

    long count(Integer status, String category);

    Long save(Blog blog);

    void update(Blog blog);

    void deleteById(Long id);

    void incrementViewCount(Long id);
}
