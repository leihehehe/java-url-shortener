package com.leih.url.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceInfo {
    /**
     * Browser name
     */
    private String browserName;
    /**
     * OS name
     */
    private String os;
    /**
     * OS version
     */
    private String osVersion;
    /**
     * Device type
     */
    private String deviceType;
    /**
     * Device manufacturer
     */
    private String deviceManufacturer;
    /**
     * Device unique id
     */
    private String uid;
}
