package com.techsophy.tsf.workflow.engine.camunda.connectors;

import org.camunda.bpm.engine.history.HistoricProcessInstance;

public class TestRuntimeContextPropertyProvider implements RuntimeContextPropertyProvider
{
    @Override
    public String getEngineUrl()
    {
        return "http://localhost:9999/engine";
    }

    @Override
    public String getBusinessKey()
    {
        return "user1";
    }

    @Override
    public String getProcessInstanceId()
    {
        return "process1";
    }

    @Override public String getProcessInitiator() {
        return null;
    }

    @Override public HistoricProcessInstance getProcessInstance() {
        return null;
    }
}
