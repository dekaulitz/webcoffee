package com.github.dekaulitz.webcoffee.executor.rest.v2;

import com.github.dekaulitz.webcoffee.models.parameters.Parameter;
import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeArgumentsRunner;
import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeDoRequest;
import com.github.dekaulitz.webcoffee.models.schema.WebCoffeeSchema;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeSpecsRequestBodyContent;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class RequestBuilder {

  public RequestSpecification requestSpecification = RestAssured.given();
  @Setter
  private String host;
  @Setter
  private Map<String, WebCoffeeArgumentsRunner> globalArguments;
  @Setter
  private Map<String, WebCoffeeArgumentsRunner> arguments;
  private String endpoint;
  private String httpMethod;
  private Set<Parameter> parameters;
  private WebCoffeeSpecsRequestBodyContent requestBody;
  @Getter
  private ValidatableResponse validateResponse;

  public static RequestBuilder initRequest(WebCoffeeDoRequest webCoffeeDoRequest) {
    RequestBuilder requestBuilder = new RequestBuilder();
    requestBuilder.endpoint = webCoffeeDoRequest.getReferenceSpec().getEndpoint();
    requestBuilder.httpMethod = webCoffeeDoRequest.getReferenceSpec().getHttpMethod();
    requestBuilder.parameters = webCoffeeDoRequest.getReferenceSpec().getParameters();
    requestBuilder.requestBody = webCoffeeDoRequest.getReferenceSpec()
        .getRequestBody();
    return requestBuilder;
  }

  public void createRequest() {
    requestSpecification.given().log().all();
    if (parameters.size() != 0) {
      parameters.forEach(parameter -> {
        String arguments = "";
        if (!StringUtils.isBlank(parameter.getValue())) {
          arguments = parameter.getValue();
        } else {
          if (StringUtils.isNoneEmpty(parameter.getArgument())) {
            arguments = (String) this.arguments.get(parameter.getArgument()).getValue();
          }
        }
        if (parameter.getIn().equals("headers")) {
          requestSpecification.header(parameter.getName(), arguments);
        } else if (parameter.getIn().equals("query")) {
          requestSpecification.queryParam(parameter.getName(), arguments);
        } else if (parameter.getIn().equals("path")) {
          requestSpecification.pathParam(parameter.getName(), arguments);
        }
      });
    }
    if (requestBody != null) {
      Map<String, Object> body = new HashMap<>();
      Map<String, WebCoffeeSchema> payload = requestBody.getPayload().getProperties();
      for (Map.Entry<String, WebCoffeeSchema> entry : payload.entrySet()) {
        Object value = "";
        if (entry.getValue().getValue() != null) {
          value = entry.getValue().getValue();
        } else {
          if (entry.getValue().getArgument() != null) {
            value = this.arguments.get(entry.getValue().getArgument()).getValue();
          }
        }
        body.put(entry.getKey(), value);
      }
      this.requestSpecification.body(body);
    }
  }

  public RequestBuilder execute() {
    requestSpecification.when();
    switch (this.httpMethod) {
      case "get":
        this.validateResponse = requestSpecification.get(this.host+this.endpoint).then();
        break;
      case "post":
        this.validateResponse = requestSpecification.post(this.host+this.endpoint).then();
        break;
      case "put":
        this.validateResponse = requestSpecification.put(this.host+this.endpoint).then();
        break;
      case "options":
        this.validateResponse = requestSpecification.options(this.host+this.endpoint).then();
        break;
      case "patch":
        this.validateResponse = requestSpecification.patch(this.host+this.endpoint).then();
        break;
      case "delete":
        this.validateResponse = requestSpecification.delete(this.host+this.endpoint).then();
        break;
      case "head":
        this.validateResponse = requestSpecification.head(this.host+this.endpoint).then();
        break;
    }
    this.validateResponse.log().all();
    return this;
  }
}