package com.techsophy.tsf.workflow.engine.camunda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.techsophy.tsf.workflow.engine.camunda.config.GlobalMessageSource;
import com.techsophy.tsf.workflow.engine.camunda.controller.runtime.impl.FormControllerImpl;
import com.techsophy.tsf.workflow.engine.camunda.dto.FormSchema;
import com.techsophy.tsf.workflow.engine.camunda.exception.GlobalExceptionHandler;
import com.techsophy.tsf.workflow.engine.camunda.service.RuntimeFormService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;
import static com.techsophy.tsf.workflow.engine.camunda.constants.WorkflowEngineConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FormControllerTest
{
    private static  final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRead = jwt().authorities(new SimpleGrantedAuthority("READ"));

    @Mock
    RuntimeFormService runtimeFormService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    WebApplicationContext webApplicationContext;
    @Mock
    GlobalMessageSource globalMessageSource;
    @InjectMocks
    FormControllerImpl formController;

    private static final String FORM_CONTENT_DATA = "testdata/form-content.json";
    private static final WireMockServer GATEWAY_URL=new WireMockServer(6666);

    @BeforeEach public void setUp()
    {
        mockMvc = MockMvcBuilders.standaloneSetup(formController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    public static ResponseDefinitionBuilder json(String apiResponse)
    {
        return aResponse().withHeader("Content-Type",MediaType.APPLICATION_JSON_VALUE).withBody(apiResponse);
    }

    @Test
    void getFormByKeyTest() throws Exception
    {
        InputStream inputStreamTest=new ClassPathResource(FORM_CONTENT).getInputStream();
        ObjectMapper objectMapperTest=new ObjectMapper();
        FormSchema formSchemaTest=objectMapperTest.readValue(inputStreamTest,FormSchema.class);
        Map<String,Object> component = new HashMap<>();
        component.put("key","value");
        FormSchema formSchema =  new FormSchema(NAME,TEST_ID,component,TEST_VERSION);
        Mockito.when(runtimeFormService.fetchFormById(any(),any())).thenReturn(formSchema);
        RequestBuilder requestbuilder =
                MockMvcRequestBuilders.get(BASE_URL + "/forms/{formKey}",1)
                        .content(objectMapperTest.writeValueAsString(formSchemaTest)).content(objectMapperTest.writeValueAsString(formSchema))
                        .with(jwtRead)
                        .contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestbuilder).andExpect(status().isOk()).andReturn();
        assertEquals(200,result.getResponse().getStatus());
    }
}
