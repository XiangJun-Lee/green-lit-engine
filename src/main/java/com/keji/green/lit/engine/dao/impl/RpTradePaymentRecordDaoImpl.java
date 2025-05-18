package com.keji.green.lit.engine.dao.impl;

import com.keji.green.lit.engine.dao.RpTradePaymentRecordDao;
import com.keji.green.lit.engine.model.TradePaymentRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Repository("rpTradePaymentRecordDao")
public class RpTradePaymentRecordDaoImpl implements RpTradePaymentRecordDao {
    @Override
    public int insert(TradePaymentRecord entity) {
        return 0;
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
