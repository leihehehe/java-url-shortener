package com.leih.url.account.manager;

import com.leih.url.account.entity.Account;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface AccountManager {
  void insertAccount(Account account);

  Account findAccountByPhone(String phone);

  Account findAccountByUsername(String username);
  Account getDetail(Long accountNo);
  boolean updateAvatar(Long accountNo, String avatar);
}
