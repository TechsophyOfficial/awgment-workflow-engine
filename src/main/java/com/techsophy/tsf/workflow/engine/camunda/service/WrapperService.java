package com.techsophy.tsf.workflow.engine.camunda.service;

import com.techsophy.tsf.workflow.engine.camunda.dto.HistoricInstanceDTO;
import com.techsophy.tsf.workflow.engine.camunda.dto.TaskInstanceDTO;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.core.io.Resource;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public interface WrapperService
{
    ResponseEntity<Resource>  handleRequest(MultiValueMap<String, String> params, RequestEntity<?> re, String appName);
    String getUserTaskExtensionByName(String taskId, String key);
    TaskInstanceDTO setExtensionElementsToTask(TaskInstanceDTO taskInstanceDTO, Task task);
    HistoricInstanceDTO setExtensionElementsToHistoricTask(HistoricInstanceDTO historicInstanceDTO, HistoricTaskInstance historicTaskInstance);
}
