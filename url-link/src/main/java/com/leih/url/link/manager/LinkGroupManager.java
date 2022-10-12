package com.leih.url.link.manager;

import com.leih.url.link.entity.LinkGroup;

import java.util.List;

public interface LinkGroupManager {
    boolean addGroup(LinkGroup linkGroup);

    boolean deleteGroup(long accountNo, long groupId);

    LinkGroup getGroup(Long accountNo, long groupId);
    List<LinkGroup> listAllGroups(Long accountNo);

//    boolean updateGroup(LinkGroup linkGroup);
    boolean updateGroup(long groupId, String name, long accountNo);
}
