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
  GroupLinkMapping findLinkByIdAndAccountNoAndGroupId(long id, long accountNo, long groupId);

  @Modifying
  @Transactional
  @Query(
      "update GroupLinkMapping set delete=1 where code=:code and accountNo=:accountNo and groupId=:groupId")
  int deleteGroupLinkMapping(
      @Param("code") String shortLinkCode,
      @Param("accountNo") long accountNo,
      @Param("groupId") long groupId);
  @Modifying
  @Transactional
  @Query(
          "update GroupLinkMapping set state=:state where code=:code and accountNo=:accountNo and groupId=:groupId")
  int updateGroupLinkMappingState(
          @Param("code") String shortLinkCode,
          @Param("accountNo") long accountNo,
          @Param("groupId") long groupId,
          @Param("state") String state);
  Page<GroupLinkMapping> findAllByAccountNoAndGroupId(
      Pageable pageable, long accountNo, long groupId);
}
