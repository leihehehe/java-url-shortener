package com.leih.url.account.component;

import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.leih.url.account.config.SmsConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class SmsComponent {
  @Autowired SmsConfig smsConfig;
  Map<String, MessageAttributeValue> smsAttributes;

//  @Async("threadPoolTaskExecutor")
  public boolean sendSms(String phone, String message) {
    try {
      PublishResult publish =
          smsConfig
              .getAmazonSNSClient()
              .publish(
                  new PublishRequest()
                      .withMessage(message)
                      .withPhoneNumber(phone)
                      .withMessageAttributes(getDefaultSmsAttributes()));
      log.info("AWS sendSms " + phone + " info={}", publish);
      return true;
    } catch (Exception e) {
      log.error("AWS sendSms " + phone + " error={}", e.getMessage());
      return false;
    }
  }

  public Map<String, MessageAttributeValue> getDefaultSmsAttributes() {
    if (smsAttributes == null) {
      smsAttributes = new HashMap<>();
      smsAttributes.put(
          "AWS.SNS.SMS.SenderID",
          new MessageAttributeValue().withStringValue("1").withDataType("String"));
      smsAttributes.put(
          "AWS.SNS.SMS.MaxPrice",
          new MessageAttributeValue().withStringValue("0.05").withDataType("Number"));
      smsAttributes.put(
          "AWS.SNS.SMS.SMSType",
          new MessageAttributeValue().withStringValue("Transactional").withDataType("String"));
    }
    return smsAttributes;
  }
}
