package com.techsophy.tsf.workflow.engine.camunda.controller;

import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.techsophy.tsf.workflow.engine.camunda.config.TestAuthorizationExtension;
import com.techsophy.tsf.workflow.engine.camunda.config.TestKeycloakServerExtension;
import com.techsophy.tsf.workflow.engine.camunda.config.TestSecurityConfig;
import com.techsophy.tsf.workflow.engine.camunda.dto.FormSchema;
import com.techsophy.tsf.workflow.engine.camunda.service.impl.WrapperServiceImpl;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static au.com.origin.snapshots.SnapshotMatcher.expect;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.BASE_URL;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestSecurityConfig.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ExtendWith(SnapshotExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@ExtendWith({TestKeycloakServerExtension.class, TestAuthorizationExtension.class})
class FormControllerTest
{
    private static final String FORM_CONTENT_DATA = "testdata/form-content.json";

    private final MockMvc mockMvc;
    private final ObjectMapper mapper;
    private final WrapperServiceImpl wrapperService;
    private static final JwtRequestPostProcessor USER1_JWT = jwt().jwt(jwt -> jwt.claim("preferred_username", "user1@test.com"));
    private static final WireMockServer GATEWAY_URL=new WireMockServer(6666);

    @BeforeAll void init() throws IOException {
        GATEWAY_URL.start();
        @Cleanup InputStream contactDetails = new ClassPathResource(FORM_CONTENT_DATA).getInputStream();
        FormSchema form = this.mapper.readValue(contactDetails, FormSchema.class);
        GATEWAY_URL.stubFor(get("/form-runtime/v1/forms/FormKeyTest")
                .willReturn(json(mapper.writeValueAsString(Map.of(
                        "data",form,
                        "success",true,
                        "message","Form details detched successfully")))));
    }

    public static ResponseDefinitionBuilder json(String apiResponse)
    {
        return aResponse().withHeader("Content-Type",MediaType.APPLICATION_JSON_VALUE).withBody(apiResponse);
    }

    @AfterAll
    void destroy(){
        GATEWAY_URL.resetAll();
        GATEWAY_URL.stop();
    }

//    @Test
//    @Order(1)
//    void getFormByKeyTest() throws Exception
//    {
//        RequestBuilder requestbuilder =
//                MockMvcRequestBuilders.get(BASE_URL + "/forms/FormKeyTest?task=null")
//                      .with(USER1_JWT).contentType(MediaType.APPLICATION_JSON);
//        MvcResult result = mockMvc.perform(requestbuilder).andExpect(status().isOk()).andReturn();
//        expect(this.mapper.readValue(result.getResponse().getContentAsByteArray(), Object.class)).orderedJson().toMatchSnapshot();
//    }


}
