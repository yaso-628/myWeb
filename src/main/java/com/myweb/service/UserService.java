package com.myweb.service;

import com.myweb.model.User;

/**
 * 用户业务接口
 */
public interface UserService {

    User findByUsername(String username);

    User findById(Long id);

    void register(String username, String password, String nickname, String email);

    boolean checkUsernameExists(String username);

    long countUsers();

    /**
     * 更新用户个人资料（昵称、邮箱、头像等）
     */
    void updateProfile(User user);
}
