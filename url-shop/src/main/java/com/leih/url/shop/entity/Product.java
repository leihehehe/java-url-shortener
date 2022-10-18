package com.leih.url.shop.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "product")
public class Product {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "detail")
    private String detail;
    @Column(name = "img")
    private String image;
    @Column(name = "level")
    private String level;
    @Column(name = "price")
    private Double price;
    @Column(name = "old_price")
    private Double oldPrice;
    @Column(name = "plugin_type")
    private String pluginType;

    @Column(name = "day_times")
    private Integer dayTimes;
    @Column(name = "total_times")
    private Integer totalTimes;
    @Column(name = "valid_days")
    private Integer validDays;

    @Column(
            name = "gmt_create",
            insertable = false,
            updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp gmtCreate;

    @Column(
            name = "gmt_modified",
            insertable = false,
            updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp gmtModified;
}
