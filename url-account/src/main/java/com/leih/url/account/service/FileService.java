package com.leih.url.account.service;

import com.leih.url.common.util.JsonData;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    JsonData uploadAvatar(MultipartFile file);
}
