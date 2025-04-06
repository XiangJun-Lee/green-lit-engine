package com.keji.green.lit.engine.mapper;

import com.keji.green.lit.engine.model.InterviewInfo;

/**
 * @author xiangjun_lee
 */
public interface InterviewInfoMapper {

    int insertSelective(InterviewInfo row);


    InterviewInfo selectByPrimaryKey(String interviewId);


    int updateByPrimaryKeySelective(InterviewInfo row);


}