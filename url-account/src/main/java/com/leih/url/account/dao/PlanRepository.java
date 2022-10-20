package com.leih.url.account.dao;

import com.leih.url.account.entity.Plan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PlanRepository extends JpaRepository<Plan,Long> {
    @Query("select p from Plan p where p.accountNo=:accountNo and p.expiredDate<:date order by p.gmtCreate desc")
    Page<Plan> findAvailablePlans(@Param("accountNo") Long accountNo, @Param("date") String date, Pageable pageable);
    Plan findPlanByAccountNoAndId(Long accountNo, Long planId);

    @Transactional
    @Modifying
    @Query("update Plan set dayUsed=:dayUsedTimes where id=:planId and accountNo=:accountNo")
    int updateDayUsedTimes(@Param("planId") Long planId, @Param("accountNo") Long accountNo,@Param("dayUsedTimes") int dayUsedTimes);
}
