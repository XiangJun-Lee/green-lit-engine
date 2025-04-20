package com.keji.green.lit.engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountLog implements Serializable {

    private Long id;

    private int shardingKey;

    private Long accountId;

    private String requestNo;

    private String orderNo;

    private Long userId;

    private int accountType;

    private int subType;

    private Long otherAccount;

    private int otherAccountType;


    private int otherAccountSubType;

    private int actionType;

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