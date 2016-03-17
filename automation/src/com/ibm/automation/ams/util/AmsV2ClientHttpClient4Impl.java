package com.ibm.automation.ams.util;


import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.ibm.automation.core.util.PropertyUtil;

public class AmsV2ClientHttpClient4Impl extends BaseHttpClient4Impl implements
		AmsClient {

	Logger log = Logger.getLogger(AmsV2ClientHttpClient4Impl.class);

	public static final String REST_CHARSET = "utf-8";

	private Properties config = new Properties();

	public AmsV2ClientHttpClient4Impl() {
		this("AMS2_HOST");
	}

	public AmsV2ClientHttpClient4Impl(String str) {
		this.om = new ObjectMapper();
		
		Properties cloudCfg = PropertyUtil.getResourceFile("config/properties/cloud.properties");
		String endPoint = cloudCfg.getProperty(str);
		
		this.endpoint = endPoint; 
	/*	try {
			is = this.getClass().getResourceAsStream(
					"/config/properties/cloud.properties");
			this.config.load(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}*/
	}

	public String getCfg(String key) {
		if (this.config.containsKey(key)) {
			return this.config.getProperty(key);
		}

		return null;
	}

	public String getUrl(String... args) {

		if (null == args || args.length < 1) {
			return null;
		}
		String table = null;
		String url = null;
		if (args.length == 1) {
			table = args[0];
			url = String.format("%s/%s", endpoint, table);
		} else if (args.length == 3) {
			table = args[0];
			String attrKey = args[1];
			String attrVal = args[2];
			if (attrKey != null && attrVal != null) {
				url = String.format("%s/%s/%s/%s", endpoint, table, attrKey,
						attrVal);
			} else if (attrKey == null && attrVal == null) {
				url = String.format("%s/%s", endpoint, table);
			}
		}
		return url;
	}

	@Override
	public <T> T auth(Class<T> clazz, JsonNode credential) {
		String url = getUrl("auth");
		ObjectNode resp = null;
		ObjectNode req = om.createObjectNode();
		ObjectNode headers = req.putObject("headers");
		headers.put("Content-Type", "application/json");
		req.put("charset", REST_CHARSET);
		req.put("method", "POST");
		req.put("url", url);
		req.set("payload", credential);

		try {
			resp = (ObjectNode) this.query(req);
			if (null != resp && resp.has("statusCode")
					&& resp.get("statusCode").intValue() == 200) {
				return convert(clazz, resp.get("payload"));
			}
		} catch (Exception ex) {
			log.error("unexpected auth error: ", ex);
			// throw ex;
		}
		return null;
	}

	public ArrayNode list(ArrayNode sort, JsonNode query, String... args) {

		String url = getUrl(args);
		ObjectNode resp = null;

		ObjectNode req = om.createObjectNode();
		ObjectNode headers = req.putObject("headers");
		headers.put("Content-Type", "application/json");
		if (null != sort) {
			// TODO
			String header = sort.toString();
			headers.put("X-Sort-Attrs", header);
		}
		this.setTokenHeader(headers);
		req.put("charset", REST_CHARSET);
		req.put("method", "GET");
		req.put("url", url);
		if (null != query) {
			req.put("payload", query.toString());
		}

		resp = (ObjectNode) this.query(req);

		return (ArrayNode) resp.get("payload");
	}
	
	

	@Override
	public JsonNode save(JsonNode query, String... args) {

		String url = getUrl(args);
		ObjectNode resp = null;

		ObjectNode req = om.createObjectNode();
		ObjectNode headers = req.putObject("headers");
		headers.put("Content-Type", "application/json");

		this.setTokenHeader(headers);
		req.put("charset", REST_CHARSET);
		req.put("method", "POST");
		req.put("url", url);
		if (null != query) {
			req.set("payload", query);
		}
		resp = (ObjectNode) this.query(req);
		return resp.get("payload");
	}

	@Override
	public JsonNode drop(JsonNode query, String... args) {
		String url = getUrl(args);
		ObjectNode resp = null;

		ObjectNode req = om.createObjectNode();
		ObjectNode headers = req.putObject("headers");
		headers.put("Content-Type", "application/json");
		
		this.setTokenHeader(headers);
		req.put("charset", REST_CHARSET);
		req.put("method", "DELETE");
		req.put("url", url);
		if (null != query) {
			req.set("payload", query);
		}
		resp = (ObjectNode) this.query(req);
		return resp.get("payload");
	}

	@Override
	public <T extends JsonNode> T build() {
		T t = (T) om.createObjectNode();	
		return t;
	}

	public <T> T convert(Class<T> clazz, JsonNode json) {
		try {
			JavaType jt = om.getTypeFactory().constructType(clazz);
			return om.convertValue(json, jt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public <T> List<T> converts(Class<T> clazz, JsonNode json) {

		try {
			if (json.isArray()) {

				CollectionType ct = om.getTypeFactory()
						.constructCollectionType(LinkedList.class, clazz);
				return om.convertValue(json, ct);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private String endpoint;
	
	private ObjectMapper om;

	public String hasAmsTokenId() {
		return null;
	}

	private void setTokenHeader(ObjectNode headers) {

	}

}