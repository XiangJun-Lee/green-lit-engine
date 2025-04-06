package com.keji.green.lit.engine.service;

import com.keji.green.lit.engine.dto.response.UserResponse;
import com.keji.green.lit.engine.model.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 用户服务接口
 * 提供用户管理相关的业务逻辑操作
 */
public interface UserService extends UserDetailsService {
    
    /**
     * 用户注册
     * 
     * @param request 注册请求信息，包含手机号、验证码和密码
     * @return 包含JWT令牌的响应
     */
    void saveUser(User request);
    
    /**
     * 注销账号
     * 
     * @param uid 用户ID
     */
    void deactivateAccount(Long uid);
    
    /**
     * 获取当前登录用户信息
     * 
     * @return 当前用户信息
     */
    UserResponse getCurrentUser();

    /**
     * 检查手机号是否已注册且账号处于活跃状态
     *
     * @param phone 手机号
     * @return true表示已注册且活跃，false表示未注册或已注销
     */
    boolean isPhoneRegisteredAndActive(String phone);

    /**
     * 根据手机号查询状态正产的用户信息
     * @param phone 手机号
     * @return 用户信息
     */
    User queryNormalUserByPhone(@NotBlank(message = "手机号不能为空") String phone);

    /**
     * 根据uid更新密码
     * @param uid uid
     * @param newPassword 新密码
     * @param version 版本号
     */
    int resetPasswordByUid(Long uid, String newPassword, Integer version);
}