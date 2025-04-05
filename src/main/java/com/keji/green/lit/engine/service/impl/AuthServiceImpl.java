package com.keji.green.lit.engine.service.impl;

import com.keji.green.lit.engine.dto.LoginRequest;
import com.keji.green.lit.engine.dto.RegisterRequest;
import com.keji.green.lit.engine.dto.TokenResponse;
import com.keji.green.lit.engine.enums.UserRole;
import com.keji.green.lit.engine.exception.BusinessException;
import com.keji.green.lit.engine.model.User;
import com.keji.green.lit.engine.security.JwtTokenProvider;
import com.keji.green.lit.engine.service.AuthService;
import com.keji.green.lit.engine.service.RateLimitService;
import com.keji.green.lit.engine.service.UserService;
import com.keji.green.lit.engine.service.VerificationCodeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static com.keji.green.lit.engine.exception.ErrorCode.*;
import static com.keji.green.lit.engine.utils.Constants.*;
import static com.keji.green.lit.engine.utils.Constants.ONE_MINUTE_SECONDS;

/**
 * @author xiangjun_lee
 * @date 2025/4/5 15:35
 */
@Service("authService")
public class AuthServiceImpl implements AuthService {


    /**
     * 用户服务
     */
    @Resource
    private UserService userService;

    /**
     * 频率限制服务
     */
    @Resource
    private RateLimitService rateLimitService;

    /**
     * HTTP请求对象
     */
    @Resource
    private HttpServletRequest request;

    /**
     * 认证管理器
     * 使用@Lazy注解避免循环依赖
     */
    @Resource
    @Lazy
    private AuthenticationManager authenticationManager;

    /**
     * JWT令牌提供者
     */
    @Resource
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 验证码服务
     */
    @Resource
    private VerificationCodeService verificationCodeService;


    @Override
    public void register(RegisterRequest request) {
        // 验证验证码
        if (!verificationCodeService.verifyCode(request.getPhone(), request.getVerificationCode())) {
            throw new BusinessException(VERIFICATION_CODE_ERROR);
        }
        // 检查用户是否已存在
        if (userService.isPhoneRegisteredAndActive(request.getPhone())) {
            throw new BusinessException(USER_ALREADY_EXISTS.getCode(), "该手机号已注册");
        }

        // 创建用户
        User user = User.builder()
                .phone(request.getPhone())
                .password(request.getPassword())
                .userRole(UserRole.USER.getCode())
                .email(request.getEmail())
                .build();

        userService.saveUser(user);
    }

    @Override
    public TokenResponse loginWithPassword(LoginRequest request) {
        String ip = getClientIp();
        String ipLimitKey = String.format(PASSWORD_LOGIN_IP_KEY, ip);
        String phoneLimitKey = String.format(PASSWORD_LOGIN_PHONE_KEY, request.getPhone());
        // 检查IP登录频率限制：5次/分钟
        if (rateLimitService.isRateLimited(ipLimitKey, INTEGER_FIVE, ONE_MINUTE_SECONDS)) {
            throw new BusinessException(RATE_LIMIT_EXCEEDED.getCode(), "登录尝试次数过多，请1分钟后再试");
        }
        // 检查手机号发送频率限制：5次/分钟
        if (rateLimitService.isRateLimited(phoneLimitKey, INTEGER_FIVE, ONE_MINUTE_SECONDS)) {
            throw new BusinessException(RATE_LIMIT_EXCEEDED.getCode(), "该手机号登录尝试次数过多，请1分钟后再试");
        }

        // 验证用户凭证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getPhone(), request.getPassword())
        );

        // 设置认证信息
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 获取用户信息
        userService.loginWithPassword(request).
        if (userOptional.isEmpty()) {
            throw new BusinessException(USER_NOT_EXIST.getCode(), "用户不存在");
        }
        User user = userOptional.get();
        // 生成token
        String token = jwtTokenProvider.createToken(authentication);
    }

    /**
     * 获取客户端IP地址
     *
     * @return 客户端IP地址
     */
    private String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
