package com.techsophy.tsf.workflow.engine.camunda.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvokeChecklistInstanceResponseModel
{
    private String checklistInstanceId;
}
