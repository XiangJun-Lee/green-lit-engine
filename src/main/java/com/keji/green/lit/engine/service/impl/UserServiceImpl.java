package com.keji.green.lit.engine.service.impl;

import com.keji.green.lit.engine.dao.UserDao;
import com.keji.green.lit.engine.dto.LoginRequest;
import com.keji.green.lit.engine.dto.TokenResponse;
import com.keji.green.lit.engine.dto.UserResponse;
import com.keji.green.lit.engine.enums.UserStatusEnum;
import com.keji.green.lit.engine.exception.BusinessException;
import com.keji.green.lit.engine.model.User;
import com.keji.green.lit.engine.enums.UserRole;
import com.keji.green.lit.engine.service.UserService;
import com.keji.green.lit.engine.service.VerificationCodeService;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Optional;
import java.util.regex.Pattern;

import static com.keji.green.lit.engine.exception.ErrorCode.*;

/**
 * 用户服务实现类
 * 实现用户管理相关的业务逻辑
 * 同时实现UserDetailsService接口以支持Spring Security认证
 * @author xiangjun_lee
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    /**
     * 用户数据访问层
     */
    @Resource
    private UserDao userDao;

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
    public void saveUser(User user) {
        // 创建用户
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (userDao.createUser(user) <= 0) {
            throw new BusinessException(DATABASE_WRITE_ERROR.getCode(), "注册失败，请联系管理员");
        }
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
    public User loginWithPassword(LoginRequest request) {
        try {
            // 验证用户凭证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getPhone(), request.getPassword())
            );

            // 设置认证信息
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 获取用户信息
            Optional<User> userOptional = userDao.findByPhone(request.getPhone());
            if (userOptional.isEmpty()) {
                throw new BusinessException(USER_NOT_EXIST.getCode(), "用户不存在");
            }
            User user = userOptional.get();
            // 生成token
            String token = jwtTokenProvider.createToken(authentication);
            return TokenResponse.of(token, user.getUid(), user.getPhone());
        } catch (Exception e) {
            throw new BusinessException(PARAM_ERROR.getCode(), "用户名或密码错误");
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
    public User loginWithVerificationCode(String phone, String code) {
        // 验证验证码
        if (!verificationCodeService.verifyCode(phone, code)) {
            throw new BusinessException(PARAM_ERROR.getCode(), "验证码错误或已过期");
        }

        // 获取用户信息
        Optional<User> userOptional = userDao.findByPhone(phone);
        if (userOptional.isEmpty()) {
            throw new BusinessException(USER_NOT_EXIST.getCode(), "用户不存在");
        }
        User user = userOptional.get();

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
            throw new BusinessException(PARAM_ERROR.getCode(), "手机号格式不正确");
        }
        // 生成并发送验证码
        String code = verificationCodeService.generateAndSendCode(phone);
        // todo 接发送短信三方平台
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
            throw new BusinessException(PARAM_ERROR.getCode(), "验证码错误或已过期");
        }

        // 获取用户信息
        User user = userDao.findByPhone(phone)
                .orElseThrow(() -> new BusinessException(USER_NOT_EXIST.getCode(),"用户不存在"));

        // 更新密码
        userDao.updatePassword(user.getUid(), passwordEncoder.encode(newPassword));
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
        User user = userDao.findById(uid)
                .orElseThrow(() -> new BusinessException(USER_NOT_EXIST.getCode(),"用户不存在"));
        userDao.updateUserStatus(user.getUid(), UserStatusEnum.CANCELLED.getCode());
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
            throw new BusinessException(UNAUTHORIZED.getCode(), "用户未登录");
        }

        String phone = authentication.getName();
        User user = userDao.findByPhone(phone)
                .orElseThrow(() -> new BusinessException(USER_NOT_EXIST.getCode(),"用户不存在"));

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
     * 检查手机号是否已注册且账号处于活跃状态
     *
     * @param phone 手机号
     * @return true表示已注册且活跃，false表示未注册或已注销
     */
    @Override
    public boolean isPhoneRegisteredAndActive(String phone) {
        // 验证手机号格式
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new BusinessException(PARAM_ERROR.getCode(), "手机号格式不正确");
        }
        // 查询用户信息
        Optional<User> userOptional = userDao.findByPhone(phone);
        if (userOptional.isEmpty()) {
            return false;
        }
        // 检查用户状态
        User user = userOptional.get();
        return UserStatusEnum.isNormal(user.getStatus());
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
        return userDao.findByPhone(username)
                .orElseThrow(() -> new UsernameNotFoundException("手机号为注册: " + username));
    }
} 