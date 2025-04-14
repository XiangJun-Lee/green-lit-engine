package com.keji.green.lit.engine.service.impl;

import com.keji.green.lit.engine.dto.request.LoginWithCodeRequest;
import com.keji.green.lit.engine.dto.request.LoginWithPasswordRequest;
import com.keji.green.lit.engine.dto.request.RegisterRequest;
import com.keji.green.lit.engine.dto.request.ResetPasswordByPhoneRequest;
import com.keji.green.lit.engine.dto.request.SendVerificationCodeRequest;
import com.keji.green.lit.engine.dto.request.UpdateClientIpRequest;
import com.keji.green.lit.engine.dto.response.TokenResponse;
import com.keji.green.lit.engine.dto.response.UserResponse;
import com.keji.green.lit.engine.enums.UserRole;
import com.keji.green.lit.engine.enums.UserStatusEnum;
import com.keji.green.lit.engine.enums.VerificationCodeScene;
import com.keji.green.lit.engine.exception.BusinessException;
import com.keji.green.lit.engine.model.User;
import com.keji.green.lit.engine.model.UserInviteRelation;
import com.keji.green.lit.engine.security.JwtTokenProvider;
import com.keji.green.lit.engine.service.*;
import com.keji.green.lit.engine.utils.InviteCodeGenerator;
import com.keji.green.lit.engine.utils.UserNameGenerator;
import com.keji.green.lit.engine.dao.UserInviteRelationDao;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.keji.green.lit.engine.exception.ErrorCode.*;
import static com.keji.green.lit.engine.utils.Constants.*;
import static com.keji.green.lit.engine.utils.Constants.ONE_MINUTE_SECONDS;
import static org.apache.commons.lang3.math.NumberUtils.INTEGER_ONE;

/**
 * @author xiangjun_lee
 * @date 2025/4/5 15:35
 */
@Slf4j
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
     */
    @Resource
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


    /**
     * 密码加密器
     */
    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 用户邀请关系数据访问对象
     */
    @Resource
    private UserInviteRelationDao userInviteRelationDao;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void register(RegisterRequest request) {
        // 验证验证码
        if (!verificationCodeService.verifyCode(request.getPhone(), request.getCode(), VerificationCodeScene.REGISTER)) {
            throw new BusinessException(VERIFICATION_CODE_ERROR);
        }
        // 检查用户是否已存在
        if (userService.isPhoneRegisteredAndActive(request.getPhone())) {
            throw new BusinessException(USER_ALREADY_EXISTS.getCode(), "该手机号已注册");
        }

        // 生成邀请码
        String inviteCode = InviteCodeGenerator.generate();

        // 创建用户
        User user = User.builder()
                .phone(request.getPhone())
                .userRole(UserRole.USER.getCode())
                .email(request.getEmail())
                .nickName(UserNameGenerator.generate())
                .inviteCode(inviteCode)
                .build();

        userService.saveUser(user);

        // 如果提供了邀请码，处理邀请关系
        if (request.getInviteCode() != null && !request.getInviteCode().isEmpty()) {
            // 查询邀请人
            User inviter = userService.findByInviteCode(request.getInviteCode());
            if (Objects.isNull(inviter)) {
                throw new BusinessException(PARAM_ERROR.getCode(), "无效的邀请码");
            }

            // 创建邀请关系
            UserInviteRelation relation = UserInviteRelation.builder()
                    .inviterUid(inviter.getUid())
                    .inviteeUid(user.getUid())
                    .build();

            if (userInviteRelationDao.save(relation) <= 0) {
                throw new BusinessException(DATABASE_WRITE_ERROR.getCode(), "保存邀请关系失败");
            }
        }
    }

    @Override
    public TokenResponse loginWithPassword(LoginWithPasswordRequest request) {
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

        try {
            // 验证用户凭证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getPhone(), request.getPassword())
            );
            // 设置认证信息
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 生成token
            String token = jwtTokenProvider.createToken(authentication);

            // 获取用户信息
            User user = (User) authentication.getPrincipal();
            if (UserStatusEnum.isCancelled(user.getStatus())) {
                throw new BusinessException(USER_NOT_EXIST.getCode(), "用户已注销");
            }
            if (!UserStatusEnum.isNormal(user.getStatus())) {
                throw new BusinessException(AUTH_ERROR.getCode(), "该账户异常，请联系管理员");
            }

            return TokenResponse.of(token, user.getUid(), user.getPhone());
        } catch (BusinessException e) {
            throw e;
        } catch (AuthenticationException e) {
            throw new BusinessException(PARAM_ERROR.getCode(), "用户名或密码错误");
        } catch (Exception e) {
            log.error("登录异常,", e);
            throw new BusinessException(SYSTEM_ERROR.getCode(), "系统异常，请稍后重试");
        }
    }

    @Override
    public TokenResponse loginWithVerificationCode(LoginWithCodeRequest request) {
        String ip = getClientIp();
        String ipLimitKey = String.format(VERIFICATION_CODE_IP_KEY, ip);
        String phoneLimitKey = String.format(VERIFICATION_CODE_PHONE_KEY, request.getPhone());

        // 检查IP登录频率限制：5次/分钟
        if (rateLimitService.isRateLimited(ipLimitKey, INTEGER_FIVE, ONE_MINUTE_SECONDS)) {
            throw new BusinessException(RATE_LIMIT_EXCEEDED.getCode(), "登录尝试次数过多，请稍后再试");
        }

        // 检查手机号发送频率限制：1次/分钟
        if (rateLimitService.isRateLimited(phoneLimitKey, INTEGER_ONE, ONE_MINUTE_SECONDS)) {
            throw new BusinessException(RATE_LIMIT_EXCEEDED.getCode(), "该手机号发送验证码次数过多，请稍后再试");
        }

        // 校验验证码
        if (!verificationCodeService.verifyCode(request.getPhone(), request.getCode(), VerificationCodeScene.LOGIN)) {
            throw new BusinessException(VERIFICATION_CODE_EXPIRED);
        }

        // 获取正常状态的用户
        User user = userService.queryNormalUserByPhone(request.getPhone());

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

    @Override
    public void requestVerificationCode(SendVerificationCodeRequest request) {
        String ip = getClientIp();
        String ipLimitKey = String.format(SEND_VERIFICATION_IP_KEY, ip);
        // 检查IP发送频率限制：10次/小时
        if (rateLimitService.isRateLimited(ipLimitKey, INTEGER_TEN, ONE_HOUR_SECONDS)) {
            throw new BusinessException(RATE_LIMIT_EXCEEDED.getCode(), "发送验证码次数过多，请稍后再试");
        }

        String code = verificationCodeService.generateAndSendCode(request.getPhone(),
                VerificationCodeScene.valueOf(request.getScene().toUpperCase()));
        // todo 发送短信验证码
    }

    @Override
    public void resetPassword(ResetPasswordByPhoneRequest request) {
        // 验证验证码
        if (!verificationCodeService.verifyCode(request.getPhone(), request.getCode(),
                VerificationCodeScene.valueOf(request.getScene().toUpperCase()))) {
            throw new BusinessException(VERIFICATION_CODE_ERROR);
        }
        // 获取用户信息
        User user = userService.queryNormalUserByPhone(request.getPhone());
        if (passwordEncoder.matches(user.getPassword(), request.getNewPassword())) {
            throw new BusinessException(SAME_PASSWORD, "新密码不能与旧密码相同");
        }
        // 更新密码
        if (userService.resetPasswordByUid(user.getUid(), request.getNewPassword(), user.getVersion()) <= 0) {
            throw new BusinessException(DATABASE_WRITE_ERROR.getCode(), "密码修改失败，请稍后重试");
        }
    }

    @Override
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(UNAUTHORIZED.getCode(), "用户未登录");
        }
        String phone = authentication.getName();
        UserResponse userResponse = UserResponse.fromUser(userService.queryNormalUserByPhone(phone));
        try {
            long inviteeCount = userInviteRelationDao.selectCountByInviterId(userResponse.getUid());
            userResponse.setInviteeCount(String.valueOf(inviteeCount));
        } catch (Exception e) {
            log.warn("查询用户邀请人数失败", e);
        }
        return userResponse;
    }

    @Override
    public void deactivateAccount(Long uid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(UNAUTHORIZED.getCode(), "用户未登录");
        }
        String phone = authentication.getName();
        User user = userService.queryNormalUserByPhone(phone);
        if (Objects.isNull(user) || !Objects.equals(UserRole.ADMIN.getCode(), user.getUserRole())) {
            throw new BusinessException(FORBIDDEN.getCode(), "用户无权限注销");
        }
        userService.deactivateAccount(uid);
    }

    @Override
    public Boolean isPhoneRegisteredAndActive(String phone) {
        try {
            userService.queryNormalUserByPhone(phone);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void updateClientIp(UpdateClientIpRequest request) {
        // 获取当前登录用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(UNAUTHORIZED.getCode(), "用户未登录");
        }

        // 获取当前用户信息
        String phone = authentication.getName();
        User currentUser = userService.queryNormalUserByPhone(phone);

        // 更新客户端IP
        if (userService.updateClientIp(currentUser.getUid(), request.getClientIp(), currentUser.getVersion()) <= 0) {
            throw new BusinessException(DATABASE_WRITE_ERROR.getCode(), "更新客户端IP失败，请稍后重试");
        }
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
