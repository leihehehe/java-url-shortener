package com.leih.url.link.dao;

import com.leih.url.link.entity.LinkGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LinkGroupRepository extends JpaRepository<LinkGroup,Long> {
    @Transactional
    @Modifying
    int deleteLinkGroupByAccountNoAndId(Long accountNo, Long id);
    LinkGroup getLinkGroupByAccountNoAndId(Long accountNo,Long id);
    List<LinkGroup> getLinkGroupsByAccountNo(Long accountNo);
    @Transactional
    @Modifying
    @Query("update LinkGroup set name = :name where id=:id and accountNo=:accountNo")
    int updateLinkGroup(@Param("id") Long groupId, @Param("name") String name, @Param("accountNo") Long accountNo);
}
