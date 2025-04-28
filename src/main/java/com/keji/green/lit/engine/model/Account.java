package com.keji.green.lit.engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    private Long id;

    private int shardingKey;

    private Long userId;

    private Long accountId;

    private int accountType;

    private int subType;

    private BigDecimal balance;

    private int status;

    private BigDecimal incomeAmount;

    private BigDecimal expenseAmount;

    private BigDecimal creditAmount;

    private BigDecimal freezeAmount;

    private Long seq;

    private Date createTime;

    private Date updateTime;

}