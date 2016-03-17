package com.ibm.automation.ams.service;

import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.automation.core.util.PropertyUtil;
import com.ibm.automation.core.exception.ParamErrorException;


public interface AmsRestService {
	
	Properties amsCfg = PropertyUtil.getResourceFile("config/properties/ams2.properties");
	Properties cloudCfg = PropertyUtil.getResourceFile("config/properties/cloud.properties");

	public List<String> getCmdInfoByAddr(String names, String addrs,String exec)  throws ParamErrorException;
	public ObjectNode savePVCNode(ObjectNode node,String url);
	public  ArrayNode getList(ArrayNode sort, JsonNode query, String url);
	public ObjectNode getVersion(String version, String os);

	
}