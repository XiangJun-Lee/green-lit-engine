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
//        List<Integer> gradeList = (List<Integer>) complexKeysShardingValue.getColumnNameAndShardingValuesMap().get("grade");
//        Iterator<Integer> iterator = gradeList.iterator();
//        while (iterator.hasNext()) {
//            Integer grade = iterator.next();
//            String tableName = logicTableName + ((id + grade) % 2 + 1);
//            result.add(tableName);
//        }
        String tableName = logicTableName + (id  % 10);
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
