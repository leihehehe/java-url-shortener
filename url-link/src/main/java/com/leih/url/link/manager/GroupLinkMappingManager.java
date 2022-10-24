package com.leih.url.link.manager;

import com.leih.url.common.enums.ShortLinkStateEnum;
import com.leih.url.link.entity.GroupLinkMapping;
import com.leih.url.link.entity.Link;

import java.util.Map;

public interface GroupLinkMappingManager {
    GroupLinkMapping findShortLinkByGroupIdAndMappingId(Long mappingId, Long accountNo, Long groupId);
    boolean addShortLink(GroupLinkMapping groupLinkMapping);
    boolean deleteShortLink(GroupLinkMapping groupLinkMapping);
    Map<String, Object> pageShortLinkByGroupId(int page, int size, Long accountNo,Long groupId);

    /**
     * Admin update
     * @param accountNo
     * @param groupId
     * @param shortLinkCode
     * @param shortLinkStateEnum
     * @return
     */
    boolean updateGroupMapping(Long accountNo, Long groupId, String shortLinkCode, ShortLinkStateEnum shortLinkStateEnum);


    /**
     * User update
     * @param groupLinkMapping
     * @return
     */
    boolean updateGroupMapping(GroupLinkMapping groupLinkMapping);

    GroupLinkMapping findShortLinkByCode(String shortLinkCode, Long id, Long accountNo);

    boolean fullyDeleteShortLinkByCode(GroupLinkMapping groupLinkMapping);
}
