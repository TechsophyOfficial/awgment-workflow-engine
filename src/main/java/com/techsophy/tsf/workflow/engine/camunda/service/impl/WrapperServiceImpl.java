package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.dto.*;
import com.techsophy.tsf.workflow.engine.camunda.service.TokenSupplier;
import com.techsophy.tsf.workflow.engine.camunda.service.WrapperService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.Query;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.camunda.bpm.model.cmmn.CmmnModelInstance;
import org.camunda.bpm.model.cmmn.instance.PlanItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CheckListConstants.CHECKLIST_INSTANCE_ID;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CheckListConstants.DOCUMENT_ID;
import static com.techsophy.tsf.workflow.engine.camunda.utils.CommonUtils.isValidString;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class WrapperServiceImpl implements WrapperService {

  public static final String END_WRAPPER_SERVICE_LAYER = "END: Wrapper service layer";
  public static final String USERTASK_ID_TASKDEFKEY = "usertaskID: {},taskdefkey: {}";
  public static final String REST_CALL_WITH_URI = "Rest call with uri {}";
  public static final String START_WRAPPER_SERVICE_LAYER = "START: Wrapper Service layer";
  public static final String CAMUNDA_VALUE = "camundaValue";
  @Value(GATEWAY_URI)
    private String gatewayUrl;

    private final RestTemplate restTemplate;
    private final TokenSupplier tokenSupplier;
    private final RuntimeService runtimeService;
    private final ObjectMapper objectMapper;
    private final TaskService taskService;
    private final RepositoryService repositoryService;
    private final RuntimeFormServiceImpl runtimeFormService;

    private final HistoryService historyService;

    @Override
    @SneakyThrows
    public ResponseEntity<Resource> handleRequest(MultiValueMap<String, String> params, RequestEntity<?> request,
                                                  String appName) {
        log.info(START_WRAPPER_SERVICE_LAYER);

        HttpHeaders httpHeaders = HttpHeaders.writableHttpHeaders(request.getHeaders());
        httpHeaders.setBearerAuth(this.tokenSupplier.getToken());
        httpHeaders.set(HttpHeaders.ACCEPT_ENCODING, "*");

        LinkedMultiValueMap<String, String> restParams = new LinkedMultiValueMap<>();
        if (DMS.equals(appName)) {
            String fileName = params.getFirst(FORM);
            LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            if (HttpMethod.POST.equals(request.getMethod())) {

                if (params.containsKey(DOCUMENT_PATH)) {
                    body.add(DOCUMENT_PATH, params.getFirst(DOCUMENT_PATH));
                } else paramsContainingProcessInstanceId(params, body);
              restParams.add(DOCUMENT_TYPE_ID, params.getFirst(DOCUMENT_TYPE_ID));
              Optional.of( request.getBody()).ifPresent(map->
                body.addAll((MultiValueMap<String, Object>) map));
              if (params.containsKey(NAME)) {
                    body.add(DOCUMENT_NAME, removeGUIDFromFilename(String.valueOf(params.getFirst(NAME))));
                }
                log.info("Body from the wrapper api {}", body);
            }
            else requestMethodGet(params, request, restParams, fileName);

          log.info("Request params from the wrapper api {}", restParams);
          return getResponseEntityWithGet(request, appName, httpHeaders, restParams, body);
        } else return getResourceResponseEntity(request, appName, httpHeaders, restParams);

    }

  private void paramsContainingProcessInstanceId(MultiValueMap<String, String> params, LinkedMultiValueMap<String, Object> body) {
    String businessKey;
    if (params.containsKey(PROCESS_INSTANCE_ID)) {
        ProcessInstance processInstance =
                runtimeService.createProcessInstanceQuery().processInstanceId(params.getFirst(PROCESS_INSTANCE_ID)).active().singleResult();
        log.info("process instance {}", processInstance);
        if (processInstance != null) {
            businessKey = processInstance.getBusinessKey();
            body.add(DOCUMENT_PATH, businessKey);
        }
    }
  }

  private void requestMethodGet(MultiValueMap<String, String> params, RequestEntity<?> request, LinkedMultiValueMap<String, String> restParams, String fileName) {
    if (HttpMethod.GET.equals(request.getMethod())) {
      checkPathAndInstance(params, restParams, fileName);
    }
  }

  private ResponseEntity<Resource> getResponseEntityWithGet(RequestEntity<?> request, String appName, HttpHeaders httpHeaders, LinkedMultiValueMap<String, String> restParams, LinkedMultiValueMap<String, Object> body) throws URISyntaxException {
    ResponseEntity<byte[]> response;
    URI finalUri =
            UriComponentsBuilder.fromUri(new URI(this.getAppUrl(appName) + DMS_UPLOAD_FILE))
                    .queryParams(restParams).build().toUri();
    log.info(REST_CALL_WITH_URI, finalUri);

    response = restTemplate.exchange(new RequestEntity<>(body, httpHeaders, request.getMethod(),
            finalUri, request.getType()), byte[].class);
    HttpHeaders responseHeaders = HttpHeaders.writableHttpHeaders(response.getHeaders());
    responseHeaders.remove(HttpHeaders.SET_COOKIE);
    log.info(END_WRAPPER_SERVICE_LAYER);
    InputStream inputStream = new ByteArrayInputStream(response.getBody());
    InputStreamResource resource = new InputStreamResource(inputStream);
    return ResponseEntity.ok()
      .headers(responseHeaders)
      .body(resource);
  }

  private ResponseEntity<Resource> getResourceResponseEntity(RequestEntity<?> request, String appName, HttpHeaders httpHeaders, LinkedMultiValueMap<String, String> restParams) throws URISyntaxException {
    ResponseEntity<byte[]> response;
    if (FORM_RUNTIME.equals(appName)) {
        LinkedHashMap<String, Object> body = new LinkedHashMap<>();
        body.putAll((HashMap<String, Object>) request.getBody());
        if (body.containsKey(TASK_ID)) {
            String version = getUserTaskExtensionByName(String.valueOf(body.get(TASK_ID)), FORM_VERSION);
            body.put(FORM_VERSION, version);
        }
        URI finalUri =
                UriComponentsBuilder.fromUri(new URI(this.getAppUrl(appName) + "/form-runtime/v1/form-data"))
                        .build().toUri();
        log.info(REST_CALL_WITH_URI, finalUri);

        response = restTemplate.exchange(new RequestEntity<>(body, httpHeaders, request.getMethod(),
                finalUri, request.getType()), byte[].class);
        HttpHeaders responseHeaders = HttpHeaders.writableHttpHeaders(response.getHeaders());
        responseHeaders.remove(HttpHeaders.SET_COOKIE);
        log.info(END_WRAPPER_SERVICE_LAYER);
        InputStream inputStream = new ByteArrayInputStream(response.getBody());
        InputStreamResource resource = new InputStreamResource(inputStream);
      return ResponseEntity.ok()
        .headers(responseHeaders)
        .body(resource);

    } else {
        RequestDataDTO requestDataDTO = objectMapper.convertValue(request.getBody(), RequestDataDTO.class);
        HttpMethod method = HttpMethod.valueOf(requestDataDTO.getMethod().toUpperCase());
        if (requestDataDTO.getRequestParams() != null) {
            Map<String, String> maps = objectMapper.convertValue(requestDataDTO.getRequestParams(), new TypeReference<Map<String, String>>() {
            });
            restParams.setAll(maps);

        }
        URI finalUri =
                UriComponentsBuilder.fromUri(new URI(this.gatewayUrl + requestDataDTO.getUrl()))
                        .queryParams(restParams)
                        .build().toUri();
        log.info(REST_CALL_WITH_URI, finalUri);
        response = restTemplate.exchange(new RequestEntity<>(requestDataDTO.getPayload(), httpHeaders, method,
                finalUri, request.getType()), byte[].class);
        HttpHeaders responseHeaders = HttpHeaders.writableHttpHeaders(response.getHeaders());
        responseHeaders.remove(HttpHeaders.SET_COOKIE);
        log.info(END_WRAPPER_SERVICE_LAYER);
        InputStream inputStream = new ByteArrayInputStream(response.getBody());
        InputStreamResource resource = new InputStreamResource(inputStream);
      return ResponseEntity.ok()
        .headers(responseHeaders)
        .body(resource);

    }
  }

  private void checkPathAndInstance(MultiValueMap<String, String> params, LinkedMultiValueMap<String, String> restParams, String fileName) {
    String businessKey;
    if (!StringUtils.isBlank(fileName) && !StringUtils.isEmpty(fileName)) {
        if (params.containsKey(DOCUMENT_PATH)) {
            restParams.add(DOCUMENT_PATH, params.getFirst(DOCUMENT_PATH));
        } else if (params.containsKey(PROCESS_INSTANCE_ID)) {
            ProcessInstance processInstance =
                    runtimeService.createProcessInstanceQuery().processInstanceId(params.getFirst(PROCESS_INSTANCE_ID)).active().singleResult();
            log.info("process instance {}", processInstance);
            if (processInstance != null) {
                businessKey = processInstance.getBusinessKey();
                restParams.add(DOCUMENT_PATH, businessKey);
            }
        }
        restParams.set(FORM, removeGUIDFromFilename(fileName));
    }
  }

  public RequestEntity<MultiValueMap<String, Resource>> getMultipartRequestEntity(MultipartHttpServletRequest servletRequest) {
        log.info("START: Conversion of MultipartHttpServletRequest to RequestEntity");
        MultiValueMap<String, Resource> multiPartPayload = new LinkedMultiValueMap<>();
        ServletServerHttpRequest request = new ServletServerHttpRequest(servletRequest);
        servletRequest.getMultiFileMap().forEach((s, files) -> multiPartPayload.put(s, getUploadedFiles(files)));
        URI uri = UriComponentsBuilder.fromUri(request.getURI()).build(Map.of());
        log.info("END: Conversion of MultipartHttpServletRequest to RequestEntity");
        return new RequestEntity<>(multiPartPayload, request.getHeaders(), request.getMethod(), uri);
    }

    private static List<Resource> getUploadedFiles(List<MultipartFile> files) {
        return files.stream().map(MultipartFile::getResource).collect(Collectors.toList());
    }

    public String getAppUrl(String appName) {
        String finalUrl = "";
        if (DMS.equals(appName) || FORM_RUNTIME.equals(appName)) {
            finalUrl = gatewayUrl;
        } else {
            log.info("Invalid App Name {}, executing the default URL", appName);
        }
        return finalUrl;
    }

    private static String removeGUIDFromFilename(String fileName) {
        log.info("START: Remove GUID from file name {}", fileName);
        if (String.valueOf(fileName.charAt(0)).equals("/")) {
            fileName = fileName.substring(1);
        }
        int guidCount = 36;
        int periodIndex = fileName.lastIndexOf(".");
        String fileNameWithoutGUID = fileName.substring(0, periodIndex - guidCount - 1) + fileName.substring(periodIndex);

        log.info("END: Remove GUID from file name {}", fileNameWithoutGUID);
        return fileNameWithoutGUID;
    }


    public String getUserTaskExtensionByName(String taskId, String key) {
        if (taskId == null) {
            return LATEST;
        }
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Collection<UserTask> userTaskDefs;
        if (task.getProcessDefinitionId() == null) {
            CmmnModelInstance cmmnModelInstance = repositoryService.getCmmnModelInstance(task.getCaseDefinitionId());
            var humanTasks = cmmnModelInstance.getModelElementsByType(PlanItem.class);
            for(PlanItem humanTaskDef : humanTasks){
              log.info(USERTASK_ID_TASKDEFKEY, humanTaskDef, task.getTaskDefinitionKey());
              String latest = compareUserTaskIdWithTaskDefinitionKey(key, task, humanTaskDef);
              if (latest != null) {
                return latest;
              }
            }
        }
        else {
            BpmnModelInstance bpmnModelInstance =
                    repositoryService.getBpmnModelInstance(task.getProcessDefinitionId());
            userTaskDefs = bpmnModelInstance.getModelElementsByType(UserTask.class);
            for (UserTask userTaskDef : userTaskDefs) {
                log.info(USERTASK_ID_TASKDEFKEY, userTaskDef, task.getTaskDefinitionKey());
              String latest = compareUserTaskIdWithTaskDefinition(key, task, userTaskDef);
              if (latest != null) return latest;
            }
        }

        return LATEST;
    }

  private static String compareUserTaskIdWithTaskDefinition(String key, Task task, UserTask userTaskDef) {
    if (userTaskDef.getId().equals(task.getTaskDefinitionKey())) {
      String latest = getLatestOrCamundavalue(key, userTaskDef);
      if (latest != null)
      {
        return latest;
      }
    }
    return null;
  }

  private static String getLatestOrCamundavalue(String key, UserTask userTaskDef) {
    if (userTaskDef.getExtensionElements() == null) {
      return LATEST;
    }
    CamundaProperties extensionsProperty = userTaskDef.getExtensionElements()//
            .getElementsQuery()//
            .filterByType(CamundaProperties.class)//
            .singleResult();
    var camundaProperties = extensionsProperty.getCamundaProperties();
    Map<String,String> camundaValue = new HashMap<>();
    camundaProperties.stream().filter(property ->property.getCamundaName().equals(key)).filter(Objects::nonNull).findFirst().ifPresent(property ->
      camundaValue.put(CAMUNDA_VALUE,property.getCamundaValue()));
    return camundaValue.get(CAMUNDA_VALUE);
  }

  private static String compareUserTaskIdWithTaskDefinitionKey(String key, Task task, PlanItem humanTaskDef) {
    if (humanTaskDef.getId().equals(task.getTaskDefinitionKey()))
    {
      String latest = getLatest(key, humanTaskDef);
      if (latest != null) return latest;
    }
    return null;
  }

  private static String getLatest(String key, PlanItem userTaskDef) {
    String latest = getString(userTaskDef);
    if (latest != null) return latest;

    CamundaProperties extensionsProperty = userTaskDef.getExtensionElements()//
      .getElementsQuery()//
      .filterByType(CamundaProperties.class)//
      .singleResult();
    var camundaProperties = extensionsProperty.getCamundaProperties();
    Map<String,String> camundaValue = new HashMap<>();
    camundaProperties.stream().filter(property ->property.getCamundaName().equals(key)).filter(Objects::nonNull).findFirst().ifPresent(property ->
      camundaValue.put("value",property.getCamundaValue()));
    return camundaValue.get("value");
  }

  private static String getString(PlanItem userTaskDef) {
    if (userTaskDef.getExtensionElements() == null) {
      return LATEST;
    }
    return null;
  }

  private static String getCamundaValue(CamundaProperty property) {
    return property.getCamundaValue();
  }

  public TaskInstanceDTO setExtensionElementsToTask(TaskInstanceDTO taskInstanceDTO, Task task)
    {
        BpmnModelInstance bpmnModelInstance =
                repositoryService.getBpmnModelInstance(task.getProcessDefinitionId());
        Collection<UserTask> userTaskDefs = bpmnModelInstance.getModelElementsByType(UserTask.class);
        Optional<UserTask> userTaskOptional = userTaskDefs.stream().filter(userTaskDef -> userTaskDef.getId().equals(task.getTaskDefinitionKey())).findFirst();

        if(userTaskOptional.isEmpty())
        {
            return taskInstanceDTO;
        }
            log.info(USERTASK_ID_TASKDEFKEY, userTaskOptional, task.getTaskDefinitionKey());
            ExtensionElements extensionElements = userTaskOptional.get().getExtensionElements();
            if(extensionElements == null)
            {
                return taskInstanceDTO;
            }
            Query<CamundaProperties> query = extensionElements
                    .getElementsQuery()
                    .filterByType(CamundaProperties.class);

            if(query.count() == 0)
            {
                return taskInstanceDTO;
            }

        CamundaProperties extensionsProperty = query.singleResult();
        var camundaProperties = extensionsProperty.getCamundaProperties();
        Map<String, Object> extensions = new HashMap<>();
        camundaProperties.stream().forEach(prop -> extensions.put(prop.getCamundaName(), getCamundaValue(prop)));
            if (extensions.containsKey(TYPE) && extensions.get(TYPE).equals(COMPONENT)
                    && extensions.containsKey(IS_DEFAULT) && extensions.get(IS_DEFAULT).equals("true"))
            {
                if(!isValidString(task.getFormKey()))
                {
                    return taskInstanceDTO;
                }
                FormSchema formSchema = this.runtimeFormService.fetchFormById(task.getFormKey());
                Map<String, Object> components = formSchema.getComponents();
                Object checklistInstanceId = taskService.getVariableLocal(task.getId(), CHECKLIST_INSTANCE_ID);
                if(checklistInstanceId != null)
                {
                    taskInstanceDTO.setChecklistInstanceId(checklistInstanceId.toString());
                }
              getTaskInstanceDTO(taskInstanceDTO, task, extensions, components, checklistInstanceId);
            }
            return taskInstanceDTO;
    }

  private TaskInstanceDTO getTaskInstanceDTO(TaskInstanceDTO taskInstanceDTO, Task task, Map<String, Object> extensions, Map<String, Object> components, Object checklistInstanceId) {
    if (components.containsKey(ACTIONS) && components.get(ACTIONS) != null && components.get(ACTIONS) instanceof List) {
      List<ActionsDTO> actions = settingUpActions(task, extensions, components, checklistInstanceId);
      taskInstanceDTO.setActions(actions);
        taskInstanceDTO.setComponentId(task.getFormKey());
      settingComponentTypeAnndQuestion(taskInstanceDTO, extensions);
      return taskInstanceDTO;
    }
    return null;
  }

  private List<ActionsDTO> settingUpActions(Task task, Map<String, Object> extensions, Map<String, Object> components, Object checklistInstanceId) {
    List<ActionsDTO> actions = this.objectMapper.convertValue(components.get(ACTIONS), new TypeReference<>() {});
    actions.stream().filter(action -> extensions.containsKey(action.getAction())).forEach(action ->
    {
        if(extensions.get(action.getAction()) != null)
        {
            action.setLabel(extensions.get(action.getAction()).toString());
        }
        if(action.getUrl().contains(CHECKLIST_INSTANCE_ID_VAR))
        {
            if(checklistInstanceId != null)
            {
                action.setUrl(action.getUrl().replace(CHECKLIST_INSTANCE_ID_VAR, checklistInstanceId.toString()));
            }
        }
        else if(action.getUrl().contains(DOCUMENT_ID_VAR))
        {
            Object documentId = taskService.getVariableLocal(task.getId(), DOCUMENT_ID);
            if(documentId != null)
            {
                action.setUrl(action.getUrl().replace(DOCUMENT_ID_VAR, documentId.toString()));
            }
        }
        else if(action.getUrl().contains(SCREEN_TYPE_ID_VAR) && extensions.containsKey(SCREEN_TYPE_ID))
        {
            action.setUrl(action.getUrl().replace(SCREEN_TYPE_ID_VAR, extensions.get(SCREEN_TYPE_ID).toString()));
        }
    });
    return actions;
  }

  private static void settingComponentTypeAnndQuestion(TaskInstanceDTO taskInstanceDTO, Map<String, Object> extensions) {
    if(extensions.containsKey(FORM_NAME) && extensions.get(FORM_NAME) != null)
    {
        taskInstanceDTO.setComponentType(extensions.get(FORM_NAME).toString());
    }
    if(extensions.containsKey(QUESTION) && extensions.get(QUESTION) != null)
    {
        taskInstanceDTO.setQuestion(extensions.get(QUESTION).toString());
    }
  }

  @Override
    public HistoricInstanceDTO setExtensionElementsToHistoricTask(HistoricInstanceDTO historicInstanceDTO, HistoricTaskInstance historicTaskInstance)
    {
        BpmnModelInstance bpmnModelInstance =
                repositoryService.getBpmnModelInstance(historicTaskInstance.getProcessDefinitionId());
        Collection<UserTask> userTaskDefs = bpmnModelInstance.getModelElementsByType(UserTask.class);
        Optional<UserTask> userTaskOptional = userTaskDefs.stream().filter(userTaskDef -> userTaskDef.getId().equals(historicTaskInstance.getTaskDefinitionKey())).findFirst();
        if(userTaskOptional.isEmpty())
        {
            return historicInstanceDTO;
        }
        log.info(USERTASK_ID_TASKDEFKEY, userTaskOptional, historicTaskInstance.getTaskDefinitionKey());
        ExtensionElements extensionElements = userTaskOptional.get().getExtensionElements();
        if(extensionElements == null)
        {
            return historicInstanceDTO;
        }
        Query<CamundaProperties> query = extensionElements
                .getElementsQuery()
                .filterByType(CamundaProperties.class);

        if(query.count() == 0)
        {
            return historicInstanceDTO;
        }
        CamundaProperties extensionsProperty = query.singleResult();
        var camundaProperties = extensionsProperty.getCamundaProperties();
        Map<String, Object> extensions = new HashMap<>();

        org.camunda.bpm.model.bpmn.instance.Task task = bpmnModelInstance.getModelElementById(historicTaskInstance.getTaskDefinitionKey());
        String formKey = task.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS, "formKey");

        String taskId = historicTaskInstance.getId();
        HistoricVariableInstance historicVariableInstance = historyService.createHistoricVariableInstanceQuery().taskIdIn(taskId).variableName(CHECKLIST_INSTANCE_ID).singleResult();
        String checklistInstanceId =  (historicVariableInstance==null) ? null : historicVariableInstance.getValue().toString();
        log.info("InstanceId :"+checklistInstanceId);

        HistoricVariableInstance historicVariableInstanceForDocument = historyService.createHistoricVariableInstanceQuery().taskIdIn(taskId).variableName(DOCUMENT_ID).singleResult();
        String documentId =  (historicVariableInstanceForDocument==null) ? null : historicVariableInstanceForDocument.getValue().toString();
        log.info("documentId :"+documentId);

        camundaProperties.stream().forEach(prop -> extensions.put(prop.getCamundaName(), getCamundaValue(prop)));
        if (formKey!= null && extensions.containsKey(TYPE) && extensions.get(TYPE).equals(COMPONENT)
                && extensions.containsKey(IS_DEFAULT) && extensions.get(IS_DEFAULT).equals("true"))
        {
            if( !isValidString(formKey))
            {
                return historicInstanceDTO;
            }
            FormSchema formSchema = this.runtimeFormService.fetchFormById(formKey);
            Map<String, Object> components = formSchema.getComponents();

            if(checklistInstanceId != null)
            {
                historicInstanceDTO.setChecklistInstanceId(checklistInstanceId);
            }
            if (components.containsKey(ACTIONS) && components.get(ACTIONS) != null && components.get(ACTIONS) instanceof List) {
              return getHistoricInstanceDTO(historicInstanceDTO, extensions, formKey, checklistInstanceId, documentId, components);
            }
        }
        return historicInstanceDTO;
    }

  private HistoricInstanceDTO getHistoricInstanceDTO(HistoricInstanceDTO historicInstanceDTO, Map<String, Object> extensions, String formKey, String checklistInstanceId, String documentId, Map<String, Object> components) {
    List<ActionsDTO> actions = this.objectMapper.convertValue(components.get(ACTIONS), new TypeReference<>() {});
    actions.stream().filter(action -> extensions.containsKey(action.getAction())).forEach(action ->
    {
        if(extensions.get(action.getAction()) != null)
        {
            action.setLabel(extensions.get(action.getAction()).toString());
        }
        if(action.getUrl().contains(CHECKLIST_INSTANCE_ID_VAR))
        {
            if(checklistInstanceId != null)
            {
                action.setUrl(action.getUrl().replace(CHECKLIST_INSTANCE_ID_VAR, checklistInstanceId));
            }
        }
        else if(action.getUrl().contains(DOCUMENT_ID_VAR))
        {
            if(documentId != null)
            {
                action.setUrl(action.getUrl().replace(DOCUMENT_ID_VAR, documentId));
            }
        }
        else if(action.getUrl().contains(SCREEN_TYPE_ID_VAR) && extensions.containsKey(SCREEN_TYPE_ID))
        {
            action.setUrl(action.getUrl().replace(SCREEN_TYPE_ID_VAR, extensions.get(SCREEN_TYPE_ID).toString()));
        }
    });
    historicInstanceDTO.setActions(actions);
    historicInstanceDTO.setComponentId(formKey);
    settingUpComponentAndQuestionForHistoricDTO(historicInstanceDTO, extensions);
    return historicInstanceDTO;
  }

  private static void settingUpComponentAndQuestionForHistoricDTO(HistoricInstanceDTO historicInstanceDTO, Map<String, Object> extensions) {
    if(extensions.containsKey(FORM_NAME) && extensions.get(FORM_NAME) != null)
    {
        historicInstanceDTO.setComponentType(extensions.get(FORM_NAME).toString());
    }
    if(extensions.containsKey(QUESTION) && extensions.get(QUESTION) != null)
    {
        historicInstanceDTO.setQuestion(extensions.get(QUESTION).toString());
    }
  }
}
