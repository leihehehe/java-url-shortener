package com.leih.url.link.service;

import com.leih.url.common.util.JsonData;

import javax.servlet.http.HttpServletRequest;

public interface LogService {
    /**
     * record logs for shortLinkCode
     * @param request
     * @param shortLinkCode
     * @param accountNo
     * @return
     */
    void recordShortLinkCode(HttpServletRequest request,String shortLinkCode,Long accountNo);
}
