package com.keji.green.lit.engine.mapper;

import com.keji.green.lit.engine.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Optional;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据手机号查询用户
     */
    Optional<User> findByPhone(@Param("phone") String phone);

    /**
     * 检查手机号是否已存在
     */
    boolean existsByPhone(@Param("phone") String phone);

    /**
     * 创建新用户
     */
    int insert(User user);

    int insertSelective(User user);

    /**
     * 更新用户最后登录时间
     */
    int updateLastLoginTime(User user);

    /**
     * 更新用户密码
     */
    int updatePassword(User user);

    /**
     * 更新用户状态
     */
    int updateUserStatus(User user);

    /**
     * 更新用户客户端连接信息
     */
    int updateClientConnection(User user);

    /**
     * 根据ID查询用户
     */
    Optional<User> findById(@Param("uid") Long uid);
} 