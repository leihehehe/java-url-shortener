package com.leih.url.account.service.impl;

import com.leih.url.account.config.RabbitMQConfig;
import com.leih.url.account.controller.request.AccountLoginRequest;
import com.leih.url.account.controller.request.AccountRegisterRequest;
import com.leih.url.account.entity.Account;
import com.leih.url.account.manager.AccountManager;
import com.leih.url.account.service.AccountService;
import com.leih.url.account.service.NotificationService;
import com.leih.url.account.vo.AccountVo;
import com.leih.url.common.enums.AuthTypeEnum;
import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.enums.EventMessageTypeEnum;
import com.leih.url.common.enums.SendCodeEnum;
import com.leih.url.common.intercepter.LoginInterceptor;
import com.leih.url.common.model.EventMessage;
import com.leih.url.common.model.LoggedInUser;
import com.leih.url.common.util.CommonUtil;
import com.leih.url.common.util.IdUtil;
import com.leih.url.common.util.JWTUtil;
import com.leih.url.common.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
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

        account.setAccountNo((Long) IdUtil.generateSnowFlakeId());
        account.setAuth(AuthTypeEnum.PERSONAL_USER.name());
        account.setSecret("$1$"+CommonUtil.getStringNumRandom(8));
        String encryptedPass = Md5Crypt.md5Crypt(registerRequest.getPassword().getBytes(), account.getSecret());
        account.setPassword(encryptedPass);
        try{
            accountManager.insertAccount(account);
            log.info("Register success: {}",account);
            //assign free plans
            userRegisterInitFree(account);
            return JsonData.buildSuccess();
        }catch (Exception e){
            log.info("Register failed: {}",e.getMessage());
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_REPEAT);
        }

    }

    private static final Long FREE_INIT_PRODUCT_ID=1L;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitMQConfig rabbitMQConfig;
    public void userRegisterInitFree(Account account){
        EventMessage eventMessage = EventMessage.builder().messageId(IdUtil.generateSnowFlakeId().toString())
                .accountNo(account.getAccountNo())
                .eventMessageType(EventMessageTypeEnum.PLAN_FREE_NEW_ACCOUNT.name())
                .bizId(FREE_INIT_PRODUCT_ID.toString())
                .build();
        rabbitTemplate.convertAndSend(rabbitMQConfig.getPlanEventExchange(),rabbitMQConfig.getPlanFreeInitRoutingKey(),eventMessage);
    }

    /***
     * Login
     * @param loginRequest
     * @return
     */
    @Override
    public JsonData login(AccountLoginRequest loginRequest) {
        Account account = accountManager.findAccountByPhone(loginRequest.getPhone());
        //TODO: login by username
        if(account!=null){
            String toBeVerified = Md5Crypt.md5Crypt(loginRequest.getPassword().getBytes(), account.getSecret());
            if(toBeVerified.equals(account.getPassword())){
                //generate token
                LoggedInUser loggedInUser = LoggedInUser.builder().build();
                BeanUtils.copyProperties(account,loggedInUser);
                String token = JWTUtil.generateJsonWebToken(loggedInUser);
                return JsonData.buildSuccess(token);
            }
        }else{
            log.info("The account {} does not exist!",loginRequest.getPhone());
            return JsonData.buildResult(BizCodeEnum.ACCOUNT_UNREGISTER);
        }
        return null;
    }

    @Override
    public JsonData getDetail() {
        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        Account account = accountManager.getDetail(accountNo);
        AccountVo accountVo = new AccountVo();
        BeanUtils.copyProperties(account,accountVo);
        return JsonData.buildSuccess(accountVo);
    }
}
