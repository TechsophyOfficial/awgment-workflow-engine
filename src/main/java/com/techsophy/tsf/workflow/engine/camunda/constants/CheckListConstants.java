package com.techsophy.tsf.workflow.engine.camunda.constants;

public class CheckListConstants
{
    private CheckListConstants()
    {
    }

    public static final String CHECKLIST_BASE_URL = "/checklist-engine/v1";
    public static final String INVOKE_CHECKLIST = CHECKLIST_BASE_URL + "/checklist-instances/start";
    public static final String GET_ITEM_INSTANCES = CHECKLIST_BASE_URL + "/checklist-item-instances";
    public static final String GET_CHECKLIST_INSTANCE_BY_ID = CHECKLIST_BASE_URL + "/checklist-instances/%s";

    public static final String GET_CHECKLIST_BY_ID = CHECKLIST_BASE_URL + "/checklist/%s";

    public static final String COMPLETE_CHECKLIST_ITEMS = CHECKLIST_BASE_URL + "/checklist-item-instances/complete";

    public static final String CHECKLIST_INSTANCE_ID_PARAM = "checklist-instance-id";

    public static final String CHECKLIST_ID = "checklistId";
    public static final String BUSINESS_KEY = "businessKey";
    public static final String DATA = "data";
    public static final String CHECKLIST_INSTANCE_ID = "checklistInstanceId";
    public static final String CHECKLIST_ITEM_INSTANCE_ID_LIST = "checklistItemInstanceIdsList";
    public static final String ID_LIST = "idList";
    public static final String DOCUMENT_ID = "documentId";

    public static final String COMPLETE = "Complete";
    public static final String COMPLETE_WITH_OVERRIDE = "Complete w/ Override";
    public static final String ASSIGNEE = "assignee";

    public static final String CHECKLIST_ITEMS_INCOMPLETE_EXCEPTION = "Unable to complete as checklist items are pending";
}
