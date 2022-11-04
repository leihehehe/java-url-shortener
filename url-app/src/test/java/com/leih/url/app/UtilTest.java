package com.leih.url.app;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class UtilTest {
    @Test
    public void TestAgentUtil(){
        String userAgentStr = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/106.0.0.0 Safari/537.36";
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
        Browser browser = userAgent.getBrowser();
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();
        String browserName = browser.getGroup().getName();
        String osName = operatingSystem.getGroup().getName();
        String deviceName = operatingSystem.getDeviceType().getName();
        String manufacturer = operatingSystem.getManufacturer().getName();
        System.out.println(browserName);
        System.out.println(osName);
        System.out.println(deviceName);
    System.out.println(manufacturer);
        log.info("browser name:{}; OS name: {}; device name:{}",browserName,osName,deviceName);
    }
}
