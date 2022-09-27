package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import org.assertj.core.api.Assertions;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricActivityInstanceQuery;
import org.camunda.bpm.engine.task.Comment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {
    @Mock
    TaskService taskService;

    @Mock
    HistoryService historyService;

    @InjectMocks
    CommentServiceImpl commentService;

    @Mock
    HistoricActivityInstanceQuery historicActivityInstanceQuery;

    @Test
    void createComment() {
        Comment actualResult = commentService.createComment("321", "72", "test");
        Comment expectedResult = taskService.createComment("321", "72", "test");
        Assertions.assertThat(actualResult).isEqualTo(expectedResult);
    }

//    @Test
//    void getComments() {
//        List<Object> stringList = List.of("test");
//        Mockito.when(historyService.createHistoricActivityInstanceQuery().processInstanceId(any()).list().stream().map(any()).filter(any()).collect(Collectors.toList())).thenReturn(stringList);
//        List<Comment> comments = commentService.getComments("321", "72", "test");
//        assertNotNull(comments);
//    }
}