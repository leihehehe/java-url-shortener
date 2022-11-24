package com.leih.url.account.manager;

import com.leih.url.account.entity.Account;

public interface AccountManager {
  void insertAccount(Account account);

  Account findAccountByPhone(String phone);

  Account findAccountByUsername(String username);
  Account getDetail(Long accountNo);
}
