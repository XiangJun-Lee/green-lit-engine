package com.keji.green.lit.engine.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.keji.green.lit.engine.dao.TradePaymentOrderDao;
import com.keji.green.lit.engine.dao.RpTradePaymentRecordDao;
import com.keji.green.lit.engine.dto.request.trade.ScanPayRequestBo;
import com.keji.green.lit.engine.enums.PayTypeEnum;
import com.keji.green.lit.engine.enums.PayWayEnum;
import com.keji.green.lit.engine.enums.TradeStatusEnum;
import com.keji.green.lit.engine.exception.BusinessException;
import com.keji.green.lit.engine.model.TradePaymentOrder;
import com.keji.green.lit.engine.model.TradePaymentRecord;
import com.keji.green.lit.engine.model.trade.PayConfig;
import com.keji.green.lit.engine.model.trade.PayWay;
import com.keji.green.lit.engine.model.trade.ScanPayResultVo;
import com.keji.green.lit.engine.service.PayWayService;
import com.keji.green.lit.engine.service.TradePaymentManagerService;
import com.keji.green.lit.engine.utils.AccountUtil;
import com.keji.green.lit.engine.utils.DateTimeUtils;
import com.keji.green.lit.engine.utils.pay.AlipayConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alipay.api.AlipayClient;


import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.keji.green.lit.engine.exception.ErrorCode.*;

@Service("tradePaymentManagerService")
public class TradePaymentManagerServiceImpl implements TradePaymentManagerService {
    private static final Logger logger = LoggerFactory.getLogger(TradePaymentManagerServiceImpl.class);
    @Autowired
    private TradePaymentOrderDao tradePaymentOrderDao;
    @Autowired
    private RpTradePaymentRecordDao rpTradePaymentRecordDao;

    @Autowired
    private PayWayService payWayService;

    @Override
    public ScanPayResultVo initDirectScanPay(PayConfig rpUserPayConfig, ScanPayRequestBo scanPayRequestBo) {
        logger.info("======>扫码支付，直连方式");
        PayWay payWay = null;
        PayTypeEnum payType = PayTypeEnum.getEnum(scanPayRequestBo.getPayType());
        if (payType == null) {
            logger.info("支付类型有误");
            throw new BusinessException(PAY_TYPE_NOT_EXIST.getCode(), "支付类型有误");
        }
        String payWayCode = payType.getWay();
        String name = payType.name();
        logger.info("name = ", name);
        payWay = payWayService.getByPayWayTypeCode(rpUserPayConfig.getProductCode(), payWayCode, payType.name());

        if (payWay == null) {
            logger.info("支付配置有误，不配置支付方式{}", payType.name());
            throw new BusinessException(PAY_CONFIG_ERRPR, "支付配置有误");
        }
        String merchantNo = AlipayConfigUtil.seller_id;
//        RpUserInfo rpUserInfo = rpUserInfoService.getDataByMerchentNo(merchantNo);
//        if (rpUserInfo == null) {
//            throw new UserBizException(UserBizException.USER_IS_NULL, "用户不存在");
//        }
//        TradePaymentOrder rpTradePaymentOrder = TradePaymentOrderDao.selectByMerchantNoAndMerchantOrderNo(merchantNo, scanPayRequestBo.getOrderNo());
        TradePaymentOrder rpTradePaymentOrder = tradePaymentOrderDao.selectByMerchantNoAndMerchantOrderNo(scanPayRequestBo.getUserId(), merchantNo, scanPayRequestBo.getOrderNo());
        if (rpTradePaymentOrder == null) {
            rpTradePaymentOrder = sealScanPayRpTradePaymentOrder(rpUserPayConfig, scanPayRequestBo, null);
            tradePaymentOrderDao.insert(rpTradePaymentOrder);
        } else {
            if (TradeStatusEnum.SUCCESS.name().equals(rpTradePaymentOrder.getStatus())) {
                throw new BusinessException(PAY_ORDER_ERROR, "订单已支付成功,无需重复支付");
            }
            if (rpTradePaymentOrder.getOrderAmount().compareTo(scanPayRequestBo.getOrderPrice()) != 0) {
                rpTradePaymentOrder.setOrderAmount(scanPayRequestBo.getOrderPrice());// 如果金额不一致,修改金额为最新的金额
            }
        }

        return getScanPayResultVo(rpTradePaymentOrder, payWay, scanPayRequestBo.getNumberOfStages());
    }


    private TradePaymentOrder sealScanPayRpTradePaymentOrder(PayConfig rpUserPayConfig, ScanPayRequestBo scanPayRequestBo, Object rpUserInfo) {

        TradePaymentOrder tradePaymentOrder = new TradePaymentOrder();
        tradePaymentOrder.setUserId(scanPayRequestBo.getUserId());
        tradePaymentOrder.setShardingKey(AccountUtil.CalShardingKey(scanPayRequestBo.getUserId()));
        tradePaymentOrder.setProductName(scanPayRequestBo.getProductName());// 商品名称

        tradePaymentOrder.setMerchantOrderNo(scanPayRequestBo.getOrderNo());// 订单号

        tradePaymentOrder.setOrderAmount(scanPayRequestBo.getOrderPrice());// 订单金额

//    rpTradePaymentOrder.setMerchantName(rpUserInfo.getUserName());// 商户名称

//    rpTradePaymentOrder.setMerchantNo(rpUserInfo.getUserNo());// 商户编号

        tradePaymentOrder.setOrderDate(DateTimeUtils.parseDate(scanPayRequestBo.getOrderDate()));// 下单日期
        Date orderTime = DateTimeUtils.parseDateTime(scanPayRequestBo.getOrderTime());

        tradePaymentOrder.setOrderTime(orderTime);// 下单时间
        tradePaymentOrder.setOrderIp(scanPayRequestBo.getOrderIp());// 下单IP
        tradePaymentOrder.setOrderRefererUrl("");// 下单前页面
        tradePaymentOrder.setReturnUrl(scanPayRequestBo.getReturnUrl());// 页面通知地址
        tradePaymentOrder.setNotifyUrl(scanPayRequestBo.getNotifyUrl());// 后台通知地址

        tradePaymentOrder.setOrderPeriod(scanPayRequestBo.getOrderPeriod());// 订单有效期

        Date expireTime = DateTimeUtils.addMinute(orderTime, scanPayRequestBo.getOrderPeriod());// 订单过期时间
        tradePaymentOrder.setExpireTime(expireTime);// 订单过期时间
        tradePaymentOrder.setStatus(TradeStatusEnum.WAITING_PAYMENT.name());// 订单状态
        // 等待支付

        PayTypeEnum payType = PayTypeEnum.getEnum(scanPayRequestBo.getPayType());
        if (payType != null) {

            tradePaymentOrder.setPayTypeCode(payType.name());// 支付类型
            tradePaymentOrder.setPayTypeName(payType.getDesc());// 支付方式

            PayWayEnum payWayEnum = PayWayEnum.getEnum(payType.getWay());
            tradePaymentOrder.setPayWayCode(payWayEnum.name());// 支付通道编码
            tradePaymentOrder.setPayWayName(payWayEnum.getDesc());// 支付通道名称
        }


        tradePaymentOrder.setFundIntoType(rpUserPayConfig.getFundIntoType());// 资金流入方向
        tradePaymentOrder.setRemark(scanPayRequestBo.getRemark());// 支付备注

        return tradePaymentOrder;
    }

    private ScanPayResultVo getScanPayResultVo(TradePaymentOrder rpTradePaymentOrder, PayWay payWay, Integer numberOfStages) {
        if (payWay == null){
            payWay = new PayWay();
            payWay.setPayTypeCode("DIRECT_PAY");
            payWay.setPayWayCode("ALIPAY");
            payWay.setPayRate(0.006);
            payWay.setPayWayCode("DIRECT_PAY");
            payWay.setPayProductCode("test");

        }
        ScanPayResultVo scanPayResultVo = new ScanPayResultVo();
        String payWayCode = payWay.getPayWayCode();// 支付方式

        PayTypeEnum payType = PayTypeEnum.getEnum(payWay.getPayTypeCode());

        rpTradePaymentOrder.setPayTypeCode(payType.name());
        rpTradePaymentOrder.setPayTypeName(payType.getDesc());
        rpTradePaymentOrder.setPayWayCode(payWay.getPayWayCode());
        rpTradePaymentOrder.setPayWayName(payWay.getPayWayName());
        tradePaymentOrderDao.update(rpTradePaymentOrder);
        TradePaymentRecord rpTradePaymentRecord = sealRpTradePaymentRecord(rpTradePaymentOrder.getMerchantNo(), rpTradePaymentOrder.getMerchantName(), rpTradePaymentOrder.getProductName(), rpTradePaymentOrder.getMerchantOrderNo(), rpTradePaymentOrder.getOrderAmount(), payWay.getPayWayCode(), payWay.getPayWayName(), payType, rpTradePaymentOrder.getFundIntoType(), BigDecimal.valueOf(payWay.getPayRate()), rpTradePaymentOrder.getOrderIp(), rpTradePaymentOrder.getReturnUrl(), rpTradePaymentOrder.getNotifyUrl(), rpTradePaymentOrder.getRemark(), rpTradePaymentOrder.getField1(), rpTradePaymentOrder.getField2(), rpTradePaymentOrder.getField3(), rpTradePaymentOrder.getField4(), rpTradePaymentOrder.getField5());
        rpTradePaymentRecordDao.insert(rpTradePaymentRecord);
//        if (PayWayEnum.ALIPAY.name().equals(payWayCode)) {
            String appId = "";
            String mchPrivateKey = "";

            if ("商家收款".equals(rpTradePaymentOrder.getFundIntoType())) {// 商户收款
//                RpUserPayInfo rpUserPayInfo = rpUserPayInfoService.getByUserNo(rpTradePaymentOrder.getMerchantNo(), payWayCode);
                appId = "";
                mchPrivateKey = "";
            }
//            else if (FundInfoTypeEnum.PLAT_RECEIVES.name().equals(rpTradePaymentOrder.getFundIntoType())) {// 平台收款
                appId = AlipayConfigUtil.app_id;
                mchPrivateKey = AlipayConfigUtil.mch_private_key;
//            }

            AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfigUtil.trade_pay_url, appId, mchPrivateKey, "json", "utf-8", AlipayConfigUtil.ali_public_key, "RSA2");

//            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
//            alipayRequest.setNotifyUrl(AlipayConfigUtil.notify_url);
//            alipayRequest.setReturnUrl(AlipayConfigUtil.return_url);
//
//            if (PayTypeEnum.DIRECT_PAY.name().equals(payType.name())) {
//
//                alipayRequest.setBizContent("{\"out_trade_no\":\"" + rpTradePaymentRecord.getBankOrderNo() + "\","
//                        + "\"total_amount\":\"" + rpTradePaymentOrder.getOrderAmount() + "\","
//                        + "\"subject\":\"" + rpTradePaymentOrder.getProductName() + "\","
//                        + "\"body\":\"" + rpTradePaymentOrder.getProductName() + "\","
//                        + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
//
//            }
//            try {
//                String alipayTradePagePayResponse = alipayClient.pageExecute(alipayRequest).getBody();
//                rpTradePaymentRecord.setBankReturnMsg(alipayTradePagePayResponse);
//                rpTradePaymentRecordDao.update(rpTradePaymentRecord);
//                scanPayResultVo.setCodeUrl(alipayTradePagePayResponse);// 设置支付宝跳转地址
//                scanPayResultVo.setPayWayCode(PayWayEnum.ALIPAY.name());
//                scanPayResultVo.setProductName(rpTradePaymentOrder.getProductName());
//                scanPayResultVo.setOrderAmount(rpTradePaymentOrder.getOrderAmount());
//
//            } catch (AlipayApiException e) {
//                logger.error("支付宝API异常：", e);
//                throw new BusinessException(PAY_REQUEST_BANK_ERR, "请求支付宝异常");
//            }
        String subject = "Javen 支付宝扫码支付测试";
        String totalAmount = "1";
        String storeId = "123";
        String notifyUrl =  "/alipay/precreate_notify_url";

        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setSubject(subject);
        model.setTotalAmount(totalAmount);
        model.setStoreId(storeId);
        model.setTimeoutExpress("5m");
        model.setOutTradeNo("test12345_001");
        try {
            String resultStr = tradePrecreatePayToResponse(alipayClient,model, notifyUrl).getBody();
            JSONObject jsonObject = JSONObject.parseObject(resultStr);
            String qr_code = jsonObject.getJSONObject("alipay_trade_precreate_response").getString("qr_code");
//            renderText(qr_code);
            scanPayResultVo.setCodeUrl(qr_code);// 设置支付宝跳转地址
        } catch (Exception e) {
            e.printStackTrace();
        }
//            rpNotifyService.orderSend(rpTradePaymentRecord.getBankOrderNo());

//        }
        return scanPayResultVo;
    }

    private TradePaymentRecord sealRpTradePaymentRecord(String merchantNo, String merchantName, String productName, String orderNo, BigDecimal orderPrice, String payWay, String payWayName, PayTypeEnum payType, String fundIntoType, BigDecimal feeRate, String orderIp, String returnUrl, String notifyUrl, String remark, String field1, String field2, String field3, String field4, String field5) {
        TradePaymentRecord rpTradePaymentRecord = new TradePaymentRecord();
        rpTradePaymentRecord.setProductName(productName);// 产品名称
        rpTradePaymentRecord.setMerchantOrderNo(orderNo);// 产品编号

//        String trxNo = buildNoService.buildTrxNo();//todo
        String trxNo = "test_001";

        rpTradePaymentRecord.setTrxNo(orderNo);// 支付流水号

//        String bankOrderNo = buildNoService.buildBankOrderNo();
        String bankOrderNo = orderNo;
        rpTradePaymentRecord.setBankOrderNo(bankOrderNo);// 银行订单号
        rpTradePaymentRecord.setMerchantName(merchantName);
        rpTradePaymentRecord.setMerchantNo(merchantNo);// 商户编号
        rpTradePaymentRecord.setOrderIp(orderIp);// 下单IP
        rpTradePaymentRecord.setOrderRefererUrl("");// 下单前页面
        rpTradePaymentRecord.setReturnUrl(returnUrl);// 页面通知地址
        rpTradePaymentRecord.setNotifyUrl(notifyUrl);// 后台通知地址
        rpTradePaymentRecord.setPayWayCode(payWay);// 支付通道编码
        rpTradePaymentRecord.setPayWayName(payWayName);// 支付通道名称
        rpTradePaymentRecord.setTrxType("支付");// 交易类型
        rpTradePaymentRecord.setOrderFrom("用户支付");// 订单来源
        rpTradePaymentRecord.setOrderAmount(orderPrice);// 订单金额
        rpTradePaymentRecord.setStatus(TradeStatusEnum.WAITING_PAYMENT.name());// 订单状态
        // 等待支付

        rpTradePaymentRecord.setPayTypeCode(payType.name());// 支付类型
        rpTradePaymentRecord.setPayTypeName(payType.getDesc());// 支付方式
        rpTradePaymentRecord.setFundIntoType(fundIntoType);// 资金流入方向

//        if ("商家收款".equals(fundIntoType)) {// 平台收款
        // 需要修改费率
        // 成本
        // 利润
//            // 收入
//            // 以及修改商户账户信息
//            BigDecimal orderAmount = rpTradePaymentRecord.getOrderAmount();// 订单金额
//            BigDecimal platIncome = orderAmount.multiply(feeRate).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP); // 平台收入
//            // =
//            // 订单金额
//            // *
//            // 支付费率(设置的费率除以100为真实费率)
//            BigDecimal platCost = orderAmount.multiply(BigDecimal.valueOf(Double.valueOf(WeixinConfigUtil.readConfig("pay_rate")))).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);// 平台成本
//            // =
//            // 订单金额
//            // *
//            // 微信费率(设置的费率除以100为真实费率)
//            BigDecimal platProfit = platIncome.subtract(platCost);// 平台利润 = 平台收入
//            // - 平台成本
//
//            rpTradePaymentRecord.setFeeRate(feeRate);// 费率
//            rpTradePaymentRecord.setPlatCost(platCost);// 平台成本
//            rpTradePaymentRecord.setPlatIncome(platIncome);// 平台收入
//            rpTradePaymentRecord.setPlatProfit(platProfit);// 平台利润
//
//        }

        rpTradePaymentRecord.setRemark(remark);// 支付备注
        rpTradePaymentRecord.setField1(field1);// 扩展字段1
        rpTradePaymentRecord.setField2(field2);// 扩展字段2
        rpTradePaymentRecord.setField3(field3);// 扩展字段3
        rpTradePaymentRecord.setField4(field4);// 扩展字段4
        rpTradePaymentRecord.setField5(field5);// 扩展字段5
        return rpTradePaymentRecord;
    }
    public static AlipayTradePrecreateResponse tradePrecreatePayToResponse(AlipayClient client,AlipayTradePrecreateModel model, String notifyUrl) throws AlipayApiException {
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setBizModel(model);
        request.setNotifyUrl(notifyUrl);
        return (AlipayTradePrecreateResponse)client.execute(request);
    }
}

