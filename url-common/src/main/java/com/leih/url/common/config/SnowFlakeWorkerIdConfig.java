package com.leih.url.common.config;

import com.mysql.cj.log.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@Slf4j
public class SnowFlakeWorkerIdConfig {
    /**
     * Dynamically calculate the worker id
     *
     */
    static {
        try {
            InetAddress inetAddress = Inet4Address.getLocalHost();
            String hostAddress = inetAddress.getHostAddress();
            //max worker id=1024
            String workerId = Math.abs(hostAddress.hashCode())%1024+"";
            System.setProperty("workerId",workerId);
            log.info(workerId);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

    }
}
