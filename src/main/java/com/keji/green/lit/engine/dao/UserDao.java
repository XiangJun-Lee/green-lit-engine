package com.keji.green.lit.engine.dao;

import com.keji.green.lit.engine.model.User;
import com.keji.green.lit.engine.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 用户数据访问对象
 */
@Repository
public class UserDao {
    
    @Resource
    private UserRepository repository;

    /**
     * 根据手机号查询用户
     */
    public Optional<User> findByPhone(String phone) {
        return repository.findByPhone(phone);
    }

    /**
     * 检查手机号是否已存在
     */
    public boolean existsByPhone(String phone) {
        return repository.existsByPhone(phone);
    }

    /**
     * 创建新用户
     */
    public User createUser(User user) {
        user.setGmtCreate(LocalDateTime.now());
        return repository.save(user);
    }

    /**
     * 更新用户最后登录时间
     */
    public User updateLastLoginTime(User user) {
        user.setLastLoginAt(LocalDateTime.now());
        return repository.save(user);
    }

    /**
     * 更新用户密码
     */
    public User updatePassword(User user, String encodedPassword) {
        user.setPassword(encodedPassword);
        user.setGmtModify(LocalDateTime.now());
        return repository.save(user);
    }

    /**
     * 更新用户状态
     */
    public User updateUserStatus(User user, boolean isActive) {
        user.setIsActive(isActive);
        user.setGmtModify(LocalDateTime.now());
        return repository.save(user);
    }

    /**
     * 更新用户客户端连接信息
     */
    public User updateClientConnection(User user, String clientConnection) {
        user.setClientConnection(clientConnection);
        user.setGmtModify(LocalDateTime.now());
        return repository.save(user);
    }

    /**
     * 根据ID查询用户
     */
    public Optional<User> findById(Long id) {
        return repository.findById(id);
    }
} 