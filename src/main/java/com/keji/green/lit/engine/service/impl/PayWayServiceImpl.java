package com.keji.green.lit.engine.service.impl;

import com.keji.green.lit.engine.exception.BusinessException;
import com.keji.green.lit.engine.model.trade.PayWay;
import com.keji.green.lit.engine.service.PayWayService;
import org.springframework.stereotype.Service;

import static io.vertx.sqlclient.impl.SocketConnectionBase.logger;

@Service("payWayService")
public class PayWayServiceImpl implements PayWayService {
    @Override
    public void saveData(PayWay rpPayWay) {

    }

    @Override
    public void updateData(PayWay rpPayWay) {

    }

    @Override
    public PayWay getDataById(String id) {
        return null;
    }

    @Override
    public PayWay getByPayWayTypeCode(String payProductCode, String payWayCode, String rpTypeCode) {
        if (payWayCode.equals("ALIPAY")){
            PayWay payWay = new PayWay();
            payWay.setPayProductCode("");
            payWay.setPayRate(0.006);
            payWay.setPayTypeName("");
            payWay.setPayProductCode("");
            payWay.setPayWayName("ALIPAY");
            payWay.setPayTypeCode("ALI_SCAN_PAY");
            return payWay;
        }
        logger.info("支付类型有误, 不支持");
        return null;
    }

    @Override
    public void createPayWay(String payProductCode, String payWayCode, String payTypeCode, Double payRate) throws BusinessException {

    }
}
