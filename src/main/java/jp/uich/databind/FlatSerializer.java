package jp.uich.databind;

import java.io.IOException;
import java.lang.reflect.Field;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import jp.uich.databind.annotation.JsonMixinProperty;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FlatSerializer extends JsonSerializer<Object> {

  private final Class<?> flatType;

  @Override
  public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers)
    throws IOException, JsonProcessingException {
    BeanWrapper targetWrapper = this.wrap(value);
    BeanWrapper mixinWrapper = this.wrap(BeanUtils.instantiateClass(this.flatType));

    ReflectionUtils.doWithFields(this.flatType, field -> {
      String targetPath = this.targetPath(field);
      if (targetWrapper.isReadableProperty(targetPath) == false) {
        return;
      }
      if (mixinWrapper.isWritableProperty(field.getName()) == false) {
        return;
      }
      mixinWrapper.setPropertyValue(field.getName(), targetWrapper.getPropertyValue(targetPath));
    });
    gen.writeObject(mixinWrapper.getWrappedInstance());
  }

  private BeanWrapper wrap(Object obj) {
    BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(obj);
    wrapper.setAutoGrowNestedPaths(true);
    return wrapper;
  }

  private String targetPath(Field field) {
    JsonMixinProperty property = field.getAnnotation(JsonMixinProperty.class);
    if (property == null) {
      return field.getName();
    }
    return property.value();
  }

}
