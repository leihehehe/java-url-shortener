package com.leih.url.link.manager;

import com.leih.url.common.enums.ShortLinkStateEnum;
import com.leih.url.link.entity.GroupLinkMapping;
import com.leih.url.link.entity.Link;

import java.util.Map;

public interface GroupLinkMappingManager {
    GroupLinkMapping findShortLinkByGroupIdAndMappingId(Long mappingId, Long accountNo, Long groupId);
    boolean addShortLink(GroupLinkMapping groupLinkMapping);
    boolean deleteShortLink(String shortLinkCode, Long accountNo, Long groupId);
    Map<String, Object> pageShortLinkByGroupId(int page, int size, Long accountNo,Long groupId);
    boolean updateGroup(Long accountNo, Long groupId, String shortLinkCode, ShortLinkStateEnum shortLinkStateEnum);

    GroupLinkMapping findShortLinkByCode(String shortLinkCode, Long id, Long accountNo);
}
