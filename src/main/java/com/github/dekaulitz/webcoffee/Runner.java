package com.github.dekaulitz.webcoffee;


import com.github.dekaulitz.webcoffee.command.WebCoffeeCommand;
import com.github.dekaulitz.webcoffee.helper.AppConfigHelper;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;
import picocli.CommandLine;

@Log4j2
public class Runner {

  public static void main(String[] args) throws IOException {
    //injecting nashron with es6
    System.setProperty("nashorn.args", "--language=es6");
    //injecting application properties
    AppConfigHelper.getInstance();
    //registering command
    int exitCode = new CommandLine(new WebCoffeeCommand()).execute(args);
    System.exit(exitCode);

  }


}
