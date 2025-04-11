package com.keji.green.lit.engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户邀请关系实体类
 *
 * @author xiangjun_lee
 * @since 2024-04-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInviteRelation {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 邀请人用户ID
     */
    private Long inviterUid;

    /**
     * 被邀请人用户ID
     */
    private Long inviteeUid;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModify;
}