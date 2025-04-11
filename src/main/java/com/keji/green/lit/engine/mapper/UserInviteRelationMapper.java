package com.keji.green.lit.engine.mapper;

import com.keji.green.lit.engine.model.UserInviteRelation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户邀请关系数据访问接口
 * @author xiangjun_lee
 */
@Mapper
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

    /**
     * 根据邀请人ID查询用户邀请关系记录
     *
     * @param inviterId 邀请人ID
     * @return 用户邀请关系实体列表
     */
    List<UserInviteRelation> selectByInviterId(Long inviterId);

    /**
     * 根据被邀请人ID查询用户邀请关系记录
     *
     * @param inviteeId 被邀请人ID
     * @return 用户邀请关系实体
     */
    UserInviteRelation selectByInviteeId(Long inviteeId);

}