package com.techsophy.tsf.workflow.engine.camunda.service;

import com.techsophy.tsf.workflow.engine.camunda.dto.FormSchema;
import java.io.IOException;

public interface RuntimeFormService
{
    FormSchema fetchFormById(String formKey,String task) throws IOException;
}
