package com.leih.url.link.manager.impl;

import com.leih.url.common.enums.ShortLinkStateEnum;
import com.leih.url.link.dao.GroupLinkMappingRepository;
import com.leih.url.link.entity.GroupLinkMapping;
import com.leih.url.link.manager.GroupLinkMappingManager;
import com.leih.url.link.vo.GroupLinkMappingVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GroupLinkMappingManagerImpl implements GroupLinkMappingManager {
    @Autowired
    GroupLinkMappingRepository groupLinkMappingRepository;
    @Override
    public GroupLinkMapping findShortLinkByGroupIdAndMappingId(Long mappingId, Long accountNo, Long groupId) {
        return groupLinkMappingRepository.findLinkByIdAndAccountNoAndGroupId(mappingId, accountNo, groupId);
    }

    @Override
    public boolean addShortLink(GroupLinkMapping groupLinkMapping) {
        try{
            groupLinkMappingRepository.save(groupLinkMapping);
            return true;
        }catch (Exception e){
            log.error("Failed to create short link for user");
            return false;
        }
    }

    @Override
    public boolean deleteShortLink(String shortLinkCode, Long accountNo, Long groupId) {
        try{
            groupLinkMappingRepository.deleteGroupLinkMapping(shortLinkCode,accountNo,groupId);
            return true;
        }catch (Exception e){
            log.error("Failed to delete the short link on user side.");
            return false;
        }
    }

    @Override
    public Map<String, Object> pageShortLinkByGroupId(int page, int size, Long accountNo, Long groupId) {

        Page<GroupLinkMapping> shortLinksPage = groupLinkMappingRepository.findAllByAccountNoAndGroupId(PageRequest.of(page, size), accountNo, groupId);
        Map<String,Object> pageInfo = new HashMap<>(3);
        pageInfo.put("total_records",shortLinksPage.getTotalElements());
        pageInfo.put("total_pages",shortLinksPage.getTotalPages());
        pageInfo.put("current_data",shortLinksPage.getContent().stream().map(this::convertObjectToVo).collect(Collectors.toList()));
        return pageInfo;
    }

    private GroupLinkMappingVo convertObjectToVo(GroupLinkMapping groupLinkMapping){
        GroupLinkMappingVo groupLinkMappingVo = new GroupLinkMappingVo();
        BeanUtils.copyProperties(groupLinkMapping,groupLinkMappingVo);
        return groupLinkMappingVo;
    }
    @Override
    public boolean updateGroup(Long accountNo, Long groupId, String shortLinkCode, ShortLinkStateEnum shortLinkStateEnum) {
        try{
            groupLinkMappingRepository.updateGroupLinkMappingState(shortLinkCode,accountNo,groupId,shortLinkStateEnum.name());
            return true;
        }catch (Exception e){
            log.error("Failed to delete the short link on user side.");
            return false;
        }
    }
}
