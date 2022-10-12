package com.leih.url.link.controller;

import com.leih.url.common.enums.ShortLinkStateEnum;
import com.leih.url.common.util.CommonUtil;
import com.leih.url.link.service.ShortLinkService;
import com.leih.url.link.vo.LinkVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
@RequestMapping("/api/link/v1/")
public class ShortLinkController {
  @Autowired private ShortLinkService shortLinkService;

  /***
   * Check if the code is valid
   * @param shortLinkCode
   * @return
   */
  private static boolean isShortLinkCode(String shortLinkCode) {
    String regx = "~[a-z0-9A-Z]+$";
    return shortLinkCode.matches(regx);
  }

  /***
   * Check if the shortened url is activated
   * @param shortLink
   * @return
   */
  private static boolean isShortLinkValid(LinkVo shortLink) {
    // check if it is null or expired
    if (shortLink != null && shortLink.getExpired().getTime() > CommonUtil.getCurrentTimestamp()) {
      if (shortLink.getState().equalsIgnoreCase(ShortLinkStateEnum.ACTIVATED.name())) {
        return true;
      }
      log.error("The shortened url has been locked.");
    } else if (shortLink != null && shortLink.getExpired().getTime() == -1) {
      if (shortLink.getState().equalsIgnoreCase(ShortLinkStateEnum.ACTIVATED.name())) {
        return true;
      }
      log.error("The shortened url has been locked.");
    }
    log.error("The shortened url does not exist or has already expired.");
    return false;
  }

  @GetMapping(path = "/{shortLinkCode}")
  public void dispatch(
      @PathVariable(name = "shortLinkCode") String shortLinkCode,
      HttpServletRequest request,
      HttpServletResponse response) {
    log.info("Short link code: {}", shortLinkCode);
    try {
      // check short link code
      if (isShortLinkCode(shortLinkCode)) {
        LinkVo linkVo = shortLinkService.parseShortLinkCode(shortLinkCode);
        if (isShortLinkValid(linkVo)) {
          response.setHeader("Location", linkVo.getOriginalUrl());
          // HTTP 302
          response.setStatus(HttpStatus.FOUND.value());
        } else {
          response.setStatus(HttpStatus.NOT_FOUND.value());
        }
      }
    } catch (Exception e) {
      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
  }
}
