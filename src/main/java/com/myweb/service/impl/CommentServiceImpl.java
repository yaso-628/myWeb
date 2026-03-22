package com.myweb.service.impl;

import com.myweb.mapper.CommentMapper;
import com.myweb.model.Comment;
import com.myweb.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 留言/评论业务实现
 */
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    @Override
    public Comment getById(Long id) {
        return commentMapper.selectById(id);
    }

    @Override
    public List<Comment> listByBlogId(Long blogId, Integer status) {
        return commentMapper.selectByBlogId(blogId, status);
    }

    @Override
    public List<Comment> listGuestbook(Integer page, Integer size, Integer status) {
        int p = page != null && page > 0 ? page : 1;
        int s = size != null && size > 0 ? Math.min(size, 100) : 10;
        int offset = (p - 1) * s;
        return commentMapper.selectGuestbook(status != null ? status : 1, offset, s);
    }

    @Override
    public long countByBlogId(Long blogId, Integer status) {
        return commentMapper.countByBlogId(blogId, status);
    }

    @Override
    public long countGuestbook(Integer status) {
        return commentMapper.countGuestbook(status != null ? status : 1);
    }

    @Override
    public long countList(Long blogId, Integer status) {
        return commentMapper.countList(blogId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(Comment comment) {
        comment.setCreateTime(LocalDateTime.now());
        // 默认状态：待审核(0)，通过后台审核后才会展示
        if (comment.getStatus() == null) comment.setStatus(0);
        commentMapper.insert(comment);
        return comment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        commentMapper.updateStatus(id, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        commentMapper.deleteById(id);
    }
}
