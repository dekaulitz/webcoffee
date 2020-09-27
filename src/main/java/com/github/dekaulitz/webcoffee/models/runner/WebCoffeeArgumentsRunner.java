package com.github.dekaulitz.webcoffee.models.runner;

import com.github.dekaulitz.webcoffee.scriptEngine.ScriptEngineFactory;
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
public class WebCoffeeArgumentsRunner {

  private String src;
  private String lang;
  private String argumentName;
  private Object value;

  public Object getValue() {
    WebCoffeeArgumentsRunner webCoffeeArgumentsRunner = this;
    return ScriptEngineFactory.execute(webCoffeeArgumentsRunner);
  }

}
