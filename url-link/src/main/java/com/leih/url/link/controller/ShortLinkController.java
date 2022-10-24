package com.leih.url.link.controller;

import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.util.JsonData;
import com.leih.url.link.controller.request.ShortLinkAddRequest;
import com.leih.url.link.controller.request.ShortLinkDeleteRequest;
import com.leih.url.link.controller.request.ShortLinkPageRequest;
import com.leih.url.link.controller.request.ShortLinkUpdateRequest;
import com.leih.url.link.service.ShortLinkService;
import com.leih.url.link.vo.LinkVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/link/v1/")
public class ShortLinkController {
    @Autowired
    ShortLinkService shortLinkService;

    @Value("${rpc.token}")
    private String rpcToken;
    @GetMapping(value = "check")
    public JsonData checkShortLink(@RequestParam("shortLinkCode") String shortLinkCode, HttpServletRequest request){
        if(request.getHeader("rpc-token").equalsIgnoreCase(rpcToken)){
            LinkVo linkVo = shortLinkService.parseShortLinkCode(shortLinkCode);
            return linkVo==null?JsonData.buildResult(BizCodeEnum.SHORT_LINK_NOT_EXIST):JsonData.buildSuccess();
        }else{
            return JsonData.buildError("Forbidden");
        }
    }
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
