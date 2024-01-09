package com.techsophy.tsf.workflow.engine.camunda.service;

import com.techsophy.tsf.workflow.engine.camunda.model.ChecklistInstanceResponseModel;
import com.techsophy.tsf.workflow.engine.camunda.model.ChecklistItemInstanceResponseModel;
import com.techsophy.tsf.workflow.engine.camunda.model.ChecklistModel;
import com.techsophy.tsf.workflow.engine.camunda.model.InvokeChecklistInstanceResponseModel;

import java.util.List;
import java.util.Map;

public interface ChecklistService
{
    InvokeChecklistInstanceResponseModel invokeChecklist(String checklistId, String businessKey, Map<String, Object> data);

    List<ChecklistItemInstanceResponseModel> getItemInstancesByChecklistInstanceId(String checklistInstanceId);

    ChecklistInstanceResponseModel getChecklistInstanceById(String id);

    ChecklistModel getChecklistById(String checklistId);

    void completeChecklistItemsByIds(Map<String, List<String>> idList);
}
