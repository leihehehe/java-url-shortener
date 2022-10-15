package com.leih.url.link.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "short_link",uniqueConstraints = {
        @UniqueConstraint(name = "uk_code",columnNames = {"code"})
})
public class Link {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "group_id")
    private Long groupId;
    @Column(name = "name",length = 128)
    private String name;
    @Column(name = "original_url",length = 1024)
    private String originalUrl;
    @Column(name = "domain",length = 128)
    private String domain;
    @Column(name = "code",length = 16)
    private String code;
    @Column(name = "sign",length = 64)
    private String sign;
    @Column(name = "account_no")
    private Long accountNo;
    @Column(name = "expired")
    private Timestamp expired;
    @Column(name = "gmt_create",insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp gmtCreate;

    @Column(
            name = "gmt_modified", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp gmtModified;
    //0 -> default, 1 -> deleted
    @Column(name = "del")
    private int del;
    @Column(name = "state",length = 16)
    private String state;
    @Column(name = "link_type",length = 16)
    private String linkType;

}
