package com.keji.green.lit.engine.controller;

import com.keji.green.lit.engine.dto.TradeDto;
import com.keji.green.lit.engine.dto.request.AccountDto;
import com.keji.green.lit.engine.dto.response.Result;
import com.keji.green.lit.engine.service.AccountService;
import com.keji.green.lit.engine.service.AuthService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Resource
    private AccountService accountService;

    /**
     * 创建账户
     */
    @PostMapping("/create")
    public Result<Void> create(@Valid @RequestBody AccountDto request) {
        accountService.createAccount(request);
        return Result.success();
    }

    /**
     * 创建账户
     */
    @PostMapping("/query")
    public Result<Void> query(@Valid @RequestBody AccountDto request) {
        accountService.queryAccount(request);
        return Result.success();
    }

    @PostMapping("/log/query")
    public Result<Void> queryLog(@Valid @RequestBody AccountDto request) {
        accountService.queryAccountLog(request);
        return Result.success();
    }

    @PostMapping("/trade")
    public Result<Void> trade(@Valid @RequestBody TradeDto request) {
        accountService.trade(request);
        return Result.success();
    }
}