package com.leih.url.link.dao;

import com.leih.url.link.entity.GroupLinkMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface GroupLinkMappingRepository extends JpaRepository<GroupLinkMapping, Long> {
  GroupLinkMapping findLinkByIdAndAccountNoAndGroupIdAndDel(Long id, Long accountNo, Long groupId,int del);

  GroupLinkMapping findByCodeAndAccountNoAndGroupIdAndDel(String code, Long accountNo, Long groupId,int del);

  @Modifying
  @Transactional
  @Query(
      "update GroupLinkMapping set del=1 where id=:mappingId and accountNo=:accountNo and groupId=:groupId and del=0")
  int deleteGroupLinkMapping(
      @Param("mappingId") Long mappingId,
      @Param("accountNo") Long accountNo,
      @Param("groupId") Long groupId);

  @Modifying
  @Transactional
  @Query(
          "delete GroupLinkMapping where code=:shortLinkCode and accountNo=:accountNo and groupId=:groupId")
  int deleteGroupLinkMappingByShortLinkCode(
          @Param("shortLinkCode") String shortLinkCode,
          @Param("accountNo") Long accountNo,
          @Param("groupId") Long groupId);

  @Modifying
  @Transactional
  @Query(
      "update GroupLinkMapping set state=:state where code=:code and accountNo=:accountNo and groupId=:groupId and del=0")
  int updateGroupLinkMappingState(
      @Param("code") String shortLinkCode,
      @Param("accountNo") Long accountNo,
      @Param("groupId") Long groupId,
      @Param("state") String state);
  @Modifying
  @Transactional
  @Query(
          "update GroupLinkMapping set name=:name, domain=:domain where id=:id and accountNo=:accountNo and groupId=:groupId and del=0")
  int updateGroupLinkMapping(
          @Param("id") Long id,
          @Param("accountNo") Long accountNo,
          @Param("groupId") Long groupId,
          @Param("name") String name,
          @Param("domain") String domain);

  Page<GroupLinkMapping> findAllByAccountNoAndGroupIdAndDel(
      Pageable pageable, Long accountNo, Long groupId, int del);
}
