package com.leih.url.shop.vo;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
public class ProductVo {
    private Long id;
    private String name;
    private String detail;
    private String image;
    private String level;
    private Double price;
    private Double oldPrice;
    private String pluginType;
    private Integer dayTimes;
    private Integer totalTimes;
    private Integer validDays;
    private Timestamp gmtCreate;
    private Timestamp gmtModified;
}
