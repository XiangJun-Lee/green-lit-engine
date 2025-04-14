package com.keji.green.lit.engine.utils;

/**
 * @author xiangjun_lee
 * @date 2025/3/29 16:42
 */
public class Constants {

    /**
     * 频率限制key
     */
    public static final String PASSWORD_LOGIN_IP_KEY = "pwd:login:ip:%s";
    public static final String PASSWORD_LOGIN_PHONE_KEY = "pwd:login:phone:%s";
    public static final String VERIFICATION_CODE_IP_KEY = "verification:code:ip:%s";
    public static final String VERIFICATION_CODE_PHONE_KEY = "verification:code:phone:%s";
    public static final String SEND_VERIFICATION_IP_KEY = "send:verification:code:ip:%s";
    public static final String SEND_VERIFICATION_CODE_PHONE_KEY = "send:verification:code:phone:%s";




    public static final Integer ONE_MINUTE_SECONDS = 60;
    public static final Integer ONE_HOUR_SECONDS = 60 * 60;
    public static final Integer ONE_DAY_SECONDS = 60 * 60 * 24;
    public static final Long FIVE_MINUTE_MILLISECONDS = 5 * 60 * 1000L;

    public static final Integer INTEGER_FIVE = 5;
    public static final Integer INTEGER_TEN = 10;
    public static final Integer INTEGER_TWENTY = 20;

    public static final Integer INTERVIEW_MAX_HOURS = 5;
}
