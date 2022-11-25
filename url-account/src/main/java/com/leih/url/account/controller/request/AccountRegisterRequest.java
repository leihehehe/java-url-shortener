package com.leih.url.account.controller.request;

import lombok.Data;

@Data
public class AccountRegisterRequest {
    private String phone;
    private String username;
    private String password;
    private String email;
    private String code;
}
