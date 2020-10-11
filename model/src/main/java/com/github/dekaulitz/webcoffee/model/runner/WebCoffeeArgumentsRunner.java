package com.github.dekaulitz.webcoffee.model.runner;


import com.github.dekaulitz.webcoffee.scriptengine.ScriptFactory;
import com.github.dekaulitz.webcoffee.scriptengine.ScriptFactory.Lang;
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
  private Lang lang;
  private String argumentName;
  private Object value;

  public Object getValue() {
    WebCoffeeArgumentsRunner webCoffeeArgumentsRunner = this;
    return ScriptFactory.getFactory(webCoffeeArgumentsRunner).execute();
  }

}
