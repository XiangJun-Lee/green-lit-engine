package com.keji.green.lit.engine.controller;

import com.keji.green.lit.engine.dto.response.Result;
import com.keji.green.lit.engine.dto.request.LoginWithCodeRequest;
import com.keji.green.lit.engine.dto.request.LoginWithPasswordRequest;
import com.keji.green.lit.engine.dto.request.RegisterRequest;
import com.keji.green.lit.engine.dto.request.ResetPasswordByPhoneRequest;
import com.keji.green.lit.engine.dto.request.SendVerificationCodeRequest;
import com.keji.green.lit.engine.dto.request.UpdateClientIpRequest;
import com.keji.green.lit.engine.dto.response.TokenResponse;
import com.keji.green.lit.engine.dto.response.UserResponse;
import com.keji.green.lit.engine.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户注册、登录、登出和账号管理等认证相关请求
 * @author xiangjun_lee
 */
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

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
    public Result<TokenResponse> loginWithPassword(@Valid @RequestBody LoginWithPasswordRequest request) {
        return Result.success(authService.loginWithPassword(request));
    }

    /**
     * 验证码登录
     * 
     * @param request 登录请求，包含手机号和验证码
     * @return JWT令牌响应
     */
    @PostMapping("/login-with-code")
    public Result<TokenResponse> loginWithCode(@Valid @RequestBody LoginWithCodeRequest request) {
        return Result.success(authService.loginWithVerificationCode(request));
    }

    /**
     * 发送验证码
     * 
     * @param request 发送验证码请求
     * @return 发送结果
     */
    @PostMapping("/code")
    public Result<Void> sendVerificationCode(@Valid @RequestBody SendVerificationCodeRequest request) {
        authService.requestVerificationCode(request);
        return Result.success();
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordByPhoneRequest request) {
        authService.resetPassword(request);
        return Result.success();
    }

    /**
     * 获取当前登录用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("/me")
    public Result<UserResponse> getCurrentUser() {
        return Result.success(authService.getCurrentUser());
    }

    /**
     * 注销账号
     * 将用户标记为非活跃状态
     * @return 注销结果
     */
    @PostMapping("/deactivate")
    public Result<Void> deactivateAccount() {
        authService.deactivateAccount();
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
        return Result.success(authService.isPhoneRegisteredAndActive(phone));
    }

    /**
     * 更新客户端IP
     * 
     * @param request 更新客户端IP请求，包含用户ID和客户端IP:端口
     * @return 更新结果
     */
    @PostMapping("/update-client-ip")
    public Result<Void> updateClientIp(@Valid @RequestBody UpdateClientIpRequest request) {
        authService.updateClientIp(request);
        return Result.success();
    }
} 