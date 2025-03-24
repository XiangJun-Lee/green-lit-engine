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
import com.keji.green.lit.engine.service.VerificationCodeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationCodeService verificationCodeService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private final String testPhone = "13800138000";
    private final String testPassword = "password123";
    private final String testEncodedPassword = "encodedPassword123";
    private final String testVerificationCode = "123456";
    private final String testToken = "jwt.token.string";

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = User.builder()
                .uid(1L)
                .phone(testPhone)
                .password(testEncodedPassword)
                .gmtCreate(LocalDateTime.now())
                .isActive(true)
                .role(UserRole.USER)
                .creditBalance(0)
                .build();
        
        // 清除安全上下文
        SecurityContextHolder.clearContext();
    }

    @Test
    void register_shouldRegisterUserSuccessfully() {
        // 准备
        RegisterRequest request = new RegisterRequest(testPhone, testVerificationCode, testPassword);
        
        when(verificationCodeService.verifyCode(testPhone, testVerificationCode)).thenReturn(true);
        when(userRepository.existsByPhone(testPhone)).thenReturn(false);
        when(passwordEncoder.encode(testPassword)).thenReturn(testEncodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtTokenProvider.createToken(any(Authentication.class))).thenReturn(testToken);

        // 执行
        TokenResponse response = userService.register(request);

        // 验证
        assertNotNull(response);
        assertEquals(testUser.getUid(), response.getUserId());
        assertEquals(testPhone, response.getPhone());
        assertEquals(testToken, response.getToken());
        
        verify(verificationCodeService).verifyCode(testPhone, testVerificationCode);
        verify(userRepository).existsByPhone(testPhone);
        verify(passwordEncoder).encode(testPassword);
        verify(userRepository).save(any(User.class));
        verify(jwtTokenProvider).createToken(any(Authentication.class));
    }

    @Test
    void register_shouldThrowExceptionWhenVerificationCodeInvalid() {
        // 准备
        RegisterRequest request = new RegisterRequest(testPhone, testVerificationCode, testPassword);
        
        when(verificationCodeService.verifyCode(testPhone, testVerificationCode)).thenReturn(false);

        // 执行并验证
        assertThrows(BusinessException.class, () -> userService.register(request));
        
        verify(verificationCodeService).verifyCode(testPhone, testVerificationCode);
        verify(userRepository, never()).existsByPhone(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_shouldThrowExceptionWhenPhoneExists() {
        // 准备
        RegisterRequest request = new RegisterRequest(testPhone, testVerificationCode, testPassword);
        
        when(verificationCodeService.verifyCode(testPhone, testVerificationCode)).thenReturn(true);
        when(userRepository.existsByPhone(testPhone)).thenReturn(true);

        // 执行并验证
        assertThrows(BusinessException.class, () -> userService.register(request));
        
        verify(verificationCodeService).verifyCode(testPhone, testVerificationCode);
        verify(userRepository).existsByPhone(testPhone);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginWithPassword_shouldLoginSuccessfully() {
        // 准备
        LoginRequest request = new LoginRequest(testPhone, testPassword);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                testPhone, 
                testPassword, 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByPhone(testPhone)).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.createToken(authentication)).thenReturn(testToken);

        // 执行
        TokenResponse response = userService.loginWithPassword(request);

        // 验证
        assertNotNull(response);
        assertEquals(testUser.getUid(), response.getUserId());
        assertEquals(testPhone, response.getPhone());
        assertEquals(testToken, response.getToken());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByPhone(testPhone);
        verify(userRepository).save(testUser);
        verify(jwtTokenProvider).createToken(authentication);
    }

    @Test
    void loginWithVerificationCode_shouldLoginSuccessfully() {
        // 准备
        when(verificationCodeService.verifyCode(testPhone, testVerificationCode)).thenReturn(true);
        when(userRepository.findByPhone(testPhone)).thenReturn(Optional.of(testUser));
        when(jwtTokenProvider.createToken(any(Authentication.class))).thenReturn(testToken);

        // 执行
        TokenResponse response = userService.loginWithVerificationCode(testPhone, testVerificationCode);

        // 验证
        assertNotNull(response);
        assertEquals(testUser.getUid(), response.getUserId());
        assertEquals(testPhone, response.getPhone());
        assertEquals(testToken, response.getToken());
        
        verify(verificationCodeService).verifyCode(testPhone, testVerificationCode);
        verify(userRepository).findByPhone(testPhone);
        verify(userRepository).save(any(User.class));
        verify(jwtTokenProvider).createToken(any(Authentication.class));
    }

    @Test
    void resetPassword_shouldResetPasswordSuccessfully() {
        // 准备
        String newPassword = "newPassword123";
        String encodedNewPassword = "encodedNewPassword123";
        
        when(verificationCodeService.verifyCode(testPhone, testVerificationCode)).thenReturn(true);
        when(userRepository.findByPhone(testPhone)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

        // 执行
        userService.resetPassword(testPhone, testVerificationCode, newPassword);

        // 验证
        verify(verificationCodeService).verifyCode(testPhone, testVerificationCode);
        verify(userRepository).findByPhone(testPhone);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(testUser);
        
        assertEquals(encodedNewPassword, testUser.getPassword());
    }

    @Test
    void deactivateAccount_shouldDeactivateAccountSuccessfully() {
        // 准备
        when(userRepository.findById(testUser.getUid())).thenReturn(Optional.of(testUser));

        // 执行
        userService.deactivateAccount(testUser.getUid());

        // 验证
        verify(userRepository).findById(testUser.getUid());
        verify(userRepository).save(testUser);
        
        assertFalse(testUser.getIsActive());
    }

    @Test
    void updateClientConnection_shouldUpdateClientConnectionSuccessfully() {
        // 准备
        String ipPort = "192.168.1.1:8080";
        when(userRepository.findById(testUser.getUid())).thenReturn(Optional.of(testUser));

        // 执行
        userService.updateClientConnection(testUser.getUid(), ipPort);

        // 验证
        verify(userRepository).findById(testUser.getUid());
        verify(userRepository).save(testUser);
        
        assertEquals(ipPort, testUser.getClientConnection());
    }

    @Test
    void getCurrentUser_shouldReturnCurrentUser() {
        // 准备
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                testPhone, 
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        when(userRepository.findByPhone(testPhone)).thenReturn(Optional.of(testUser));

        // 执行
        UserResponse response = userService.getCurrentUser();

        // 验证
        assertNotNull(response);
        assertEquals(testUser.getUid(), response.getUid());
        assertEquals(testPhone, response.getPhone());
        
        verify(userRepository).findByPhone(testPhone);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        // 准备
        when(userRepository.findByPhone(testPhone)).thenReturn(Optional.of(testUser));

        // 执行
        UserDetails userDetails = userService.loadUserByUsername(testPhone);

        // 验证
        assertNotNull(userDetails);
        assertEquals(testPhone, userDetails.getUsername());
        assertEquals(testEncodedPassword, userDetails.getPassword());
        
        verify(userRepository).findByPhone(testPhone);
    }

    @Test
    void loadUserByUsername_shouldThrowExceptionWhenUserNotFound() {
        // 准备
        when(userRepository.findByPhone(testPhone)).thenReturn(Optional.empty());

        // 执行并验证
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(testPhone));
        
        verify(userRepository).findByPhone(testPhone);
    }
} 