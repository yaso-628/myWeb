package com.myweb.service;

import com.myweb.model.Link;

import java.util.List;

/**
 * 友链业务接口
 */
public interface LinkService {

    Link getById(Long id);

    List<Link> list(Integer status);

    Long save(Link link);

    void update(Link link);

    void updateStatus(Long id, Integer status);

    void deleteById(Long id);
}
