package jp.uich.web.controller;

import jp.uich.dto.Category;
import jp.uich.dto.Item;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DemoController {

  @Data
  static class Result {

    final String message;
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  Result handleException(Exception e) {
    e.printStackTrace();
    return new Result("エラーです。");
  }

  @GetMapping("/items")
  List<Item> getItems() {
    return List.of(Item.builder()
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
