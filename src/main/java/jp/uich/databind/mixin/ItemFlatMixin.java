package jp.uich.databind.mixin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jp.uich.databind.annotation.JsonFlatMixinFor;
import jp.uich.databind.annotation.JsonMixinProperty;
import jp.uich.dto.Item;
import lombok.Data;

@Data
@JsonPropertyOrder({ "商品ID", "商品名", "大カテゴリ", "中カテゴリ" })
@JsonFlatMixinFor(Item.class)
//@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemFlatMixin {
  @JsonProperty("商品ID")
  private Long id;

  @JsonProperty("商品名")
  private String name;

  @JsonProperty("大カテゴリ")
  @JsonMixinProperty("category.parent.name")
  private String largeCategoryName;

  @JsonProperty("中カテゴリ")
  @JsonMixinProperty("category.name")
  private String middleCategoryName;
}
