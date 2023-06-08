package com.techsophy.tsf.workflow.engine.camunda.listeners;

import com.techsophy.multitenancy.mongo.config.TenantContext;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.jobexecutor.JobExecutorContext;

public class RetryCycle implements JavaDelegate {
    private static final String NO_RETRIES_ERROR = "NO_RETRIES";

    private static boolean fail = false;
    private static int countFailed = 0;
    private static int countSuccess = 0;

    @Override
    public void execute(DelegateExecution ctx) throws Exception {
        TenantContext.setTenantId(ctx.getTenantId());
        JobExecutorContext jobExecutorContext = Context.getJobExecutorContext();
        if (jobExecutorContext!=null && jobExecutorContext.getCurrentJob()!=null && jobExecutorContext.getCurrentJob().getRetries()<=1) {
            // this is called from a Job and the job will run out of retries when it fails again
            try {
                doExecute();
            } catch (Exception ex) {
                // Probably save the exception somewhere
                throw new BpmnError(NO_RETRIES_ERROR);
            }
            return;
        }
        // otherwise normal behavior (including retries possibly)
        doExecute();
    }

    private static void doExecute() {
        if (fail) {
            countFailed++;
            throw new UnsupportedOperationException("ServiceA fails as expected");
        }
        countSuccess++;
    }
}
