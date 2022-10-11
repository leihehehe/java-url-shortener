package com.leih.url.link.manager.impl;

import com.leih.url.common.intercepter.LoginInterceptor;
import com.leih.url.link.dao.LinkRepository;
import com.leih.url.link.entity.Link;
import com.leih.url.link.manager.ShortLinkManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ShortLinkManagerImpl implements ShortLinkManager {
    @Autowired
    LinkRepository linkRepository;
    @Override
    public boolean addShortLink(Link shortLink) {
        try{
            linkRepository.save(shortLink);
            return true;
        }catch (Exception e){
            log.error("Failed to create new short link: {};\nexception: {}", shortLink, e.getMessage());
            return false;
        }
    }

    /**
     * Find short url by code without account no
     * @param linkCode
     * @return
     */
    @Override
    public Link findShortLinkByCode(String linkCode) {
        return linkRepository.findLinkByCode(linkCode);
    }

    @Override
    public boolean deleteShortLink(String linkCode) {
        try{
            Link shortLink = findShortLinkByCode(linkCode);
            shortLink.setDelete(1);
            linkRepository.save(shortLink);
            return true;
        }catch (Exception e){
            log.error("Failed to delete the short link code: {};\nexception: {}", linkCode, e.getMessage());
            return false;
        }

    }
}
