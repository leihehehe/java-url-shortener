package com.leih.url.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoggedInUser {
    private Long accountNo;
    private String username;
    private String avatar;
//    private String email;
//    private String phone;
    private String auth;
}
