package com.leih.url.account.db;

import com.leih.url.account.dao.AccountRepository;
import com.leih.url.account.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
public class DatabaseTest {
    @Autowired
    AccountRepository accountRepository;

    @Test
    public void testPageable(){
        accountRepository.findAll(PageRequest.of(0,2)).forEach(System.out::println);
    }
    @Test
    public void testUsernameQuery(){
        Account accountByUsername = accountRepository.findAccountByUsername("123456");
        System.out.println(accountByUsername);
    }
}
