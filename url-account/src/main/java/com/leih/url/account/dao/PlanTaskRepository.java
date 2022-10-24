package com.leih.url.account.dao;

import com.leih.url.account.entity.PlanTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PlanTaskRepository extends JpaRepository<PlanTask,Long> {
    PlanTask findByIdAndAccountNo(Long id, Long accountNo);
    @Modifying
    @Transactional
    int deleteByIdAndAccountNo(Long id, Long accountNo);
}
