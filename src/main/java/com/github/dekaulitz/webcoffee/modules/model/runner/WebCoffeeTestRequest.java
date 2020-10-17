package com.github.dekaulitz.webcoffee.modules.model.runner;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class WebCoffeeTestRequest {

  private String host;
  private Map<String, WebCoffeeArgumentsRunner> arguments;
  private WebCoffeeDoRequest doRequest;
  private Integer order;
  private StatusTest status = StatusTest.SKIPPED;
  private long startTime;
  private long endTime;
  private String executionTime = "-";

  public long getEndTime() {
    return System.currentTimeMillis() - startTime;
  }

  public enum StatusTest {
    SUCCESS, FAIL, SKIPPED
  }


}
