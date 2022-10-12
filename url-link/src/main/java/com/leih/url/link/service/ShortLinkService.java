package com.leih.url.link.service;

import com.leih.url.link.vo.LinkVo;

public interface ShortLinkService {
    LinkVo parseShortLinkCode(String shortLinkCode);
}
