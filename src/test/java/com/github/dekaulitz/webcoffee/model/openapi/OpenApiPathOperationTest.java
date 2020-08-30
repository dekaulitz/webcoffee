package com.github.dekaulitz.webcoffee.model.openapi;


class OpenApiPathOperationTest {
//    private final OpenAPI openAPI;
//
//    OpenApiPathOperationTest() throws IOException {
//        ClassLoader classLoader = getClass().getClassLoader();
//        //using static swagger json for testing
//        File file = new File(classLoader.getResource("cofee.dologin.json").getFile());
//        String defaultTemplate = new String(Files.readAllBytes(file.toPath()));
//        SwaggerParseResult result = new OpenAPIParser().readContents(defaultTemplate, null, null);
//        this.openAPI = result.getOpenAPI();
//    }
//
//    @Test
//    void getPath() {
//        String path = "#/path/v1/auth/dolaogin#post";
//        OpenApiPathOperation openApiPathOperation = new OpenApiPathOperation();
//        try {
//            openApiPathOperation.setOpenApiPath(this.openAPI, path);
//        } catch (Exception e) {
//            Assert.assertNotNull(e);
//        }
//        path = "/v1/auth/dologin#post";
//        openApiPathOperation = new OpenApiPathOperation();
//        try {
//            openApiPathOperation.setOpenApiPath(this.openAPI, path);
//            Assert.assertNotNull(openApiPathOperation.getItemOperation().getPathItem());
//            Assert.assertEquals("post", openApiPathOperation.getItemOperation().getMethod());
//            Assert.assertEquals("/v1/auth/dologin", openApiPathOperation.getItemOperation().getPath());
//        } catch (Exception e) {
//            Assert.assertNull(e);
//        }
//    }
//
//    @Test
//    void getServerEnv() throws InvalidWebCofeeComponent {
//        String env = "server#0";
//        OpenApiServersComponent serversComponent = new OpenApiServersComponent();
//        serversComponent.setOpenApiPath(this.openAPI, env);
//        Assert.assertEquals(serversComponent.getComponent().getHostname(), "http://localhost:7070/mocks/mocking/123123?path=");
//        env = "server#1";
//        serversComponent = new OpenApiServersComponent();
//        serversComponent.setOpenApiPath(this.openAPI, env);
//        Assert.assertEquals(serversComponent.getComponent().getHostname(), "http://localhost:7070");
//        Assert.assertNotNull(serversComponent.getComponent().getServer());
//    }
//
//    @Test
//    void getSchema() throws InvalidWebCofeeComponent {
//        String schema = "#/components/schemas/GENERATE_OTP";
//        OpenApiSchema openApiSchema = new OpenApiSchema();
//        openApiSchema.setOpenApiPath(this.openAPI, schema);
//        Assert.assertNotNull( openApiSchema.getSchema().getProperties().get("phoneNumber"));
//    }
}
