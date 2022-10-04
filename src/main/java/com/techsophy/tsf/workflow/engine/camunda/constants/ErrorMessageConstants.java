package com.techsophy.tsf.workflow.engine.camunda.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorMessageConstants {
    public static final String PROCESS_OR_BUSINESSKEY_NOT_FOUND = "AUGMNT-WORKFLOW-ENGINE-1001";

    public static final String HISTORY_ERROR = "AUGMNT-WORKFLOW-ENGINE-1117";
    public static final String MORE_THAN_ONE_PROCESS_FOUND_FOR_BUSINESSKEY = "AUGMNT-WORKFLOW-ENGINE-1002";
    public static final String TASK_NOT_FOUND = "AUGMNT-WORKFLOW-ENGINE-1003";
    public static final String TASK_NOT_FOUND_WITH_ID = "AUGMNT-WORKFLOW-ENGINE-1004";
    public static final String PROVIDE_TASK_ID_OR_BUSINESS_KEY = "AUGMNT-WORKFLOW-ENGINE-1005";
    public static final String UNABLE_TO_EVALUATE = "AUGMNT-WORKFLOW-ENGINE-1006";

    public static final String UNABLE_TO_FRAME_DOCUMENT_PATH = "AUGMNT-WORKFLOW-ENGINE-1007";

    public static final String MISSING_TEMPLATE_ID_PROP = "AUGMNT-WORKFLOW-ENGINE-1008";
    public static final String MISSING_DOCUMENT_NAME_PROP = "AUGMNT-WORKFLOW-ENGINE-1009";
    public static final String MISSING_DOCUMENT_DESCRIPTION_PROP = "AUGMNT-WORKFLOW-ENGINE-1010";
    public static final String MISSING_DOCUMENT_PATH_PROP = "AUGMNT-WORKFLOW-ENGINE-1011";
    public static final String MISSING_PROPERTY = "AUGMNT-WORKFLOW-ENGINE-1012";
    public static final String INVALID_ALGORITHM_EXCEPTION = "AUGMNT-WORKFLOW-ENGINE-1013";
    public static final String UNABLE_TO_EXTRACT_GROUP_FROM_DMN = "AUGMNT-WORKFLOW-ENGINE-1014";
    public static final String UNABLE_TO_PARSE_CHECKLIST_ITEM_INSTANCE_ID_LIST_VARIABLE = "AUGMNT-WORKFLOW-ENGINE-1015";
    public static final String NOT_VALID_CHECKLIST_ASSIGNEE = "AUGMNT-WORKFLOW-ENGINE-1016";
    public static final String UNABLE_TO_PARSE_EXECUTION_RESULT = "AUGMNT-WORKFLOW-ENGINE-1017";

    public static final String NO_USERS_IN_GROUP = "AUGMNT-WORKFLOW-ENGINE-1018";
    public static final String INVALID_VARIABLE_IN_ASSIGNEE_FIELD = "AUGMNT-WORKFLOW-ENGINE-1019";
    public static final String MISMATCHED_DMN_OUTPUT_AND_ASSIGNEE_VALUE = "AUGMNT-WORKFLOW-ENGINE-1020";
    public static final String SUB_TASKS_COMPLETE_EXCEPTION = "AUGMNT-WORKFLOW-ENGINE-1116";
    public static final String NO_SUB_TASK_EXCEPTION = " No sub tasks found for a given parent task";
}
