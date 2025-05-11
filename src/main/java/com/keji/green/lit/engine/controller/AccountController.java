package com.keji.green.lit.engine.controller;

import com.keji.green.lit.engine.dto.TradeDto;
import com.keji.green.lit.engine.dto.request.AccountDto;
import com.keji.green.lit.engine.dto.response.AccountInfoResponse;
import com.keji.green.lit.engine.dto.response.AccountTradeResponse;
import com.keji.green.lit.engine.dto.response.Result;
import com.keji.green.lit.engine.model.Account;
import com.keji.green.lit.engine.service.AccountService;
import com.keji.green.lit.engine.service.AuthService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    @GetMapping("/query")
    public Result<AccountInfoResponse> query(@Valid @RequestBody AccountDto request) {

        List<AccountInfoResponse.AccountWrapper> list = new ArrayList<>();
        List<Account> accounts = accountService.queryAccount(request);
        for (Account account : accounts){
            AccountInfoResponse.AccountWrapper accountWrapper = new AccountInfoResponse.AccountWrapper();
            accountWrapper.setId(account.getId());
            accountWrapper.setUserId(account.getUserId());
            accountWrapper.setAccountId(account.getAccountId());
            accountWrapper.setAccountType(account.getAccountType());
            accountWrapper.setBalance(account.getBalance().toString());
            accountWrapper.setStatus(account.getStatus());
            accountWrapper.setGmtCreate(account.getCreateTime().toString());
            list.add(accountWrapper);
        }
        AccountInfoResponse response = AccountInfoResponse.builder()
                .accountList(list)
                .build();
        return Result.success(response);
    }

    @GetMapping("/log/query")
    public Result<Void> queryLog(@Valid @RequestBody AccountDto request) {
        accountService.queryAccountLog(request);
        return Result.success();
    }

    @PostMapping("/trade")
    public Result<Void> trade(@Valid @RequestBody TradeDto request) {
        AccountTradeResponse res = accountService.trade(request);
        if (res != null) {
            return Result.error(res.getErr_no(),res.getErr_msg());
        }
        return Result.success();
    }
}