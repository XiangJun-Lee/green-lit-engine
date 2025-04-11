package com.keji.green.lit.engine.utils;

import java.util.Random;

/**
 * 邀请码生成器
 *
 * @author xiangjun_lee
 * @since 2024-04-11
 * 生成规则：6位随机字符串(数字+大写字母)
 */
public class InviteCodeGenerator {
    
    private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int CODE_LENGTH = 6;
    private static final Random RANDOM = new Random();
    
    /**
     * 生成随机邀请码
     * 
     * @return 生成的邀请码
     */
    public static String generate() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        return sb.toString();
    }
} 