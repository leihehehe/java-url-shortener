package com.leih.url.data.repository;

import com.leih.url.data.entity.VisitStats;
import com.leih.url.data.vo.VisitStatsVo;
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

  @Query(
      nativeQuery = true,
      value =
          "select country,sum(pv) pv_count,sum(uv) uv_count,count(DISTINCT ip) ip_count from visit_stats WHERE account_no = :accountNo and code =:code and toYYYYMMDD(start_time) BETWEEN :startTime and :endTime group by country order by pv_count desc")
  List<VisitStats> queryRegion(
      @Param("code") String code,
      @Param("startTime") String startTime,
      @Param("endTime") String endTime,
      @Param("accountNo") Long accountNo);
}
