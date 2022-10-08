package com.leih.url.link;

import com.google.common.hash.Hashing;
import com.leih.url.common.util.CommonUtil;
import com.leih.url.link.component.ShortLinkComponent;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LinkApplication.class)
public class ShortLinkTest {
  @Autowired
  ShortLinkComponent shortLinkComponent;
  @Test
  public void shorLinkTest() {
    String originalUrl =
        "https://github.com/leihehehe";
    String shortLink = shortLinkComponent.createShortLink(originalUrl);
    log.info(shortLink);
  }
}
