package jp.uich.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
  private Long id;
  private String name;
  private Category category;
}
