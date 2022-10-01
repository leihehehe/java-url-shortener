package com.leih.url.account.service.impl;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.leih.url.account.config.S3Config;
import com.leih.url.account.service.FileService;
import com.leih.url.common.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.DateFormatter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Autowired
    S3Config s3Config;
    @Override
    public String uploadAvatar(MultipartFile file) {
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
                return s3Client.getUrl(s3Config.getBucketName(),filename).toString();
            }
        } catch (IOException e) {
            log.error("Failed to upload the avatar: {}",e.getMessage());
        }
        return null;
    }
}
