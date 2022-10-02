package com.leih.url.account.service.impl;

import com.leih.url.account.controller.request.AccountLoginRequest;
import com.leih.url.account.controller.request.AccountRegisterRequest;
import com.leih.url.account.entity.Account;
import com.leih.url.account.manager.AccountManager;
import com.leih.url.account.service.AccountService;
import com.leih.url.account.service.NotificationService;
import com.leih.url.common.constant.RedisKey;
import com.leih.url.common.enums.AuthTypeEnum;
import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.enums.SendCodeEnum;
import com.leih.url.common.model.LoggedInUser;
import com.leih.url.common.util.CommonUtil;
import com.leih.url.common.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    NotificationService notificationService;
    @Autowired
    AccountManager accountManager;

    /***
     * Register
     * @param registerRequest
     * @return
     */
    @Override
    public JsonData register(AccountRegisterRequest registerRequest) {
        //check if code is correct
        boolean checkCode=false;
        if(StringUtils.hasLength(registerRequest.getPhone())){
            checkCode = notificationService.checkCode(SendCodeEnum.USER_REGISTER, registerRequest.getPhone(), registerRequest.getCode());
        }
        if(!checkCode){
            return JsonData.buildResult(BizCodeEnum.CODE_ERROR);
        }
        Account account = new Account();
        BeanUtils.copyProperties(registerRequest,account);
        account.setAccountNo(CommonUtil.getCurrentTimestamp());
        account.setAuth(AuthTypeEnum.PERSONAL_USER.name());
        account.setSecret("$1$"+CommonUtil.getStringNumRandom(8));
        String encryptedPass = Md5Crypt.md5Crypt(registerRequest.getPassword().getBytes(), account.getSecret());
        account.setPassword(encryptedPass);
        accountManager.insertAccount(account);
        log.info("Register success: {}",account);
        return JsonData.buildSuccess();
    }

    /***
     * Login
     * @param loginRequest
     * @return
     */
    @Override
    public JsonData login(AccountLoginRequest loginRequest) {
        Account account = accountManager.findAccountByPhone(loginRequest.getPhone());
        if(account!=null){
            String toBeVerified = Md5Crypt.md5Crypt(loginRequest.getPassword().getBytes(), account.getSecret());
            if(toBeVerified.equals(account.getPassword())){
                //generate token
                LoggedInUser loggedInUser = LoggedInUser.builder().build();
                BeanUtils.copyProperties(account,loggedInUser);

                return JsonData.buildSuccess("");
            }
        }else{
            log.info("The account {} does not exist!",loginRequest.getPhone());
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_UNREGISTER);
        }
        return null;
    }
}
