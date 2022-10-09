package com.leih.url.link.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "short_link",uniqueConstraints = {
        @UniqueConstraint(name = "uk_code",columnNames = {"code"})
})
public class Link {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "group_id")
    private long groupId;
    @Column(name = "title",length = 128)
    private String title;
    @Column(name = "original_url",length = 1024)
    private String originalUrl;
    @Column(name = "domain",length = 128)
    private String domain;
    @Column(name = "code",length = 16)
    private String code;
    @Column(name = "sign",length = 64)
    private String sign;
    @Column(name = "account_no")
    private long accountNo;
    @Column(name = "expired")
    private Timestamp expired;
    @Column(name = "gmt_create")
    @CreationTimestamp
    private Timestamp gmtCreate;

    @Column(
            name = "gmt_modified")
    @UpdateTimestamp
    private Timestamp gmtModified;
    //0 -> default, 1 -> deleted
    @Column(name = "del")
    private int delete;
    @Column(name = "state",length = 16)
    private String state;
    @Column(name = "link_type",length = 16)
    private String linkType;

}
