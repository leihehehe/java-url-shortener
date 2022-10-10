package com.leih.url.link.service;

import com.leih.url.link.controller.request.LinkGroupAddRequest;
import com.leih.url.link.controller.request.LinkGroupUpdateRequest;
import com.leih.url.link.vo.LinkGroupVo;

import java.util.List;

public interface LinkGroupService {
    boolean createGroup(LinkGroupAddRequest request);

    boolean deleteGroup(long groupId);

    LinkGroupVo getDetails(long groupId);

    List<LinkGroupVo> listAllGroups();

    boolean updateGroup(LinkGroupUpdateRequest request);

}
