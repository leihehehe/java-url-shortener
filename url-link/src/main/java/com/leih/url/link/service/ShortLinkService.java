package com.leih.url.link.service;

import com.leih.url.common.util.JsonData;
import com.leih.url.link.controller.request.ShortLinkAddRequest;
import com.leih.url.link.vo.LinkVo;

public interface ShortLinkService {
    LinkVo parseShortLinkCode(String shortLinkCode);

    JsonData createShortLink(ShortLinkAddRequest request);

}
