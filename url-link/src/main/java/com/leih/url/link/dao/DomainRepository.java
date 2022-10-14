package com.leih.url.link.dao;

import com.leih.url.link.entity.Domain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DomainRepository extends JpaRepository<Domain,Long> {
    Domain findByIdAndAccountNo(Long id, Long accountNo);
    Domain findByIdAndDomainType(Long id,String domainType);
    List<Domain> findDomainsByDomainType(String domainType);
    List<Domain> findDomainsByAccountNoAndDomainType(Long accountNo,String domainType);
}
