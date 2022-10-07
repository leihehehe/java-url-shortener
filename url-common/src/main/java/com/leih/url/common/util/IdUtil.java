package com.leih.url.common.util;

import org.apache.shardingsphere.core.strategy.keygen.SnowflakeShardingKeyGenerator;

public class IdUtil {
    private static SnowflakeShardingKeyGenerator shardingKeyGenerator = new SnowflakeShardingKeyGenerator();

    /**
     * SnakeFlake ID generator
     * @return
     */
    public static synchronized Comparable<?> generateSnowFlakeId(){
        return shardingKeyGenerator.generateKey();
    }
}
