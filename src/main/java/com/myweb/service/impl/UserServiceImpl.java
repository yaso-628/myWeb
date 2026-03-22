package com.myweb.service.impl;

import com.myweb.mapper.UserMapper;
import com.myweb.model.User;
import com.myweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户业务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(String username, String password, String nickname, String email) {
        if (checkUsernameExists(username)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname != null ? nickname : username);
        user.setEmail(email);
        user.setRole(userMapper.count() == 0 ? 1 : 0);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return userMapper.selectByUsername(username) != null;
    }

    @Override
    public long countUsers() {
        return userMapper.count();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(User user) {
        if (user == null || user.getId() == null) {
            return;
        }
        // 只允许更新昵称、邮箱、头像、状态及更新时间，具体字段更新逻辑在 Mapper 中控制
        userMapper.updateById(user);
    }
}
