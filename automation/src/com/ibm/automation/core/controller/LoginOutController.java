package com.ibm.automation.core.controller;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.log4j.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.ibm.automation.core.util.PropertyUtil;
@Controller
public class LoginOutController {
	Properties amsCfg = PropertyUtil.getResourceFile("config/properties/ams2.properties");
	Properties cloudCfg = PropertyUtil.getResourceFile("config/properties/cloud.properties");
	ObjectMapper om = new ObjectMapper();
	private static Logger logger = Logger.getLogger(LoginActionController.class);
	@RequestMapping("/loginOut")
	public String logout(HttpServletRequest request,HttpServletResponse response)
	{
		
		request.getSession().invalidate();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("已经退出...");
		return "login";
	}
}
