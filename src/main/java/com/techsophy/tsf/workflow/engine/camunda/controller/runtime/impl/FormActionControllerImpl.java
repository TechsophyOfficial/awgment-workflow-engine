package com.techsophy.tsf.workflow.engine.camunda.controller.runtime.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.SwaggerApiEnroll;
import com.techsophy.tsf.workflow.engine.camunda.controller.runtime.FormActionController;
import com.techsophy.tsf.workflow.engine.camunda.dto.*;
import com.techsophy.tsf.workflow.engine.camunda.model.ApiResponse;
import com.techsophy.tsf.workflow.engine.camunda.service.impl.RuntimeProcessServiceImpl;
import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;


import java.util.List;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;

/**
 * form actions to be performed for user task with form
 */
@SwaggerApiEnroll
@RestController
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class FormActionControllerImpl implements FormActionController {
    ObjectMapper objectMapper;
    private final RuntimeProcessServiceImpl runtimeProcessService;

    /**
     * create process instance
     *
     * @param processInstanceRequest
     * @return ApiResponse
     */
    @Override
    public ApiResponse<ProcessInstanceResponseDTO> createProcessInstance(ProcessInstanceDTO processInstanceRequest) {
        return new ApiResponse<>(runtimeProcessService.createProcessInstance(processInstanceRequest), true, PROCESS_CREATION);
    }

    @Override
    public PaginationDTO<List<TaskInstanceDTO>> getAllTasks(Integer page, Integer size) {
        return this.runtimeProcessService.getAllTasks(null, page, size);
    }

    @Override
    public PaginationDTO<List<TaskInstanceDTO>> getAllTasksByQuery(TaskQueryDTO taskQueryDTO, Integer page, Integer size) {
        return this.runtimeProcessService.getAllTasks(taskQueryDTO, page, size);
    }

    @Override
    public PaginationDTO<List<HistoricInstanceDTO>> getAllHistoryTasksByQuery(HistoricQueryInstanceDTO historicQueryInstanceDTO, Integer page, Integer size) {
        return this.runtimeProcessService.getAllHistoryTask(historicQueryInstanceDTO,page,size);
    }

    /**
     * get next task based on business key
     *
     * @param businessKey
     * @return ApiResponse
     */
    @Override
    public ApiResponse<FormSchemaResponse> getNextTask(String businessKey) {
        return new ApiResponse<>(runtimeProcessService.getNextTask(businessKey), true, GET_FORM_SUCCESS);
    }

    /**
     * complete and fetch next task
     *
     * @param processInstanceRequest
     * @return ApiResponse
     */
    @Override
    public ApiResponse<FormSchemaResponse> completeAndFetchNextTask(TaskDTO processInstanceRequest) {
        FormSchemaResponse form = runtimeProcessService.completeAndFetchNextTask(processInstanceRequest);
        if (form != null) {
            return new ApiResponse<>(form, true, GET_FORM_SUCCESS);
        }
        return new ApiResponse<>(null, true, NO_FORM_AVAILABLE);
    }

    /**
     * complete current active task
     *
     * @param taskDTO
     * @return ApiResponse
     */
    @Override
    public ApiResponse<Void> completeCurrentActiveTask(TaskDTO taskDTO) {
        this.runtimeProcessService.completeTask(taskDTO);
        return new ApiResponse<>(null, true, COMPLETE_TASK_SUCCESS);
    }

    /**
     * update process variables based on process instance
     *
     * @param processInstanceRequest
     * @return ApiResponse
     */
    @Override
    public ApiResponse<Void> updateProcessVariables(ProcessVariablesDTO processInstanceRequest) {
        runtimeProcessService.updateProcessVariables(processInstanceRequest);
        return new ApiResponse<>(null, true, FORM_UPDATE_SUCCESS);
    }

    /**
     * create or fetch process instance
     *
     * @param processInstanceRequest
     * @return ApiResponse
     */
    @Override
    public ApiResponse<ProcessInstanceResponseDTO> createOrFetchProcessInstance(ProcessInstanceDTO processInstanceRequest) {
        ProcessInstanceResponseDTO processInstanceResponseDTO = runtimeProcessService.createOrFetchProcessInstance(processInstanceRequest);
        if (Boolean.TRUE.equals(processInstanceResponseDTO.getAlreadyExists())) {
            return new ApiResponse<>(processInstanceResponseDTO, true, PROCESS_RETRIEVED);
        }
        return new ApiResponse<>(processInstanceResponseDTO, true, PROCESS_CREATION);
    }


    public ApiResponse<Void> resumeProcess(@Validated ResumeProcessRequestDto reqDto) {
        runtimeProcessService.resumeProcess(reqDto);
        return new ApiResponse<>(null, true, "Process instance resumed successfully");
    }

    @Override
    public ApiResponse<List<Task>> getSubTasks(String taskId) throws JsonProcessingException {
       runtimeProcessService.getSubTasks(taskId);

        return new ApiResponse<>(runtimeProcessService.getSubTasks(taskId), true, "sub tasks fetched  successfully");
    }

    @Override
    public ApiResponse<Void> completeTask(String checklistInstanceId) {
        return new ApiResponse<>(null,true,"Task is completed successfully");
    }


}
