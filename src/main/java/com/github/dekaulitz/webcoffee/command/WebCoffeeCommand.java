package com.github.dekaulitz.webcoffee.command;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.dekaulitz.webcoffee.command.WebCoffeeCommand.CoffeeRunner;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.modules.executor.CoffeeFactory;
import com.github.dekaulitz.webcoffee.modules.model.WebCoffee;
import com.github.dekaulitz.webcoffee.modules.model.runner.WebCoffeeTestRequest;
import com.github.dekaulitz.webcoffee.modules.model.runner.WebCoffeeTestRequest.StatusTest;
import com.github.dekaulitz.webcoffee.parser.WebCoffeeParser;
import java.io.File;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import javax.validation.constraints.NotNull;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(version = "0.0.1-ALPHA", mixinStandardHelpOptions = true, subcommands = {
    CoffeeRunner.class})
@Log4j2
public class WebCoffeeCommand {

  @Option(names = {"-log"}, description = "log request and response")
  @NotNull
  public static boolean isLoggingActivity;

  public static WebCoffee executeWebCoffee(String path) throws JsonProcessingException {
    WebCoffee webCoffee = null;
    if (path.contains(".coffee")) {
      try {
        log.info("===starting web coffee===");
        log.info("load file test from {}", path);
        WebCoffeeParser webCoffeeParser = new WebCoffeeParser().loadContent(path);
        webCoffee = webCoffeeParser.getWebCoffee();
        CoffeeFactory coffeeFactory = new CoffeeFactory();
        coffeeFactory.executor(webCoffee.getRunner().getMode())
            .prepare(webCoffee.getRunner())
            .execute();
        return webCoffee;
      } catch (AssertionError assertionError) {
        log.error(assertionError);
        System.exit(0);
      }
    }
    return null;
  }

  public static void getResults(String coffeeTestPath, WebCoffee webCoffee) {
    boolean isSuccess = false;
    AtomicInteger totalUseCase = new AtomicInteger(webCoffee.getRunner().getCoffeeTest().size());
    log.info("==================================");
    log.info("WEB COFFEE STATUS");
    webCoffee.getRunner().getCoffeeTest().entrySet().stream()
        .sorted(Comparator.comparing(o -> o.getValue().getOrder()))
        .forEach(entry -> {
          WebCoffeeTestRequest webCoffeeTestRequest = entry
              .getValue();
          log.info("{} status {} time {}", entry.getKey(), webCoffeeTestRequest.getStatus(),
              webCoffeeTestRequest.getExecutionTime());
          if (webCoffeeTestRequest.getStatus().equals(StatusTest.SUCCESS)) {
            totalUseCase.decrementAndGet();
          }
        });
    if (totalUseCase.get() == 0) {
      isSuccess = true;
    }
    log.info("==================================");
    log.info("webCoffee {} success: {}", coffeeTestPath, isSuccess);

  }

  @Command(name = "coffee-runner", subcommands = {CoffeeRunnerDir.class})
  static class CoffeeRunner implements Runnable {

    @Option(names = {"-f",
        "--coffee-file"}, description = "starting web coffee from file", required = true)
    @NotNull
    private String coffeeFile;

    @SneakyThrows
    @Override
    public void run() {
      try {
        WebCoffee webCoffee = executeWebCoffee(coffeeFile);
        if (webCoffee != null) {
          getResults(coffeeFile, webCoffee);
        }
      } catch (WebCoffeeException | JsonProcessingException e) {
        log.error(e);
      }
    }
  }

  @Command(name = "directory")
  static class CoffeeRunnerDir implements Runnable {

    @Option(names = {"-dir",
        "--directory"}, description = "starting web coffee from directory", required = true)
    @NotNull
    private String directory;

    @SneakyThrows
    @Override
    public void run() {
      System.out.println(isLoggingActivity);
      File files = new File(directory);
      if (files.isDirectory()) {
        for (final File fileEntry : files.listFiles()) {
          WebCoffee webCoffee = executeWebCoffee(fileEntry.getPath());
          if (webCoffee != null) {
            getResults(fileEntry.getPath(), webCoffee);
          }
        }
      } else {
        log.error("{} is not directory", directory);
      }
    }
  }
}
