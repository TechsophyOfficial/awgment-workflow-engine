//package com.techsophy.tsf.workflow.engine.camunda.services;
//
//import au.com.origin.snapshots.junit5.SnapshotExtension;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.github.tomakehurst.wiremock.WireMockServer;
//import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
//import com.techsophy.tsf.workflow.engine.camunda.config.TestSecurityConfig;
//import com.techsophy.tsf.workflow.engine.camunda.dto.FormSchema;
//import com.techsophy.tsf.workflow.engine.camunda.service.impl.RuntimeFormServiceImpl;
//import lombok.AllArgsConstructor;
//import lombok.Cleanup;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Map;
//
//import static au.com.origin.snapshots.SnapshotMatcher.expect;
//import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
//import static com.github.tomakehurst.wiremock.client.WireMock.get;
//import static com.techsophy.tsf.workflow.engine.camunda.constants.FormConstants.FORM_KEY;
//import static com.techsophy.tsf.workflow.engine.camunda.constants.FormConstants.FORM_NAME;
//import static org.assertj.core.api.Assertions.assertThat;
//
//
//@ActiveProfiles("test")
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@ExtendWith(SnapshotExtension.class)
//@AllArgsConstructor(onConstructor_ = {@Autowired})
//@SpringBootTest(classes = TestSecurityConfig.class)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//class RuntimeFormServiceTest
//{
//    private static final String FORM_CONTENT_DATA = "testdata/form-content.json";
//
//    private final RuntimeFormServiceImpl formServiceTest;
////    private final RuntimeFormRepository runtimeForm;
//    private final ObjectMapper objectMapper;
//
//    private static final WireMockServer GATEWAY_URL=new WireMockServer(6666);
//
//    @BeforeAll void init() throws IOException {
//        GATEWAY_URL.start();
//        @Cleanup InputStream contactDetails = new ClassPathResource(FORM_CONTENT_DATA).getInputStream();
//        FormSchema form = this.objectMapper.readValue(contactDetails, FormSchema.class);
//        GATEWAY_URL.stubFor(get("/form-runtime/v1/forms/FormKeyTest")
//                .willReturn(json(objectMapper.writeValueAsString(Map.of(
//                        "data",form,
//                        "success",true,
//                        "message","Form details detched successfully")))));
//    }
//
//    public static ResponseDefinitionBuilder json(String apiResponse)
//    {
//        return aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBody(apiResponse);
//    }
//
//    @AfterAll
//    void destroy(){
//        GATEWAY_URL.resetAll();
//        GATEWAY_URL.stop();
//    }
//
//
//
//    @Test
//    @Order(1)
//    void getFormTest()
//    {
//        FormSchema formDetails = formServiceTest.fetchFormById(FORM_KEY,null);
//        assertThat(formDetails.getId()).isEqualTo(FORM_KEY);
//        assertThat(formDetails.getName()).isEqualTo(FORM_NAME);
//        assertThat(formDetails.getComponents()).isNotNull();
//
//        expect(formDetails).orderedJson().toMatchSnapshot();
//    }
//
//
//
//
//}
