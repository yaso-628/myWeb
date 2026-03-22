package com.myweb.service.impl;

import com.myweb.mapper.LinkMapper;
import com.myweb.model.Link;
import com.myweb.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 友链业务实现
 */
@Service
@RequiredArgsConstructor
public class LinkServiceImpl implements LinkService {

    private final LinkMapper linkMapper;

    @Override
    public Link getById(Long id) {
        return linkMapper.selectById(id);
    }

    @Override
    public List<Link> list(Integer status) {
        return linkMapper.selectList(status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(Link link) {
        link.setCreateTime(LocalDateTime.now());
        link.setUpdateTime(LocalDateTime.now());
        if (link.getStatus() == null) link.setStatus(0);
        if (link.getSort() == null) link.setSort(0);
        linkMapper.insert(link);
        return link.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Link link) {
        link.setUpdateTime(LocalDateTime.now());
        linkMapper.updateById(link);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        linkMapper.updateStatus(id, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        linkMapper.deleteById(id);
    }
}
