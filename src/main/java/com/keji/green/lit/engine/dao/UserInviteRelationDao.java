package com.keji.green.lit.engine.dao;

import com.keji.green.lit.engine.mapper.UserInviteRelationMapper;
import com.keji.green.lit.engine.model.UserInviteRelation;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户邀请关系数据访问对象
 *
 * @author xiangjun_lee
 * @since 2024-04-11
 */
@Repository
public class UserInviteRelationDao {
    
    @Resource
    private UserInviteRelationMapper mapper;

    /**
     * 保存邀请关系
     */
    public int save(UserInviteRelation relation) {
        return mapper.insertSelective(relation);
    }

    /**
     * 根据邀请人ID查询邀请关系
     */
    public List<UserInviteRelation> findByInviterId(Long inviterId) {
        return mapper.selectByInviterId(inviterId);
    }

    /**
     * 根据被邀请人ID查询邀请关系
     */
    public UserInviteRelation findByInviteeId(Long inviteeId) {
        return mapper.selectByInviteeId(inviteeId);
    }

    public long selectCountByInviterId(Long inviterId) {
        return mapper.selectCountByInviterId(inviterId);
    }
}