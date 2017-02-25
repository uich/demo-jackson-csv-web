package jp.uich.web.controller.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ContentsDownloadHandlerInterceptor extends HandlerInterceptorAdapter {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    if (request.getParameterMap().containsKey("download")) {
      response.addHeader("Content-Transfer-Encoding", "binary");
      response.addHeader("Content-Disposition", "attachment");
    }

    return true;
  }

}
