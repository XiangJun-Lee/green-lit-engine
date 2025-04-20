package com.keji.green.lit.engine.enums;


import com.keji.green.lit.engine.exception.BusinessException;
import lombok.Getter;

import static com.keji.green.lit.engine.exception.ErrorCode.TRANS_TYPE_NOT_EXIST;

@Getter
public enum TransTypeEnum {

    TRANS_IN(101, "转入", "+"),
    TRANS_OUT(102, "转出", "-"),
    TRADE_IN(103, "入账", "+"),
    TRADE_OUT(104, "出账", "-"),

    FREEZE(201, "冻结", "|"),
    UNFREEZE(202, "解冻", "|"),
    FREEZE_OUT(203, "解冻并出账", "-"),
    FREEZE_IN(204, "入账并冻结", "+"),


    ;


    TransTypeEnum(Integer code, String name, String opt) {
        this.code = code;
        this.name = name;
        this.opt = opt;
    }

    private Integer code;

    private String name;

    private String opt;

    /**
     * 交易交易类型
     */
    public static TransTypeEnum checkTransType(Integer transType){

        for (TransTypeEnum node : TransTypeEnum.values()){
            if(node.code == transType){
                return node;
            }
        }
        throw new BusinessException(TRANS_TYPE_NOT_EXIST, "交易类型不存在");
    }




}

