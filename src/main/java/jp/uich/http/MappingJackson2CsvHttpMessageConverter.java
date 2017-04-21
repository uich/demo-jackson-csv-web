package jp.uich.http;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Optional;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import lombok.Setter;

public class MappingJackson2CsvHttpMessageConverter extends AbstractJackson2HttpMessageConverter {

  @Setter
  private boolean printHeader;

  public MappingJackson2CsvHttpMessageConverter(CsvMapper csvMapper) {
    super(csvMapper);
  }

  @Override
  protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
    throws IOException, HttpMessageNotWritableException {
    CsvMapper csvMapper = (CsvMapper) this.getObjectMapper();

    // レスポンスの文字コード
    // #getJsonEncoding(MediaType)は使わない。Shift_JISに対応していないから。
    Charset actualCharset = Optional.ofNullable(outputMessage.getHeaders().getContentType())
      // レスポンスヘッダに指定されていたらそれを使用する
      .map(contentType -> contentType.getCharset())
      // そうでなければデフォルトのCharset
      .orElseGet(this::getDefaultCharset);

    JsonGenerator generator = csvMapper.getFactory().createGenerator(
      new OutputStreamWriter(outputMessage.getBody(), actualCharset));

    try {
      ObjectWriter objectWriter = Optional.ofNullable(this.getSchemaType(type))
        .map(csvMapper::schemaFor)
        .map(schema -> this.printHeader ? schema.withHeader() : schema)
        .map(csvMapper::writer)
        .orElseGet(csvMapper::writer);

      objectWriter.writeValue(generator, object);
      generator.flush();

    } catch (JsonProcessingException ex) {
      throw new HttpMessageNotWritableException("Could not write content: " + ex.getMessage(), ex);
    }
  }

  private Class<?> getSchemaType(Type type) {
    if (type == null) {
      return null;
    }
    JavaType javaType = this.getJavaType(type, null);
    Class<?> rawClass;

    if (javaType.isCollectionLikeType()) {
      rawClass = Optional.ofNullable(javaType.getContentType())
        .map(JavaType::getRawClass)
        .orElse(null);

      if (rawClass == null) {
        return null;
      }
    } else {
      rawClass = javaType.getRawClass();
    }

    Class<?> classForSchema = this.getObjectMapper().findMixInClassFor(rawClass);
    if (classForSchema == null) {
      return rawClass;
    }

    return classForSchema;
  }

}
