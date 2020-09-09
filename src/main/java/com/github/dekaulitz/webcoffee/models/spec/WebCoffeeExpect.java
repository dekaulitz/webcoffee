package com.github.dekaulitz.webcoffee.models.spec;


import com.github.dekaulitz.webcoffee.models.parameters.Parameter;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import okhttp3.Response;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WebCoffeeExpect {

  private int httpCode;
  private List<Parameter> parameters;
  private Map<String, WebCoffeeSchemaValidation> content;
  private Response response;
}
