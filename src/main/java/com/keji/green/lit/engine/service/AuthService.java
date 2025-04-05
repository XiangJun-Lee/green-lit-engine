package com.keji.green.lit.engine.service;

import com.keji.green.lit.engine.dto.LoginRequest;
import com.keji.green.lit.engine.dto.RegisterRequest;
import com.keji.green.lit.engine.dto.TokenResponse;
import jakarta.validation.Valid;

/**
 * @author xiangjun_lee
 * @date 2025/4/5 15:34
 */
public interface AuthService {


    void register(@Valid RegisterRequest request);

    TokenResponse loginWithPassword(@Valid LoginRequest request);
}
