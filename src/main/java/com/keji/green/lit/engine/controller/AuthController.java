package com.keji.green.lit.engine.controller;

import com.keji.green.lit.engine.common.Result;
import com.keji.green.lit.engine.dto.LoginRequest;
import com.keji.green.lit.engine.dto.RegisterRequest;
import com.keji.green.lit.engine.dto.TokenResponse;
import com.keji.green.lit.engine.dto.UserResponse;
import com.keji.green.lit.engine.model.User;
import com.keji.green.lit.engine.security.JwtTokenProvider;
import com.keji.green.lit.engine.service.AuthService;
import com.keji.green.lit.engine.service.UserService;
import com.keji.green.lit.engine.service.RateLimitService;
import com.keji.green.lit.engine.exception.BusinessException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.keji.green.lit.engine.exception.ErrorCode.RATE_LIMIT_EXCEEDED;
import static com.keji.green.lit.engine.exception.ErrorCode.USER_NOT_EXIST;
import static com.keji.green.lit.engine.utils.Constants.*;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;

/**
 * 认证控制器
 * 处理用户注册、登录、登出和账号管理等认证相关请求
 * @author xiangjun_lee
 */
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

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

    @Resource
    private AuthService authService;
    /**
     * 用户注册
     * 
     * @param request 注册请求，包含手机号、验证码和密码
     * @return JWT令牌响应
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return Result.success();
    }

    /**
     * 密码登录
     * 
     * @param request 登录请求，包含手机号和密码
     * @return JWT令牌响应
     */
    @PostMapping("/login")
    public Result<TokenResponse> loginWithPassword(@Valid @RequestBody LoginRequest request) {

        TokenResponse response = authService.loginWithPassword(request);

        return Result.success(TokenResponse.of(token, user.getUid(), user.getPhone()));
    }

    /**
     * 验证码登录
     * 
     * @param phone 手机号
     * @param code 验证码
     * @return JWT令牌响应
     */
    @PostMapping("/login-with-code")
    public Result<TokenResponse> loginWithCode(
            @RequestParam @NotBlank(message = "手机号不能为空") 
            @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone,
            @RequestParam @NotBlank(message = "验证码不能为空") String code) {
        String ip = getClientIp();
        String ipLimitKey = String.format(VERIFICATION_CODE_IP_KEY, ip);
        String phoneLimitKey = String.format(VERIFICATION_CODE_PHONE_KEY, phone);
        
        // 检查IP登录频率限制：5次/分钟
        if (rateLimitService.isRateLimited(ipLimitKey, INTEGER_FIVE, ONE_MINUTE_SECONDS)) {
            throw new BusinessException(RATE_LIMIT_EXCEEDED.getCode(),"登录尝试次数过多，请稍后再试");
        }

        // 检查手机号发送频率限制：1次/分钟
        if (rateLimitService.isRateLimited(phoneLimitKey, INTEGER_ONE, ONE_MINUTE_SECONDS)) {
            throw new BusinessException(RATE_LIMIT_EXCEEDED.getCode(),"该手机号发送验证码次数过多，请稍后再试");
        }
        return Result.success(userService.loginWithVerificationCode(phone, code));
    }

    /**
     * 发送验证码
     * 
     * @param phone 手机号
     * @return 发送结果
     */
    @PostMapping("/code")
    public Result<Void> sendVerificationCode(
            @RequestParam @NotBlank(message = "手机号不能为空") 
            @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone) {
        String ip = getClientIp();
        String ipLimitKey = String.format(SEND_VERIFICATION_IP_KEY, ip);
        String phoneLimitKey = String.format(SEND_VERIFICATION_CODE_PHONE_KEY, phone);

        // 检查IP发送频率限制：10次/小时
        if (rateLimitService.isRateLimited(ipLimitKey, INTEGER_TEN, ONE_HOUR_SECONDS)) {
            throw new BusinessException(RATE_LIMIT_EXCEEDED.getCode(),"发送验证码次数过多，请稍后再试");
        }

        // 检查手机号发送频率限制：1次/分钟
        if (rateLimitService.isRateLimited(phoneLimitKey, INTEGER_ONE, ONE_MINUTE_SECONDS)) {
            throw new BusinessException(RATE_LIMIT_EXCEEDED.getCode(),"该手机号发送验证码次数过多，请稍后再试");
        }

        userService.requestVerificationCode(phone);
        return Result.success();
    }

    /**
     * 重置密码
     * 
     * @param phone 手机号
     * @param code 验证码
     * @param newPassword 新密码
     * @return 重置结果
     */
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(
            @RequestParam @NotBlank(message = "手机号不能为空") 
            @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone,
            @RequestParam @NotBlank(message = "验证码不能为空") String code,
            @RequestParam @NotBlank(message = "密码不能为空") 
            @Pattern(regexp = "^.{6,20}$", message = "密码长度必须在6-20位之间") String newPassword) {
        userService.resetPassword(phone, code, newPassword);
        return Result.success();
    }

    /**
     * 用户登出
     * JWT是无状态的，客户端只需清除本地保存的token即可完成登出
     * 此接口仅返回登出成功的消息，实际操作由客户端完成
     * 
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT无状态，客户端只需清除token即可
        return Result.success();
    }

    /**
     * 获取当前登录用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("/me")
    public Result<UserResponse> getCurrentUser() {
        return Result.success(userService.getCurrentUser());
    }

    /**
     * 注销账号
     * 将用户标记为非活跃状态
     * 
     * @param uid 用户ID
     * @return 注销结果
     */
    @PostMapping("/deactivate")
    public Result<Void> deactivateAccount(@RequestParam Long uid) {
        userService.deactivateAccount(uid);
        return Result.success();
    }

    /**
     * 检查手机号是否已注册且账号处于活跃状态
     * 
     * @param phone 手机号
     * @return 是否已注册且活跃
     */
    @GetMapping("/check-phone")
    public Result<Boolean> checkPhoneRegistered(
            @RequestParam @NotBlank(message = "手机号不能为空") 
            @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone) {
        return Result.success(userService.isPhoneRegisteredAndActive(phone));
    }


} 