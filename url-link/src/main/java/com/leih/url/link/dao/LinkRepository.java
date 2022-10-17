package com.leih.url.link.dao;

import com.leih.url.link.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {
  Link findLinkByCodeAndDel(String code, int del);

  @Transactional
  @Modifying
  @Query("update Link set del = 1 where del=0 and accountNo=:accountNo and code=:code")
  int deleteShortLink(@Param("code") String code, @Param("accountNo") Long accountNo);

  @Transactional
  @Modifying
  @Query(
      "update Link set name = :name,domain=:domain where del=0 and accountNo=:accountNo and code=:code")
  int updateShortLink(
      @Param("name") String name,
      @Param("domain") String domain,
      @Param("accountNo") Long accountNo,
      @Param("code") String code);
}
