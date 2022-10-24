package com.leih.url.link.service;

import com.leih.url.common.model.EventMessage;
import com.leih.url.common.util.JsonData;
import com.leih.url.link.controller.request.ShortLinkAddRequest;
import com.leih.url.link.controller.request.ShortLinkDeleteRequest;
import com.leih.url.link.controller.request.ShortLinkPageRequest;
import com.leih.url.link.controller.request.ShortLinkUpdateRequest;
import com.leih.url.link.vo.LinkVo;

import java.util.Map;

public interface ShortLinkService {
    LinkVo parseShortLinkCode(String shortLinkCode);

    JsonData createShortLink(ShortLinkAddRequest request);

    /**
     * Handle the action of adding a short link
     * @param eventMessage
     * @return
     */
    boolean handleAddShortLink(EventMessage eventMessage);

    Map<String, Object> pageByGroupId(ShortLinkPageRequest request);

    JsonData deleteShortLink(ShortLinkDeleteRequest request);

    JsonData updateShortLink(ShortLinkUpdateRequest request);
    boolean handleUpdateShortLink(EventMessage eventMessage);
    boolean handleDeleteShortLink(EventMessage eventMessage);

    boolean deleteShortLinkMappingByCode(EventMessage eventMessage);
}
