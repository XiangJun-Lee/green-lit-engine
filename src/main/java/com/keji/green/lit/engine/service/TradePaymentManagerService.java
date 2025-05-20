package com.keji.green.lit.engine.service;

import com.keji.green.lit.engine.dto.request.trade.ScanPayRequestBo;
import com.keji.green.lit.engine.model.trade.PayConfig;
import com.keji.green.lit.engine.model.trade.ScanPayResultVo;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface TradePaymentManagerService {
    /**
     * 初始化直连扫码支付数据,直连扫码支付初始化方法规则
     * 1:根据(商户编号 + 商户订单号)确定订单是否存在
     * 1.1:如果订单不存在,创建支付订单
     * 2:创建支付记录
     * 3:根据相应渠道方法
     * 4:调转到相应支付渠道扫码界面
     **/
    public ScanPayResultVo initDirectScanPay(PayConfig rpUserPayConfig , ScanPayRequestBo scanPayRequestBo);
    /**
     * 完成扫码支付(支付宝即时到账支付)
     *
     * @param payWayCode
     * @param notifyMap
     * @return
     */
    public String completeScanPay(String payWayCode, Map<String, String> notifyMap);

}
