
//import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
//import static com.github.tomakehurst.wiremock.client.WireMock.get;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.anyMap;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ActiveProfiles("test")
////@ExtendWith(SnapshotExtension.class)
//@ExtendWith(MockitoExtension.class)
////@TestInstance(TestInstance.Lifecycle.PER_CLASS)
////@RequiredArgsConstructor(onConstructor_ = {@Autowired})
////@SpringBootTest(classes = TestSecurityConfig.class)
////@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class ProcessServiceTest
//{
//    private static final String PROCESS_REQUEST_DATA = "testdata/process-request.json";
//    private static final String CONTACT_DETAILS_DATA = "testdata/contact-details.json";
//    private static final String CUSTOMER_DETAILS_DATA = "testdata/business-details.json";
//    private static final String COMPLETE_TASK_DATA = "testdata/complete-task.json";
//    @Mock
//    private  ObjectMapper objectMapper;
//    @Mock
//    private  RuntimeFormServiceImpl formServiceTest;
//    @InjectMocks
//    private  RuntimeProcessServiceImpl runtimeProcessService;
//    @Mock
//    ProcessInstantiationBuilder processInstantiationBuilder;
//    @Mock
//    RuntimeService runtimeService;
//    @Mock
//    ProcessInstance processInstance;
//    @Mock
//    RuntimeFormService runtimeFormService;
//
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
//        return aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE).withBody(apiResponse);
//    }
//
//
//
//    @Test
//    @Order(1)
//    @Deployment(resources = {"testdata/api-controller.bpmn"})
//    void testStartProcess() throws Exception
//    {
//        try(MockedStatic<OAuth2AndJwtAwareRequestFilter> oAuth2 = Mockito.mockStatic(OAuth2AndJwtAwareRequestFilter.class)) {
//            Map<String,Object> variables = new HashMap<>();
//            variables.put("name","nandini");
//            ProcessInstanceDTO processInstanceDTO = new ProcessInstanceDTO("a",variables,"a","a");
//            FormSchema formSchema = new FormSchema("abc","1",variables,1);
//            oAuth2.when(OAuth2AndJwtAwareRequestFilter::getTenantName).thenReturn("abc");
//            @Cleanup InputStream stream = new ClassPathResource(PROCESS_REQUEST_DATA).getInputStream();
//            doReturn(processInstantiationBuilder).when(runtimeService).createProcessInstanceByKey(anyString());
//            doReturn(processInstantiationBuilder).when(processInstantiationBuilder).processDefinitionTenantId(anyString());
//            doReturn(processInstantiationBuilder).when(processInstantiationBuilder).businessKey(anyString());
//            doReturn(processInstantiationBuilder).when(processInstantiationBuilder).setVariables(anyMap());
//            doReturn(processInstance).when(processInstantiationBuilder).execute();
//            ProcessInstanceDTO process = this.objectMapper.readValue(stream, ProcessInstanceDTO.class);
//            Mockito.when(runtimeFormService.fetchFormById(anyString(),anyString())).thenReturn(formSchema);
//            ProcessInstanceResponseDTO processResponse = runtimeProcessService.createProcessInstance(processInstanceDTO);
//            assertThat(processResponse.getBusinessKey()).isEqualTo(process.getBusinessKey());
//        }
//    }
//
//}
