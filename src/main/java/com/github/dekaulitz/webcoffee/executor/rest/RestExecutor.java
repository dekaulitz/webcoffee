package com.github.dekaulitz.webcoffee.executor.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.dekaulitz.webcoffee.errorHandler.WebCoffeeException;
import com.github.dekaulitz.webcoffee.executor.base.CoffeeExecutor;
import com.github.dekaulitz.webcoffee.helper.JsonMapper;
import com.github.dekaulitz.webcoffee.models.runner.WebCoffeeRunnerEnv;
import com.github.dekaulitz.webcoffee.models.schema.WebCoffeeSchema;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeExpect;
import com.github.dekaulitz.webcoffee.models.spec.WebCoffeeSpecs;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.swagger.v3.oas.models.parameters.Parameter;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;

@Log4j2
public class RestExecutor implements CoffeeExecutor {

  protected final Map<String, Map<String, Object>> extractResBody = new HashMap<>();
  protected String mediaType = "application/json";
  protected RequestBuilder requestBuilder;
  protected String endpoint;
  protected String host;
  protected String method;
  protected String environment;
  protected WebCoffeeSpecs webCoffeeSpecs;
  protected String usecase;

  public void prepare(String usecase, WebCoffeeSpecs webCoffeeSpecs) throws WebCoffeeException {
    this.webCoffeeSpecs = webCoffeeSpecs;
    this.endpoint = webCoffeeSpecs.getGiven().getPathEndpoint();
    this.method = webCoffeeSpecs.getGiven().getHttpMethod();
    this.usecase = usecase;
  }

  @Override
  public void setEnvironment(WebCoffeeRunnerEnv runner) {
    this.host = runner.getHostname();
    this.environment = runner.getEnvironment();
  }

  /**
   * @return
   * @throws WebCoffeeException
   */
  @Override
  public RestExecutor execute() throws WebCoffeeException {
    this.requestBuilder = new RequestBuilder(this.webCoffeeSpecs);
    this.requestBuilder.setMediaType(this.mediaType);
    this.requestBuilder.given()
        .execute(this.method, this.host + endpoint);
    return this;
  }

  @Override
  public void validate() throws WebCoffeeException, JsonProcessingException {
    WebCoffeeExpect expect = this.webCoffeeSpecs.getExpect();
    Response response = this.requestBuilder
        .getValidateResponse().log().all()
        .extract().response();
    assertThat("status code fallen", response.statusCode(), equalTo(expect.getHttpCode()));
    expect.getParameters().stream().filter(Parameter::getRequired)
        .forEach(parameter -> {
          if (parameter.getIn().equals("header")) {
            assertThat("parameter header failing", response.getHeader(parameter.getName()),
                notNullValue());
          }

          if (parameter.getIn().equals("cookie")) {
            assertThat("parameter cookie failing", response.getHeader(parameter.getName()),
                notNullValue());
          }
        });
    assertThat("media type failing", response.contentType(), equalTo(this.mediaType));
    @SuppressWarnings("unchecked")
    WebCoffeeSchema webCoffeeSchemaValidation = expect.getContent().get(this.mediaType)
        .getExpectValue();
    JsonNode nodeResBody = JsonMapper.mapper().readTree(response.getBody().asString());

    if (webCoffeeSchemaValidation != null) {
      @SuppressWarnings("unchecked")
      Map<String, WebCoffeeSchema> webCoffeeSchemaMap = webCoffeeSchemaValidation.getProperties();
      webCoffeeSchemaMap.forEach((s, webCoffeeSchema) -> {
        assertThat("response body expectation failing", nodeResBody.get(s), notNullValue());
        if (webCoffeeSchema.getValue() != "" && webCoffeeSchema.getValue() != null) {
          //@TODO create another helper for validate type
          assertThat("response body expectation failing", nodeResBody.get(s).asText(),
              equalTo(webCoffeeSchema.getValue()));
        }
      });
    }
    this.webCoffeeSpecs.getShared()
        .put(this.usecase, JsonMapper.mapper().convertValue(nodeResBody, Map.class));
    Assert.assertTrue("schema response failing", SchemaValidator
        .validateSchema(this.webCoffeeSpecs.getExpect().getContent().get("application/json")
            .getRawSchema(), response.body().asString()));
  }

  public void restAssured() {
    Response response = RestAssured.given().accept(ContentType.JSON)
        .get(this.getHost() + this.getEndpoint())
        .then()
        .assertThat()
        .statusCode(this.getWebCoffeeSpecs().getExpect().getHttpCode())
        .body(JsonSchemaValidator.matchesJsonSchema(
            this.getWebCoffeeSpecs().getExpect().getContent().get("application/json").getRawSchema()))
        .log().everything();
}
