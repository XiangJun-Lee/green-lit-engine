package com.keji.green.lit.engine.sharding;


import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingValue;

import java.util.*;

public class AccountAlgorithm implements ComplexKeysShardingAlgorithm {
    @Override
    public Collection<String> doSharding(Collection collection, ComplexKeysShardingValue complexKeysShardingValue) {
        String logicTableName = complexKeysShardingValue.getLogicTableName();
        Collection<String> result = new ArrayList<>();
        List<Long> ids = (List<Long>) complexKeysShardingValue.getColumnNameAndShardingValuesMap().get("user_id");
        Long id = ids.get(0);
        String tableName = logicTableName + "_"+(id  % 10);
        result.add(tableName);
        return result;
    }

    @Override
    public Properties getProps() {
        return null;
    }

    @Override
    public void init(Properties properties) {

    }
}
