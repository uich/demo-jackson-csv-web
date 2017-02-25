package jp.uich.web.controller;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jp.uich.dto.Category;
import jp.uich.dto.Item;
import lombok.Getter;

@RestController
@RequestMapping
public class DemoController {

  interface Result {}

  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Result handleException(Exception e) {
    e.printStackTrace();
    return new Result() {
      @Getter
      String message = "エラーです。";
    };
  }

  @GetMapping("/items")
  public List<Item> getItems(HttpServletResponse response) {
    return Collections.singletonList(Item.builder()
      .id(1234L)
      .name("手提げバッグ")
      .category(Category.builder()
        .id(345)
        .name("小物")
        .parent(Category.builder()
          .id(345)
          .name("バッグ")
          .build())
        .build())
      .build());
  }
}
