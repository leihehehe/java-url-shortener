package com.leih.url.link.controller.request;

import lombok.Data;

@Data
public class LinkGroupUpdateRequest {
    /***
     * Group ID
     */
    private long id;
    /**
     * Group name
     */
    private String name;

}
