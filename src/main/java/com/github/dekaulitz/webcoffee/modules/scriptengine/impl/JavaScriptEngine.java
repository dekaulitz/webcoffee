package com.github.dekaulitz.webcoffee.modules.scriptengine.impl;


import com.github.dekaulitz.webcoffee.modules.model.runner.WebCoffeeArgumentsRunner;
import com.github.dekaulitz.webcoffee.modules.scriptengine.base.ScriptEngine;
import java.io.FileNotFoundException;
import java.io.FileReader;
import javax.script.Invocable;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
