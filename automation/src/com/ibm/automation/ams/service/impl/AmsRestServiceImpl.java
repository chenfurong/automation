package com.ibm.automation.ams.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.ibm.automation.ams.service.AmsRestService;
import com.ibm.automation.ams.util.AmsClient;
import com.ibm.automation.ams.util.AmsV2ClientHttpClient4Impl;
import com.ibm.automation.core.constants.OPSTPropertyKeyConst;
import com.ibm.automation.core.exception.ParamErrorException;

import com.ibm.automation.core.util.HttpClientUtil;



@Service("amsRestServiceImpl")
public class AmsRestServiceImpl implements AmsRestService {
	public static Logger logger = Logger.getLogger(AmsRestServiceImpl.class);
	AmsClient amsClient = new AmsV2ClientHttpClient4Impl();

	private ArrayNode serverlists; // 放入认证信息

	public ArrayNode getServerList() // 获取servers 的列表
	{
		ArrayNode lists = getList(null, null, "odata/servers"); // 获取server表数据
		return lists;
	}

	ObjectMapper om = new ObjectMapper();

	public List<String> getCmdInfoByAddr(String names, String addrs, String exec) throws ParamErrorException  {
		// 获取参数信息
		String hdiskHost = cloudCfg.getProperty(OPSTPropertyKeyConst.AMS2_HOST);
		String hdiskApi = cloudCfg.getProperty(OPSTPropertyKeyConst.POST_ams2_service_cmd);
		// 拼接URL
		String strOrgUrl = hdiskHost + hdiskApi;
		// 构建json
		ObjectNode node = om.createObjectNode();
		node.put("name", names);
		node.put("type", "shell");
		node.put("exec", exec);
		node.put("async", false);
		node.set("node", om.createObjectNode().put("host", addrs).put("addr", addrs));
		
		serverlists = getServerList();
		for (JsonNode jn : serverlists) {
			if (addrs.equals(jn.get("IP").asText())) {				
				node.set("cred", om.createObjectNode().put("user", jn.get("UserID").asText()).put("pass",
						jn.get("Password").asText()));
				break;
			}
		}

		String response = "";
		try {
			if(logger.isDebugEnabled())
			{
				logger.debug("before getCmdInfoByAddr:: the post url is "+strOrgUrl +" and the post data is " + node.toString() );
			}
			response = HttpClientUtil.postMethod(strOrgUrl, node.toString());
			if(logger.isDebugEnabled())
			{
				logger.debug("after getCmdInfoByAddr:: the receive data is " + response );
			}
		} catch (Exception e1) {
			// throw new
			// OPSTOperationException("调用cmd接口获取数据异常，类型为："+e1.getMessage());
			//e1.printStackTrace();
			if(logger.isDebugEnabled())
			logger.error("调用cmd接口获取数据异常，类型为："+e1.getMessage());
		}
		try {
			if (!response.equals("")) {
				JSONObject jsonObj = JSONObject.fromObject(response);
				Object resp = jsonObj.get("msg");
				String msg = resp.toString();
				// 解析
				String[] lines = msg.split("\\\n");
				List<String> list = Arrays.asList(lines);
				if(logger.isInfoEnabled())
				{
					logger.info("call getCmdInfoByAddr success.");
				}
				return list;
			}
		} catch (Exception e) {
			if(logger.isDebugEnabled())
			logger.debug("获取HDisks的时候出现处理json数据异常，类型为：" + e.getMessage());
		}
		if(logger.isDebugEnabled())
		{
			logger.debug("call getCmdInfoByAddr error");
		}
		return null;
	}


	public ObjectNode savePVCNode(ObjectNode node, String url) {
		try {
			JsonNode jsonnode = amsClient.save(node, url);
			return (ObjectNode) jsonnode;
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("调用 AmsRestServiceImpl::savePVCNode出错，出错信息为"+e.getMessage());
		}
		return null;
	}

	public ArrayNode getList(ArrayNode sort, JsonNode query, String url) {
		try {
			return amsClient.list(sort, query, url);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("调用AmsRestServiceImpl::getList出错，出错信息为"+e.getMessage());
		}
		return null;
	}

	


	/**
	 * @author warren(hj)
	 * @param version
	 *            the version of ibm product.such as DB2 WAS MQ ORACLE
	 * @param os
	 *            the operation system such as AIX LINUX
	 * @return ObjectNode 返回一个JSON对象，包含所有的版本、补丁数据
	 * @throws ParamErrorException
	 * 
	 */
	@Override
	public ObjectNode getVersion(String version, String os) {
		// TODO Auto-generated method stub

		if (version == null || version.equals("")) {
			try {
				throw new ParamErrorException("产品版本参数为空！");
			} catch (ParamErrorException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				return null;
			} 

		}
		if (os == null || os.equals("")) {
			try {
				throw new ParamErrorException("os参数为空！");
			} catch (ParamErrorException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				return null;
			} 
		}

		String hdiskHost = cloudCfg.getProperty(OPSTPropertyKeyConst.AMS2_HOST);
		String hdiskApi = cloudCfg.getProperty(OPSTPropertyKeyConst.POST_ams2_service_db2_vers);
		// 拼接URL
		String strOrgUrl = hdiskHost + hdiskApi;

		String response = "";
		ObjectNode on = om.createObjectNode();
		on.put("product", version.toUpperCase());
		on.put("os", os.toLowerCase());
		try {
			response = HttpClientUtil.postMethod(strOrgUrl, on.toString());
			try {
				return (ObjectNode) om.readTree(response);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ParamErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public static void main(String[] args) {
		AmsRestService ars = new AmsRestServiceImpl();
		//ObjectNode on = ars.getVersion("mq", "linux");
		ArrayNode an =ars.getList(null, null, "odata/servers?_id=567a39f5f833e74e60e52f1b");
		JsonNode oo=an.get(an.size()-1);
		 System.out.println(oo);
	}

}