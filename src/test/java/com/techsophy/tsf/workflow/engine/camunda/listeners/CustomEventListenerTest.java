package com.techsophy.tsf.workflow.engine.camunda.listeners;

import com.techsophy.tsf.workflow.engine.camunda.config.GlobalMessageSource;
import com.techsophy.tsf.workflow.engine.camunda.model.ChecklistInstanceResponseModel;
import com.techsophy.tsf.workflow.engine.camunda.service.ChecklistService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CheckListConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
public class CustomEventListenerTest {
    @Mock
    DelegateTask delegateTask;
    @Mock
    GlobalMessageSource globalMessageSource;
    @Mock
    ChecklistService checklistService;
    @Mock
    DelegateExecution delegateExecution;
    @InjectMocks
    CustomEventListener customEventListener;

    Map<String,Object> map = new HashMap<>();
    List<String> list = new ArrayList<>();
    @BeforeEach
    public void init()
    {
        map.put("key","abc");
        map.put("key2","abc");
        list.add("abc");
        list.add("qwe");
        delegateTask.setName("delegate");
        delegateTask.setVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST,CHECKLIST_ITEM_INSTANCE_ID_LIST);
    }


    @Test
    void onTaskCompleteEventTest(){
        ChecklistInstanceResponseModel checklistInstanceResponseModel = new ChecklistInstanceResponseModel("1",true,true,COMPLETE,CHECKLIST_ID,"cat",ASSIGNEE,true,"reviewer","type",true,true,"createdbyid","updatedbyid","createdby","updatedby", Instant.now(),Instant.now(),map,list,List.of(map));
        Mockito.when(delegateTask.hasVariableLocal(CHECKLIST_INSTANCE_ID)).thenReturn(true);
        Mockito.when(delegateTask.hasVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST)).thenReturn(true);
        Mockito.when(delegateTask.getVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST)).thenReturn(List.of("abc"));
        Mockito.when(delegateTask.getVariable(CHECKLIST_INSTANCE_ID)).thenReturn("abc");
        Mockito.when(checklistService.getChecklistInstanceById(delegateTask.getVariable(CHECKLIST_INSTANCE_ID).toString())).thenReturn(checklistInstanceResponseModel);
        customEventListener.onTaskCompleteEvent(delegateTask);
    }

    @Test
    void onTaskCompleteEventExceptionTest(){
        ChecklistInstanceResponseModel checklistInstanceResponseModel = new ChecklistInstanceResponseModel("1",true,true,"ok","1","cat",ASSIGNEE,true,"reviewer","type",true,true,"createdbyid","updatedbyid","createdby","updatedby", Instant.now(),Instant.now(),map,list,List.of(map));
        Mockito.when(delegateTask.hasVariableLocal(CHECKLIST_INSTANCE_ID)).thenReturn(true);
        Mockito.when(delegateTask.hasVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST)).thenReturn(true);
        Mockito.when(delegateTask.getVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST)).thenReturn(List.of("abc"));
        Mockito.when(delegateTask.getVariable(CHECKLIST_INSTANCE_ID)).thenReturn("abc");
        Mockito.when(checklistService.getChecklistInstanceById(delegateTask.getVariable(CHECKLIST_INSTANCE_ID).toString())).thenReturn(checklistInstanceResponseModel);
        Assertions.assertThrows(IllegalArgumentException.class,()->customEventListener.onTaskCompleteEvent(delegateTask));
    }

    @Test
    void onTaskCompleteEventIllegalArgumentExceptionTest(){
        Mockito.when(delegateTask.getExecution()).thenReturn(delegateExecution);
        Mockito.when(delegateTask.hasVariableLocal(CHECKLIST_INSTANCE_ID)).thenReturn(true);
        Mockito.when(delegateTask.hasVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST)).thenReturn(true);
        Mockito.when(delegateTask.getVariable(CHECKLIST_ITEM_INSTANCE_ID_LIST)).thenReturn("abc");
        Mockito.when(globalMessageSource.get(anyString(),any())).thenReturn("abc");
        Assertions.assertThrows(IllegalArgumentException.class,()->customEventListener.onTaskCompleteEvent(delegateTask));
    }

//    @Test
//    void onTaskCreatedEventNullTest(){
//        UserTask userTask = mock(UserTask.class);
//        Mockito.when(delegateTask.getBpmnModelElementInstance()).thenReturn(userTask);
//        customEventListener.onTaskCreatedEvent(delegateTask);
//    }

//    @Test
//    void onTaskCreatedEventTest(){
//
//    }
}
