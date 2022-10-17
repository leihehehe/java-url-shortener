package com.leih.url.link.controller;

import com.leih.url.common.util.JsonData;
import com.leih.url.link.controller.request.ShortLinkAddRequest;
import com.leih.url.link.controller.request.ShortLinkDeleteRequest;
import com.leih.url.link.controller.request.ShortLinkPageRequest;
import com.leih.url.link.controller.request.ShortLinkUpdateRequest;
import com.leih.url.link.service.ShortLinkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/link/v1/")
public class ShortLinkController {
    @Autowired
    ShortLinkService shortLinkService;

    /**
     * Create short link
     * @param request
     * @return
     */
    @PostMapping("add")
    public JsonData createShortLink(@RequestBody ShortLinkAddRequest request){
         return shortLinkService.createShortLink(request);
    }

    /**
     * Page short links
     * @param request
     * @return
     */
    @PostMapping("page")
    public JsonData pageByGroupId(@RequestBody ShortLinkPageRequest request){
        Map<String,Object> result = shortLinkService.pageByGroupId(request);
        return JsonData.buildSuccess(result);
    }
    @PostMapping("del")
    public JsonData deleteShortLink(@RequestBody ShortLinkDeleteRequest request){
        return shortLinkService.deleteShortLink(request);
    }

    @PutMapping("update")
    public JsonData updateShortLink(@RequestBody ShortLinkUpdateRequest request){
        return shortLinkService.updateShortLink(request);
    }
}
