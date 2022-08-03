package com.techsophy.tsf.workflow.engine.camunda.service;

import com.techsophy.tsf.workflow.engine.camunda.model.PropertiesModel;

import java.util.List;

public interface AppUtilService
{
    List<PropertiesModel> getProperties(String projectName);
}
