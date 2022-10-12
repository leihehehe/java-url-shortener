package com.leih.url.link.manager;

import com.leih.url.common.enums.ShortLinkStateEnum;
import com.leih.url.link.entity.GroupLinkMapping;

import java.util.Map;

public interface GroupLinkMappingManager {
    GroupLinkMapping findShortLinkByGroupIdAndMappingId(long mappingId, long accountNo, long groupId);
    boolean addShortLink(GroupLinkMapping groupLinkMapping);
    boolean deleteShortLink(String shortLinkCode, long accountNo, long groupId);
    Map<String, Object> pageShortLinkByGroupId(int page, int size, long accountNo,long groupId);
    boolean updateGroup(long accountNo, long groupId, String shortLinkCode, ShortLinkStateEnum shortLinkStateEnum);

}
