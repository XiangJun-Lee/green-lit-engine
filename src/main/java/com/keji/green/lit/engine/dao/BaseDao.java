package com.keji.green.lit.engine.dao;

import java.util.List;
import java.util.Map;

public interface BaseDao<T> {
    int insert(T entity);
    int insert(List<T> list);
    int update(T entity);
    int update(List<T> list);
    int update(Map<String, Object> paramMap);
//    public T getById(String id) {
//        return sessionTemplate.selectOne(getStatement(SQL_SELECT_BY_ID), id);
//    }

}
