package com.techsophy.tsf.workflow.engine.camunda.service;

import com.techsophy.tsf.workflow.engine.camunda.dto.*;
import org.camunda.bpm.engine.task.Task;

import java.util.List;

public interface RuntimeProcessService
{
    FormSchemaResponse getNextTask(String businessKey);

    FormSchemaResponse completeAndFetchNextTask(TaskDTO processInstanceRequest);

    void completeTask(TaskDTO taskDTO);

    void updateProcessVariables(ProcessVariablesDTO processInstanceRequest);

    ProcessInstanceResponseDTO createProcessInstance(ProcessInstanceDTO processInstanceRequest);

    ProcessInstanceResponseDTO createOrFetchProcessInstance(ProcessInstanceDTO processInstanceRequest);

    void resumeProcess(ResumeProcessRequestDto reqDto);

    PaginationDTO<List<TaskInstanceDTO>> getAllTasks(TaskQueryDTO taskQueryDTO, Integer page, Integer size);
    List<Task> getSubTasks(String taskId);

    PaginationDTO<List<HistoricInstanceDTO>> getAllHistoryTask(HistoricQueryInstanceDTO historicQueryInstanceDTO,Integer page,Integer size);

}
