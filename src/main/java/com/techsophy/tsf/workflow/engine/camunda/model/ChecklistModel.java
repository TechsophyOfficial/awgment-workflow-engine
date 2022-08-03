package com.techsophy.tsf.workflow.engine.camunda.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Value;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Value
public class ChecklistModel
{
    private String id;
    private Map<String, Object> checklist = new LinkedHashMap<>();
    @JsonAnySetter
    public void setChecklist(String key, Object value)
    {
        checklist.put(key, value);
    }

    public Map<String, Object> getChecklist()
    {
        return checklist;
    }
    private List<String> groupList;
    private Instant createdOn;
    private String createdBy;
    private String createdById;
    private String updatedById;
    private String updatedBy;
    private Instant updatedOn;
}
