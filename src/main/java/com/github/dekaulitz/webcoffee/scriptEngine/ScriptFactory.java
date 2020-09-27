package com.github.dekaulitz.webcoffee.scriptEngine;

import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeValidationExcepton;
import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeArgumentsRunner;

public class ScriptFactory {

  public static ScriptEngine getFactory(WebCoffeeArgumentsRunner webCoffeeArgumentsRunner) {

    ScriptEngine scriptEngine = null;
    if (webCoffeeArgumentsRunner.getLang().equals(Lang.JAVASCRIPT)) {
      scriptEngine = new JavaScriptEngine(webCoffeeArgumentsRunner);
    } else {
      throw new WebCoffeeValidationExcepton(
          webCoffeeArgumentsRunner.getLang() + " is not supported yet");
    }
    return scriptEngine;
  }

  public enum Lang {

    JAVASCRIPT("js");

    private String script;

    Lang(String script) {
      this.script = script;
    }

    public String getScript() {
      return script;
    }

    public static Lang getLang(final String lang) {
      if (lang.equals("js")) {
        return Lang.JAVASCRIPT;
      }
      throw new WebCoffeeValidationExcepton(
          lang + " is not supported yet");
    }
  }

}
