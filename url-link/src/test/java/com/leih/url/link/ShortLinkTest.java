package com.leih.url.link;

import com.leih.url.common.util.CommonUtil;
import com.leih.url.link.component.ShortLinkComponent;
import com.leih.url.link.dao.LinkGroupRepository;
import com.leih.url.link.entity.Link;
import com.leih.url.link.entity.LinkGroup;
import com.leih.url.link.manager.ShortLinkManager;
import com.leih.url.link.strategry.ShardingDBConfig;
import com.leih.url.link.strategry.ShardingTableConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LinkApplication.class)
public class ShortLinkTest {
  @Autowired ShortLinkComponent shortLinkComponent;
  @Autowired LinkGroupRepository linkGroupRepository;
  @Autowired private ShortLinkManager shortLinkManager;

  @Test
  public void shorLinkTest() {
    String originalUrl = "https://github.com/leihehehe";
    String shortLink = shortLinkComponent.createShortLink(originalUrl);
    log.info(shortLink);
  }

  @Test
  public void databaseTest() {
    LinkGroup linkGroup = new LinkGroup();
    linkGroup.setName("test");
    linkGroup.setAccountNo(122223L);
    linkGroupRepository.save(linkGroup);
  }

/*  @Test
  public void testRandomDBPrefix() {
    for (int i = 0; i < 20; i++) {
      log.info(ShardingDBConfig.getRandomDBPrefix());
    }
    for (int i = 0; i < 20; i++) {
      log.info(ShardingTableConfig.getRandomTableSuffix());
    }
  }*/

  @Test
  public void testSaveShortLink() {
    Random random = new Random();
    for (int i = 0; i < 10; i++) {
      String originalUrl = "https://github.com/leihehehe" + i;
      String code = shortLinkComponent.createShortLink(originalUrl);
      int accountNo = random.nextInt(100000000);
      log.info(code);
      Link shortLink = new Link();
      shortLink.setCode(code);
      shortLink.setAccountNo((long) accountNo);
      shortLink.setSign(CommonUtil.MD5(originalUrl));
      shortLink.setDel(0);
      shortLinkManager.addShortLink(shortLink);
    }
  }
  @Test
  public void testFindCode(){
    Link shortShortLinkByCode = shortLinkManager.findShortLinkByCode("13TXpI5a");
    log.info("query: {}", shortShortLinkByCode);
  }

  @Test
  public void testGenerateCode(){
    for(int i =0;i<5;i++){
      String originalUrl = "https://leihehe.top";
      String shortLink = shortLinkComponent.createShortLink(originalUrl);
      System.out.println(shortLink);
    }
  }
}
