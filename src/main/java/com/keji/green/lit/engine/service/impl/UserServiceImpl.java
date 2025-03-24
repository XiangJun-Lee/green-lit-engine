package com.keji.green.lit.engine.service.impl;

import com.keji.green.lit.engine.dto.LoginRequest;
import com.keji.green.lit.engine.dto.RegisterRequest;
import com.keji.green.lit.engine.dto.TokenResponse;
import com.keji.green.lit.engine.dto.UserResponse;
import com.keji.green.lit.engine.exception.BusinessException;
import com.keji.green.lit.engine.model.User;
import com.keji.green.lit.engine.model.UserRole;
import com.keji.green.lit.engine.repository.UserRepository;
import com.keji.green.lit.engine.security.JwtTokenProvider;
import com.keji.green.lit.engine.service.UserService;
import com.keji.green.lit.engine.service.VerificationCodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * 用户服务实现类
 * 实现用户管理相关的业务逻辑
 * 同时实现UserDetailsService接口以支持Spring Security认证
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    /**
     * 用户数据访问层
     */
    @Resource
    private UserRepository userRepository;

    /**
     * 验证码服务
     */
    @Resource
    private VerificationCodeService verificationCodeService;

    /**
     * 密码编码器
     */
    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * JWT令牌提供者
     */
    @Resource
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 认证管理器
     * 使用@Lazy注解避免循环依赖
     */
    @Resource
    @Lazy
    private AuthenticationManager authenticationManager;

    /**
     * 手机号正则表达式
     * 匹配中国大陆手机号
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 用户注册
     * 1. 验证手机号格式
     * 2. 验证验证码
     * 3. 检查用户是否已存在
     * 4. 创建用户并保存
     * 5. 生成JWT令牌
     *
     * @param request 注册请求，包含手机号、验证码和密码
     * @return JWT令牌响应
     * @throws BusinessException 参数验证失败或用户已存在时抛出
     */
    @Override
    @Transactional
    public TokenResponse register(RegisterRequest request) {
        // 验证手机号格式
        if (!PHONE_PATTERN.matcher(request.getPhone()).matches()) {
            throw new BusinessException("手机号格式不正确");
        }

        // 验证验证码
        if (!verificationCodeService.verifyCode(request.getPhone(), request.getVerificationCode())) {
            throw new BusinessException("验证码错误或已过期");
        }

        // 检查用户是否已存在
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("该手机号已注册");
        }

        // 创建用户
        User user = User.builder()
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .isActive(true)
                .creditBalance(0)
                .gmtCreate(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);
        log.info("用户注册成功: {}", savedUser.getPhone());

        // 生成token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser.getPhone(),
                request.getPassword()
        );
        String token = jwtTokenProvider.createToken(authentication);

        return TokenResponse.of(token, savedUser.getUid(), savedUser.getPhone());
    }

    /**
     * 使用密码登录
     * 1. 验证用户凭证
     * 2. 设置认证信息
     * 3. 更新最后登录时间
     * 4. 生成JWT令牌
     *
     * @param request 登录请求，包含手机号和密码
     * @return JWT令牌响应
     * @throws BusinessException 用户名或密码错误时抛出
     */
    @Override
    public TokenResponse loginWithPassword(LoginRequest request) {
        try {
            // 验证用户凭证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getPhone(), request.getPassword())
            );

            // 设置认证信息
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 获取用户信息
            User user = userRepository.findByPhone(request.getPhone())
                    .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));

            // 更新最后登录时间
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);

            // 生成token
            String token = jwtTokenProvider.createToken(authentication);

            return TokenResponse.of(token, user.getUid(), user.getPhone());
        } catch (Exception e) {
            throw new BusinessException("用户名或密码错误", e);
        }
    }

    /**
     * 使用验证码登录
     * 1. 验证验证码
     * 2. 获取用户信息
     * 3. 更新最后登录时间
     * 4. 创建认证信息
     * 5. 生成JWT令牌
     *
     * @param phone 手机号
     * @param code  验证码
     * @return JWT令牌响应
     * @throws BusinessException 验证码错误或用户不存在时抛出
     */
    @Override
    @Transactional
    public TokenResponse loginWithVerificationCode(String phone, String code) {
        // 验证验证码
        if (!verificationCodeService.verifyCode(phone, code)) {
            throw new BusinessException("验证码错误或已过期");
        }

        // 获取用户信息
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BusinessException("用户不存在，请先注册"));

        // 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // 创建认证信息
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getPhone(),
                null,
                user.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成token
        String token = jwtTokenProvider.createToken(authentication);

        return TokenResponse.of(token, user.getUid(), user.getPhone());
    }

    /**
     * 请求发送验证码
     * 1. 验证手机号格式
     * 2. 生成并发送验证码
     *
     * @param phone 手机号
     * @throws BusinessException 手机号格式不正确时抛出
     */
    @Override
    public void requestVerificationCode(String phone) {
        // 验证手机号格式
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new BusinessException("手机号格式不正确");
        }

        // 生成并发送验证码
        verificationCodeService.generateAndSendCode(phone);
    }

    /**
     * 重置密码
     * 1. 验证验证码
     * 2. 获取用户信息
     * 3. 更新密码
     *
     * @param phone            手机号
     * @param verificationCode 验证码
     * @param newPassword      新密码
     * @throws BusinessException 验证码错误或用户不存在时抛出
     */
    @Override
    @Transactional
    public void resetPassword(String phone, String verificationCode, String newPassword) {
        // 验证验证码
        if (!verificationCodeService.verifyCode(phone, verificationCode)) {
            throw new BusinessException("验证码错误或已过期");
        }

        // 获取用户信息
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BusinessException("用户不存在，请先注册"));

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setGmtModify(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 注销账号
     * 将用户状态设置为非活跃
     *
     * @param uid 用户ID
     * @throws BusinessException 用户不存在时抛出
     */
    @Override
    @Transactional
    public void deactivateAccount(Long uid) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        user.setIsActive(false);
        user.setGmtModify(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * 更新客户端连接信息
     *
     * @param uid    用户ID
     * @param ipPort IP和端口信息，格式为ip:port
     * @throws BusinessException 用户不存在时抛出
     */
    @Override
    @Transactional
    public void updateClientConnection(Long uid, String ipPort) {
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        user.setClientConnection(ipPort);
        user.setGmtModify(LocalDateTime.now());
        userRepository.save(user);

        log.info("用户 {} 客户端连接信息已更新: {}", user.getPhone(), ipPort);
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息响应
     * @throws BusinessException 用户未登录或不存在时抛出
     */
    @Override
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }

        String phone = authentication.getName();
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        return UserResponse.fromUser(user);
    }

    /**
     * 验证手机验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 验证结果，true表示验证通过
     */
    @Override
    public boolean verifyCode(String phone, String code) {
        return verificationCodeService.verifyCode(phone, code);
    }

    /**
     * 根据用户名（手机号）加载用户详情
     * 实现UserDetailsService接口的方法，用于Spring Security认证
     *
     * @param username 用户名（手机号）
     * @return 用户详情
     * @throws UsernameNotFoundException 用户不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByPhone(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
    }
} 