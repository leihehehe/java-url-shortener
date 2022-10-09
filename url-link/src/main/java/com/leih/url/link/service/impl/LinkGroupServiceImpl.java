package com.leih.url.link.service.impl;

import com.leih.url.common.intercepter.LoginInterceptor;
import com.leih.url.link.controller.request.LinkGroupAddRequest;
import com.leih.url.link.entity.LinkGroup;
import com.leih.url.link.manager.LinkGroupManager;
import com.leih.url.link.service.LinkGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinkGroupServiceImpl implements LinkGroupService {
    @Autowired
    LinkGroupManager linkGroupManager;
    @Override
    public boolean createGroup(LinkGroupAddRequest request) {
        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        LinkGroup linkGroup = new LinkGroup();
        linkGroup.setAccountNo(accountNo);
        linkGroup.setName(request.getName());
        return linkGroupManager.addGroup(linkGroup);
    }

    @Override
    public boolean deleteGroup(long groupId) {
        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
        return linkGroupManager.deleteGroup(accountNo,groupId);
    }
}
