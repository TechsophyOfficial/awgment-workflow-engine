package com.techsophy.tsf.workflow.engine.camunda.service;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

public interface TokenSupplier extends Supplier<String>
{
    @Override
    default String get()
    {
        return this.getToken();
    }

    String getToken() ;
}
