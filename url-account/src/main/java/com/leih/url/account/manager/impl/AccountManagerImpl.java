package com.leih.url.account.manager.impl;

import com.leih.url.account.dao.AccountRepository;
import com.leih.url.account.entity.Account;
import com.leih.url.account.manager.AccountManager;
import com.leih.url.account.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountManagerImpl implements AccountManager {
    @Autowired
    AccountRepository accountRepository;
    @Override
    public void insertAccount(Account account) {
        accountRepository.save(account);
    }
    @Override
    public Account findAccountByPhone(String phone) {
        return accountRepository.findAccountByPhone(phone);
    }

    @Override
    public Account getDetail(Long accountNo) {
        return accountRepository.findAccountByAccountNo(accountNo);
    }
}
