package com.leih.url.link.service.impl;

import com.leih.url.common.intercepter.LoginInterceptor;
import com.leih.url.link.controller.request.LinkGroupAddRequest;
import com.leih.url.link.controller.request.LinkGroupUpdateRequest;
import com.leih.url.link.entity.LinkGroup;
import com.leih.url.link.manager.LinkGroupManager;
import com.leih.url.link.service.LinkGroupService;
import com.leih.url.link.vo.LinkGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LinkGroupServiceImpl implements LinkGroupService {
  @Autowired LinkGroupManager linkGroupManager;

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
    return linkGroupManager.deleteGroup(accountNo, groupId);
  }

  @Override
  public LinkGroupVo getDetails(long groupId) {
    Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
    LinkGroup linkGroup = linkGroupManager.getGroup(accountNo, groupId);
    LinkGroupVo linkGroupVo = new LinkGroupVo();
    BeanUtils.copyProperties(linkGroup, linkGroupVo);
    return linkGroupVo;
  }

  @Override
  public List<LinkGroupVo> listAllGroups() {
    Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
    List<LinkGroup> linkGroups = linkGroupManager.listAllGroups(accountNo);
    List<LinkGroupVo> groups =
        linkGroups.stream()
            .map(
                obj -> {
                  LinkGroupVo linkGroupVo = new LinkGroupVo();
                  BeanUtils.copyProperties(obj, linkGroupVo);
                  return linkGroupVo;
                })
            .toList();

    return groups;
  }

  @Override
  public boolean updateGroup(LinkGroupUpdateRequest request) {
    Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();
    //    LinkGroup linkGroup = new LinkGroup();
    //    linkGroup.setName(request.getName());
    //    linkGroup.setAccountNo(accountNo);
    //    linkGroup.setId(request.getId());
    return linkGroupManager.updateGroup(request.getId(), request.getName(), accountNo);
  }
}
