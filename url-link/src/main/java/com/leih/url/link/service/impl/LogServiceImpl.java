package com.leih.url.link.service.impl;

import com.leih.url.common.enums.LogTypeEnum;
import com.leih.url.common.model.LogRecord;
import com.leih.url.common.util.CommonUtil;
import com.leih.url.common.util.JsonData;
import com.leih.url.common.util.JsonUtil;
import com.leih.url.link.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Slf4j
public class LogServiceImpl implements LogService {
  private static final String TOPIC_NAME = "ods_link_visit_topic";
  /** Test */
/*  private static List<String> ipList = new ArrayList<>();

  private static List<String> refererList = new ArrayList<>();

  static {
    ipList.add("43.252.113.23");
    ipList.add("69.162.81.155");
    refererList.add("https://youtube.com");
    refererList.add("https://google.com");
  }*/

  @Autowired private KafkaTemplate<String, Object> kafkaTemplate;
  private Random random = new Random();

  @Override
  public void recordShortLinkCode(
      HttpServletRequest request, String shortLinkCode, Long accountNo) {
    // IP, agent
    String ipAddress = CommonUtil.getIpAddress(request);
    //    String ipAddress = ipList.get(random.nextInt(ipList.size()));
    // get all headers
    Map<String, String> allRequestHeader = CommonUtil.getAllRequestHeader(request);
    Map<String, String> map = new HashMap<>();
    map.put("user-agent", allRequestHeader.get("user-agent"));
    //    map.put("referer", refererList.get(random.nextInt(refererList.size())));
    map.put("referer", allRequestHeader.get("referer"));
    map.put("accountNo", String.valueOf(accountNo));
    LogRecord logRecord =
        LogRecord.builder()
            .event(LogTypeEnum.SHORT_LINK.name())
            .content(map)
            .ip(ipAddress)
            .timestamp(CommonUtil.getCurrentTimestamp())
            .bizId(shortLinkCode)
            .build();
    String jsonLog = JsonUtil.obj2Json(logRecord);
    // print out the log
    log.info(jsonLog);
    // send messages through kafka
    kafkaTemplate.send(TOPIC_NAME, jsonLog);
  }
}
