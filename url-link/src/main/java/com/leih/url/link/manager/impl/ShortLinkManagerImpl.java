package com.leih.url.link.manager.impl;

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
        return linkRepository.findLinkByCodeAndDel(linkCode,0);
    }

    @Override
    public boolean deleteShortLink(Link shortLink) {
        try{
      linkRepository.deleteShortLink(shortLink.getCode(), shortLink.getAccountNo());
            return true;
        }catch (Exception e){
            log.error("Failed to delete the short link code: {};\nexception: {}", shortLink.getCode(), e.getMessage());
            return false;
        }

    }

    @Override
    public boolean updateShortLink(Link updatedLink) {
        try{
      linkRepository.updateShortLink(
          updatedLink.getName(),
          updatedLink.getDomain(),
          updatedLink.getAccountNo(),
          updatedLink.getCode());
            return true;
        }catch (Exception e){
            log.error("Failed to delete the short link code: {};\nexception: {}", updatedLink, e.getMessage());
            return false;
        }
    }

}
