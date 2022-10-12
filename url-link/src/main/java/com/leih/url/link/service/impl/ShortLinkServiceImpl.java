package com.leih.url.link.service.impl;

import com.leih.url.link.entity.Link;
import com.leih.url.link.manager.ShortLinkManager;
import com.leih.url.link.service.ShortLinkService;
import com.leih.url.link.vo.LinkVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShortLinkServiceImpl implements ShortLinkService {

    @Autowired
    ShortLinkManager shortLinkManager;
    @Override
    public LinkVo parseShortLinkCode(String shortLinkCode) {
        Link shortLink = shortLinkManager.findShortLinkByCode(shortLinkCode);
        if(shortLink==null){
            log.error("No short link was found, short link code: {}",shortLinkCode);
            return null;
        }
        LinkVo linkVo = new LinkVo();
        BeanUtils.copyProperties(shortLink,linkVo);
        return linkVo;
    }
}
