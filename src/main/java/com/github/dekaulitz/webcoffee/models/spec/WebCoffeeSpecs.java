package com.github.dekaulitz.webcoffee.models.spec;

import com.github.dekaulitz.webcoffee.models.parameters.Parameter;
import io.swagger.v3.oas.models.Operation;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString(callSuper = true)
public class WebCoffeeSpecs {

  //referencing into open api resources pathItem
  private PathItem pathItem;
  private Set<Parameter> parameters;
  private WebCoffeeSpecsRequestBody requestBody;
  //open api spec operation
  private Operation operation;
  private String httpMethod;

  @Getter
  @Setter
  public static class PathItem {

    private String $ref;

    public String get$ref() {
      return $ref;
    }

    public void set$ref(String $ref) {
      this.$ref = $ref;
    }

  }
}
