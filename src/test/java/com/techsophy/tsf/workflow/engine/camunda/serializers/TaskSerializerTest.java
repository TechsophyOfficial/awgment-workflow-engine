package com.techsophy.tsf.workflow.engine.camunda.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.camunda.bpm.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskSerializerTest {
    @InjectMocks
    TaskSerializer taskSerializer;

    @Test
    void serializeTest() throws IOException {
        Task task = mock(Task.class);
        JsonGenerator jsonGenerator = mock(JsonGenerator.class);
        SerializerProvider serializerProvider = mock(SerializerProvider.class);
        taskSerializer.serialize(task,jsonGenerator,serializerProvider);
        verify(jsonGenerator, times(1)).writeStartObject();
    }
}
