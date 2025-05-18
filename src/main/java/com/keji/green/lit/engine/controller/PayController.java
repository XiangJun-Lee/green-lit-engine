package com.keji.green.lit.engine.controller;

import com.keji.green.lit.engine.dto.request.trade.ScanPayRequestBo;
import com.keji.green.lit.engine.model.trade.PayConfig;
import com.keji.green.lit.engine.model.trade.ScanPayResultVo;
import com.keji.green.lit.engine.service.TradePaymentManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

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
}