package com.github.dekaulitz.webcoffee.modules.scriptengine;


import com.github.dekaulitz.webcoffee.modules.model.runner.WebCoffeeArgumentsRunner;
import com.github.dekaulitz.webcoffee.modules.scriptengine.base.ScriptEngine;
import com.github.dekaulitz.webcoffee.modules.scriptengine.impl.JavaScriptEngine;

public class ScriptFactory {

  public static com.github.dekaulitz.webcoffee.modules.scriptengine.base.ScriptEngine getFactory(WebCoffeeArgumentsRunner webCoffeeArgumentsRunner) {

    ScriptEngine scriptEngine = null;
    if (webCoffeeArgumentsRunner.getLang().equals(Lang.JAVASCRIPT)) {
      scriptEngine = new JavaScriptEngine(webCoffeeArgumentsRunner);
    } else {
      throw new RuntimeException(
          webCoffeeArgumentsRunner.getLang() + " is not supported yet");
    }
    return scriptEngine;
  }

  public enum Lang {

    JAVASCRIPT("js");

    private final String script;

    Lang(String script) {
      this.script = script;
    }

    public static Lang getLang(final String lang) {
      if (lang.equals("js")) {
        return Lang.JAVASCRIPT;
      }
      throw new RuntimeException(
          lang + " is not supported yet");
    }

    public String getScript() {
      return script;
    }
  }

}
