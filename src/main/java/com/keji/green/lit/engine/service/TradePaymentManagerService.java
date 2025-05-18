package com.keji.green.lit.engine.service;

import com.keji.green.lit.engine.dto.request.trade.ScanPayRequestBo;
import com.keji.green.lit.engine.model.trade.PayConfig;
import com.keji.green.lit.engine.model.trade.ScanPayResultVo;
import org.springframework.stereotype.Service;

@Service
public interface TradePaymentManagerService {
    public ScanPayResultVo initDirectScanPay(PayConfig rpUserPayConfig , ScanPayRequestBo scanPayRequestBo);
}
