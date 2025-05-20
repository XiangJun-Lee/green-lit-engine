package com.keji.green.lit.engine.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoResponse {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccountWrapper {
        private Long id;
        private Long userId;
        private Long accountId;
        private Integer accountType;
        private Integer subAccountType;
        private String balance;
        private Integer status;
        private String gmtCreate;
    }

    private List<AccountWrapper> accountList;

    public List<AccountWrapper> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<AccountWrapper> accountList) {
        this.accountList = accountList;
    }

}
