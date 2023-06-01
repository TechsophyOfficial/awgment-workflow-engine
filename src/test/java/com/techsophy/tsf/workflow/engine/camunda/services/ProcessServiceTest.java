package com.techsophy.tsf.workflow.engine.camunda.services;

import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.techsophy.tsf.workflow.engine.camunda.config.TestSecurityConfig;
import com.techsophy.tsf.workflow.engine.camunda.dto.FormSchema;
import com.techsophy.tsf.workflow.engine.camunda.dto.ProcessInstanceDTO;
import com.techsophy.tsf.workflow.engine.camunda.dto.ProcessInstanceResponseDTO;
import com.techsophy.tsf.workflow.engine.camunda.service.impl.RuntimeFormServiceImpl;
import com.techsophy.tsf.workflow.engine.camunda.service.impl.RuntimeProcessServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import org.camunda.bpm.engine.test.Deployment;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ExtendWith(SnapshotExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AllArgsConstructor(onConstructor_ = {@Autowired})
@SpringBootTest(classes = TestSecurityConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProcessServiceTest
{
    private static final String PROCESS_REQUEST_DATA = "testdata/process-request.json";
    private static final String CONTACT_DETAILS_DATA = "testdata/contact-details.json";
    private static final String CUSTOMER_DETAILS_DATA = "testdata/business-details.json";
    private static final String COMPLETE_TASK_DATA = "testdata/complete-task.json";

    private final ObjectMapper objectMapper;
    private final RuntimeFormServiceImpl formServiceTest;
    private final RuntimeProcessServiceImpl runtimeProcessService;
    private static final WireMockServer GATEWAY_URL=new WireMockServer(6666);

    @BeforeAll
    void init() throws IOException {
        GATEWAY_URL.start();
        @Cleanup InputStream contactDetails = new ClassPathResource(CONTACT_DETAILS_DATA).getInputStream();
        FormSchema form = this.objectMapper.readValue(contactDetails, FormSchema.class);
        GATEWAY_URL.stubFor(get("/form-runtime/v1/forms/contactDetails")
                .willReturn(json(objectMapper.writeValueAsString(Map.of(
                        "data",form,
                        "success",true,
                        "message","Form details detched successfully")))));
        @Cleanup InputStream businessDetails = new ClassPathResource(CUSTOMER_DETAILS_DATA).getInputStream();
        FormSchema businsessForm = this.objectMapper.readValue(businessDetails, FormSchema.class);
        GATEWAY_URL.stubFor(get("/form-runtime/v1/forms/businessDetails")
                .willReturn(json(objectMapper.writeValueAsString(Map.of(
                        "data",businsessForm,
                        "success",true,
                        "message","Form details detched successfully")))));
    }

    @AfterAll
    void destroy(){
        GATEWAY_URL.resetAll();
        GATEWAY_URL.stop();
    }

    public static ResponseDefinitionBuilder json(String apiResponse)
    {
        return aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBody(apiResponse);
    }



    @Test
    @Order(1)
    @Deployment(resources = {"testdata/api-controller.bpmn"})
    void testStartProcess() throws Exception
    {
        @Cleanup InputStream stream = new ClassPathResource(PROCESS_REQUEST_DATA).getInputStream();
        ProcessInstanceDTO process = this.objectMapper.readValue(stream, ProcessInstanceDTO.class);
        ProcessInstanceResponseDTO processResponse = runtimeProcessService.createProcessInstance(process);

        assertThat(processResponse.getBusinessKey()).isEqualTo(process.getBusinessKey());
    }

//    @Test
//    @Order(2)
//    void testNextTask()
//    {
//        FormSchemaResponse form = runtimeProcessService.getNextTask(BUSINESS_KEY);
//
//        expect(form.getFormContent(), form.getVariables(), form.getTaskName(), form.getFormKey()).orderedJson().toMatchSnapshot();
//    }
//
//    @Test
//    @Order(2)
//    void testCompleteTask() throws Exception
//    {
//        @Cleanup InputStream stream = new ClassPathResource(COMPLETE_TASK_DATA).getInputStream();
//        TaskDTO task = this.objectMapper.readValue(stream, TaskDTO.class);
//        FormSchemaResponse form = runtimeProcessService.completeAndFetchNextTask(task);
//
//        expect(form.getFormContent(), form.getVariables(), form.getTaskName(), form.getFormKey()).orderedJson().toMatchSnapshot();
//    }

//    @Test
//    @Order(3)
//    void testCompleteLastTask() throws Exception
//    {
//        @Cleanup InputStream stream = new ClassPathResource(COMPLETE_TASK_DATA).getInputStream();
//        TaskDTO task = this.objectMapper.readValue(stream, TaskDTO.class);
//        FormSchemaResponse form = runtimeProcessService.completeAndFetchNextTask(task);
//
//        assertThat(form).isNull();
//    }



}
