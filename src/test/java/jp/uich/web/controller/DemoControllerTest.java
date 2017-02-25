package jp.uich.web.controller;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import lombok.SneakyThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DemoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  @SneakyThrows
  public void test_拡張子無し_JSONが返ること() {
    this.mockMvc.perform(get("/item"))
      .andDo(print())
      .andExpect(header().string("content-type", startsWith("application/json")))
      .andExpect(status().is2xxSuccessful());
  }

  @Test
  @SneakyThrows
  public void test_拡張子json_JSONが返ること() {
    this.mockMvc.perform(get("/item.json"))
      .andDo(print())
      .andExpect(header().string("content-type", startsWith("application/json")))
      .andExpect(status().is2xxSuccessful());
  }

  @Test
  @SneakyThrows
  public void test_拡張子csv_CSVが返ること() {
    this.mockMvc.perform(get("/item.csv"))
      .andDo(print())
      .andExpect(header().string("content-type", startsWith("text/csv")))
      .andExpect(status().is2xxSuccessful());
  }

}
