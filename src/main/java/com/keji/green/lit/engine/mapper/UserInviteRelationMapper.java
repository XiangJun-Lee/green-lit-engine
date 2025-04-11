package com.keji.green.lit.engine.mapper;

import com.keji.green.lit.engine.model.UserInviteRelation;

import java.util.List;

/**
 * 用户邀请关系数据访问接口
 * @author xiangjun_lee
 */
public interface UserInviteRelationMapper {
    /**
     * 根据主键删除用户邀请关系记录
     *
     * @param id 主键ID
     * @return 影响的行数
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 选择性插入用户邀请关系记录（只插入非空字段）
     *
     * @param row 用户邀请关系实体
     * @return 影响的行数
     */
    int insertSelective(UserInviteRelation row);

    /**
     * 根据主键查询用户邀请关系记录
     *
     * @param id 主键ID
     * @return 用户邀请关系实体
     */
    UserInviteRelation selectByPrimaryKey(Long id);

    /**
     * 根据主键选择性更新用户邀请关系记录（只更新非空字段）
     *
     * @param row 用户邀请关系实体
     * @return 影响的行数
     */
    int updateByPrimaryKeySelective(UserInviteRelation row);

    List<UserInviteRelation> selectByInviterId(Long inviterId);

    UserInviteRelation selectByInviteeId(Long inviteeId);

}