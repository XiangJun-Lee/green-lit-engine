package com.keji.green.lit.engine.utils;

import com.keji.green.lit.engine.exception.BusinessException;
import com.keji.green.lit.engine.exception.ErrorCode;
import com.keji.green.lit.engine.model.Account;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;


public class AccountUtil {
    public static void opt(Account account, BigDecimal amount, String opt, Boolean credit) {
        BigDecimal balance = account.getBalance();
        if (StringUtils.equals("+", opt)) {
            // 设置账户余额和累计收入
            account.setBalance(balance.add(amount));
            account.setIncomeAmount(account.getIncomeAmount().add(amount));
            return;
        }
        if (StringUtils.equals("-", opt)) {
            // 支出金额 = 原支出金额 + 交易金额
            account.setExpenseAmount(account.getExpenseAmount().add(amount));
            if (isGreaterOrEqual(balance, amount)) {
                account.setBalance(balance.subtract(amount));
            } else {
                // 允许欠款
                if (credit) {
                    account.setBalance(BigDecimal.ZERO);
                    // 欠款金额 = 原欠款金额 + (交易金额 - 账户余额)
                    BigDecimal add = account.getCreditAmount().add(amount.subtract(balance));
                    account.setCreditAmount(add);
                } else {
                    throw new BusinessException(ErrorCode.ACCOUNT_NOT_ENOUGH, "账户余额不足,操作失败!");
                }
            }
            return;
        }
        throw new BusinessException(ErrorCode.ACCOUNT_OPERATION_NOT_EXIST, "操作类型不存在");
    }
    public static boolean isGreaterOrEqual(BigDecimal bigNum1, BigDecimal bigNum2) {
        return bigNum1.compareTo(bigNum2) >= 0;
    }
    public static long GenAccountId(int accountType, int subAccountType,long userId) {
        // 验证 userId 范围
        if (userId <= 0 || userId > 9999999999L) {
            throw new IllegalArgumentException("userId 必须是大于0且不超过10位的整数");
        }

//        // 验证 accountType
//        if (accountType < 1 || accountType > 3) {
//            throw new IllegalArgumentException("accountType 必须是1、2、3中的一个");
//        }

//        // 验证 subAccountType
//        if (subAccountType < 1 || subAccountType > 2) {
//            throw new IllegalArgumentException("subAccountType 必须是1、2中的一个");
//        }

        // 格式化 subAccountType 为3位字符串
        String subAccountTypeStr = String.format("%03d", subAccountType);

        // 格式化 userId 为10位字符串，不足10位时前面补0
        String userIdStr = String.format("%010d", userId);

        // 组合各部分生成 accountId
        String accountIdStr = accountType + subAccountTypeStr + userIdStr;

        // 转换为 long 类型
        return Long.parseLong(accountIdStr);
    }
    public static int CalShardingKey(long value) {
        return (int) (value % 10);
    }
}