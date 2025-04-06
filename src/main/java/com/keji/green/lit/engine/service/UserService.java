package com.keji.green.lit.engine.service;

import com.keji.green.lit.engine.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 用户服务接口
 * 提供用户管理相关的业务逻辑操作
 * @author xiangjun_lee
 */
public interface UserService extends UserDetailsService {
    
    /**
     * 用户注册
     */
    void saveUser(User request);
    
    /**
     * 注销账号
     */
    void deactivateAccount(Long uid);

    /**
     * 检查手机号是否已注册且账号处于活跃状态
     */
    boolean isPhoneRegisteredAndActive(String phone);

    /**
     * 根据手机号查询状态正产的用户信息
     */
    User queryNormalUserByPhone(String phone);

    /**
     * 根据uid更新密码
     */
    int resetPasswordByUid(Long uid, String newPassword, Integer version);
}