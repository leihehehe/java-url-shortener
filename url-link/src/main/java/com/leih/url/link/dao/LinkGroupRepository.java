package com.leih.url.link.dao;

import com.leih.url.link.entity.LinkGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LinkGroupRepository extends JpaRepository<LinkGroup,Long> {
    @Transactional
    @Modifying
    int deleteLinkGroupByAccountNoAndId(long accountNo, long id);
    LinkGroup getLinkGroupByAccountNoAndId(long accountNo,long id);
    List<LinkGroup> getLinkGroupsByAccountNo(long accountNo);

}
