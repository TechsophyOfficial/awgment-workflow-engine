package com.techsophy.tsf.workflow.engine.camunda.connectors;

import org.camunda.bpm.engine.history.HistoricProcessInstance;

/**
 * Interface for runtime fields
 */
public interface RuntimeContextPropertyProvider
{
    String getEngineUrl();

    String getBusinessKey();

    String getProcessInstanceId();

    String getProcessInitiator();

    HistoricProcessInstance getProcessInstance();
}
