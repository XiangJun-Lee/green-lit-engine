package com.keji.green.lit.engine.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Data
public class TradeDto implements Serializable {

    private static final long serialVersionUID = 2787588891032132609L;

    // 交易账户id
    private Long transId;

    private Long userId;

    private Integer accountType;

    private List<Integer> subAccountTypes;

    // 交易金额
    private BigDecimal amount;

    private String requestNo;

    private String orderNo;

    // 对方账户
    private Long otherAccount;

    private Integer otherAccountType;

    private Integer otherSubAccountType;

    // 交易类型
    private String prodType;
    // 交易类型
    private Integer transType;

    private String source;

    private String remark;

//    private String appId;

    // 记否记录欠款
    private Boolean credit = false;

    // 处理状态
    private Integer status;


//    public static void main(String[] args) {
//
//        BigDecimal dd = new BigDecimal("1.1");
//        System.out.println(dd.setScale(0, BigDecimal.ROUND_UP));
//
//        BigDecimal div = NumberUtil.div(new BigDecimal(23), BigDecimal.valueOf(10), 0, RoundingMode.DOWN);
//        System.out.println(div);
//
//    }

}
