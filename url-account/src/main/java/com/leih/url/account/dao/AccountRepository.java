package com.leih.url.account.dao;

import com.leih.url.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
    Account findAccountByUsername(String username);
    Account findAccountByPhone(String phone);
    Account findAccountByAccountNo(Long accountNo);
}
