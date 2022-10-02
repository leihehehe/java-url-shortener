package com.leih.url.account.controller.request;

import lombok.Data;

@Data
public class AccountLoginRequest {
    private String phone;
    private String password;
}
