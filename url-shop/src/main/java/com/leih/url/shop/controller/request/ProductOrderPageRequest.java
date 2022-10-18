package com.leih.url.shop.controller.request;

import lombok.Data;

import javax.swing.plaf.nimbus.State;

@Data
public class ProductOrderPageRequest {
    private String state;
    private int size;
    private int page;
}
