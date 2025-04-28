package com.keji.green.lit.engine.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.keji.green.lit.engine.dao.UserDao;
import com.keji.green.lit.engine.dto.TradeDto;
import com.keji.green.lit.engine.dto.request.AccountDto;
import com.keji.green.lit.engine.enums.AccountTypeEnum;
import com.keji.green.lit.engine.enums.SubAccountTypeEnum;
import com.keji.green.lit.engine.enums.TransTypeEnum;
import com.keji.green.lit.engine.exception.BusinessException;
import com.keji.green.lit.engine.mapper.AccountLogMapper;
import com.keji.green.lit.engine.mapper.AccountMapper;
import com.keji.green.lit.engine.model.Account;
import com.keji.green.lit.engine.model.AccountLog;
import com.keji.green.lit.engine.model.User;
import com.keji.green.lit.engine.service.AccountService;
import com.keji.green.lit.engine.utils.AccountUtil;
import com.keji.green.lit.engine.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static com.keji.green.lit.engine.enums.SubAccountTypeEnum.CASH;
import static com.keji.green.lit.engine.enums.SubAccountTypeEnum.PROMOTION;
import static com.keji.green.lit.engine.exception.ErrorCode.*;

@Slf4j
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Resource
    private AccountMapper accountMapper;
    @Resource
    private AccountLogMapper logMapper;
    @Resource
    private TransactionTemplate template;
    @Resource
    private UserDao userDao;

    private final List<SubAccountTypeEnum> subAccountTypeLists = List.of(CASH, PROMOTION);

    @Override
    public boolean createAccount(AccountDto dto) {

        Integer accountType = dto.getAccountType();
        // 检查账户类型 10-内部账户 11-外部账户 12-管理者
        AccountTypeEnum anEnum = AccountTypeEnum.checkAccountType(accountType);
        User user = userDao.findById(dto.getUserId()).orElseThrow(() -> new BusinessException(USER_NOT_EXIST.getCode(), "用户不存在"));

        // 构建查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("uid", dto.getUserId());
        List<Account> accounts = new ArrayList<>();
        accounts = accountMapper.queryAccountByCond(params);
        if (!accounts.isEmpty()) {
            return true;
        }
        for (SubAccountTypeEnum subAccountTypeEnum : SubAccountTypeEnum.values()) {
            // 初始化账户数据
            Account account = new Account();
            account.setUserId(dto.getUserId());
            account.setAccountType(dto.getAccountType());
            account.setSubType(subAccountTypeEnum.getCode());
            account.setBalance(BigDecimal.ZERO);
            account.setStatus(1);
            account.setIncomeAmount(BigDecimal.ZERO);
            account.setExpenseAmount(BigDecimal.ZERO);
            account.setCreditAmount(BigDecimal.ZERO);
            account.setSeq(Constants.DEFAULT_SEQ);
            accounts.add(account);
        }

        List<Account> finalAccounts = accounts;
        Object obj = template.execute(status -> {
            try {

                int insert = accountMapper.insertBatch(finalAccounts);
                return SqlHelper.retBool(insert);
            } catch (Exception e) {
                log.error("账户开户异常 {} error", JSONObject.toJSONString(finalAccounts), e);
                status.setRollbackOnly();
                throw e;
            }
        });
        if (obj instanceof Exception) {
            throw new BusinessException(ACCOUNT_SERVICE_ERROR, "保存账户数据失败！");
        }
        return (Boolean) obj;
    }

    @Override
    public boolean trade(TradeDto tradeDto) {

        // 查询账户信息
        List<Account> accounts = accountMapper.queryAccount(tradeDto.getUserId(), tradeDto.getAccountType(), tradeDto.getSubAccountTypes());
        if (accounts.isEmpty()) {
            throw new BusinessException(ACCOUNT_NOT_EXIST, "账户不存在");
        }
        if (!canDeduct(accounts, tradeDto.getAmount())) {
            log.warn("余额不足");
            return false;
        }
        // 交易类型和交易金额 是否允许欠款
        Integer transType = tradeDto.getTransType();
        BigDecimal amount = tradeDto.getAmount();
        // 交易类型
        TransTypeEnum transTypeEnum = TransTypeEnum.checkTransType(transType);

        // 账户事务操作
        Object obj = template.execute(status -> {
            try {
                BigDecimal remainingAmount = tradeDto.getAmount();
                for (SubAccountTypeEnum subType : SubAccountTypeEnum.values()) {
                    if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                        break;
                    }
                    Optional<Account> accountOptional = accounts.stream()
                            .filter(account -> account.getSubType() == subType.getCode())
                            .findFirst();
                    if (accountOptional.isPresent()) {
                        Account account = accountOptional.get();
                        BigDecimal deductionAmount;
                        if (AccountUtil.isGreaterOrEqual(account.getBalance(), remainingAmount)) {
                            deductionAmount = remainingAmount;
                            remainingAmount = BigDecimal.ZERO;
                        } else {
                            deductionAmount = account.getBalance();
                            remainingAmount = remainingAmount.subtract(account.getBalance());
                        }
                        // 检查账户余额是否足够
                        Account update = accountMapper.queryAccountForUpdate(account.getAccountId());
                        BigDecimal beforeBalance = update.getBalance();
                        // 初始化账户流水数据
                        Boolean credit = tradeDto.getCredit();
                        AccountLog accountLog = new AccountLog();
                        accountLog.setAccountId(account.getId());
                        accountLog.setRequestNo(tradeDto.getRequestNo());
                        accountLog.setOrderNo(tradeDto.getOrderNo());
                        accountLog.setUserId(account.getUserId());
                        accountLog.setAccountType(account.getAccountType());
                        accountLog.setOtherAccount(tradeDto.getOtherAccount());
                        accountLog.setOtherAccountType(tradeDto.getOtherAccountType());
                        accountLog.setActionType(transType);
                        accountLog.setProdType(tradeDto.getProdType());
                        accountLog.setTransAmount(deductionAmount);
                        accountLog.setSource(tradeDto.getSource());
                        accountLog.setRemark(tradeDto.getRemark());
                        accountLog.setSeq(Constants.DEFAULT_SEQ);
                        //计算账户金额
                        AccountUtil.opt(update, deductionAmount, transTypeEnum.getOpt(), credit);
                        accountLog.setBeforeBalance(beforeBalance);
                        accountLog.setAfterBalance(update.getBalance());
                        //记录交易记录 更新交易流水表
                        logMapper.insert(accountLog);
                        accountMapper.updateById(update);
                    }
                }
                return true;
            } catch (Exception e) {
                log.error("账户交易失败 {} error", JSONObject.toJSONString(tradeDto), e);
                status.setRollbackOnly();
                throw e;
            }
        });
        if (obj instanceof Exception) {
            throw new BusinessException(ACCOUNT_SAVE_FAILURE, "操作账户数据失败！");
        }
        return (Boolean) obj;
    }

    public boolean canDeduct(List<Account> list, BigDecimal deductionAmount) {
        BigDecimal totalBalance = BigDecimal.ZERO;
        for (Account account : list) {
            totalBalance = totalBalance.add(account.getBalance());
        }
        return totalBalance.compareTo(deductionAmount) >= 0;
    }

    @Override
    public List<Account> queryAccount(AccountDto accountDto) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", accountDto.getUserId());
        List<Account> accounts = new ArrayList<>();
        accounts = accountMapper.queryAccountByCond(params);
//        List<Account> accounts = accountMapper.queryAccount(accountDto.getUserId(), accountDto.getAccountType(), accountDto.getSubTypes());
        return accounts;
    }

    @Override
    public List<AccountLog> queryAccountLog(AccountDto accountDto) {
        List<AccountLog> accountLogs = logMapper.queryAccountLog(accountDto.getUserId(), accountDto.getAccountType(), accountDto.getSubTypes());
        return accountLogs;
    }


}
