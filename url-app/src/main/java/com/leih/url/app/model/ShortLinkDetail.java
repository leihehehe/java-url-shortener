package com.leih.url.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortLinkDetail {
    //short link
    /**
     * Short link code
     */
    private String code;
    /**
     * Account no
     */
    private Long accountNo;
    /**
     * Visit time
     */
    private Long visitTime;
    /**
     * Referer
     */
    private String referer;
    /**
     * visitor is new or old(1->new; 0->old)
     */
    private Integer isNew;
    //device
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
    /**
     * Country
     */
    private String country;
    /**
     * Ip address
     */
    private String ip;
}
