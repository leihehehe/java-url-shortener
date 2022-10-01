package com.leih.url.account.controller;

import com.leih.url.account.service.FileService;
import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.util.JsonData;
import com.netflix.discovery.converters.Auto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/api/v1/account")
public class AccountController {
    @Autowired
    FileService fileService;

    /**
     * Upload file
     * No more than 1MB
     * @param file
     * @return
     */
    @PostMapping("upload")
    public JsonData uploadAvatar(@RequestPart("file")MultipartFile file){
        String result = fileService.uploadAvatar(file);
        return result!=null?JsonData.buildSuccess(result):JsonData.buildResult(BizCodeEnum.FILE_UPLOAD_USER_IMG_FAIL);
    }

}
