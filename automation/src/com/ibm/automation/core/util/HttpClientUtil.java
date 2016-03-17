package com.ibm.automation.core.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.Header;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;

import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.ibm.automation.core.exception.NetWorkException;
import com.ibm.automation.core.exception.ParamErrorException;




public class HttpClientUtil {
	private static Logger logger = Logger.getLogger(HttpClientUtil.class);
	/**
	 * get方法
	 * 
	 * @param tokenId
	 *            tokenid
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public static String getMethod(String tokenId, String url) throws ParamErrorException {
		HttpGet httpGet = new HttpGet(url);
		httpGet.addHeader("Content-Type", "application/json");
		httpGet.addHeader("X-Auth-Token", tokenId);
		return executeMethod(httpGet);

	}
	
	/**
	 * post
	 * 
	 * @param tokenId
	 * @param url
	 * @param bodyJson
	 * @return
	 * @throws ParamErrorException
	 */
	public static String postMethod(String tokenId, String url, String bodyJson) throws ParamErrorException {
		HttpPost postMethod = new HttpPost(url);

		postMethod.addHeader("Content-Type", "application/json");
		postMethod.addHeader("X-Auth-Token", tokenId);

		try {
			postMethod.setEntity(new StringEntity(bodyJson));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return executeMethod(postMethod);
	}

	/**
	 * post
	 * 
	 * @param tokenId
	 * @param url
	 * @param bodyJson
	 * @return
	 * @throws ParamErrorException
	 */
	public static String postMethod(String url, String bodyJson) throws ParamErrorException {

		HttpPost postMethod = new HttpPost(url);

		postMethod.addHeader("Content-Type", "application/json");

		StringEntity se = null;
		try {
			se = new StringEntity(bodyJson);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		postMethod.setEntity(se);

		return executeMethod(postMethod);
	}

	public static String putMethod(String tokenId, String url) throws ParamErrorException {

		HttpPut putMethod = new HttpPut(url);

		putMethod.addHeader("Content-Type", "application/json");
		putMethod.addHeader("X-Auth-Token", tokenId);

		return executeMethod(putMethod);

	}

	public static String deleteMethod(String tokenId, String url) throws ParamErrorException {
		HttpDelete deleteMethod = new HttpDelete(url);
		deleteMethod.addHeader("Content-Type", "application/json");
		deleteMethod.addHeader("X-Auth-Token", tokenId);
		return executeMethod(deleteMethod);
	}

	public static String updateMethod(String tokenId, String url, String bodyJson) throws ParamErrorException {

		HttpPost postMethod = new HttpPost(url);

		postMethod.addHeader("Content-Type", "application/json");
		postMethod.addHeader("X-Auth-Token", tokenId);

		StringEntity se = null;
		try {
			se = new StringEntity(bodyJson);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		postMethod.setEntity(se);

		return executeMethod(postMethod);

		
	}

	public static String patchMethod(String tokenId, String url, String bodyJson) throws ParamErrorException {

		HttpPatch patchMethod = new HttpPatch(url);

		patchMethod.addHeader("Content-Type", "application/json");
		patchMethod.addHeader("X-Auth-Token", tokenId);

		StringEntity se = null;
		try {
			se = new StringEntity(bodyJson);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		patchMethod.setEntity(se);

		return executeMethod(patchMethod);

		/*
		 * logger.debug("url = " + url); logger.debug("tokenId===" + tokenId);
		 * PutMethod putMethod = new PutMethod(); // PatchMethod patchMethod =
		 * new PatchMethod(url); putMethod.addRequestHeader("Content-Type",
		 * "application/json"); if (tokenId != null) {
		 * putMethod.setRequestHeader("X-Auth-Token", tokenId); } if (bodyJson
		 * != null) { logger.debug("post json=" + bodyJson); try { bodyJson =
		 * StringUtil.unicodeEscape(bodyJson); logger.debug("post json=" +
		 * bodyJson); putMethod.setRequestEntity(new
		 * StringRequestEntity(bodyJson, "application/json", null)); } catch
		 * (UnsupportedEncodingException e) { e.printStackTrace(); throw new
		 * OPSSystemException("HttpClientUtil在编码时出现异常，类型为：" + e.getMessage(),
		 * e); } } return executeMethod(putMethod);
		 */
	}

	/**
	 * post方法。
	 * 
	 * @param tokenId
	 * @param url
	 * @param bodyJson
	 * @param headerMap
	 * @param postFile
	 * @return
	 * @throws OPSTOperationException
	 */
	public static String postMethod(String tokenId, String url, String bodyJson, Map<String, String> headerMap,
			File postFile) throws ParamErrorException {

		HttpPost pothMethod = new HttpPost(url);

		pothMethod.addHeader("Content-Type", "application/json");
		pothMethod.addHeader("X-Auth-Token", tokenId);

		StringEntity se = null;
		try {
			se = new StringEntity(bodyJson);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		pothMethod.setEntity(se);

		try {
			return executeMethod(pothMethod);
		} catch (ParamErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

		/*
		 * logger.debug("url=" + url); PostMethod postMethod = new
		 * PostMethod(url); int status = 0; if (tokenId != null) {
		 * postMethod.setRequestHeader("X-Auth-Token", tokenId); } if (headerMap
		 * != null && headerMap.size() > 0) { for (String key :
		 * headerMap.keySet()) { if (key != null) { String value =
		 * headerMap.get(key); postMethod.setRequestHeader(key, value); } } }
		 * try { if (bodyJson != null) { logger.debug("post json=" + bodyJson);
		 * bodyJson = StringUtil.unicodeEscape(bodyJson);
		 * logger.debug("post json=" + bodyJson);
		 * postMethod.setRequestEntity(new StringRequestEntity(bodyJson,
		 * "application/json", null)); } if (postFile != null) { Part[] parts =
		 * { new FilePart(postFile.getName(), postFile) };
		 * postMethod.setRequestEntity(new MultipartRequestEntity(parts,
		 * postMethod.getParams())); } return executeMethod(postMethod); } catch
		 * (Exception e) { e.printStackTrace(); String para = "url=" + url +
		 * ";status=" + status; if (bodyJson != null) { para += ";bodyJson=" +
		 * bodyJson; } // throw new OPSTOperationException(para); throw new
		 * OPSTOperationException(e.getMessage() + para, e); }
		 */
	}

	/**
	 * post方法。
	 * 
	 * @param tokenId
	 * @param url
	 * @param bodyJson
	 * @param headerMap
	 * @param postFile
	 * @return
	 * @throws OPSTOperationException
	 */
	public static String updateMethod(String tokenId, String url, String bodyJson, Map<String, String> headerMap)
			throws ParamErrorException {

		HttpPost postMethod = new HttpPost(url);

		postMethod.addHeader("Content-Type", "application/json");
		postMethod.addHeader("X-Auth-Token", tokenId);
		if (headerMap != null && headerMap.size() > 0) {
			for (String key : headerMap.keySet()) {
				if (key != null) {
					String value = headerMap.get(key);
					postMethod.addHeader(key, value);
				}
			}
		}
		StringEntity se = null;
		try {
			se = new StringEntity(bodyJson);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		postMethod.setEntity(se);

		try {
			return executeMethod(postMethod);
		} catch (ParamErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

		/*
		 * logger.debug("url = " + url); logger.debug("tokenId===" + tokenId);
		 * PutMethod putMethod = new PutMethod(url); int status = 0; if (tokenId
		 * != null) { putMethod.setRequestHeader("X-Auth-Token", tokenId); } if
		 * (headerMap != null && headerMap.size() > 0) { for (String key :
		 * headerMap.keySet()) { if (key != null) { String value =
		 * headerMap.get(key); putMethod.setRequestHeader(key, value); } } } try
		 * { if (bodyJson != null) { logger.debug("post json=" + bodyJson);
		 * bodyJson = StringUtil.unicodeEscape(bodyJson);
		 * logger.debug("post json=" + bodyJson); putMethod.setRequestEntity(new
		 * StringRequestEntity(bodyJson, "application/json", null)); } return
		 * executeMethod(putMethod); } catch (Exception e) {
		 * e.printStackTrace(); String para = "url=" + url + ";status=" +
		 * status; if (bodyJson != null) { para = new
		 * StringBuilder().append(para
		 * ).append(";bodyJson=").append(bodyJson).toString(); //
		 * para+=";bodyJson="+bodyJson; } throw new
		 * OPSTOperationException(para); }
		 */
	}

	public static String executeMethod(HttpRequestBase method) throws  ParamErrorException {

		CloseableHttpClient httpclient = (CloseableHttpClient) createSSLClientDefault();
		int status = 0;
		CloseableHttpResponse response;
		String resStringString = "";
		try {
			response = httpclient.execute(method);
			status = response.getStatusLine().getStatusCode();
			;
			// 获取响应消息实体
			HttpEntity entity = response.getEntity();
			resStringString = EntityUtils.toString(entity);
			response.getAllHeaders();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
			logger.info("网络超时，请检查和服务器的连接");
		}
		if(status == 0 && resStringString.equals(""))//io超时
		{
			throw new  NetWorkException(new StringBuilder().append("网络超时，请检查和服务器的连接").append(resStringString)
					.toString());
		}

		if (status == 400) {
			throw new NetWorkException(new StringBuilder().append("restful返回的错误信息").append(resStringString)
					.toString());
		}
		if (status == 401) {
			throw new NetWorkException(new StringBuilder().append("restful返回的错误信息").append(resStringString)
					.toString());
		}
		if (status == 403) {
			throw new NetWorkException(new StringBuilder().append("restful返回的错误信息").append(resStringString)
					.toString());
		}
		if (status == 404) {
			throw new NetWorkException(new StringBuilder().append("restful返回的错误信息").append(resStringString)
					.toString());
		}
		if (status == 405) {
			throw new NetWorkException(new StringBuilder().append("restful返回的错误信息").append(resStringString)
					.toString());
		}
		if (status == 413) {
			throw new NetWorkException(new StringBuilder().append("restful返回的错误信息").append(resStringString)
					.toString());
		}
		if (status == 500) {
			throw new NetWorkException(new StringBuilder().append("restful返回的错误信息").append(resStringString)
					.toString());
		}

		method.releaseConnection();
		// System.out.println(response);
		return resStringString;
	}

	public static HttpClient createSSLClientDefault() {

		X509TrustManager tm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] xcs, String string) {
			}

			public void checkServerTrusted(X509Certificate[] xcs, String string) {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		SSLContext sslcontext = null;
		try {
			sslcontext = SSLContext.getInstance("TLSv1.2");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			sslcontext.init(null, new TrustManager[] { tm }, null);
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
		
		LayeredConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,SSLConnectionSocketFactory.STRICT_HOSTNAME_VERIFIER); 
		
		
		Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", plainsf).register("https", sslsf).build();
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(r);
		// Increase max total connection to 200
		cm.setMaxTotal(200);
		// Increase default max connection per route to 20
		cm.setDefaultMaxPerRoute(20);
		HttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
		return httpClient;

	}

	public void test() throws InterruptedException, ExecutionException, IOException {
		HttpClientContext context = HttpClientContext.create();
		HttpClientConnectionManager connMrg = new BasicHttpClientConnectionManager();
		HttpRoute route = new HttpRoute(new HttpHost("localhost", 80));
		// Request new connection. This can be a long process
		ConnectionRequest connRequest = connMrg.requestConnection(route, null);
		// Wait for connection up to 10 sec
		HttpClientConnection conn = connRequest.get(10, TimeUnit.SECONDS);
		try {
			// If not open
			if (!conn.isOpen()) {
				// establish connection based on its route info
				connMrg.connect(conn, route, 1000, context);
				// and mark it as route complete
				connMrg.routeComplete(conn, route, context);
			}
			// Do useful things with the connection.
		} finally {
			connMrg.releaseConnection(conn, null, 1, TimeUnit.MINUTES);
		}
	}

	public static Map<String, Object> executeMethodReturnWithHeader(HttpRequestBase method) throws ParamErrorException {
		Map<String, Object> map = new HashMap();

		CloseableHttpClient httpclient = (CloseableHttpClient) createSSLClientDefault();
		int status = 0;
		CloseableHttpResponse response;
		String resStringString = "";
		try {
			Date beginString = new Date();
			System.out.println("begin:" + beginString.toGMTString());
			response = httpclient.execute(method);

			System.out.println("end:" + new Date().toGMTString());
			System.out.println(new Date().getTime() - beginString.getTime());

			status = response.getStatusLine().getStatusCode();
			;
			// 获取响应消息实体
			HttpEntity entity = response.getEntity();
			// 响应状态
			System.out.println("status:" + response.getStatusLine());

			resStringString = EntityUtils.toString(entity);
			Header[] headers = response.getAllHeaders();

			for (Header header : headers) {
				if ("X-Subject-Token".equals(header.getName())) {
					map.put("tokenId", header.getValue());
				}
			}

			map.put("response", resStringString);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (status == 400) {
			throw new ParamErrorException(new StringBuilder().append("restful返回的错误信息").append(resStringString)
					.toString());
		}
		if (status == 401) {
			throw new ParamErrorException(new StringBuilder().append("restful返回的错误信息").append(resStringString)
					.toString());
		}
		if (status == 403) {
			throw new ParamErrorException(new StringBuilder().append("restful返回的错误信息").append(resStringString)
					.toString());
		}
		if (status == 404) {
			throw new ParamErrorException(new StringBuilder().append("restful返回的错误信息").append(resStringString)
					.toString());
		}
		if (status == 405) {
			throw new ParamErrorException(new StringBuilder().append("restful返回的错误信息").append(resStringString)
					.toString());
		}
		if (status == 413) {
			throw new ParamErrorException(new StringBuilder().append("restful返回的错误信息").append(resStringString)
					.toString());
		}
		if (status == 500) {
			throw new ParamErrorException(new StringBuilder().append("restful返回的错误信息").append(resStringString)
					.toString());
		}

		method.releaseConnection();
		// System.out.println(response);
		return map;

	}

	public static Map<String, Object> postMethodReturnWithHeader(String url, String bodyJson) throws ParamErrorException {

		HttpPost postMethod = new HttpPost(url);

		postMethod.addHeader("Content-Type", "application/json");

		try {
			postMethod.setEntity(new StringEntity(bodyJson));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return executeMethodReturnWithHeader(postMethod);
	}

}
