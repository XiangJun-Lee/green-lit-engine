package com.keji.green.lit.engine.dao.impl;

import com.keji.green.lit.engine.dao.TradePaymentOrderDao;
import com.keji.green.lit.engine.mapper.TradePaymentOrderMapper;
import com.keji.green.lit.engine.model.TradePaymentOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("tradePaymentOrderDao")
public class TradePaymentOrderDaoImpl  implements TradePaymentOrderDao {
    @Autowired
    TradePaymentOrderMapper tradePaymentOrderMapper;
    @Override
    public TradePaymentOrder selectByMerchantNoAndMerchantOrderNo(long userId, String merchantNo, String merchantOrderNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("merchant_no", merchantNo);
        params.put("merchant_order_no", merchantOrderNo);
        return tradePaymentOrderMapper.queryTradePaymentOrder(params);
    }

    @Override
    public int insert(TradePaymentOrder entity) {
        return tradePaymentOrderMapper.insertSelective(entity);
    }

    @Override
    public int insert(List<TradePaymentOrder> list) {
        return 0;
    }

    @Override
    public int update(TradePaymentOrder entity) {
        return 0;
    }

    @Override
    public int update(List<TradePaymentOrder> list) {
        return 0;
    }

    @Override
    public int update(Map<String, Object> paramMap) {
        return 0;
    }
}
