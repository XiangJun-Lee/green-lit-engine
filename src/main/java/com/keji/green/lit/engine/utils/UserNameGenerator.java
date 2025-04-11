package com.keji.green.lit.engine.utils;

import java.util.Random;

/**
 * 用户名生成器
 *
 * @author xiangjun_lee
 * @since 2024-04-11
 * 生成规则：前缀是"greenLit "，后面随机生成6个字符(数字+字母)
 */
public class UserNameGenerator {
    
    private static final String PREFIX = "Offer绿灯侠 ";
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int RANDOM_LENGTH = 6;
    private static final Random RANDOM = new Random();
    
    /**
     * 生成随机用户名
     * 
     * @return 生成的用户名
     */
    public static String generate() {
        StringBuilder sb = new StringBuilder(PREFIX);
        for (int i = 0; i < RANDOM_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        return sb.toString();
    }
} 