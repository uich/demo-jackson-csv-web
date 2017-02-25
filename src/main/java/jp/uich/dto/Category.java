package jp.uich.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Category {
  private Integer id;
  private String name;

  private Category parent;
}
