package jp.uich.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import jp.uich.databind.SampleSerializers;
import jp.uich.databind.annotation.JsonFlatMixinFor;
import jp.uich.http.MappingJackson2CsvHttpMessageConverter;

@Configuration
public class Config {

  @Bean
  CsvMapper csvMapper() {
    CsvMapper csvMapper = new CsvMapper();
    csvMapper.setMixIns(this.flatterMap());

    csvMapper.registerModule(new SimpleModule() {
      @Override
      public void setupModule(SetupContext context) {
        context.addSerializers(new SampleSerializers(Config.this.flatterMap()));
      }
    });

    return csvMapper;
  }

  Map<Class<?>, Class<?>> flatterMap() {
    return new Reflections("jp.uich.databind.mixin").getTypesAnnotatedWith(JsonFlatMixinFor.class)
      .stream()
      .collect(Collectors.toMap(
        type -> type.getAnnotation(JsonFlatMixinFor.class).value(), Function.identity()));
  }

  @Bean
  HttpMessageConverters converters() {
    return new HttpMessageConverters(false, Arrays.asList(
      this.jsonConverter(),
      this.yamlConverter(),
      this.csvConverter()));
  }

  @Bean
  MappingJackson2HttpMessageConverter jsonConverter() {
    return new MappingJackson2HttpMessageConverter(
      Jackson2ObjectMapperBuilder.json()
        .indentOutput(true)
        .build());
  }

  @Bean
  MappingJackson2HttpMessageConverter yamlConverter() {
    MappingJackson2HttpMessageConverter yamlConverter = new MappingJackson2HttpMessageConverter(
      new ObjectMapper(new YAMLFactory()));
    yamlConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.parseMediaType("application/yaml")));
    return yamlConverter;
  }

  @Bean
  MappingJackson2CsvHttpMessageConverter csvConverter() {
    MappingJackson2CsvHttpMessageConverter converter = new MappingJackson2CsvHttpMessageConverter(this.csvMapper());
    converter.setSupportedMediaTypes(Collections.singletonList(MediaType.parseMediaType("text/csv")));
    converter.setPrintHeader(true);
    return converter;
  }

}
