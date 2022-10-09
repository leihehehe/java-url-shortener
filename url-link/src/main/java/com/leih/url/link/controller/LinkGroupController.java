package com.leih.url.link.controller;

import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.util.JsonData;
import com.leih.url.link.controller.request.LinkGroupAddRequest;
import com.leih.url.link.service.LinkGroupService;
import feign.Param;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/group/v1/")
public class LinkGroupController {
    @Autowired
    private LinkGroupService linkGroupService;
    /**
     * Create group
     * @param request
     * @return
     */
    @PostMapping("/create")
    public JsonData createGroup(@RequestBody LinkGroupAddRequest request){
        return linkGroupService.createGroup(request)?JsonData.buildSuccess():JsonData.buildResult(BizCodeEnum.GROUP_CREATE_FAILED);
    }
    @DeleteMapping("/delete/{group_id}")
    public JsonData deleteGroup(@PathVariable("group_id") long groupId){
        return linkGroupService.deleteGroup(groupId)?JsonData.buildSuccess():JsonData.buildResult(BizCodeEnum.GROUP_DELETE_FAILED);
    }
}
