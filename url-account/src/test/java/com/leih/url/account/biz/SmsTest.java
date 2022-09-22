package com.leih.url.account.biz;

import com.leih.url.account.UrlAccountApplication;
import com.leih.url.account.component.SmsComponent;
import com.leih.url.account.config.SmsConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UrlAccountApplication.class)
@Slf4j
public class SmsTest {
  @Autowired
  SmsComponent smsComponent;

  @Test
  public void testSendingSms() {
    smsComponent.sendSms("+61 433826966","haha hello");
  }
}
