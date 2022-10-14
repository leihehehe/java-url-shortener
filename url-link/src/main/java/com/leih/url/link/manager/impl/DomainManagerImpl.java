package com.leih.url.link.manager.impl;

import com.leih.url.common.enums.DomainTypeEnum;
import com.leih.url.link.dao.DomainRepository;
import com.leih.url.link.entity.Domain;
import com.leih.url.link.manager.DomainManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
@Slf4j
@Component
public class DomainManagerImpl implements DomainManager {
    @Autowired
    DomainRepository domainRepository;
    @Override
    public Domain findById(Long id, Long accountNo) {
        return domainRepository.findByIdAndAccountNo(id,accountNo);
    }

    @Override
    public Domain findByDomainTypeEnum(Long id, DomainTypeEnum domainTypeEnum) {
        return domainRepository.findByIdAndDomainType(id,domainTypeEnum.name());
    }

    @Override
    public boolean addDomain(Domain domain) {
        try{
            domainRepository.save(domain);
            return true;
        }
        catch (Exception e){
            log.error("Failed to add domain.\nException: {}",e.getMessage());
            return false;
        }

    }

    @Override
    public List<Domain> listOfficialDomains() {
        return domainRepository.findDomainsByDomainType(DomainTypeEnum.OFFICIAL.name());
    }

    @Override
    public List<Domain> listCustomDomains(Long accountNo) {
        return domainRepository.findDomainsByAccountNoAndDomainType(accountNo,DomainTypeEnum.CUSTOM.name());
    }
}
