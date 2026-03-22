package com.myweb.service.impl;

import com.myweb.mapper.*;
import com.myweb.model.*;
import com.myweb.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 后台管理业务实现
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final BlogMapper blogMapper;
    private final CommentMapper commentMapper;
    private final LinkMapper linkMapper;
    private final UserMapper userMapper;

    @Override
    public long countBlogs(Integer status) {
        return blogMapper.count(status, null);
    }

    @Override
    public long countComments(Integer status) {
        return commentMapper.countList(null, status);
    }

    @Override
    public long countLinks(Integer status) {
        return linkMapper.count(status);
    }

    @Override
    public List<Blog> listBlogs(Integer page, Integer size, Integer status) {
        int p = page != null && page > 0 ? page : 1;
        int s = size != null && size > 0 ? Math.min(size, 100) : 10;
        return blogMapper.selectList((p - 1) * s, s, status, null);
    }

    @Override
    public List<Comment> listComments(Integer page, Integer size, Long blogId, Integer status) {
        int p = page != null && page > 0 ? page : 1;
        int s = size != null && size > 0 ? Math.min(size, 100) : 10;
        return commentMapper.selectList(blogId, status, (p - 1) * s, s);
    }

    @Override
    public List<Link> listLinks(Integer page, Integer size, Integer status) {
        int p = page != null && page > 0 ? page : 1;
        int s = size != null && size > 0 ? Math.min(size, 100) : 10;
        return linkMapper.selectListForAdmin(status, (p - 1) * s, s);
    }

    @Override
    public List<User> listUsers(Integer page, Integer size) {
        int p = page != null && page > 0 ? page : 1;
        int s = size != null && size > 0 ? Math.min(size, 100) : 10;
        return userMapper.selectList((p - 1) * s, s);
    }
}
