package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.techsophy.tsf.workflow.engine.camunda.config.GlobalMessageSource;
import com.techsophy.tsf.workflow.engine.camunda.exception.InvalidAlgorithmException;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstanceQuery;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.identity.UserQuery;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.TaskQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskAssignmentServiceTest {
    @Mock
    TaskService taskService;
    @Mock
    User user1;
    @Mock
    UserQuery userQuery;
    @Mock
    IdentityService identityService;
    @Mock
    HistoryService historyService;
    @Mock
    HistoricTaskInstanceQuery historicTaskInstanceQuery;
    @Mock
    TaskQuery taskQuery;
    @Mock
    HistoricTaskInstance historicTaskInstance;
    @Mock
    GlobalMessageSource globalMessageSource;
    @Mock
    IdentityLink identityLink;
    @Mock
    DelegateTask delegateTask;
    @InjectMocks
    TaskAssignmentService taskAssignmentService;

    @Test
    void setAssigneeByAlgorithmExceptionTest(){
        String algorithm = "";
        Assertions.assertThrows(InvalidAlgorithmException.class,()->taskAssignmentService.setAssigneeByAlgorithm(delegateTask,algorithm));
    }
    @Test
    void setAssigneeByAlgorithmEmptyUserTest(){
        IdentityLink identityLink = mock(IdentityLink.class);
        String algorithm = "round-robin";
        Mockito.when(delegateTask.getCandidates()).thenReturn(Set.of(identityLink));
        Mockito.when(identityLink.getType()).thenReturn("candidate");
        Assertions.assertThrows(IllegalArgumentException.class,()->taskAssignmentService.setAssigneeByAlgorithm(delegateTask,algorithm));
    }
    @Test
    void setAssigneeByAlgorithmWithUserTest(){
        user1.setId("1");
        user1.setEmail("abc");
        user1.setFirstName("qwe");
        user1.setLastName("asd");
        user1.setPassword("pass");

        String algorithm = "round-robin";

        Mockito.when(delegateTask.getCandidates()).thenReturn(Set.of(identityLink));
        Mockito.when(identityLink.getType()).thenReturn("candidate");
        Mockito.when(identityLink.getGroupId()).thenReturn("1");
        Mockito.when(identityService.createUserQuery()).thenReturn(userQuery);
        Mockito.when(userQuery.memberOfGroup("1")).thenReturn(userQuery);
        Mockito.when(userQuery.list()).thenReturn(List.of(user1));
        Mockito.when(user1.getId()).thenReturn("qwe");
        Mockito.when(historyService.createHistoricTaskInstanceQuery()).thenReturn(historicTaskInstanceQuery);
        Mockito.when(delegateTask.getTaskDefinitionKey()).thenReturn("abc");
        Mockito.when(historicTaskInstanceQuery.taskDefinitionKey("abc")).thenReturn(historicTaskInstanceQuery);
        Mockito.when(historicTaskInstanceQuery.taskAssigneeLike("%")).thenReturn(historicTaskInstanceQuery);
        Mockito.when(historicTaskInstanceQuery.orderByHistoricActivityInstanceStartTime()).thenReturn(historicTaskInstanceQuery);
        Mockito.when(historicTaskInstanceQuery.desc()).thenReturn(historicTaskInstanceQuery);
        Mockito.when(historicTaskInstanceQuery.listPage(0, 1)).thenReturn(List.of(historicTaskInstance));
        Mockito.when(List.of(historicTaskInstance).get(0).getAssignee()).thenReturn("qwe");
        taskAssignmentService.setAssigneeByAlgorithm(delegateTask,algorithm);
        verify(historyService,times(1)).createHistoricTaskInstanceQuery();
    }

    @Test
    void setAssigneeByLoadBalanceAlgorithmTest(){
        user1.setId("1");
        user1.setEmail("abc");
        user1.setFirstName("qwe");
        user1.setLastName("asd");
        user1.setPassword("pass");
        String algorithm = "load-balance";
        Mockito.when(delegateTask.getCandidates()).thenReturn(Set.of(identityLink));
        Mockito.when(identityLink.getType()).thenReturn("candidate");
        Mockito.when(identityLink.getGroupId()).thenReturn("1");
        Mockito.when(identityService.createUserQuery()).thenReturn(userQuery);
        Mockito.when(userQuery.memberOfGroup("1")).thenReturn(userQuery);
        Mockito.when(userQuery.list()).thenReturn(List.of(user1));
        Mockito.when(user1.getId()).thenReturn("qwe");
        Mockito.when(taskService.createTaskQuery()).thenReturn(taskQuery);
        Mockito.when(delegateTask.getTaskDefinitionKey()).thenReturn("abc");
        Mockito.when(taskQuery.taskDefinitionKey(any())).thenReturn(taskQuery);
        Mockito.when(taskQuery.taskAssignee(any())).thenReturn(taskQuery);
        Mockito.when(taskQuery.count()).thenReturn(2L);
        taskAssignmentService.setAssigneeByAlgorithm(delegateTask,algorithm);
        verify(taskService,times(1)).createTaskQuery();
    }

    @Test
    void setAssigneeByAlgorithmInvalidExceptionTest(){
        String algorithm = "algo";
        Assertions.assertThrows(InvalidAlgorithmException.class,()->taskAssignmentService.setAssigneeByAlgorithm(delegateTask,algorithm));
    }
}
