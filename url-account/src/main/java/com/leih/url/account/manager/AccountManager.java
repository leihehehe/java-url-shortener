package com.leih.url.account.manager;

import com.leih.url.account.entity.Account;

public interface AccountManager {
    public void insertAccount(Account account);
    public Account findAccountByPhone(String phone);

    Account getDetail(Long accountNo);
}
