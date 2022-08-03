//package com.techsophy.tsf.workflow.engine.camunda.controller;
//
//import au.com.origin.snapshots.junit5.SnapshotExtension;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.type.TypeFactory;
//import com.github.tomakehurst.wiremock.WireMockServer;
//import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
//import com.techsophy.tsf.workflow.engine.camunda.config.TestAuthorizationExtension;
//import com.techsophy.tsf.workflow.engine.camunda.config.TestKeycloakServerExtension;
//import com.techsophy.tsf.workflow.engine.camunda.config.TestSecurityConfig;
//import com.techsophy.tsf.workflow.engine.camunda.dto.*;
//import com.techsophy.tsf.workflow.engine.camunda.model.ApiResponse;
//import com.techsophy.tsf.workflow.engine.camunda.service.RuntimeProcessService;
//import lombok.Cleanup;
//import lombok.RequiredArgsConstructor;
//import org.camunda.bpm.engine.test.Deployment;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.RequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.util.Map;
//
//import static au.com.origin.snapshots.SnapshotMatcher.expect;
//import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
//import static com.github.tomakehurst.wiremock.client.WireMock.get;
//import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.BASE_URL;
//import static com.techsophy.tsf.workflow.engine.camunda.constants.FormConstants.BUSINESS_KEY;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@ExtendWith(SnapshotExtension.class)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@SpringBootTest(classes = TestSecurityConfig.class)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@RequiredArgsConstructor(onConstructor_ = {@Autowired})
//@ExtendWith({TestKeycloakServerExtension.class, TestAuthorizationExtension.class})
//class ProcessControllerTest
//{
//    private static final String COMPLETE_TASK_DATA = "testdata/complete-task.json";
//    private static final String PROCESS_REQUEST_DATA = "testdata/process-request.json";
//    private static final String PROCESS_VARIABLES_DATA = "testdata/processvariables.json";
//    private static final String CONTACT_DETAILS_DATA = "testdata/contact-details.json";
//    private static final String CUSTOMER_DETAILS_DATA = "testdata/business-details.json";
//
//    private static final TypeFactory TYPE_FACTORY = TypeFactory.defaultInstance();
//    private static final JwtRequestPostProcessor USER1_JWT = jwt().jwt(jwt -> jwt.claim("preferred_username", "user1@test.com"));
//
//    private final MockMvc mockMvc;
//    private final ObjectMapper objectMapper;
//    private final RuntimeProcessService runtimeProcessService;
//    private static final WireMockServer GATEWAY_URL=new WireMockServer(6666);
//
//    @BeforeAll
//    void init() throws IOException {
//        GATEWAY_URL.start();
//        @Cleanup InputStream contactDetails = new ClassPathResource(CONTACT_DETAILS_DATA).getInputStream();
//        FormSchema form = this.objectMapper.readValue(contactDetails, FormSchema.class);
//        GATEWAY_URL.stubFor(get("/form-runtime/v1/forms/contactDetails")
//                .willReturn(json(objectMapper.writeValueAsString(Map.of(
//                        "data",form,
//                        "success",true,
//                        "message","Form details detched successfully")))));
//        @Cleanup InputStream businessDetails = new ClassPathResource(CUSTOMER_DETAILS_DATA).getInputStream();
//        FormSchema businsessForm = this.objectMapper.readValue(businessDetails, FormSchema.class);
//        GATEWAY_URL.stubFor(get("/form-runtime/v1/forms/businessDetails")
//                .willReturn(json(objectMapper.writeValueAsString(Map.of(
//                        "data",businsessForm,
//                        "success",true,
//                        "message","Form details detched successfully")))));
//    }
//
//    @AfterAll
//    void destroy(){
//        GATEWAY_URL.resetAll();
//        GATEWAY_URL.stop();
//    }
//
//    public static ResponseDefinitionBuilder json(String apiResponse)
//    {
//        return aResponse().withHeader("Content-Type",MediaType.APPLICATION_JSON_VALUE).withBody(apiResponse);
//    }
//
//    @Test
//    @Order(1)
//    @Deployment(resources = {"testdata/api-controller.bpmn"})
//    void testStartProcess() throws Exception
//    {
//        @Cleanup InputStream stream = new ClassPathResource(PROCESS_REQUEST_DATA).getInputStream();
//        ProcessInstanceDTO process = this.objectMapper.readValue(stream, ProcessInstanceDTO.class);
//
//        RequestBuilder requestbuilder = MockMvcRequestBuilders.post(BASE_URL + "/process-instance/start")
//                .with(USER1_JWT).content(this.objectMapper.writeValueAsBytes(process)).contentType(MediaType.APPLICATION_JSON);
//
//        MvcResult result = mockMvc.perform(requestbuilder).andExpect(status().isOk()).andReturn();
//
//        ProcessInstanceResponseDTO processResponse = this.getResponse(result, ProcessInstanceResponseDTO.class).getData();
//
//        assertThat(processResponse.getBusinessKey()).isEqualTo(process.getBusinessKey());
//        FormSchemaResponse task =runtimeProcessService.getNextTask(process.getBusinessKey());
//        CommentDTO commentDTO=new CommentDTO(task.getTaskId(),processResponse.getProcessInstanceId(),"Hello");
//
//        RequestBuilder requestbuilderComments = MockMvcRequestBuilders.post(BASE_URL + "/comment/create")
//                .with(USER1_JWT).content(this.objectMapper.writeValueAsBytes(commentDTO)).contentType(MediaType.APPLICATION_JSON);
//        mockMvc.perform(requestbuilderComments).andExpect(status().isOk()).andReturn();
//
//        RequestBuilder requestbuilderGetComments = MockMvcRequestBuilders.get(BASE_URL + "/comment").queryParam(
//                "businessKey",process.getBusinessKey())
//                .with(USER1_JWT).content(this.objectMapper.writeValueAsBytes(commentDTO)).contentType(MediaType.APPLICATION_JSON);
//        mockMvc.perform(requestbuilderGetComments).andExpect(status().isOk()).andReturn();
//
//    }
//
//    private <T> ApiResponse<T> getResponse(MvcResult result, Class<T> type) throws UnsupportedEncodingException, JsonProcessingException
//    {
//        return this.objectMapper.readValue(result.getResponse().getContentAsString(), TYPE_FACTORY.constructParametricType(ApiResponse.class, type));
//    }
//
//    @Test
//    @Order(2)
//    void testNextTask() throws Exception
//    {
//
//
//        RequestBuilder requestbuilder =
//                MockMvcRequestBuilders.get(BASE_URL + "/task/next-task")
//                        .queryParam("businessKey", BUSINESS_KEY)
//                        .with(USER1_JWT).contentType(MediaType.APPLICATION_JSON);
//
//        MvcResult result = mockMvc.perform(requestbuilder).andExpect(status().isOk()).andReturn();
//        ApiResponse<?> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), ApiResponse.class);
//        FormSchemaResponse form = this.objectMapper.readValue(this.objectMapper.writeValueAsBytes(response.getData()), FormSchemaResponse.class);
//
//        expect(form.getFormContent(), form.getVariables(), form.getTaskName(), form.getFormKey()).orderedJson().toMatchSnapshot();
//    }
//
//    @Test
//    @Order(4)
//    void testExistingProcessInstanceTask() throws Exception
//    {
//        @Cleanup InputStream stream = new ClassPathResource(PROCESS_REQUEST_DATA).getInputStream();
//
//        RequestBuilder requestbuilder = MockMvcRequestBuilders.post(BASE_URL + "/process-instance")
//                .with(USER1_JWT).content(new String(stream.readAllBytes())).contentType(MediaType.APPLICATION_JSON);
//
//        MvcResult result = mockMvc.perform(requestbuilder).andExpect(status().isOk()).andReturn();
//
//        ApiResponse<?> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), ApiResponse.class);
//        ProcessInstanceResponseDTO processResponse = this.objectMapper.readValue(this.objectMapper.writeValueAsBytes(response.getData()), ProcessInstanceResponseDTO.class);
//
//        assertThat(processResponse.getAlreadyExists()).isTrue();
//    }
//
//    @Test
//    @Order(5)
//    void testCompleteTask() throws Exception
//    {
//        @Cleanup InputStream stream = new ClassPathResource(COMPLETE_TASK_DATA).getInputStream();
//
//        RequestBuilder requestbuilder =
//                MockMvcRequestBuilders.post(BASE_URL + "/task/next-task")
//                        .with(USER1_JWT).content(new String(stream.readAllBytes())).contentType(MediaType.APPLICATION_JSON);
//
//        MvcResult result = mockMvc.perform(requestbuilder).andExpect(status().isOk()).andReturn();
//        ApiResponse<?> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), ApiResponse.class);
//        FormSchemaResponse form = this.objectMapper.readValue(this.objectMapper.writeValueAsBytes(response.getData()), FormSchemaResponse.class);
//
//        expect(form.getFormContent(), form.getVariables(), form.getTaskName(), form.getFormKey()).toMatchSnapshot();
//    }
//
//    @Test
//    @Order(6)
//    void testUpdateProcessVariables() throws Exception
//    {
//        @Cleanup InputStream stream = new ClassPathResource(PROCESS_VARIABLES_DATA).getInputStream();
//
//        RequestBuilder requestbuilder =
//                MockMvcRequestBuilders.post(BASE_URL + "/task/variables")
//                        .with(USER1_JWT).content(new String(stream.readAllBytes())).contentType(MediaType.APPLICATION_JSON);
//
//        MvcResult result = mockMvc.perform(requestbuilder).andExpect(status().isOk()).andReturn();
//        ApiResponse<?> response = this.objectMapper.readValue(result.getResponse().getContentAsString(), ApiResponse.class);
//
//        assertThat(response.getSuccess()).isTrue();
//    }
//
//    @Test
//    @Order(7)
//    void testCompleteLastTask() throws Exception
//    {
//        @Cleanup InputStream stream = new ClassPathResource(COMPLETE_TASK_DATA).getInputStream();
//
//        RequestBuilder requestbuilder =
//                MockMvcRequestBuilders.post(BASE_URL + "/task/next-task")
//                        .with(USER1_JWT).content(new String(stream.readAllBytes())).contentType(MediaType.APPLICATION_JSON);
//
//        MvcResult result = mockMvc.perform(requestbuilder).andExpect(status().isOk()).andReturn();
//
//        expect(this.objectMapper.readValue(result.getResponse().getContentAsByteArray(), Object.class)).orderedJson().toMatchSnapshot();
//    }
//
////    @AfterAll
////    void cleanup()
////    {
////        assertThat(formRepository.findAll()).hasSize(2);
////
////        formService.deleteForm("contactDetails");
////
////        assertThat(formRepository.findAll()).hasSize(1);
////
////        formService.deleteForm("businessDetails");
////
////        assertThat(formRepository.findAll()).isEmpty();
////    }
//}
