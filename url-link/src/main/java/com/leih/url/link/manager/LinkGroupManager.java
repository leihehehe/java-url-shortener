package com.leih.url.link.manager;

import com.leih.url.link.entity.LinkGroup;

import java.util.List;

public interface LinkGroupManager {
    boolean addGroup(LinkGroup linkGroup);

    boolean deleteGroup(Long accountNo, Long groupId);

    LinkGroup getGroup(Long accountNo, Long groupId);
    List<LinkGroup> listAllGroups(Long accountNo);

//    boolean updateGroup(LinkGroup linkGroup);
    boolean updateGroup(Long groupId, String name, Long accountNo);
}
