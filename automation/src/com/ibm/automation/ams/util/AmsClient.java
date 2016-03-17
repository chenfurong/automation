package com.ibm.automation.ams.util;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public interface AmsClient {

	String getCfg(String key);

	String getUrl(String... args);

	<T> T auth(Class<T> clazz, JsonNode credential);

	ArrayNode list(ArrayNode sort,
			JsonNode query, String... args);

	JsonNode save(JsonNode query, String... args);

	JsonNode drop(JsonNode query, String... args);
	
	<T extends JsonNode> T build();

	<T> List<T> converts(Class<T> clazz, JsonNode json);

	<T> T convert(Class<T> clazz, JsonNode json);

}