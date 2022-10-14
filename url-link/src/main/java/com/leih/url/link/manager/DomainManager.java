package com.leih.url.link.manager;

import com.leih.url.common.enums.DomainTypeEnum;
import com.leih.url.link.entity.Domain;

import java.util.List;

public interface DomainManager {
    Domain findById(Long id,Long accountNo);
    Domain findByDomainTypeEnum(Long id, DomainTypeEnum domainTypeEnum);
    boolean addDomain(Domain domain);
    List<Domain> listOfficialDomains();
    List<Domain> listCustomDomains(Long accountNo);
}
