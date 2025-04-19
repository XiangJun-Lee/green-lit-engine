package com.keji.green.lit.engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountLog {

    private Long id;

    private Byte shardingKey;

    private Long accountId;

    private String requestNo;

    private String orderNo;

    private Long userId;

    private Byte accountType;

    private Byte subType;

    private Long otherAccount;

    private Byte otherAccountType;


    private Byte otherAccountSubType;

    private Byte actionType;

    private String prodType;

    private BigDecimal beforeBalance;

    private BigDecimal transAmount;

    private BigDecimal afterBalance;

    private String source;

    private String remark;

    private Long seq;

    private Date createTime;

    private Date updateTime;

}