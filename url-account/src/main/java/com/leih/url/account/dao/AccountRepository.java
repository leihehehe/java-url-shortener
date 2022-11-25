package com.leih.url.account.dao;

import com.leih.url.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
    Account findAccountByUsername(String username);
    Account findAccountByPhone(String phone);
    Account findAccountByAccountNo(Long accountNo);


    @Modifying
    @Transactional
    @Query("update Account set avatar=:avatar where accountNo=:accountNo")
    int updateAvatar(@Param("accountNo") Long accountNo, @Param("avatar") String avatar);
}
