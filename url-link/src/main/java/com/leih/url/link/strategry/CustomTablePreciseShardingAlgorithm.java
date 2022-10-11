package com.leih.url.link.strategry;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

public class CustomTablePreciseShardingAlgorithm implements PreciseShardingAlgorithm<String> {
    /***
     *
     * @param collection all the data sources
     *                   databaseNames
     *                   tablesNames
     * @param preciseShardingValue Partition Attributes
     *                             logicTableName
     *                             columnName
     *                             value
     * @return
     */
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<String> preciseShardingValue) {
        //get the table name
        String tableName = collection.iterator().next();
        //get code
        String code = preciseShardingValue.getValue();
        //get the last character of the code
        String codeSuffix = code.substring(code.length() - 1);
        return tableName+"_"+codeSuffix;
    }
}
