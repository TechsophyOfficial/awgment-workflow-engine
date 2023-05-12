package com.techsophy.tsf.workflow.engine.camunda.service;

import com.techsophy.tsf.workflow.engine.camunda.dto.ClientDetails;


public interface FetchClientCredentials {

    ClientDetails fetchClientDetails(String tenant, boolean refreshSecret) ;

}


