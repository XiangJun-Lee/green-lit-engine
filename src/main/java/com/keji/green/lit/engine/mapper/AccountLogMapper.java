package com.keji.green.lit.engine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.keji.green.lit.engine.model.Account;
import com.keji.green.lit.engine.model.AccountLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface AccountLogMapper extends BaseMapper<AccountLog> {
    List<AccountLog> queryAccountLog(@Param("userId") Long userId, @Param("accountType") Integer accountType, @Param("subAccountTypes") List<Integer> subAccountTypes);
}