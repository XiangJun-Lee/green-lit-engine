package com.keji.green.lit.engine.mapper;

import com.keji.green.lit.engine.model.InterviewInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author xiangjun_lee
 */
@Mapper
public interface InterviewInfoMapper {

    int insertSelective(InterviewInfo row);


    Optional<InterviewInfo> selectByPrimaryKey(String interviewId);


    int updateByPrimaryKeySelective(InterviewInfo row);
    
    /**
     * 分页查询面试列表
     * @param params 包含分页参数和查询条件
     * @return 面试信息列表
     */
    List<InterviewInfo> selectPageByUserId(Map<String, Object> params);
    
    /**
     * 统计用户的面试数量
     * @param params 包含查询条件
     * @return 满足条件的面试数量
     */
    long countByUserIdAndStatus(Map<String, Object> params);
}