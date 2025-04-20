package com.keji.green.lit.engine.enums;


import com.keji.green.lit.engine.exception.BusinessException;
import lombok.Getter;

import static com.keji.green.lit.engine.exception.ErrorCode.ACCOUNT_SERVICE_ERROR;

@Getter
public enum SubAccountTypeEnum {

//    MAIN_ACCOUNT(0,"主账户", "0"),
    CASH(10,"现金子账户", "10"),
    PROMOTION(20,"返现子账户", "20"),

    ;

    SubAccountTypeEnum(Integer code, String name, String prefix) {
        this.code = code;
        this.name = name;
        this.prefix = prefix;
    }

    private Integer code;
    private String name;
    private String prefix;


    public static Integer accountType(String accNo){

        for (SubAccountTypeEnum node : SubAccountTypeEnum.values()){
            if(accNo.startsWith(node.getPrefix())){
                return node.code;
            }
        }
        return -1;
    }


    /**
     * 校验账户类型
     */
    public static SubAccountTypeEnum checkAccountType(Integer accountType){

        for (SubAccountTypeEnum node : SubAccountTypeEnum.values()){
            if(node.code == accountType){
                return node;
            }
        }
        throw new BusinessException(ACCOUNT_SERVICE_ERROR, "子账户类型不存在");
    }


}
