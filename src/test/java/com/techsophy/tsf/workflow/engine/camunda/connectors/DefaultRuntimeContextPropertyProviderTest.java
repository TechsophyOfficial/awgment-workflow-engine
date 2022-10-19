package com.techsophy.tsf.workflow.engine.camunda.connectors;

import org.camunda.bpm.engine.ProcessEngine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefaultRuntimeContextPropertyProviderTest {
    @Mock
    ProcessEngine processEngine;
    @InjectMocks
    DefaultRuntimeContextPropertyProvider defaultRuntimeContextPropertyProvider;

    @Test
    void getEngineUrlTest(){
        String result = defaultRuntimeContextPropertyProvider.getEngineUrl();
        Assertions.assertNotNull(result);
    }

//    @Test
//    void getProcessInstanceId(){
//        Context context = mock(Context.class);
//        Command command =  mock(Command.class);
//        ProcessEngineConfigurationImpl processEngineConfiguration  = mock(ProcessEngineConfigurationImpl.class);
//        CommandInvocationContext commandInvocationContext =  new CommandInvocationContext(command,processEngineConfiguration);
//        when(processEngine.getProcessEngineConfiguration()).thenReturn(processEngineConfiguration);
//        //when(context.getCommandInvocationContext()).thenReturn(commandInvocationContext);
//        System.out.println(defaultRuntimeContextPropertyProvider.getBusinessKey());
//    }


}
