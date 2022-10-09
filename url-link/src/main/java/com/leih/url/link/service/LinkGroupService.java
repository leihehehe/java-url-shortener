package com.leih.url.link.service;

import com.leih.url.link.controller.request.LinkGroupAddRequest;

public interface LinkGroupService {
    boolean createGroup(LinkGroupAddRequest request);

    boolean deleteGroup(long groupId);

}
