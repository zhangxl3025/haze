package com.zxl.haze.web.util;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class RequestUtil {

  public static Map<String, String> getRequestParams(HttpServletRequest request) {
    Map<String, String> typeSafeRequestMap = new HashMap<>();
    Enumeration<?> requestParamNames = request.getParameterNames();
    while (requestParamNames.hasMoreElements()) {
      String requestParamName = (String) requestParamNames.nextElement();
      String requestParamValue = request.getParameter(requestParamName);
      typeSafeRequestMap.put(requestParamName, requestParamValue);
    }
    return typeSafeRequestMap;
  }

  public static Map<String, String> getHeaderMap(HttpServletRequest request) {
    Map<String, String> map = new HashMap<String, String>();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String key = headerNames.nextElement();
      String value = request.getHeader(key);
      map.put(key, value);
    }
    return map;
  }

  public static String getOrDefault(HttpServletRequest request, String key, String defaultValue) {
    Map<String, String> headersInfo = getHeaderMap(request);
    return headersInfo.getOrDefault(key, defaultValue);
  }

  public static String get(HttpServletRequest request, String key) {
    Map<String, String> headersInfo = getHeaderMap(request);
    return headersInfo.get(key);
  }

  public static String getBodyString(ServletRequest request) throws IOException {
    StringBuilder stringBuilder = new StringBuilder();
    BufferedReader bufferedReader = null;
    try {
      InputStream inputStream = request.getInputStream();
      if (inputStream != null) {
        bufferedReader = new BufferedReader(new InputStreamReader(inputStream,StandardCharsets.UTF_8));
        char[] charBuffer = new char[128];
        int bytesRead = -1;
        while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
          stringBuilder.append(charBuffer, 0, bytesRead);
        }
      }
    } finally {
      if (bufferedReader != null) {
        bufferedReader.close();
      }
    }
    //Store request body content in 'body' variable
    return stringBuilder.toString();
  }

  public static HttpServletRequest getRequest() {
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (requestAttributes == null) {
      return new MockHttpServletRequest();
    }
    return requestAttributes.getRequest();
  }
}
