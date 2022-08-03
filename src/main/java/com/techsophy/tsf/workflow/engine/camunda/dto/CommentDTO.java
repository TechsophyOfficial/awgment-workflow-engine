package com.techsophy.tsf.workflow.engine.camunda.dto;

import lombok.Value;

/**
 * comment DTO
 */
@Value
public class CommentDTO
{
    private final String taskId;

    private final String processInstanceId;

    private final String comment;
}
