package com.keji.green.lit.engine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.keji.green.lit.engine.model.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {

    List<Account> queryAccount(@Param("userId") Long userId, @Param("accountType") Integer accountType,@Param("subAccountTypes") List<Integer> subAccountTypes);

    List<Account> queryAccountByCondV1(Map<String, Object> params);

    Account queryAccountForUpdate(Map<String, Object> params);

    int insertBatch(List<Account> accounts);

    int updateByAccountId(Account  account);

}