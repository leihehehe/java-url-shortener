package com.leih.url.data.repository;

import com.leih.url.data.entity.VisitStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitStatsRepository extends JpaRepository<VisitStats, String> {
  @Query(
      nativeQuery = true,
      value =
          "select count(1) from visit_stats where account_no=:accountNo and code = :code limit 1000")
  int countAllByAccountNoAndCode(@Param("accountNo") Long accountNo, @Param("code") String code);

  @Query(
      nativeQuery = true,
      value =
          "select * from visit_stats where account_no=:accountNo and code = :code order by timestamp desc limit :from,:size")
  List<VisitStats> paginateVisitRecord(
      @Param("accountNo") Long accountNo,
      @Param("code") String code,
      @Param("from") int from,
      @Param("size") int size);
}
