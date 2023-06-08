package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.techsophy.tsf.workflow.engine.camunda.config.GlobalMessageSource;
import com.techsophy.tsf.workflow.engine.camunda.dto.*;
import com.techsophy.tsf.workflow.engine.camunda.exception.ProcessException;
import com.techsophy.tsf.workflow.engine.camunda.exception.TaskNotFoundException;
import com.techsophy.tsf.workflow.engine.camunda.keycloak.sso.OAuth2AndJwtAwareRequestFilter;
import com.techsophy.tsf.workflow.engine.camunda.serializers.TaskSerializer;
import com.techsophy.tsf.workflow.engine.camunda.service.RuntimeFormService;
import com.techsophy.tsf.workflow.engine.camunda.service.RuntimeProcessService;
import com.techsophy.tsf.workflow.engine.camunda.service.WrapperService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstanceQuery;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.rest.dto.history.HistoricTaskInstanceQueryDto;
import org.camunda.bpm.engine.rest.dto.task.TaskQueryDto;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;
import static com.techsophy.tsf.workflow.engine.camunda.constants.ErrorMessageConstants.*;

/**
 * Process run time service
 */
@Service
@Transactional
@Slf4j
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class RuntimeProcessServiceImpl implements RuntimeProcessService {
    private final ObjectMapper objectMapper;
    private final TaskService taskService;
    private final RuntimeService runtimeService;
    private final HistoryService historyService;
    private final RuntimeFormService runtimeFormService;

    private final CaseService caseService;
    private final WrapperService wrapperService;
    private final GlobalMessageSource globalMessageSource;
    private final ProcessEngine processEngine;


    /**
     * get next task based on businesskey
     *
     * @param businessKey
     * @return FormSchemaResponse
     */
    @Override
    public FormSchemaResponse getNextTask(String businessKey) {
        Task task = getTask(businessKey);
        return getFormByTask(task);
    }

    /**
     * complete and fetch next task
     *
     * @param processInstanceRequest
     * @return FormSchemaResponse
     */
    public FormSchemaResponse completeAndFetchNextTask(TaskDTO processInstanceRequest) {


        Map<String, Object> processVariables = null;
        Task task = Optional.ofNullable(this.taskService.createTaskQuery().processInstanceBusinessKey(processInstanceRequest.getBusinessKey()).active().initializeFormKeys().singleResult()).orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND, globalMessageSource.get(TASK_NOT_FOUND + processInstanceRequest.getBusinessKey())));

        if (task.getFormKey() != null) {
            processVariables = getProcessVariables(task.getFormKey(), processInstanceRequest.getVariables(), task.getId());
        }
        this.taskService.complete(task.getId(), processVariables);
        Task activeTask = this.taskService.createTaskQuery().processInstanceBusinessKey(processInstanceRequest.getBusinessKey()).active().singleResult();
        if (activeTask != null) {
            task = getTask(processInstanceRequest.getBusinessKey());
            return getFormByTask(task);
        }
        return null;
    }

    /**
     * complete current active task
     *
     * @param taskDTO
     */
    @Override
    public void completeTask(TaskDTO taskDTO) {

        Task task = taskService.createTaskQuery().taskId(taskDTO.getTaskId()).singleResult();
        if(task.getId()!=null && task.getParentTaskId()!=null){
            taskComplete(taskDTO);
        }
        else {
            List<Task> subTasks = this.taskService.getSubTasks(taskDTO.getTaskId());
            if (subTasks == null || subTasks.isEmpty()) {
                    taskComplete(taskDTO);
                } else {
                    throw new IllegalArgumentException(globalMessageSource.get(SUB_TASKS_COMPLETE_EXCEPTION));
                }
        }
    }

    /**
     * update process variable base on process instance id
     *
     * @param processInstanceRequest
     */
    @Override
    public void updateProcessVariables(ProcessVariablesDTO processInstanceRequest) {
        Task task = this.taskService.createTaskQuery().processInstanceBusinessKey(processInstanceRequest.getBusinessKey()).active().initializeFormKeys().singleResult();
        Map<String, Object> processVariables = processInstanceRequest.getVariables();
        if (task.getFormKey() != null) {
            processVariables = getProcessVariables(task.getFormKey(), processInstanceRequest.getVariables(), task.getId());
        }
        runtimeService.setVariables(task.getExecutionId(), processVariables);
    }

    /**
     * create process instance
     *
     * @param processInstanceRequest
     * @return ProcessInstanceResponseDTO
     */
    @Override
    public ProcessInstanceResponseDTO createProcessInstance(ProcessInstanceDTO processInstanceRequest)
    {
        String tenantName = OAuth2AndJwtAwareRequestFilter.getTenantName().orElseThrow();
        Map<String, Object> processVariables = processInstanceRequest.getVariables();
        if (processInstanceRequest.getFormKey() != null && !processInstanceRequest.getFormKey().equals(""))
        {
            processVariables = getProcessVariables(processInstanceRequest.getFormKey(),
                    processInstanceRequest.getVariables(), LATEST);
        }
        ProcessInstance processInstance = runtimeService.createProcessInstanceByKey(processInstanceRequest.getProcessDefinitionKey()).processDefinitionTenantId(tenantName).businessKey(processInstanceRequest.getBusinessKey()).setVariables(processVariables).execute();
        Map<String, Object> variables = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId()).list().stream().collect(Collectors.toMap(HistoricVariableInstance::getName, HistoricVariableInstance::getValue));
        if (variables.containsKey(ERROR_CODE) || variables.containsKey(ERROR_MESSAGE))
        {
            throw new IllegalArgumentException(variables.get(ERROR_MESSAGE).toString());
        }
        return new ProcessInstanceResponseDTO(processInstance.getProcessInstanceId(), processInstance.getBusinessKey(), variables, false);
    }

    /**
     * create or fetch process instance
     *
     * @param processInstanceRequest
     * @return ProcessInstanceResponseDTO
     */
    @Override
    public ProcessInstanceResponseDTO createOrFetchProcessInstance(ProcessInstanceDTO processInstanceRequest) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(processInstanceRequest.getBusinessKey()).active().singleResult();
        if (processInstance != null) {
            Map<String, Object> variables = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId()).list().stream().collect(Collectors.toMap(HistoricVariableInstance::getName, HistoricVariableInstance::getValue));
            return new ProcessInstanceResponseDTO(processInstance.getProcessInstanceId(), processInstance.getBusinessKey(), variables, true);
        }
        return createProcessInstance(processInstanceRequest);
    }

    /**
     * get task based on business key
     *
     * @param businessKey
     * @return Task
     */
    private Task getTask(String businessKey) {
        return Optional.ofNullable(this.taskService.createTaskQuery().processInstanceBusinessKey(businessKey).active().initializeFormKeys().singleResult()).orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND, globalMessageSource.get(TASK_NOT_FOUND + businessKey)));
    }

    /**
     * get process variables
     *
     * @param formKey
     * @param formData
     * @return Map
     */
    @SneakyThrows({IOException.class})
    private Map<String, Object> getProcessVariables(String formKey, Map<String, Object> formData, String taskId) {
        FormSchema runtime = runtimeFormService.fetchFormById(formKey, taskId);
        FormioFormSchema components =
                this.objectMapper.convertValue(runtime.getComponents(), FormioFormSchema.class);
        return mapFormDataToProcessVariables(formData, components.getComponents());
    }

    /**
     * map form data to process variables
     *
     * @param formData
     * @param components
     * @return Map
     */
    private Map<String, Object> mapFormDataToProcessVariables(Map<String, Object> formData, FormComponent[] components) {
        Map<String, Object> variables = new HashMap<>();
        Arrays.stream(components).forEach(component -> {
            String formComponentType = component.getType();
            String formComponentKey = component.getKey();
            Object processVariableValue = formData.get(formComponentKey);
            if (processVariableValue != null) {
                if (formComponentType.equals(DATE_TIME)) {
                    processVariableValue = Date.from(ZonedDateTime.parse(processVariableValue.toString()).toInstant());
                }
                variables.put(formComponentKey, processVariableValue);
            }
        });
        return variables;
    }

    @SneakyThrows({IOException.class})
    private FormSchemaResponse getFormByTask(Task task) {
        Map<String, Object> variables = runtimeService.getVariables(task.getExecutionId());
        FormSchema runtime = runtimeFormService.fetchFormById(task.getFormKey(), task.getId());
        FormioFormSchema components =
                this.objectMapper.convertValue(runtime.getComponents(), FormioFormSchema.class);
        return FormSchemaResponse.builder()
                .formKey(runtime.getId())
                .formName(runtime.getName())
                .formContent(components)
                .formVersion(runtime.getVersion())
                .taskId(task.getId())
                .taskName(task.getName())
                .variables(variables)
                .build();
    }


    /**
     * Resumes the process based on processInstanceId passed in the requestBody
     */
    @Override
    public void resumeProcess(ResumeProcessRequestDto reqDto) {
        if (reqDto.getProcessInstanceId() == null && reqDto.getBusinessKey() == null) {
            throw new ProcessException(PROCESS_OR_BUSINESSKEY_NOT_FOUND, globalMessageSource.get(PROCESS_OR_BUSINESSKEY_NOT_FOUND));
        }
        if (reqDto.getProcessInstanceId() != null) {
            ProcessInstance instanceObj = runtimeService.createProcessInstanceQuery().processInstanceId(reqDto.getProcessInstanceId()).active().singleResult();

            if (instanceObj != null) {
                runtimeService.createMessageCorrelation(reqDto.getMessage())
                        .processInstanceBusinessKey(instanceObj.getBusinessKey())
                        .setVariables(reqDto.getVariables())
                        .correlate();

            }

        } else if (reqDto.getBusinessKey() != null) {
            List<ProcessInstance> instanceObj;

            instanceObj = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(reqDto.getBusinessKey()).active().unlimitedList();

            if (instanceObj != null)
            {
                if (instanceObj.size() > 1) {
                    throw new ProcessException(MORE_THAN_ONE_PROCESS_FOUND_FOR_BUSINESSKEY, globalMessageSource.get(MORE_THAN_ONE_PROCESS_FOUND_FOR_BUSINESSKEY));
                }
                runtimeService.createMessageCorrelation(reqDto.getMessage())
                        .processInstanceBusinessKey(reqDto.getBusinessKey()).setVariables(reqDto.getVariables())
                        .correlate();
            }
        }
    }

    @Override
    public PaginationDTO<List<TaskInstanceDTO>> getAllTasks(TaskQueryDTO taskQueryDTO, Integer page, Integer size) {
        TaskQuery taskQuery = this.taskService.createTaskQuery().orderByTaskCreateTime().desc();
        if (taskQueryDTO != null) {
            TaskQueryDto taskQueryDto = this.objectMapper.convertValue(taskQueryDTO, TaskQueryDto.class);
            taskQuery = taskQueryDto.toQuery(processEngine).orderByTaskCreateTime().desc();
        }
        List<Task> tasks = taskQuery.initializeFormKeys().listPage(page * size, size);
        long taskCount = taskQuery.count();
        List<TaskInstanceDTO> taskInstances = new ArrayList<>();
        tasks.forEach(task ->
        {
            TaskInstanceDTO taskInstanceDTO = this.getTaskInstance(task);
            taskInstanceDTO = wrapperService.setExtensionElementsToTask(taskInstanceDTO, task);
            taskInstances.add(taskInstanceDTO);
            String businessKey = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult().getBusinessKey();
            taskInstanceDTO.setBusinessKey(businessKey);
        });
        return new PaginationDTO<>(taskInstances, page, size, taskInstances.size(), taskCount / size, taskCount);
    }

    private TaskInstanceDTO getTaskInstance(Task task) {
        TaskInstanceDTO taskInstanceDTO = new TaskInstanceDTO();
        taskInstanceDTO.setId(task.getId());
        taskInstanceDTO.setName(task.getName());
        taskInstanceDTO.setAssignee(task.getAssignee());
        taskInstanceDTO.setCreated(task.getCreateTime());
        taskInstanceDTO.setDue(task.getDueDate());
        taskInstanceDTO.setFollowUp(task.getFollowUpDate());
        if (task.getDelegationState() != null) {
            taskInstanceDTO.setDelegationState(task.getDelegationState().toString());
        }
        taskInstanceDTO.setDescription(task.getDescription());
        taskInstanceDTO.setExecutionId(task.getExecutionId());
        taskInstanceDTO.setOwner(task.getOwner());
        taskInstanceDTO.setPriority(task.getPriority());
        taskInstanceDTO.setParentTaskId(task.getParentTaskId());
        taskInstanceDTO.setProcessDefinitionId(task.getProcessDefinitionId());
        taskInstanceDTO.setProcessInstanceId(task.getProcessInstanceId());
        taskInstanceDTO.setTaskDefinitionKey(task.getTaskDefinitionKey());
        taskInstanceDTO.setCaseExecutionId(task.getCaseExecutionId());
        taskInstanceDTO.setCaseInstanceId(task.getCaseInstanceId());
        taskInstanceDTO.setSuspended(task.isSuspended());
        taskInstanceDTO.setFormKey(task.getFormKey());
        taskInstanceDTO.setTenantId(task.getTenantId());
        return taskInstanceDTO;
    }

    public void taskComplete(TaskDTO taskDTO) {
        Task task = null;

        if (taskDTO.getTaskId() != null) {
            task = Optional.ofNullable(this.taskService.createTaskQuery().taskId(taskDTO.getTaskId()).active().initializeFormKeys().singleResult()).orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND_WITH_ID, globalMessageSource.get(TASK_NOT_FOUND_WITH_ID + taskDTO.getTaskId())));
        } else if (taskDTO.getBusinessKey() != null) {
            task = Optional.ofNullable(this.taskService.createTaskQuery().processInstanceBusinessKey(taskDTO.getBusinessKey()).active().initializeFormKeys().singleResult()).orElseThrow(() -> new TaskNotFoundException(TASK_NOT_FOUND, globalMessageSource.get(TASK_NOT_FOUND + taskDTO.getBusinessKey())));
        } else {
            throw new IllegalArgumentException(globalMessageSource.get(PROVIDE_TASK_ID_OR_BUSINESS_KEY));
        }
        this.taskService.complete(task.getId(), taskDTO.getVariables());
    }

    @Override
    @SneakyThrows
    public List<Task> getSubTasks(String taskId) {

        List<Task> subTasks = taskService.getSubTasks(taskId);
        SimpleModule module = new SimpleModule();
        module.addSerializer(Task.class, new TaskSerializer());
        objectMapper.registerModule(module);
        String serialized = objectMapper.writeValueAsString(subTasks);
        List<Task> subTasksSerialized = objectMapper.readValue(serialized, List.class);
        if (subTasks.isEmpty()) {
            throw new IllegalArgumentException(NO_SUB_TASK_EXCEPTION);
        } else {
            return subTasksSerialized;
        }
    }


    @Override
    public PaginationDTO<List<HistoricInstanceDTO>> getAllHistoryTask(HistoricQueryInstanceDTO historicQueryInstanceDTO,Integer page, Integer size)
    {
        log.info("getAllHistoryTask is started");
        HistoricTaskInstanceQuery historicTaskInstanceQuery =this.historyService.createHistoricTaskInstanceQuery().orderByHistoricActivityInstanceStartTime();
        if(historicQueryInstanceDTO != null)
        {
            HistoricTaskInstanceQueryDto historicTaskInstanceQueryDto = this.objectMapper.convertValue(historicQueryInstanceDTO, HistoricTaskInstanceQueryDto.class);
            historicTaskInstanceQuery = historicTaskInstanceQueryDto.toQuery(processEngine).orderByHistoricActivityInstanceStartTime().desc();
        }
        List<HistoricTaskInstance>historicTaskInstances =historicTaskInstanceQuery.endOr().listPage(page * size, size);
        long historicTaskCount = historicTaskInstanceQuery.count();

        List<HistoricInstanceDTO> historicInstanceDTOS = new ArrayList<>();
        historicTaskInstances.stream().forEach(historicTaskInstance ->
        {
            log.info("Inside Loop:"+historicTaskInstance);
            HistoricInstanceDTO historicInstanceDTO = this.getHistoricTaskInstance(historicTaskInstance);
            try
            {
                historicInstanceDTO = wrapperService.setExtensionElementsToHistoricTask(historicInstanceDTO, historicTaskInstance);
                historicInstanceDTOS.add(historicInstanceDTO);
            }
            catch (Exception e)
            {
                log.error(e.getMessage());
                e.printStackTrace();
                throw new ProcessException(HISTORY_ERROR,e.getMessage());
            }
            log.info("getAllHistoryTask is done");
        });
        return new PaginationDTO<>(historicInstanceDTOS, page, size, historicInstanceDTOS.size(), historicTaskCount / size, historicTaskCount);
    }

    private HistoricInstanceDTO getHistoricTaskInstance(HistoricTaskInstance historicTaskInstance) {
        HistoricInstanceDTO historicInstanceDTO = new HistoricInstanceDTO();
        historicInstanceDTO.setId(historicTaskInstance.getId());
        historicInstanceDTO.setName(historicTaskInstance.getName());
        historicInstanceDTO.setAssignee(historicTaskInstance.getAssignee());
        historicInstanceDTO.setStartTime(historicTaskInstance.getStartTime());
        historicInstanceDTO.setDue(historicTaskInstance.getDueDate());
        historicInstanceDTO.setFollowUp(historicTaskInstance.getFollowUpDate());
        historicInstanceDTO.setDescription(historicTaskInstance.getDescription());
        historicInstanceDTO.setExecutionId(historicTaskInstance.getExecutionId());
        historicInstanceDTO.setOwner(historicTaskInstance.getOwner());
        historicInstanceDTO.setPriority(historicTaskInstance.getPriority());
        historicInstanceDTO.setParentTaskId(historicTaskInstance.getParentTaskId());
        historicInstanceDTO.setProcessDefinitionId(historicTaskInstance.getProcessDefinitionId());
        historicInstanceDTO.setProcessInstanceId(historicTaskInstance.getProcessInstanceId());
        historicInstanceDTO.setTaskDefinitionKey(historicTaskInstance.getTaskDefinitionKey());
        historicInstanceDTO.setCaseExecutionId(historicTaskInstance.getCaseExecutionId());
        historicInstanceDTO.setCaseInstanceId(historicTaskInstance.getCaseInstanceId());
        historicInstanceDTO.setTenantId(historicTaskInstance.getTenantId());
        historicInstanceDTO.setDeleteReason(historicTaskInstance.getDeleteReason());
        historicInstanceDTO.setRootProcessInstanceId(historicTaskInstance.getRootProcessInstanceId());
        return historicInstanceDTO;
    }

}