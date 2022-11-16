package com.leih.url.data.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VisitStatsVo {
    private String startTime;
    private String endTime;
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
    @Column(name = "timestamp")
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
     * Country
     */
    private String country;
    /**
     * Ip address
     */
    private String ip;
    //
    /**
     * Unique visitor
     */
    private Long uv=0L;
    /**
     * Page view
     */
    private Long pv=0L;

    private Long ipCount=0L;

    private Long pvCount=0L;

    private Long uvCount=0L;

    private Long newUVCount=0L;

    private String dateTimeStr;
}
