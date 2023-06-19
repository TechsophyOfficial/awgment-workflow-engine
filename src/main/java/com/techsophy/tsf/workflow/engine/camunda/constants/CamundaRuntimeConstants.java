package com.techsophy.tsf.workflow.engine.camunda.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CamundaRuntimeConstants
{
    //JWTRoleConverter
    public static final String CLIENT_ROLES="clientRoles";
    public static final String USER_INFO_URL= "/protocol/openid-connect/userinfo";
    public static final String AWGMENT_ROLES_MISSING_IN_CLIENT_ROLES ="AwgmentRoles are missing in clientRoles";
    public static final String CLIENT_ROLES_MISSING_IN_USER_INFORMATION="ClientRoles are missing in the userInformation";
    public static final String PREFERED_USERNAME="preferred_username";
    public static final String EMPTY_STRING="";
    public static final String REGEX_SPLIT="\\.";
    public static final String ISS="iss";
    public static final String URL_SEPERATOR="/";
    public static final int SEVEN=7;
    public static final int ONE=1;
    public static final String GET="GET";
    public static final String POST = "POST";

    /*CustomFilterConstants*/
    public static final String AUTHORIZATION="Authorization";
    public static final String BASE_URL = "/service/v1";
    public static final String FORM_RUNTIME_ENDPOINT_URL = "/form-runtime/v1/forms";
    public static final String FORM_MODELER_ENDPOINT_URL = "/form-modeler/v1/history/forms/";
    public static final String LATEST = "latest";
    public static final String GET_FORM_SUCCESS = "Form fetched successfully";
    public static final String PROCESS_CREATION = "Process instance created successfully";
    public static final String PROCESS_RETRIEVED = "Process instance retrieved successfully";
    public static final String FORM_UPDATE_SUCCESS = "Form updated successfully";
    public static final String NO_FORM_AVAILABLE = "No Form Available";
    public static final String COMMENT_CREATED_SUCCESS = "Comment created successfully";
    public static final String FETCH_COMMENT_SUCCESS = "Comments fetched successfully";
    public static final String COMPLETE_TASK_SUCCESS = "Task completed successfully";
    public static final String DATE_TIME = "datetime";
    public static final String BEARER = "Bearer";
    public static final String USERNAME = "X-USERID";
    public static final String  DEPLOYMENT_URI="/camunda/engine-rest/deployment/create";
    public static final String CURRENT_PROJECT="com.techsophy.tsf.workflow.engine.camunda.*";

    //    cors config
    public static final String ALL_PATHS = "/**";

    //    rest connector
    public static final String GATEWAY_URI = "${gateway.uri}";
    public static final String REST_CONNECTOR = "rest-connector";

    //    default runtime context property provider
    public static final String GITLAB_ENVIRONMENT_URL = "GITLAB_ENVIRONMENT_URL";

    //    comment controller
    public static final String COMMENT_CREATE = "/comment/create";
    public static final String COMMENT = "/comment";

    //form action controller
    public static final String PROCESS_INSTANCE_START = "/process-instance/start";
    public static final String PROCESS_INSTANCE_RESUME = "/process-instance/resume";
    public static final String NEXT_TASK = "/task/next-task";
    public static final String TASK_COMPLETE = "/task/complete";
    public static final String TASK_URL = "/task";
    public static final String HISTORY_TASK_URL = "/history/task";
    public static final String PROCESS_INSTANCE = "/process-instance";
    public static final String SUB_TASKS = "/task/subtasks";

    // rule service
    public static final String DMN_ENGINE_BASEURL = "/rules/v1";
    public static final String EXECUTE_DMN_RULES = DMN_ENGINE_BASEURL + "/execute-dmn";

    // tp-app-util service
    public static final String APP_UTIL_BASEURL = "/util/v1";
    public static final String PROPERTIES_BY_PROJECT_NAME = APP_UTIL_BASEURL + "/properties/project";
    public static final String PROJECT_NAME_REQUEST_PARAM = "projectName";

    //DMS Service
    public static final String DMS_BASEURL = "/dms/v1";
    public static final String GENERATE_DOCUMENT = DMS_BASEURL + "/documents/publish";

    public static final String TYPE = "type";
    public static final String COMPONENT = "component";
    public static final String IS_DEFAULT = "isDefault";
    public static final String ACTIONS = "actions";
    public static final String SCREEN_TYPE_ID = "screenTypeId";
    public static final String DOCUMENT_ID_VAR = ":documentId";
    public static final String SCREEN_TYPE_ID_VAR = ":" + SCREEN_TYPE_ID;

    public static final String FORM_NAME = "formName";
    public static final String QUESTION = "question";
    public static final String CHECKLIST_INSTANCE_ID_VAR = ":checklistInstanceId";
    public static final String RESULT = "result";
    public static final String TENANT_ID = "tenant-id";
    public static final String DEPLOYMENT = "deployment";
    public static final String INVALID_TENANT = "Invalid tenant";
    public static final String NO_TENANT = "No tenant id found for the request";

    //    oauth2 and jwt aware request filter
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String ERROR = "error";
    public static final String ERROR_CODE = "errorCode";
    public static final String FORM = "form";
    public static final String DMS = "dms";
    public static final String FORM_RUNTIME = "form-runtime";
    public static final String GATEWAY_URL = "${gateway.uri}";
    public static final String WORKFLOW_ENGINE = "tp-workflow-engine";
    public static final String VERSION_V1 = "v1";
    public static final String WORKFLOW_ENGINE_API_VERSION_V1 = "workflow Engine API v1";

    public static final String TASK_COMPLETE_EVENT_LISTENER_CONDITION = "#delegateTask.eventName=='complete'";
    public static final String TASK_CREATE_EVENT_LISTENER_CONDITION = "#delegateTask.eventName=='create'";

    public static final String EXECUTION_START_EVENT_LISTENER_CONDITION = "#delegateExecution.eventName=='start'";
    public static final String EXECUTION_END_EVENT_LISTENER_CONDITION = "#delegateExecution.eventName=='end'";
    public static final String START_EVENT_ACTIVITY = "startEvent";
    public static final String END_EVENT_ACTIVITY = "endEvent";
    public static final String CHECKLIST_INSTANCE_ID_FOR_PROCESS_INSTANCE = "ChecklistInstanceIdForProcessInstance";

    public static final String DOCUMENT_TYPE_ID = "documentTypeId";
    public static final String DOCUMENT_PATH = "documentPath";
    public static final String DOCUMENT_NAME = "documentName";
    public static final String NAME = "name";
    public static final String PROCESS_INSTANCE_ID = "processInstanceId";
    public static final String DMS_UPLOAD_FILE = "/dms/v1/documents/file";
    public static final String TASK_ID = "taskId";
    public static final String FORM_VERSION = "formVersion";
    public static final String PREFERRED_USER_NAME = "preferred_username";
    public static final String GROUPS = "groups";

    //LoggingHandler
    public static final String CONTROLLER_CLASS_PATH = "execution(* com.techsophy.tsf.workflow.engine.camunda.controller.runtime..*(..))";
    public static final String SERVICE_CLASS_PATH = "execution(* com.techsophy.tsf.workflow.engine.camunda.service..*(..))";
    public static final String EXCEPTION = "ex";
    public static final String IS_INVOKED_IN_CONTROLLER = "() is invoked in controller ";
    public static final String IS_INVOKED_IN_SERVICE = "() is invoked in service ";
    public static final String EXECUTION_IS_COMPLETED_IN_CONTROLLER = "() execution is completed  in controller";
    public static final String EXECUTION_IS_COMPLETED_IN_SERVICE = "() execution is completed  in service";
    public static final String EXCEPTION_THROWN = "An exception has been thrown in ";
    public static final String CAUSE = "Cause : ";
    public static final String BRACKETS_IN_CONTROLLER = "() in controller";
    public static final String BRACKETS_IN_SERVICE = "() in service";
    /*LocaleConfig Constants*/
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String BASENAME_ERROR_MESSAGES = "classpath:errorMessages";
    public static final String BASENAME_MESSAGES = "classpath:messages";
    public static final Long CACHEMILLIS = 3600L;
    public static final Boolean USEDEFAULTCODEMESSAGE = true;
    public static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String TIME_ZONE = "UTC";

    // API Response variables
    public static final String ID = "id";

    public static final String DOCUMENT_ID = "documentId";

    // Properties
    public static final String DOCUMENT_TYPE_AND_TEMPLATE_MAPPING = "DOCUMENT_TYPE_AND_TEMPLATE_MAPPING";
    public static final String TEMPLATE_ID_PROP = "templateId";
    public static final String DOCUMENT_NAME_PROP = "documentName";
    public static final String DOCUMENT_DESCRIPTION_PROP = "documentDescription";
    public static final String DOCUMENT_PATH_PROP = "documentPath";
    public static final String CREATE_PATH_SUPPLIER = "createPatSupplier";

    // task assignment
    public static final String LOAD_BALANCE_ALGORITHM = "load-balance";
    public static final String ROUND_ROBIN_ALGORITHM = "round-robin";
    public static final String PARENT_TASK_ID = "parentTaskId";
    public static final String DESCRIPTION = "description";
    public static final String DELEGATION_STATE = "delegationState";
    public static final String CREATED = "created";
    public static final String DUE = "due";
    public static final String FOLLOW_UP = "followUp";
    public static final String ASSIGNEEE="assignee";
    public static final String UNABLE_TO_FIND_TENANT="Unable to find tenant";
}
