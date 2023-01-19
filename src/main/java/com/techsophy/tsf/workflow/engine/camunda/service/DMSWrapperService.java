package com.techsophy.tsf.workflow.engine.camunda.service;

import com.techsophy.tsf.workflow.engine.camunda.model.PublishRequestModel;

import java.util.Map;

public interface DMSWrapperService
{
    Map<String, Object> generateDocument(PublishRequestModel publishRequestModel);
}
