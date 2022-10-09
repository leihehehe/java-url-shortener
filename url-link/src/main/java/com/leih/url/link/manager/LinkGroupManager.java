package com.leih.url.link.manager;

import com.leih.url.link.entity.LinkGroup;

public interface LinkGroupManager {
    boolean addGroup(LinkGroup linkGroup);

    boolean deleteGroup(long accountNo, long groupId);
}
