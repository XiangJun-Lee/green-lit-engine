package com.keji.green.lit.engine.controller;

import com.keji.green.lit.engine.dto.request.trade.ScanPayRequestBo;
import com.keji.green.lit.engine.enums.PayWayEnum;
import com.keji.green.lit.engine.model.trade.PayConfig;
import com.keji.green.lit.engine.model.trade.ScanPayResultVo;
import com.keji.green.lit.engine.service.TradePaymentManagerService;
import com.keji.green.lit.engine.utils.pay.PayUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static io.vertx.sqlclient.impl.SocketConnectionBase.logger;

@Validated
@RestController
@RequestMapping("/api/pay")
public class PayController {
    private static final Logger logger = LoggerFactory.getLogger(PayController.class);


    @Autowired
    private TradePaymentManagerService tradePaymentManagerService;

    @RequestMapping("/ali/prePay")
    public String aliPrePay(@RequestBody ScanPayRequestBo scanPayRequestBo, BindingResult bindingResult, Model model, HttpServletRequest httpServletRequest) {
        logger.info("======>进入扫码支付{}", scanPayRequestBo);
        PayConfig rpUserPayConfig = new PayConfig();
        BigDecimal orderPrice = scanPayRequestBo.getOrderPrice();
        ScanPayResultVo scanPayResultVo = tradePaymentManagerService.initDirectScanPay(rpUserPayConfig , scanPayRequestBo);

        model.addAttribute("codeUrl", scanPayResultVo.getCodeUrl());//支付二维码

        return scanPayResultVo.getCodeUrl();
    }

    // WEIXIN / ALIPAY
    @RequestMapping("/notify/{payWayCode}")
    public void notify(@PathVariable("payWayCode") String payWayCode, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        Map<String, String> notifyMap = new HashMap<String, String>();
        if (PayWayEnum.ALIPAY.name().equals(payWayCode)) {
            Map<String, String[]> requestParams = httpServletRequest.getParameterMap();
            notifyMap = PayUtil.parseNotifyMsg(requestParams);
        }

        String completeWeiXinScanPay = tradePaymentManagerService.completeScanPay(payWayCode, notifyMap);
        if (!StringUtils.isEmpty(completeWeiXinScanPay)) {
            httpServletResponse.getWriter().print(completeWeiXinScanPay);
        }

    }
}