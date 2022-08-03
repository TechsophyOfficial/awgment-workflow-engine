package com.techsophy.tsf.workflow.engine.camunda.service;

import java.util.List;
import java.util.Map;

public interface DMNService
{
    List<Map<String, Object>> executeDMN(String ruleId, Map<String, Object> data);
}
