package com.leih.url.link.manager.impl;

import com.leih.url.link.dao.LinkGroupRepository;
import com.leih.url.link.entity.LinkGroup;
import com.leih.url.link.manager.LinkGroupManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LinkGroupManagerImpl implements LinkGroupManager {
    @Autowired
    LinkGroupRepository linkGroupRepository;
    @Override
    public boolean addGroup(LinkGroup linkGroup) {
        try{
            linkGroupRepository.save(linkGroup);
            return true;
        }catch (Exception e){
            log.error("Failed to save new group: {};\nexception: {}",linkGroup,e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteGroup(long accountNo, long groupId) {
        try{
            linkGroupRepository.deleteLinkGroupByAccountNoAndId(accountNo,groupId);
            return true;
        }catch (Exception e){
            log.error("Failed to delete the group id: {} for accountNo: {} ;\nexception: {}",groupId,accountNo,e.getMessage());
            return false;
        }
    }
}
