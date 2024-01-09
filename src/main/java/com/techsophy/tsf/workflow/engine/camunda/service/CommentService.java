package com.techsophy.tsf.workflow.engine.camunda.service;

import org.camunda.bpm.engine.task.Comment;

import java.util.List;

/**
 * comment service
 */
public interface CommentService
{
    /**
     * create comment
     * @param taskId
     * @param processInstanceId
     * @param comment
     * @return Comment
     */
    Comment createComment(String taskId, String processInstanceId, String comment);

    /**
     * bet comments
     * @param processInstanceId
     * @param caseInstanceId
     * @param businessKey
     * @return List
     */
    List<Comment> getComments(String processInstanceId,String caseInstanceId,String businessKey);
}
