package com.techsophy.tsf.workflow.engine.camunda.utils;

import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.camunda.bpm.model.xml.impl.ModelInstanceImpl;
import org.camunda.bpm.model.xml.impl.type.ModelElementTypeImpl;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CommonUtilsTest {
    @Mock
    CamundaProperty camundaProperty;
    @InjectMocks
    CommonUtils commonUtils;
    @Mock
    ModelInstanceImpl model;
    @Mock
    DomElement domElement;
    @Mock
    ModelElementTypeImpl modelType;

    @Test
    void isValidStringTest(){
        Assertions.assertTrue(commonUtils.isValidString("abc"));
        Assertions.assertFalse(commonUtils.isValidString(""));
    }

    @Test
    void isValidCamundaValueTest(){
        Assertions.assertFalse(commonUtils.isValidCamundaValue(Optional.ofNullable(camundaProperty)));
    }
}
