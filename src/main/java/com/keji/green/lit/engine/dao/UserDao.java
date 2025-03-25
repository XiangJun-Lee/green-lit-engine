package com.keji.green.lit.engine.dao;

import com.keji.green.lit.engine.mapper.UserMapper;
import com.keji.green.lit.engine.model.User;
import com.keji.green.lit.engine.model.UserRole;
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
    private UserMapper mapper;

    /**
     * 根据手机号查询用户
     */
    public Optional<User> findByPhone(String phone) {
        return mapper.findByPhone(phone);
    }

    /**
     * 检查手机号是否已存在
     */
    public boolean existsByPhone(String phone) {
        return mapper.existsByPhone(phone);
    }

    /**
     * 创建新用户
     */
    public User createUser(User user) {
        user.setGmtCreate(LocalDateTime.now());
        if (user.getCreditBalance() == null) {
            user.setCreditBalance(0);
        }
        if (user.getIsActive() == null) {
            user.setIsActive(true);
        }
        if (user.getRole() == null) {
            user.setRole(UserRole.USER.getCode());
        }
        mapper.insert(user);
        return user;
    }

    /**
     * 更新用户最后登录时间
     */
    public User updateLastLoginTime(User user) {
        user.setLastLoginAt(LocalDateTime.now());
        mapper.updateLastLoginTime(user);
        return user;
    }

    /**
     * 更新用户密码
     */
    public User updatePassword(User user, String encodedPassword) {
        user.setPassword(encodedPassword);
        user.setGmtModify(LocalDateTime.now());
        mapper.updatePassword(user);
        return user;
    }

    /**
     * 更新用户状态
     */
    public User updateUserStatus(User user, boolean isActive) {
        user.setIsActive(isActive);
        user.setGmtModify(LocalDateTime.now());
        mapper.updateUserStatus(user);
        return user;
    }

    /**
     * 更新用户客户端连接信息
     */
    public User updateClientConnection(User user, String clientConnection) {
        user.setClientConnection(clientConnection);
        user.setGmtModify(LocalDateTime.now());
        mapper.updateClientConnection(user);
        return user;
    }

    /**
     * 根据ID查询用户
     */
    public Optional<User> findById(Long id) {
        return mapper.findById(id);
    }
} 