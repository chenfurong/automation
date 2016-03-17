package com.ibm.automation.core.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.automation.ams.service.AmsRestService;
import com.ibm.automation.core.util.PropertyUtil;
import com.ibm.automation.core.util.SecurityUtil;

import com.ibm.automation.core.bean.AddHostBean;
import com.ibm.automation.core.constants.OPSTPropertyKeyConst;
import com.ibm.automation.core.exception.NetWorkException;
import com.ibm.automation.core.exception.ParamErrorException;
import com.ibm.automation.core.util.HttpClientUtil;

import net.sf.json.JSONObject;

@Controller
public class LoginActionController extends BaseController {
	Properties amsCfg = PropertyUtil.getResourceFile("config/properties/ams2.properties");
	Properties cloudCfg = PropertyUtil.getResourceFile("config/properties/cloud.properties");
	ObjectMapper om = new ObjectMapper();
	private static Logger logger = Logger.getLogger(LoginActionController.class);
	@Autowired
	private AmsRestService amsRestService;

	@RequestMapping("/AllLoginAction")
	public String LoginAction(HttpServletRequest request, HttpServletResponse resp, HttpSession session) {
		String username = request.getParameter("userName");
		String password1= request.getParameter("password");
		byte[]   passByte=Base64.decodeBase64(password1);//base64解密
		String password= new String(passByte);
		if (username == null || password == null) {
			try {
				resp.sendRedirect("login.jsp");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.info("未登录用户无法访问！");
				return "login";
			}
		}
		String pass = SecurityUtil.encrypt(password, amsCfg.getProperty("key")); // 加密字符串
		ObjectNode on = om.createObjectNode();
		on.put("type", "Login");
		on.put("name", username);
		on.put("password", pass);
		String hdiskHost = cloudCfg.getProperty(OPSTPropertyKeyConst.AMS2_HOST);
		String hdiskApi = cloudCfg.getProperty(OPSTPropertyKeyConst.POST_ams2_service_dbops);
		String strOrgUrl = hdiskHost + hdiskApi;
		String response = "";
		Integer retVal = -1;
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("before login::the post url is " + strOrgUrl + " the post data is " + on.toString());
			}
			response = HttpClientUtil.postMethod(strOrgUrl, on.toString());
			if (logger.isDebugEnabled()) {
				logger.debug("after login::the receive data is " + response);
			}

			if (!response.equals("")) {
				JSONObject jsonObj = JSONObject.fromObject(response);
				retVal = (Integer) jsonObj.get("status");

				// return retVal;//0 ok 1 no user /2 mima cuowu
			}
		} catch (ParamErrorException e) {
			// TODO Auto-generated catch block
			// logger.error("登录有异常,异常原因是"+e.getMessage());
			// request.setAttribute("errorMessageFlag",
			// "登录有异常,异常原因是"+e.getMessage());
			e.printStackTrace();
			logger.error("参数存在格式异常！");
			// return "login";
		} catch (NetWorkException e1) {
			logger.error("登录有异常,异常原因是" + e1.getMessage());
			request.setAttribute("errorMessageFlag", "networkerror");
			e1.printStackTrace();
			return "login";
		}
		if (retVal.intValue() == 2) {
			request.setAttribute("errorMessageFlag", "fail");
			if (logger.isInfoEnabled()) {
				logger.info("login check:: failed");
			}
			return "login";
		} else if (retVal.intValue() == 1) {
			if (logger.isInfoEnabled()) {
				logger.info("login check:: failed");
			}
			request.setAttribute("errorMessageFlag", "fail");
			return "login";
		} else if (retVal.intValue() == 0) {

			ArrayNode lists = amsRestService.getList(null, null, "odata/servers");
			if (logger.isDebugEnabled()) {
				logger.debug("Servers data:: get " + lists);
			}
			List<AddHostBean> lahb = new ArrayList<AddHostBean>();
			for (JsonNode js : lists) {
				AddHostBean ahb = new AddHostBean();

				ahb.setIP(js.get("IP").asText() != null ? js.get("IP").asText() : "");
				ahb.setName(js.get("Name").asText() != null ? js.get("Name").asText() : "");
				ahb.setOS(js.get("OS").asText() != null ? js.get("OS").asText() : "");
				ahb.setPassword(js.get("Password").asText() != null ? js.get("Password").asText() : "");
				// ahb.setType(js.get("type").toString());
				ahb.setUserID(js.get("UserID").asText() != null ? js.get("UserID").asText() : "");
				ahb.setStatus(js.get("Status").asText() != null ? js.get("Status").asText() : "");
				ahb.set_id(js.get("_id").asText() != null ? js.get("_id").asText() : "");
				ahb.setHConf(js.get("HConf").asText() != null ? js.get("HConf").asText() : "");
			//	ahb.setHVisor(js.get("HVisor").asText() != null ? js.get("HVisor").asText() : "");
				lahb.add(ahb);
			}
			Collections.sort(lahb);

			request.setAttribute("servers", lahb);
			request.setAttribute("total", lahb.size());
			request.getSession().setAttribute("userName", username);
			if (logger.isInfoEnabled()) {
				logger.info("login success!");
			}
			return "instance_aix_add_newlist";
		} else {

			return "error";
		}
	}
}
