package com.keji.green.lit.engine.dao.impl;

import com.keji.green.lit.engine.dao.TradePaymentRecordDao;
import com.keji.green.lit.engine.mapper.TradePaymentOrderMapper;
import com.keji.green.lit.engine.mapper.TradePaymentRecordMapper;
import com.keji.green.lit.engine.model.TradePaymentRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
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
        TradePaymentRecord update = new TradePaymentRecord();
        update.setId(entity.getId());
        update.setUserId(entity.getUserId());
        update.setShardingKey(entity.getShardingKey());
        update.setTrxNo(entity.getTrxNo());
        if (StringUtils.isNotBlank(entity.getBankReturnMsg())){
            update.setBankReturnMsg(entity.getBankReturnMsg());
        }
        if (StringUtils.isNotBlank(entity.getStatus())){
            update.setStatus(entity.getStatus());
        }
        if (StringUtils.isNotBlank(entity.getTrxNo())){
            update.setTrxNo(entity.getTrxNo());
        }
        if (StringUtils.isNotBlank(entity.getBankTrxNo())){
            update.setBankTrxNo(entity.getBankTrxNo());
        }
        if (entity.getPaySuccessTime() != null){
            update.setPaySuccessTime(entity.getPaySuccessTime());
        }
        update.setVersion(entity.getVersion());
        return tradePaymentRecordMapper.updateByPrimaryKeySelective(entity);

    }

    @Override
    public int update(List<TradePaymentRecord> list) {
        return 0;
    }

    @Override
    public int update(Map<String, Object> paramMap) {
        return 0;
    }

    @Override
    public TradePaymentRecord getByBankOrderNo(int shardingKey, long userId, String bankOrderNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("sharding_key", shardingKey);
        params.put("bank_order_no", bankOrderNo);
        return tradePaymentRecordMapper.queryTradePaymentRecord(params);
    }
}
