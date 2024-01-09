package com.techsophy.tsf.workflow.engine.camunda.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkflowEngineConstants
{
    //TokenUtilsTest
    public final static String FORM_KEY = "FormKeyTest";
    public final static String BUSINESS_KEY = "gsfttestprocess";

    //FormServiceConstants
    public final static @NotNull String TEST_ID ="1";
    public final static @NotNull Integer TEST_VERSION = 1;
    public static final String FORM_CONTENT = "testdata/form-content.json";
    public final static String TOKEN ="token";
    public final static String TEST = "test";
    public static final String LOCAL_HOST_URL="http://localhost:8080";
    public final static String KEY ="key";
    public final static String ARGS ="args";
    public static final String KIMS="KIMS";
    public static final String TECHSOPHY_PLATFORM="techsophy-platform";
    public static final String TEST_URL="http://localhost:8080/";
    public static final String CLIENT_ID="camunda-identity-service";
    public static final String CLIENT_SECRET="101";
    public static final String REALMS="realms/";
}
