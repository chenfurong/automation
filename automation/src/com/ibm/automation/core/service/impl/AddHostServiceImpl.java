package com.ibm.automation.core.service.impl;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.ibm.automation.core.util.PropertyUtil;

import com.ibm.automation.core.bean.AddHostBean;
import com.ibm.automation.core.constants.OPSTPropertyKeyConst;
import com.ibm.automation.core.exception.ParamErrorException;
import com.ibm.automation.core.service.AddHostService;
import com.ibm.automation.core.util.HttpClientUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

/**
 * 增加用户主机
 */
//@Service("addhostserivce")
@Component
public class AddHostServiceImpl implements AddHostService {
	Properties amsCfg = PropertyUtil.getResourceFile("config/properties/ams2.properties");
	Properties cloudCfg = PropertyUtil.getResourceFile("config/properties/cloud.properties");
	private static Logger logger = Logger.getLogger(AddHostServiceImpl.class);
	ObjectMapper om = new ObjectMapper();
	public AddHostServiceImpl() {
		// TODO Auto-generated constructor stub

	}

	/**
	 * -1 已经存在，无法注册 -2 不成功，原因未知 1成功
	 */
	@Override
	public int create(AddHostBean ah) {
		// TODO Auto-generated method stub
		if (ah == null)
			return -2;
		ObjectNode node = om.createObjectNode();
		node.put("type", ah.getType());
		node.put("Name", ah.getName());
		node.put("IP", ah.getIP());
		node.put("UserID", ah.getUserID());
		node.put("Password", ah.getPassword());
		node.put("OS", ah.getOS());
		node.put("HVisor", ah.getHVisor());
		String hdiskHost = cloudCfg.getProperty(OPSTPropertyKeyConst.AMS2_HOST);
		String hdiskApi = cloudCfg.getProperty(OPSTPropertyKeyConst.POST_ams2_service_dbops);
		String strOrgUrl = hdiskHost + hdiskApi;
		String response = "";
		try {
			response = HttpClientUtil.postMethod(strOrgUrl, node.toString());
			logger.info(response);
			if (!response.equals("")) {
				JSONObject jsonObj = JSONObject.fromObject(response);
				Boolean retVal = (Boolean) jsonObj.get("Status");
				if(retVal)
				return 1;
			}
		} catch (ParamErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("*****"+e.getMessage());
		}

		// 检测IP是否已经存在
		// ams
		return -1;

	}

	/**
	 * -1 有问题 1 已经注册 0 未注册
	 */
	// @SuppressWarnings("null")
	@Override
	public int HostCheck(String ip, String type) {
		// TODO Auto-generated method stub
		if ((ip == null || ip.equals("")) || (type == null && "".equals(type)))
			return -1;

		String hdiskHost = cloudCfg.getProperty(OPSTPropertyKeyConst.AMS2_HOST);
		String hdiskApi = cloudCfg.getProperty(OPSTPropertyKeyConst.POST_ams2_service_check);
		String strOrgurl = hdiskHost + hdiskApi;// 总的URL
		ObjectNode node = om.createObjectNode();
		node.put("type", type);
		node.put("IP", ip);
					 
		//String url = "http://10.28.0.235:5000/api/v2/check";
		// logger.info(node);
		String response = "";//
		try {
			response = HttpClientUtil.postMethod(strOrgurl, node.toString());
			if (!response.equals("")) {
				JSONObject jsonObj = JSONObject.fromObject(response);
				Integer retVal = (Integer) jsonObj.get("status");
				// logger.info(retVal);
				return retVal.intValue();// 1
			}
		} catch (ParamErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;// 0未注册
	}
	/**
	 * -1 不成功，原因未知 1成功
	 */
	@Override
	public int createOne(ObjectNode on) {
		// TODO Auto-generated method stub
		if(null==on)
			return -1;
		String hdiskHost = cloudCfg.getProperty(OPSTPropertyKeyConst.AMS2_HOST);
		String hdiskApi = cloudCfg.getProperty(OPSTPropertyKeyConst.POST_ams2_service_dbops);
		String strOrgUrl = hdiskHost + hdiskApi;
		String response = "";
		try {
			response = HttpClientUtil.postMethod(strOrgUrl, on.toString());
			logger.info(response);
			if (!response.equals("")) {
				JSONObject jsonObj = JSONObject.fromObject(response);
				Integer retVal = (Integer) jsonObj.get("status");
				if(retVal==1)
				return 1;
			//	else return -1;
			}
		} catch (ParamErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("参数存在异常"+e.getMessage());
		}catch(JSONException e1)
		{
			logger.error("json 格式存在异常！"+e1.getMessage());
			e1.printStackTrace();
		}
		return -1;
	}
/**
 * 检索IP
 * 
 * */
	@Override
	public String searchServers(String iporname) {
		// TODO Auto-generated method stub
		if(iporname==null)
			return "error";
		ObjectNode on = om.createObjectNode();
		String hdiskHost = cloudCfg.getProperty(OPSTPropertyKeyConst.AMS2_HOST);
		String hdiskApi = cloudCfg.getProperty(OPSTPropertyKeyConst.POST_ams2_service_dbops);
		String strOrgUrl = hdiskHost + hdiskApi;
		String response = "";
		on.put("type", "search");
		on.put("keyword", iporname);
		try {
			response = HttpClientUtil.postMethod(strOrgUrl, on.toString());
			logger.info(response);
			if (!response.equals("")) {
				JSONArray jsonObj = JSONArray.fromObject(response);
			
				
				
				
				String str = jsonObj.toString();
				logger.info(str);
				return jsonObj.toString();
			//	else return -1;
			}
		} catch (ParamErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("*****"+e.getMessage());
		}
		return "error";
	}
/**
 * 1 成功 0 失败
 * */
@Override
public int modifyOne(ObjectNode on) {
	// TODO Auto-generated method stub
	if(null==on)
		return -1;
	String hdiskHost = cloudCfg.getProperty(OPSTPropertyKeyConst.AMS2_HOST);
	String hdiskApi = cloudCfg.getProperty(OPSTPropertyKeyConst.POST_ams2_service_dbops);
	String strOrgUrl = hdiskHost + hdiskApi;
	String response = "";
	try {
		response = HttpClientUtil.postMethod(strOrgUrl, on.toString());
		logger.info(response);
		if (!response.equals("")) {
			JSONObject jsonObj = JSONObject.fromObject(response);
			Integer retVal = (Integer) jsonObj.get("status");
			if(retVal==1)
			return 1;
		//	else return -1;
		}
	} catch (ParamErrorException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		logger.error("参数存在异常"+e.getMessage());
	}catch(JSONException e1)
	{
		logger.error("json 格式存在异常！"+e1.getMessage());
		e1.printStackTrace();
	}
	return 0;
}

	
}
