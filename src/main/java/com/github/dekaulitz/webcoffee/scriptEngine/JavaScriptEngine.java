package com.github.dekaulitz.webcoffee.scriptEngine;

import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeArgumentsRunner;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.script.Invocable;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JavaScriptEngine implements ScriptEngine {

  private final WebCoffeeArgumentsRunner argument;

  public JavaScriptEngine(
      WebCoffeeArgumentsRunner argument) {
    this.argument = argument;
  }

  @Override
  public Object execute() {
    try {
      javax.script.ScriptEngine engine = new ScriptEngineManager().getEngineByName("Nashorn");
      engine.eval(new FileReader(argument.getSrc()));
      Invocable invocable = (Invocable) engine;
      return invocable.invokeFunction(argument.getArgumentName());
    } catch (ScriptException | FileNotFoundException | NoSuchMethodException e) {
      log.warn("no such argument {} on {}", e.getMessage(), argument.getSrc());
      return null;
    }

  }
}
