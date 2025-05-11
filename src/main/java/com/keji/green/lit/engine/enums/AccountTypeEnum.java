package com.keji.green.lit.engine.enums;


import com.keji.green.lit.engine.exception.BusinessException;
import lombok.Getter;

import static com.keji.green.lit.engine.exception.ErrorCode.ACCOUNT_SERVICE_ERROR;

@Getter
public enum AccountTypeEnum {

    USER(100,"用户账户", "100"),
    PLATFORM(200,"平台账户", "200"),

    ;

    AccountTypeEnum(int code, String name, String prefix) {
        this.code = code;
        this.name = name;
        this.prefix = prefix;
    }

    private int code;
    private String name;
    private String prefix;


    public static Integer accountType(String accNo){

        for (AccountTypeEnum node : AccountTypeEnum.values()){
            if(accNo.startsWith(node.getPrefix())){
                return node.code;
            }
        }
        return -1;
    }


    /**
     * 校验账户类型
     */
    public static AccountTypeEnum checkAccountType(int accountType){

        for (AccountTypeEnum node : AccountTypeEnum.values()){
            if(node.code == accountType){
                return node;
            }
        }
        throw new BusinessException(ACCOUNT_SERVICE_ERROR, "账户类型不存在");
    }


}
