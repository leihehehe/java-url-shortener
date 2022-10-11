package com.leih.url.link.strategry;

import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.exception.BizException;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

public class CustomDatabasePreciseShardingAlgorithm implements PreciseShardingAlgorithm<String> {
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
        //database
        String codePrefix = preciseShardingValue.getValue().substring(0, 1);

        for(String sourceName:collection){
            //get the last character in the datasource name
            String sourceSuffix = sourceName.substring(sourceName.length() - 1);
            //if code prefix matches the datasource suffix
            if(codePrefix.equals(sourceSuffix)){
                return sourceName;
            }
        }
        throw new BizException(BizCodeEnum.DB_ROUTE_NOT_FOUND);
    }
}
