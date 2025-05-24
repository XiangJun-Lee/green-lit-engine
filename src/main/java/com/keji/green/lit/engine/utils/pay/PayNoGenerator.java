package com.keji.green.lit.engine.utils.pay;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class PayNoGenerator {

    private static final Log LOG = LogFactory.getLog(PayNoGenerator.class);

    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

//    /** 对账批次号前缀 **/
//    private static final String RECONCILIATION_BATCH_NO = "5";

    /** 银行订单号 **/
    private static final String BANK_ORDER_NO_PREFIX = "6";
    /** 支付流水号前缀 **/
    private static final String TRX_NO_PREFIX = "7";

//    /** 用户编号前缀 **/
//    private static final String USER_NO_PREFIX = "8";
//
//    /** 账户编号前缀 **/
//    private static final String ACCOUNT_NO_PREFIX = "9";

    private static final int NUMBER_LENGTH = 6;
    private static final Random RANDOM = new Random();

    /**
     * 获取银行订单号
     * @return
     */
    public static String genOutTradeNo() {
        String key = getDatePrefix();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < NUMBER_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        return BANK_ORDER_NO_PREFIX+key+sb;
    }

    /**
     * 获取支付流水号
     * @return
     */
    public static String genTrxNo() {
        String key = getDatePrefix();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < NUMBER_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        String trxNo = TRX_NO_PREFIX + key + sb;
        return trxNo;
    }

    public static String getDatePrefix(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);
        key = key + System.currentTimeMillis();
        key = key.substring(0, 17);
        return key;
    }
}
