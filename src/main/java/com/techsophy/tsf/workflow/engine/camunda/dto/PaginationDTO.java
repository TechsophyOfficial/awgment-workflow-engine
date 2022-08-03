package com.techsophy.tsf.workflow.engine.camunda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaginationDTO<T>
{
    private final T rows;
    private final Integer page;
    private final Integer size;
    private final Integer numberOfRecords;
    private final long totalPages;
    private final Long totalRecords;
}
