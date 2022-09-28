package com.techsophy.tsf.workflow.engine.camunda.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class TaskSerializerTest {
    @InjectMocks
    TaskSerializer taskSerializer;

    @Test
    void serializeTest() throws IOException {
        Task task = mock(Task.class);
        JsonGenerator jsonGenerator = mock(JsonGenerator.class);
        SerializerProvider serializerProvider = mock(SerializerProvider.class);
        taskSerializer.serialize(task,jsonGenerator,serializerProvider);
        Assertions.assertTrue(true);
    }
}
