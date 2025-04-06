package com.keji.green.lit.engine.mapper;

import com.keji.green.lit.engine.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserMapper {

    /**
     * 插入用户
     */
    int insertSelective(User row);

    /**
     * 根据主键查询用户
     */
    Optional<User> selectByUid(Long uid);

    /**
     * 根据手机号查询用户
     */
    Optional<User> selectByPhone(String phone);

    /**
     * 根据主键更新用户
     */
    int updateSelectiveByUid(User row);

    /**
     * 根据手机号更新用户
     */
    int updateSelectiveByPhone(User row);

}