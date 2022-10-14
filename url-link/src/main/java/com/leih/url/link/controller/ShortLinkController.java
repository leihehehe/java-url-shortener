package com.leih.url.link.controller;

import com.leih.url.common.util.JsonData;
import com.leih.url.link.controller.request.ShortLinkAddRequest;
import com.leih.url.link.service.ShortLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/link/v1/")
public class ShortLinkController {
    @Autowired
    ShortLinkService shortLinkService;
    @PostMapping("add")
    public JsonData createShortLink(@RequestBody ShortLinkAddRequest request){
         return shortLinkService.createShortLink(request);
    }
}
