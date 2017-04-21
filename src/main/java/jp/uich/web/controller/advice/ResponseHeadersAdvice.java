package jp.uich.web.controller.advice;

import java.nio.charset.Charset;
import java.nio.file.Paths;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jp.uich.http.MappingJackson2CsvHttpMessageConverter;

@RestControllerAdvice
public class ResponseHeadersAdvice implements ResponseBodyAdvice<Object> {

  @Override
  public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    if (returnType.getMethodAnnotation(ExceptionHandler.class) != null) {
      return false;
    }
    return true;
  }

  @Override
  public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
    Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
    ServerHttpResponse response) {
    if (((ServletServerHttpRequest) request).getServletRequest().getParameterMap().containsKey("download")) {
      String fileName = Paths.get(request.getURI().getRawPath()).getFileName().toString();
      // For IE
      response.getHeaders().add("Content-Transfer-Encoding", "binary");
      response.getHeaders().add("Content-Disposition", "attachment; filename=" + fileName);

      if (MappingJackson2CsvHttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
        response.getHeaders().setContentType(new MediaType(selectedContentType, Charset.forName("Shift_JIS")));
      }
    }
    return body;
  }
}
