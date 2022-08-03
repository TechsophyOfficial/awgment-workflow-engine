package com.techsophy.tsf.workflow.engine.camunda.connectors;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.interceptor.CommandInvocationContext;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.Objects;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.GITLAB_ENVIRONMENT_URL;

@Component
@AllArgsConstructor
public class DefaultRuntimeContextPropertyProvider implements RuntimeContextPropertyProvider
{
    private static final String WORKFLOW_ENGINE_URL_OR_NULL = System.getenv(GITLAB_ENVIRONMENT_URL);

    private final ProcessEngine engine;

    /**
     * get process url
     * @return String
     */
    @Override
    public String getEngineUrl()
    {
        return Objects.requireNonNullElseGet(WORKFLOW_ENGINE_URL_OR_NULL, DefaultRuntimeContextPropertyProvider::getLocalNodeUrl);
    }

    /**
     * get business key
     * @return String
     */
    @Override
    public String getBusinessKey()
    {
        return getValue(this.getConfiguration().getLoggingContextBusinessKey());
    }

    /**
     * get process instance id
     * @return String
     */
    @Override
    public String getProcessInstanceId()
    {
        return getValue(this.getConfiguration().getLoggingContextProcessInstanceId());
    }

    /**
     * get process initiator
     * @return String
     */
    @Override
    public String getProcessInitiator()
    {
        return this.getProcessInstance().getStartUserId();
    }

    /**
     * get current process instance
     * @return HistoricProcessInstance
     */
    @Override
    public HistoricProcessInstance getProcessInstance()
    {
        return this.getConfiguration().getProcessEngine().getHistoryService().createHistoricProcessInstanceQuery().processInstanceId(this.getProcessInstanceId()).singleResult();
    }

    /**
     * get configuration
     * @return ProcessEngineConfigurationImpl
     */
    private ProcessEngineConfigurationImpl getConfiguration()
    {
        return (ProcessEngineConfigurationImpl) this.engine.getProcessEngineConfiguration();
    }

    /**
     * get value for process variable
     * @param key
     * @return String
     */
    private static String getValue(String key)
    {
        return getContext().getProcessDataContext().getLatestPropertyValue(key);
    }

    /**
     * get context
     * @return CommandInvocationContext
     */
    private static CommandInvocationContext getContext()
    {
        return Objects.requireNonNull(
                Context.getCommandInvocationContext(), "Illegal State. No active command invocation context found");
    }

    /**
     * get URL
     * @return String
     */
    @SneakyThrows
    private static String getLocalNodeUrl()
    {
        return InetAddress.getLocalHost().getCanonicalHostName();
    }
}
