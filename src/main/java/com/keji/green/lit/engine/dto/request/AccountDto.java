package com.keji.green.lit.engine.dto.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AccountDto implements Serializable {

    private static final long serialVersionUID = -6749243131059493189L;

    private Long userId;

    private Integer accountType;

    private List<Integer> subTypes;

}
