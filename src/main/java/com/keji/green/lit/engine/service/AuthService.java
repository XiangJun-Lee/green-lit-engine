package com.keji.green.lit.engine.service;

import com.keji.green.lit.engine.dto.request.LoginWithCodeRequest;
import com.keji.green.lit.engine.dto.request.LoginWithPasswordRequest;
import com.keji.green.lit.engine.dto.request.RegisterRequest;
import com.keji.green.lit.engine.dto.request.ResetPasswordByPhoneRequest;
import com.keji.green.lit.engine.dto.response.TokenResponse;
import com.keji.green.lit.engine.dto.response.UserResponse;

/**
 * @author xiangjun_lee
 * @date 2025/4/5 15:34
 */
public interface AuthService {


    void register(RegisterRequest request);

    TokenResponse loginWithPassword(LoginWithPasswordRequest request);

    TokenResponse loginWithVerificationCode(LoginWithCodeRequest request);

    void requestVerificationCode(String phone);

    void resetPassword(ResetPasswordByPhoneRequest request);

    UserResponse getCurrentUser();

    void deactivateAccount(Long uid);

    Boolean isPhoneRegisteredAndActive(String phone);
}
