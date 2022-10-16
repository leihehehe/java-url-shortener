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
  @Autowired private RedisTemplate<Object, Object> redisTemplate;

  @GetMapping("list")
  public JsonData listAllDomains() {
    List<DomainVo> domainVos = domainService.listAllDomains();
    return JsonData.buildSuccess(domainVos);
  }

//  @GetMapping("test")
//  public JsonData test(
//      @RequestParam(name = "code") String code, @RequestParam(name = "accountNo") Long accountNo) {
//    String script =
//        // check if the key(short link code) exist in the redis
//        "if redis.call('EXISTS',KEYS[1])==0 then redis.call('set',KEYS[1],ARGV[1]); "
//            // if it does not exist, set key and expire
//            + "redis.call('expire',KEYS[1],ARGV[2]); return 1;"
//            // if it does exist, and the accountNo is the same, return 2
//            + " elseif redis.call('get',KEYS[1]) == ARGV[1] then return 2;"
//            + " else return 0; end;";
//    Long result =
//        redisTemplate.execute(
//            new DefaultRedisScript<>(script, Long.class), List.of(code), accountNo, 100);
//    return JsonData.buildSuccess(result);
//  }
}
