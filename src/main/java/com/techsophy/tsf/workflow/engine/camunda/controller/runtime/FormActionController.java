package com.techsophy.tsf.workflow.engine.camunda.controller.runtime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.techsophy.tsf.workflow.engine.camunda.dto.*;
import com.techsophy.tsf.workflow.engine.camunda.model.ApiResponse;
import org.camunda.bpm.engine.task.Task;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;

/**
 * All the required task for before or after form execution
 */
@RequestMapping(BASE_URL)
public interface FormActionController
{
    /**
     * start process instance
     * @param processInstanceRequest
     * @return ApiResponse
     */
    @PostMapping(PROCESS_INSTANCE_START)
    ApiResponse<ProcessInstanceResponseDTO> createProcessInstance(@RequestBody @Validated ProcessInstanceDTO processInstanceRequest);


    @GetMapping(TASK_URL)
    PaginationDTO<List<TaskInstanceDTO>> getAllTasks(@RequestParam(required = false, defaultValue = "0") Integer page, @RequestParam(required = false, defaultValue = "20") Integer size);


    @PostMapping(TASK_URL)
    PaginationDTO<List<TaskInstanceDTO>> getAllTasksByQuery(@RequestBody TaskQueryDTO taskQueryDTO, @RequestParam(required = false, defaultValue = "0") Integer page, @RequestParam(required = false, defaultValue = "20") Integer size);

    @PostMapping(HISTORY_TASK_URL)
    PaginationDTO<List<HistoricInstanceDTO>> getAllHistoryTasksByQuery(@RequestBody HistoricQueryInstanceDTO historicQueryInstanceDTO, @RequestParam(required = false, defaultValue = "0") Integer page, @RequestParam(required = false, defaultValue = "20") Integer size);

    /**
     * Get All tasks
     * @param businessKey
     * @return ApiResponse
     */
    @GetMapping(NEXT_TASK)
    ApiResponse<FormSchemaResponse> getNextTask(@RequestParam(required = true) String businessKey);

    /**
     * complete current and fetch next task
     * @param processInstanceRequest
     * @return ApiResponse
     */
    @PostMapping(NEXT_TASK)
    ApiResponse<FormSchemaResponse> completeAndFetchNextTask(@RequestBody TaskDTO processInstanceRequest);

    /**
     * complete current active task
     * @param taskDTO
     * @return ApiResponse
     */
    @PostMapping(TASK_COMPLETE)
    ApiResponse<Void> completeCurrentActiveTask(@RequestBody TaskDTO taskDTO);

    /**
     * update process variable based on process instance
     * @param processInstanceRequest
     * @return ApiResponse
     */
    @PostMapping(TASK_URL+"/variables")
    ApiResponse<Void> updateProcessVariables(@RequestBody ProcessVariablesDTO processInstanceRequest);

    /**
     * create or fetch process instance
     * @param processInstanceRequest
     * @return ApiResponse
     */
    @PostMapping(PROCESS_INSTANCE)
    ApiResponse<ProcessInstanceResponseDTO> createOrFetchProcessInstance(@RequestBody @Validated ProcessInstanceDTO processInstanceRequest);

    @PostMapping(PROCESS_INSTANCE_RESUME)
    ApiResponse<Void> resumeProcess(@RequestBody @Validated ResumeProcessRequestDto reqDto);

    @GetMapping(SUB_TASKS)
    ApiResponse<List<Task>> getSubTasks(@RequestParam String  taskId) throws JsonProcessingException;

    @PostMapping("/taskById/complete")
    ApiResponse<Void> completeTask(@RequestParam(name = "checklistInstanceId",required = false) String checklistInstanceId );

}
