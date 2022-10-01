package com.leih.url.account.service;

import com.leih.url.account.controller.request.AccountRegisterRequest;
import com.leih.url.common.util.JsonData;
import org.springframework.stereotype.Service;


public interface AccountService {
    JsonData register(AccountRegisterRequest registerRequest);
}
