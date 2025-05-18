package com.keji.green.lit.engine.dao.impl;

import com.keji.green.lit.engine.dao.TradePaymentRecordDao;
import com.keji.green.lit.engine.mapper.TradePaymentOrderMapper;
import com.keji.green.lit.engine.mapper.TradePaymentRecordMapper;
import com.keji.green.lit.engine.model.TradePaymentRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Repository("tradePaymentRecordDao")
public class TradePaymentRecordDaoImpl implements TradePaymentRecordDao {

    @Autowired
    TradePaymentRecordMapper tradePaymentRecordMapper;


    @Override
    public int insert(TradePaymentRecord entity) {
        return tradePaymentRecordMapper.insertSelective(entity);
    }

    @Override
    public int insert(List<TradePaymentRecord> list) {
        return 0;
    }

    @Override
    public int update(TradePaymentRecord entity) {
        return 0;
    }

    @Override
    public int update(List<TradePaymentRecord> list) {
        return 0;
    }

    @Override
    public int update(Map<String, Object> paramMap) {
        return 0;
    }
}
