package com.keji.green.lit.engine.controller;

import com.keji.green.lit.engine.dto.LoginRequest;
import com.keji.green.lit.engine.dto.RegisterRequest;
import com.keji.green.lit.engine.dto.TokenResponse;
import com.keji.green.lit.engine.dto.UserResponse;
import com.keji.green.lit.engine.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 处理用户注册、登录、登出和账号管理等认证相关请求
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * 用户服务
     */
    private final UserService userService;

    /**
     * 用户注册
     * 
     * @param request 注册请求，包含手机号、验证码和密码
     * @return JWT令牌响应
     */
    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    /**
     * 密码登录
     * 
     * @param request 登录请求，包含手机号和密码
     * @return JWT令牌响应
     */
    @PostMapping("/login/password")
    public ResponseEntity<TokenResponse> loginWithPassword(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.loginWithPassword(request));
    }

    /**
     * 验证码登录
     * 
     * @param phone 手机号
     * @param code 验证码
     * @return JWT令牌响应
     */
    @PostMapping("/login/code")
    public ResponseEntity<TokenResponse> loginWithCode(
            @RequestParam @NotBlank(message = "手机号不能为空") 
            @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone,
            @RequestParam @NotBlank(message = "验证码不能为空") String code) {
        return ResponseEntity.ok(userService.loginWithVerificationCode(phone, code));
    }

    /**
     * 发送验证码
     * 
     * @param phone 手机号
     * @return 发送结果
     */
    @PostMapping("/code/send")
    public ResponseEntity<Map<String, String>> sendVerificationCode(
            @RequestParam @NotBlank(message = "手机号不能为空") 
            @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone) {
        userService.requestVerificationCode(phone);
        Map<String, String> response = new HashMap<>();
        response.put("message", "验证码已发送");
        return ResponseEntity.ok(response);
    }

    /**
     * 重置密码
     * 
     * @param phone 手机号
     * @param code 验证码
     * @param newPassword 新密码
     * @return 重置结果
     */
    @PostMapping("/password/reset")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestParam @NotBlank(message = "手机号不能为空") 
            @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone,
            @RequestParam @NotBlank(message = "验证码不能为空") String code,
            @RequestParam @NotBlank(message = "密码不能为空") 
            @Pattern(regexp = "^.{6,20}$", message = "密码长度必须在6-20位之间") String newPassword) {
        userService.resetPassword(phone, code, newPassword);
        Map<String, String> response = new HashMap<>();
        response.put("message", "密码重置成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 用户登出
     * JWT是无状态的，客户端只需清除本地保存的token即可完成登出
     * 此接口仅返回登出成功的消息，实际操作由客户端完成
     * 
     * @return 登出结果
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        // JWT无状态，客户端只需清除token即可
        Map<String, String> response = new HashMap<>();
        response.put("message", "登出成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取当前登录用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    /**
     * 注销账号
     * 将用户标记为非活跃状态
     * 
     * @return 注销结果
     */
    @DeleteMapping("/account")
    public ResponseEntity<Map<String, String>> deactivateAccount() {
        UserResponse currentUser = userService.getCurrentUser();
        userService.deactivateAccount(currentUser.getUid());
        Map<String, String> response = new HashMap<>();
        response.put("message", "账号已注销");
        return ResponseEntity.ok(response);
    }
    
    /**
     * 更新客户端连接信息
     * 
     * @param ipPort IP和端口信息，格式为ip:port
     * @return 更新结果
     */
    @PostMapping("/client-connection")
    public ResponseEntity<Map<String, String>> updateClientConnection(
            @RequestParam @NotBlank(message = "客户端连接信息不能为空") String ipPort) {
        UserResponse currentUser = userService.getCurrentUser();
        userService.updateClientConnection(currentUser.getUid(), ipPort);
        Map<String, String> response = new HashMap<>();
        response.put("message", "客户端连接信息已更新");
        return ResponseEntity.ok(response);
    }
} 