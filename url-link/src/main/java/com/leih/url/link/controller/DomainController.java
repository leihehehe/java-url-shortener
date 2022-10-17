package com.leih.url.link.controller;

import com.leih.url.common.util.JsonData;
import com.leih.url.link.service.DomainService;
import com.leih.url.link.vo.DomainVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/domain/v1/")
public class DomainController {
  @Autowired DomainService domainService;

  @GetMapping("list")
  public JsonData listAllDomains() {
    List<DomainVo> domainVos = domainService.listAllDomains();
    return JsonData.buildSuccess(domainVos);
  }

}
