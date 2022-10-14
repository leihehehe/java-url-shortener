package com.leih.url.link.service.impl;

import com.leih.url.common.intercepter.LoginInterceptor;
import com.leih.url.link.entity.Domain;
import com.leih.url.link.manager.DomainManager;
import com.leih.url.link.service.DomainService;
import com.leih.url.link.vo.DomainVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DomainServiceImpl implements DomainService {
    @Autowired
    private DomainManager domainManager;
    @Override
    public List<DomainVo> listAllDomains() {
        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        List<Domain> customDomains = domainManager.listCustomDomains(accountNo);
        List<Domain> officialDomains = domainManager.listOfficialDomains();
        customDomains.addAll(officialDomains);
        return customDomains.stream().map(this::convertDomainToDomainVo).collect(Collectors.toList());
    }

    private DomainVo convertDomainToDomainVo(Domain domain){
        DomainVo domainVo = new DomainVo();
        BeanUtils.copyProperties(domain,domainVo);
        return domainVo;
    }
}
