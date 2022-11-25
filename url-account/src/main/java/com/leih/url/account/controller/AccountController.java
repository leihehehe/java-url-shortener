package com.leih.url.account.controller;

import com.leih.url.account.controller.request.AccountLoginRequest;
import com.leih.url.account.controller.request.AccountRegisterRequest;
import com.leih.url.account.entity.Account;
import com.leih.url.account.service.AccountService;
import com.leih.url.account.service.FileService;
import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.util.JsonData;
import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/api/account/v1/")
public class AccountController {
    @Autowired
    FileService fileService;
    @Autowired
    AccountService accountService;
    /**
     * Upload file
     * No more than 1MB
     * @param file
     * @return
     */
    @PostMapping("upload")
    public JsonData uploadAvatar(@RequestPart("file")MultipartFile file){
        return fileService.uploadAvatar(file);
    }

    /***
     * User Registration
     * @param registerRequest
     * @return
     */
    @PostMapping("register")
    public JsonData register(@RequestBody AccountRegisterRequest registerRequest){
        try{
            return accountService.register(registerRequest);
        }catch (Exception e){
            log.error("Failed to register the account:{}",e.getMessage());
            if(e.getMessage().contains("uk_phone")){
                return JsonData.buildResult(BizCodeEnum.ACCOUNT_PHONE_DUPLICATES);
            }else {
                return JsonData.buildResult(BizCodeEnum.ACCOUNT_USERNAME_DUPLICATES);
            }

        }

    }

    @PostMapping("login")
    public JsonData login(@RequestBody AccountLoginRequest request){
        return accountService.login(request);
    }

    @GetMapping("detail")
    public JsonData detail(){
        JsonData jsonData = accountService.getDetail();
        return jsonData;
    }

}
