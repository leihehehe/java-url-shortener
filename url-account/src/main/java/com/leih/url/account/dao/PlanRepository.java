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

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan,Long> {
    @Query("select p from Plan p where p.accountNo=:accountNo and p.expiredDate>:date order by p.gmtCreate desc")
    Page<Plan> paginateAvailablePlans(@Param("accountNo") Long accountNo, @Param("date") Timestamp date, Pageable pageable);
    Plan findPlanByAccountNoAndId(Long accountNo, Long planId);

    @Transactional
    @Modifying
    @Query("update Plan set dayUsed=:dayUsedTimes where id=:planId and accountNo=:accountNo")
    int updateDayUsedTimes(@Param("planId") Long planId, @Param("accountNo") Long accountNo,@Param("dayUsedTimes") Integer dayUsedTimes);

    @Query("select p from Plan p where p.accountNo=:accountNo and p.pluginType=:pluginType and (p.expiredDate>:date or p.orderNo='free_init') order by p.gmtCreate desc")
    List<Plan> findAvailablePlans(@Param("accountNo") Long accountNo, @Param("pluginType") String pluginType, @Param("date") Timestamp date);

    @Transactional
    @Modifying
    @Query("update Plan set dayUsed=dayUsed+:dayUsedTimes where id=:planId and accountNo=:accountNo and dayLimit-dayUsed>=:dayUsedTimes")
    int addDayUsedTimes(@Param("planId") Long planId, @Param("accountNo") Long accountNo,@Param("dayUsedTimes") Integer dayUsedTimes);

    @Transactional
    @Modifying
    @Query("update Plan set dayUsed=dayUsed-:dayUsedTimes where id=:planId and accountNo=:accountNo and dayUsed-:dayUsedTimes>=0 and function('date_format',gmtModified,'%Y-%m-%d')=:taskCreateDate")
    int restoreDayUsedTimes(@Param("planId") Long planId, @Param("accountNo") Long accountNo,@Param("dayUsedTimes") Integer dayUsedTimes,@Param("taskCreateDate") String taskCreateDate);

    @Transactional
    @Modifying
    @Query("delete from Plan where expiredDate<:currentDatetime and (orderNo not like 'free_init')")
    int deleteExpiredPlans(@Param("currentDatetime") Timestamp currentDatetime);

}
