package com.leih.url.common.intercepter;

import com.leih.url.common.enums.BizCodeEnum;
import com.leih.url.common.model.LoggedInUser;
import com.leih.url.common.util.CommonUtil;
import com.leih.url.common.util.JWTUtil;
import com.leih.url.common.util.JsonData;
import feign.Request;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
  public static ThreadLocal<LoggedInUser> threadLocal = new ThreadLocal<>();
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    if (HttpMethod.OPTIONS.toString().equalsIgnoreCase(request.getMethod())) {
      response.setStatus(HttpStatus.NO_CONTENT.value());
      return true;
    }
    String accessToken = request.getHeader("token");
    if (!StringUtils.hasLength(accessToken)) {
      accessToken = request.getParameter("token");
    }
    if (StringUtils.hasLength(accessToken)) {
      Claims claims = JWTUtil.checkJWT(accessToken);
      if (claims == null) {
        // not logged in
        CommonUtil.sendJsonMessage(response, JsonData.buildResult(BizCodeEnum.ACCOUNT_UNLOGIN));
        return false;
      }
      Long accountNo = Long.valueOf(claims.get("account_no").toString());
      String avatar = claims.get("avatar").toString();
      String username = claims.get("username").toString();
//      String email = claims.get("email").toString();
//      String phone = claims.get("phone").toString();
      String auth = claims.get("auth").toString();
      LoggedInUser loggedInUser = LoggedInUser.builder()
              .accountNo(accountNo)
              .auth(auth)
//              .phone(phone)
//              .email(email)
              .username(username)
              .avatar(avatar)
              .build();
      threadLocal.set(loggedInUser);
      log.info("logged-in user: {}",loggedInUser);
      return true;
    }
    CommonUtil.sendJsonMessage(response,JsonData.buildResult(BizCodeEnum.ACCOUNT_UNLOGIN));
    return false;
  }

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView)
      throws Exception {
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {
    threadLocal.remove();
  }
}
