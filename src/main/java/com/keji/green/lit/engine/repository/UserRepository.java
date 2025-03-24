package com.keji.green.lit.engine.repository;

import com.keji.green.lit.engine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层
 * 提供对用户实体的数据库操作
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据手机号查找用户
     * 
     * @param phone 手机号
     * @return 用户信息，不存在则返回空
     */
    Optional<User> findByPhone(String phone);
    
    /**
     * 检查手机号是否已被注册
     * 
     * @param phone 手机号
     * @return true表示手机号已存在，false表示手机号未注册
     */
    boolean existsByPhone(String phone);
} 