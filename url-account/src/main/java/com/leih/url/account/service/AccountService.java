package com.leih.url.account.service;

import com.leih.url.account.controller.request.AccountLoginRequest;
import com.leih.url.account.controller.request.AccountRegisterRequest;
import com.leih.url.account.entity.Account;
import com.leih.url.account.vo.AccountVo;
import com.leih.url.common.util.JsonData;
import org.springframework.stereotype.Service;


public interface AccountService {
    JsonData register(AccountRegisterRequest registerRequest);
    JsonData login(AccountLoginRequest loginRequest);

    JsonData getDetail();

    boolean updateAvatar(String uploadUrl);
}
