package jp.uich.databind;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Map.Entry;

@RequiredArgsConstructor
public class SampleSerializers extends SimpleSerializers {

  private final Map<Class<?>, Class<?>> flatterMap;

  @Override
  public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
    var rawClass = type.getRawClass();
    var flatClass = this.flatterMap.entrySet().stream()
        .filter(entry -> entry.getKey().isAssignableFrom(rawClass))
        .findFirst()
        .map(Entry::getValue)
        .orElse(null);

    if (flatClass != null) {
      return new FlatSerializer(flatClass);
    }

    return super.findSerializer(config, type, beanDesc);
  }
}
