package com.keji.green.lit.engine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.keji.green.lit.engine.model.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {

    /**
     * 查询账户信息
     * @param userId
     * @param accountType
     * @return
     */
    List<Account> queryAccount(@Param("userId") Long userId, @Param("accountType") Integer accountType,@Param("subAccountTypes") List<Integer> subAccountTypes);

    List<Account> queryAccountByCond(Map<String, Object> params);

    @Select("select * from account where ")
    List<Account> selectListBySql();
    /**
     * 根据账户查询
     * @param accountId
     * @return
     */
    Account queryAccountForUpdate(Long accountId);


    int insertBatch(List<Account> accounts);

}