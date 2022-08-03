package com.techsophy.tsf.workflow.engine.camunda.controller.runtime;

import com.techsophy.tsf.workflow.engine.camunda.dto.FormSchema;
import com.techsophy.tsf.workflow.engine.camunda.model.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.BASE_URL;

@RequestMapping(BASE_URL)
public interface FormController
{
    /**
     * this api is used to get form by form key
     * @param formKey
     * @return ApiResponse
     */
    @GetMapping("/forms/{formKey}")
    ApiResponse<FormSchema> getFormByFormKey(@PathVariable String formKey,@RequestParam(required = false) String task);
}
