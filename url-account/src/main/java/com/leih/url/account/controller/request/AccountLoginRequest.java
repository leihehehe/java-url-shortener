package com.leih.url.account.controller.request;

import lombok.Data;

@Data
public class AccountLoginRequest {
    private String loginReference;
    private String password;
    private String loginType;
}
