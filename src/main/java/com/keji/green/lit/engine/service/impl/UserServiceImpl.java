package com.keji.green.lit.engine.service.impl;

import com.keji.green.lit.engine.dao.UserDao;
import com.keji.green.lit.engine.enums.UserStatusEnum;
import com.keji.green.lit.engine.exception.BusinessException;
import com.keji.green.lit.engine.model.User;
import com.keji.green.lit.engine.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.util.Optional;
import static com.keji.green.lit.engine.exception.ErrorCode.*;

/**
 * 用户服务实现类
 * 实现用户管理相关的业务逻辑
 * 同时实现UserDetailsService接口以支持Spring Security认证
 * @author xiangjun_lee
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    /**
     * 用户数据访问层
     */
    @Resource
    private UserDao userDao;

    /**
     * 密码编码器
     */
    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 用户注册
     *
     * @param user 注册请求，包含手机号、验证码和密码
     * @throws BusinessException 注册失败，请联系管理员
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
     * 注销账号
     * 将用户状态设置为非活跃
     *
     * @param uid 用户ID
     * @throws BusinessException 用户不存在时抛出
     */
    @Override
    @Transactional
    public void deactivateAccount(Long uid) {
        User user = userDao.findById(uid).orElseThrow(() -> new BusinessException(USER_NOT_EXIST.getCode(), "用户不存在"));
        userDao.updateUserStatus(user.getUid(), UserStatusEnum.CANCELLED.getCode());
    }


    /**
     * 检查手机号是否已注册且账号处于活跃状态
     *
     * @param phone 手机号
     * @return true表示已注册且活跃，false表示未注册或已注销
     */
    @Override
    public boolean isPhoneRegisteredAndActive(String phone) {
        // 查询用户信息
        Optional<User> userOptional = userDao.findByPhone(phone);
        if (userOptional.isEmpty()) {
            return false;
        }
        // 检查用户状态
        User user = userOptional.get();
        return UserStatusEnum.isNormal(user.getStatus());
    }

    @Override
    public User queryNormalUserByPhone(String phone) {
        Optional<User> userOptional = userDao.findByPhone(phone);
        if (userOptional.isEmpty()) {
            throw new BusinessException(USER_NOT_EXIST.getCode(), "用户不存在");
        }
        User user = userOptional.get();
        if (UserStatusEnum.isCancelled(user.getStatus())) {
            throw new BusinessException(USER_NOT_EXIST.getCode(), "用户已注销");
        }
        if (!UserStatusEnum.isNormal(user.getStatus())) {
            throw new BusinessException(AUTH_ERROR.getCode(), "该账户异常，请联系管理员");
        }
        return user;
    }

    @Override
    public int resetPasswordByUid(Long uid, String newPassword, Integer version) {
        User user = new User();
        user.setUid(uid);
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setVersion(version);
        return userDao.updateSelectiveByUid(user);

    }

    @Override
    public int updateClientIp(Long uid, String clientIp, Integer version) {
        User user = new User();
        user.setUid(uid);
        user.setClientIp(clientIp);
        user.setVersion(version);
        return userDao.updateSelectiveByUid(user);
    }

    @Override
    public User findByPhone(String phone) {
        Optional<User> userDaoByPhone = userDao.findByPhone(phone);
        return userDaoByPhone.orElse(null);
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
                .orElseThrow(() -> new UsernameNotFoundException("手机号未注册: " + username));
    }
} 