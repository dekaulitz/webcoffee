package com.github.dekaulitz.webcoffee;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
class WebCoffeeRunnerTest {

  @Test
  void main() {
    String x = "2";
    Assert.assertEquals("test", "2", x);
  }
}
