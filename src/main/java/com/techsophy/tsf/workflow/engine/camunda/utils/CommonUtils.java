package com.techsophy.tsf.workflow.engine.camunda.utils;

import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class CommonUtils
{
    private CommonUtils()
    {
    }

    public static boolean isValidString(String value)
    {
        return isNotEmpty(value) && isNotBlank(value);
    }

    public static boolean isValidCamundaValue(Optional<CamundaProperty> value)
    {
        return value.isPresent() && isNotEmpty(value.get().getCamundaValue()) && isNotBlank(value.get().getCamundaValue());
    }
}
