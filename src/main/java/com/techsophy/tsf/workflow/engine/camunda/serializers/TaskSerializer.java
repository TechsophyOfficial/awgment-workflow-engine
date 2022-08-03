package com.techsophy.tsf.workflow.engine.camunda.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.camunda.bpm.engine.task.Task;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;

import java.io.IOException;

public class TaskSerializer extends StdSerializer<Task> {

    public TaskSerializer() {
        this(null);
    }

    public TaskSerializer(Class<Task> t) {
        super(t);
    }

    @Override
    public void serialize(
            Task task, JsonGenerator jsonGenerator, SerializerProvider provider)
            throws IOException {

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(ID, task.getId());
        jsonGenerator.writeStringField(PARENT_TASK_ID, task.getParentTaskId());
        jsonGenerator.writeStringField(NAME, task.getName());
        jsonGenerator.writeStringField(DESCRIPTION, task.getDescription());
        jsonGenerator.writeStringField(ASSIGNEEE, task.getAssignee());
        jsonGenerator.writeStringField(DELEGATION_STATE, String.valueOf(task.getDelegationState()));
        jsonGenerator.writeStringField(CREATED, String.valueOf(task.getCreateTime()));
        jsonGenerator.writeStringField(DUE, String.valueOf(task.getDueDate()));
        jsonGenerator.writeStringField(FOLLOW_UP, String.valueOf(task.getFollowUpDate()));
        jsonGenerator.writeEndObject();
    }

}
