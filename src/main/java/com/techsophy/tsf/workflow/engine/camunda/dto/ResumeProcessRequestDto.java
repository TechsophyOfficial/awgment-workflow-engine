package com.techsophy.tsf.workflow.engine.camunda.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResumeProcessRequestDto {

	private String processInstanceId;
	private String businessKey;
	private Map<String,Object> variables;
	private String message;
}
