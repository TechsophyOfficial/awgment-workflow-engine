package com.techsophy.tsf.workflow.engine.camunda.constants;

public class LogMessages
{
    public static final String CREATE_TASK_EVENT_START = "Create task event started for %s";
    public static final String CREATE_TASK_EVENT_END = "Create task event ended for %s";
    public static final String COMPLETE_TASK_EVENT_START = "Complete task event started for %s";
    public static final String COMPLETE_TASK_EVENT_END = "Complete task event ended for %s";
    public static final String EXECUTION_RULE_START = "Execution rule dmn service call started";
    public static final String EXECUTION_RULE_END = "Execution rule dmn service call ended";

    public static final String ASSIGNMENT_RULE_START = "Assignment rule dmn service call started";
    public static final String ASSIGNMENT_RULE_END = "Assignment rule dmn service call ended";

    public static final String CHECKLIST_INVOCATION_START = "Checklist invocation started for checklist id :%s";
    public static final String CHECKLIST_INVOCATION_END = "Checklist invocation completed with checklist instance id: %s";
    public static final String DOCUMENT_GENERATION_START = "Document generation started using template id :%s";
    public static final String DOCUMENTATION_GENERATION_END = "Document generation completed with document id :%s";
    public static final String REST_CALL_WITH_URI = "Rest call with uri {}";
    public static final String WRAPPER_SERVICE_END = "END: Wrapper service layer";
    public static final String USERTASK_INFO ="usertaskID: {},taskdefkey: {}";
    public static final String DESCRYPTION ="Catalog of Application endpoints that can be used in DMP applications.";

    private LogMessages(){}

}
