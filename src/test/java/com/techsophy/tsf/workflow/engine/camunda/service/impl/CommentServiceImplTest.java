package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import org.assertj.core.api.Assertions;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.*;
import org.camunda.bpm.engine.task.Comment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommentServiceImplTest {
    @Mock
    TaskService taskService;

    @Mock
    HistoryService historyService;

    @InjectMocks
    CommentServiceImpl commentService;

    @Test
    void createCommentTest() {
        Comment actualResult = commentService.createComment("321", "72", "test");
        Comment expectedResult = taskService.createComment("321", "72", "test");
        Assertions.assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getCommentsTestBasedOnProcessInstanceId() {
        HistoricActivityInstanceQuery historicActivityInstanceQuery = Mockito.mock(HistoricActivityInstanceQuery.class);
        HistoricProcessInstanceQuery historicProcessInstanceQuery = Mockito.mock(HistoricProcessInstanceQuery.class);
        Mockito.when(historyService.createHistoricActivityInstanceQuery()).thenReturn(historicActivityInstanceQuery);
        Mockito.when(historyService.createHistoricActivityInstanceQuery().processInstanceId("321")).thenReturn(historicActivityInstanceQuery);

        List<Comment> comments = commentService.getComments("321", "", "BUSINESS_KEY");
        assertNotNull(comments);
    }

    @Test
    void getCommentsTestBasedOnCaseInstanceId() {
        HistoricCaseActivityInstanceQuery historicCaseActivityInstanceQuery = Mockito.mock(HistoricCaseActivityInstanceQuery.class);
        Mockito.when(historyService.createHistoricCaseActivityInstanceQuery()).thenReturn(historicCaseActivityInstanceQuery);
        Mockito.when(historyService.createHistoricCaseActivityInstanceQuery().caseInstanceId("72")).thenReturn(historicCaseActivityInstanceQuery);
        List<Comment> comments = commentService.getComments("", "72", "");
        assertNotNull(comments);
    }

    @Test
    void getCommentsTestBasedOnBusineesKey() {
        HistoricProcessInstanceQuery processInstanceQuery = Mockito.mock(HistoricProcessInstanceQuery.class);
        HistoricProcessInstance processInstance = Mockito.mock(HistoricProcessInstance.class);
        HistoricCaseInstanceQuery historicCaseInstanceQuery = Mockito.mock(HistoricCaseInstanceQuery.class);
        HistoricCaseInstance historicCaseInstance = Mockito.mock(HistoricCaseInstance.class);
        HistoricProcessInstance historicProcessInstance = Mockito.mock(HistoricProcessInstance.class);
        HistoricActivityInstanceQuery historicActivityInstanceQuery = Mockito.mock(HistoricActivityInstanceQuery.class);
        HistoricActivityInstance historicActivityInstance = Mockito.mock(HistoricActivityInstance.class);
        HistoricCaseActivityInstanceQuery historicCaseActivityInstanceQuery = Mockito.mock(HistoricCaseActivityInstanceQuery.class);
        HistoricCaseActivityInstance historicCaseActivityInstance = Mockito.mock(HistoricCaseActivityInstance.class);

        Mockito.when(historyService.createHistoricProcessInstanceQuery()).thenReturn(processInstanceQuery);
        Mockito.when(historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey("BUSINESS_KEY")).thenReturn(processInstanceQuery);
        Mockito.when(historyService.createHistoricCaseInstanceQuery()).thenReturn(historicCaseInstanceQuery);
        Mockito.when(historyService.createHistoricCaseInstanceQuery().caseInstanceBusinessKey("BUSINESS_KEY")).thenReturn(historicCaseInstanceQuery);
        Mockito.when(historyService.createHistoricCaseInstanceQuery().caseInstanceBusinessKey("BUSINESS_KEY").list()).thenReturn(List.of(historicCaseInstance));


        Mockito.when(historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey("BUSINESS_KEY").list()).thenReturn(List.of(historicProcessInstance));
        Mockito.when(historicProcessInstance.getId()).thenReturn("val1");
        Mockito.when(historicActivityInstance.getTaskId()).thenReturn("task1");
        Mockito.when(historicCaseActivityInstance.getTaskId()).thenReturn("val1");
        Mockito.when(historicCaseInstance.getId()).thenReturn("val1");

        Mockito.when(historyService.createHistoricActivityInstanceQuery()).thenReturn(historicActivityInstanceQuery);
        Mockito.when(historicActivityInstanceQuery.processInstanceId("val1")).thenReturn(historicActivityInstanceQuery);
        Mockito.when(historyService.createHistoricActivityInstanceQuery().processInstanceId("val1").list()).thenReturn(List.of(historicActivityInstance));
        Mockito.when(historyService.createHistoricCaseActivityInstanceQuery()).thenReturn(historicCaseActivityInstanceQuery);
        Mockito.when(historicCaseActivityInstanceQuery.caseInstanceId("val1")).thenReturn(historicCaseActivityInstanceQuery);
        Mockito.when(historicCaseInstanceQuery.list()).thenReturn(List.of(historicCaseInstance));

        List<Comment> comments = commentService.getComments("", "", "BUSINESS_KEY");
        assertNotNull(comments);
    }
}
