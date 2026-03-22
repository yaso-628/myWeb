package com.myweb.service.impl;

import com.myweb.mapper.BlogMapper;
import com.myweb.model.Blog;
import com.myweb.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 博客业务实现
 */
@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogMapper blogMapper;

    @Override
    public Blog getById(Long id) {
        return blogMapper.selectById(id);
    }

    @Override
    public List<Blog> list(Integer page, Integer size, Integer status, String category) {
        int p = page != null && page > 0 ? page : 1;
        int s = size != null && size > 0 ? Math.min(size, 100) : 10;
        int offset = (p - 1) * s;
        return blogMapper.selectList(offset, s, status, category);
    }

    @Override
    public long count(Integer status, String category) {
        return blogMapper.count(status, category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(Blog blog) {
        blog.setCreateTime(LocalDateTime.now());
        blog.setUpdateTime(LocalDateTime.now());
        if (blog.getStatus() != null && blog.getStatus() == 1) {
            blog.setPublishTime(LocalDateTime.now());
        }
        if (blog.getViewCount() == null) blog.setViewCount(0);
        blogMapper.insert(blog);
        return blog.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Blog blog) {
        blog.setUpdateTime(LocalDateTime.now());
        if (blog.getStatus() != null && blog.getStatus() == 1) {
            Blog existing = blogMapper.selectById(blog.getId());
            if (existing != null && (existing.getPublishTime() == null || (existing.getStatus() != null && existing.getStatus() != 1))) {
                blog.setPublishTime(LocalDateTime.now());
            } else if (existing != null) {
                blog.setPublishTime(existing.getPublishTime());
            }
        }
        blogMapper.updateById(blog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        blogMapper.deleteById(id);
    }

    @Override
    public void incrementViewCount(Long id) {
        blogMapper.incrementViewCount(id);
    }
}
