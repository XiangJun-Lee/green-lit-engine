package com.keji.green.lit.engine.utils.pay;

public class ShardingUtil {
    public static int getShardingKey(String str, int tableCount) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        // 使用Java字符串的hashCode()方法
        int hashCode = Math.abs(str.hashCode());
        return hashCode % tableCount;
    }
}
