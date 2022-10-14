package com.leih.url.link;

import com.leih.url.link.entity.Domain;
import com.leih.url.link.manager.DomainManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LinkApplication.class)
@Slf4j
public class DomainTest {
    @Autowired
    private DomainManager domainManager;
    @Test
    public void testListDomain(){
        List<Domain> domains = domainManager.listOfficialDomains();
        log.info("domains: {}",domains);
    }
}
