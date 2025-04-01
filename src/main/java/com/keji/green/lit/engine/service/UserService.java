package com.keji.green.lit.engine.service;

import com.keji.green.lit.engine.dto.LoginRequest;
import com.keji.green.lit.engine.dto.RegisterRequest;
import com.keji.green.lit.engine.dto.TokenResponse;
import com.keji.green.lit.engine.dto.UserResponse;
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
    TokenResponse register(RegisterRequest request);
    
    /**
     * 使用密码登录
     * 
     * @param request 登录请求信息，包含手机号和密码
     * @return 包含JWT令牌的响应
     */
    TokenResponse loginWithPassword(LoginRequest request);
    
    /**
     * 使用验证码登录
     * 
     * @param phone 手机号
     * @param code 验证码
     * @return 包含JWT令牌的响应
     */
    TokenResponse loginWithVerificationCode(String phone, String code);
    
    /**
     * 请求发送验证码
     * 
     * @param phone 手机号
     */
    void requestVerificationCode(String phone);
    
    /**
     * 重置密码
     * 
     * @param phone 手机号
     * @param verificationCode 验证码
     * @param newPassword 新密码
     */
    void resetPassword(String phone, String verificationCode, String newPassword);
    
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
     * 验证手机验证码
     * 
     * @param phone 手机号
     * @param code 验证码
     * @return 验证结果，true表示验证通过
     */
    boolean verifyCode(String phone, String code);

    /**
     * 检查手机号是否已注册且账号处于活跃状态
     *
     * @param phone 手机号
     * @return true表示已注册且活跃，false表示未注册或已注销
     */
    boolean isPhoneRegisteredAndActive(String phone);
} 