package jp.uich.databind;

import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleSerializers;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SampleSerializers extends SimpleSerializers {

  private final Map<Class<?>, Class<?>> flatterMap;

  @Override
  public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
    Class<?> rawClass = type.getRawClass();
    Class<?> flatClass = this.flatterMap.entrySet().stream()
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
