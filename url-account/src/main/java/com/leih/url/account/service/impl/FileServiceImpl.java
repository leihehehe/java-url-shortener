package com.leih.url.account.service.impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.leih.url.account.config.S3Config;
import com.leih.url.account.service.AccountService;
import com.leih.url.account.service.FileService;
import com.leih.url.account.vo.AccountVo;
import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.util.CommonUtil;
import com.leih.url.common.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Autowired
    S3Config s3Config;
    @Autowired
    AccountService accountService;
    @Override
    public JsonData uploadAvatar(MultipartFile file) {
        //get s3 client
        AmazonS3Client s3Client = s3Config.getAmazonS3Client();
        //build dest file
        String originalFilename = file.getOriginalFilename();
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String dir = pattern.format(localDateTime);
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String filename = "user/"+dir+"/"+CommonUtil.generateUUID()+extension;
        //set up meta data
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        try {
            PutObjectResult putObjectResult = s3Client.putObject(s3Config.getBucketName(), filename, file.getInputStream(),objectMetadata);
            if(putObjectResult!=null){
                String uploadUrl = s3Client.getUrl(s3Config.getBucketName(), filename).toString();
                log.info("avatar url:{}",uploadUrl);
                boolean updatedAccount = accountService.updateAvatar(uploadUrl);
                if (updatedAccount) return JsonData.buildSuccess();
            }
        } catch (IOException e) {
            log.error("Failed to upload the avatar: {}",e.getMessage());
        }
    return JsonData.buildResult(BizCodeEnum.FILE_UPLOAD_USER_AVATAR_FAILED);
    }
}
