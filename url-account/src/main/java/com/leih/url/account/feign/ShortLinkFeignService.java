package com.leih.url.account.feign;

import com.leih.url.common.util.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "url-link-service")
public interface ShortLinkFeignService {
    @GetMapping(value = "/api/link/v1/check",headers = {"rpc-token=${rpc.token}"})
    JsonData checkShortLink(@RequestParam("shortLinkCode") String shortLinkCode);
}
