package com.keji.green.lit.engine.dao;

import com.keji.green.lit.engine.model.TradePaymentOrder;

public interface TradePaymentOrderDao extends BaseDao<TradePaymentOrder>{
    TradePaymentOrder selectByMerchantNoAndMerchantOrderNo(long userId,String merchantNo, String merchantOrderNo);
}
