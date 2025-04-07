package com.keji.green.lit.engine.mapper;

import com.keji.green.lit.engine.model.InterviewInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author xiangjun_lee
 */
@Mapper
public interface InterviewInfoMapper {

    int insertSelective(InterviewInfo row);


    InterviewInfo selectByPrimaryKey(String interviewId);


    int updateByPrimaryKeySelective(InterviewInfo row);

}