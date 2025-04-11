package com.keji.green.lit.engine.dao;

import com.keji.green.lit.engine.mapper.UserMapper;
import com.keji.green.lit.engine.model.User;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问对象
 *
 * @author xiangjun_lee
 * @since 2024-04-11
 */
@Repository
public class UserDao {
    
    @Resource
    private UserMapper mapper;

    /**
     * 根据手机号查询用户
     */
    public Optional<User> findByPhone(String phone) {
        return mapper.selectByPhone(phone);
    }

    /**
     * 创建新用户
     */
    public int createUser(User user) {
        return mapper.insertSelective(user);
    }

    /**
     * 更新用户密码
     */
    public int updatePassword(Long uid, String encodedPassword) {
        User user = new User();
        user.setUid(uid);
        user.setPassword(encodedPassword);
        return mapper.updateSelectiveByUid(user);
    }

    /**
     * 更新用户状态
     */
    public int updateUserStatus(Long uid, int userStatus) {
        User user = new User();
        user.setUid(uid);
        user.setStatus(userStatus);
        return mapper.updateSelectiveByUid(user);
    }

    public int updateSelectiveByUid(User user){
        return mapper.updateSelectiveByUid(user);
    }

    /**
     * 根据ID查询用户
     */
    public Optional<User> findById(Long uid) {
        return mapper.selectByUid(uid);
    }

    /**
     * 根据邀请码查询用户
     */
    public Optional<User> findByInviteCode(String inviteCode) {
        return mapper.selectByInviteCode(inviteCode);
    }
} 