package com.techsophy.tsf.workflow.engine.camunda.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FormConstants
{
    public final static String FORM_KEY = "FormKeyTest";
    public final static String FORM_NAME = "FormTest";
    public final static String BUSINESS_KEY = "gsfttestprocess";

    //FormServiceConstants
    public final static String TEST_TYPE_COMPONENT ="component";
    public static final String TEST_ACTIVE_PROFILE="test";
    public final static String TEST_TYPE_FORM ="form";
    public static final String TEST_FORMS_DATA = "testdata/form-schema1.json";
    public final static String TEST_ID_OR_NAME_LIKE ="abc";
    public final static @NotNull String TEST_ID ="1";
    public final static @NotNull String TEST_NAME ="form1";
    public final static Map TEST_COMPONENTS =Map.of("name","name");
    public final static @NotNull Integer TEST_VERSION = 1;
    public final static String TEST_CREATED_BY_ID = "1";
    public final static Instant TEST_CREATED_ON = Instant.now();
    public final static String TEST_UPDATED_BY_ID = "1";
    public final static  Instant TEST_UPDATED_ON = Instant.now();
    public final static String TEST_CREATED_BY_NAME ="user1";
    public final static String TEST_UPDATED_BY_NAME ="user1";

    public final static String KEY ="key";
    public final static String ARGS ="args";
}
