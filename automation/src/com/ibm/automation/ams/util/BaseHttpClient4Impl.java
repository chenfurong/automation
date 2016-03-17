package com.ibm.automation.ams.util;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BaseHttpClient4Impl {

	private ObjectMapper om = new ObjectMapper();

	public JsonNode query(final JsonNode req) {

		ObjectNode resp = om.createObjectNode();

		String url = req.get("url").asText();

		String payload = req.has("payload") ? req.get("payload").toString()
				: null;
		//System.out.println("p:" + req.get("payload"));

		String charset = req.has("charset") ? req.get("charset").textValue()
				: "utf-8";
		;

		JsonNode headers = req.has("headers") ? req.get("headers") : om
				.createObjectNode();

		String mimeType = headers.has("Content-Type") ? headers.get(
				"Content-Type").asText() : "application/json";

		HttpRequestBase method = null;

		try {

			if (null != payload) {

				HttpEntity he = new StringEntity(payload, ContentType.create(
						mimeType, Charset.forName(charset)));

				HttpEntityEnclosingRequestBase hrb = new HttpEntityEnclosingRequestBase() {

					public String getMethod() {
						return req.has("method") ? req.get("method").asText()
								: "GET";
					}

				};

				hrb.setEntity(he);

				method = hrb;

			} else {

				method = new HttpRequestBase() {

					public String getMethod() {
						return req.has("method") ? req.get("method").asText()
								: "GET";
					}

				};
			}

			method.setURI(new URI(url));

			for (Iterator<String> itr = headers.fieldNames(); itr.hasNext();) {
				String key = itr.next();
				if (!key.equalsIgnoreCase("Content-Type")) {
					Header[] values = method.getHeaders(key);
					if (null != values && values.length > 0) {
						method.addHeader(key, headers.get(key).asText());
					} else {
						method.setHeader(key, headers.get(key).asText());
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		HttpClientContext hcc = null;
		CloseableHttpClient hc = null;

		resp.put("url", url);
		resp.put("method", method.getMethod());
		try {
			hc = HttpClients.createDefault();
			hcc = HttpClientContext.create();
			CloseableHttpResponse chr = hc.execute(method, hcc);
			ObjectNode respHeaders = resp.putObject("headers");
			for (Header header : chr.getAllHeaders()) {
				String headerKey = header.getName();
				String headerVal = respHeaders.has(headerKey) ? respHeaders
						.get(headerKey).asText() : null;
				if (null == headerVal) {
					respHeaders.put(headerKey, header.getValue());
				} else {
					respHeaders.put(headerKey,
							headerVal + ";" + header.getValue());
				}
			}
			resp.set("payload", om.readTree(chr.getEntity().getContent()));
			resp.put("statusCode", chr.getStatusLine().getStatusCode());

		} catch (ClientProtocolException e) {
			e.printStackTrace();
			resp.put("statusCode", 500);
		} catch (IOException e) {
			e.printStackTrace();
			resp.put("statusCode", 500);
		} finally {
			HttpClientUtils.closeQuietly(hc);
		}

		return resp;
	}

}
