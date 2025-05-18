package com.keji.green.lit.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.keji.green.lit.engine.dto.TradeDto;
import com.keji.green.lit.engine.dto.request.account.AccountDto;
import com.keji.green.lit.engine.dto.response.AccountTradeResponse;
import com.keji.green.lit.engine.model.Account;
import com.keji.green.lit.engine.model.AccountLog;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AccountService extends IService<Account> {

    boolean createAccount(AccountDto account);

    List<Account> queryAccount(AccountDto account);

    List<AccountLog> queryAccountLog(AccountDto account);

    AccountTradeResponse trade(TradeDto tradeDto);

}
