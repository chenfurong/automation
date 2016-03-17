package com.ibm.automation.core.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.automation.ams.service.AmsRestService;
import com.ibm.automation.core.bean.AddHostBean;
import com.ibm.automation.core.service.AddHostService;
import com.ibm.automation.core.util.PropertyUtil;
import com.ibm.automation.core.util.SecurityUtil;

import common.Logger;
import jxl.Sheet;
import jxl.Workbook;
import jxl.Cell;
import jxl.read.biff.BiffException;
import net.minidev.json.JSONObject;


@Controller
public class AddHostController extends BaseController {
	Properties amsCfg = PropertyUtil.getResourceFile("config/properties/ams2.properties");
	private static Logger logger = Logger.getLogger(AddHostController.class);
	ObjectMapper om = new ObjectMapper();
	@Autowired
	private AddHostService addhostserivce;
	// public AddHostService addHostService = new AddHostServiceImpl();
	@Autowired
	private AmsRestService amsRestService;

	// Logger logger = Logger.getLogger(cl)
	public AddHostController() {
		// TODO Auto-generated constructor stub
		// ars= new Ams
	}

	/**
	 * 根据主机条件获取server
	 */
	@RequestMapping("/searchServers")
	@ResponseBody
	public String searchServers(HttpServletRequest request, HttpSession session) {
		String iporname = request.getParameter("data");
		// String retVal = addhostserivce.searchServers(iporname);

		Pattern p = Pattern.compile(iporname);

		ArrayNode lists = amsRestService.getList(null, null, "odata/servers");
		ArrayNode retnode = om.createArrayNode();
		if (iporname == null || iporname.equals("")) {
			for (JsonNode js : lists) {
				ObjectNode on = om.createObjectNode();// 创建一个ojbectnode
				on.put("_id", js.get("_id").asText() != null ? js.get("_id").asText() : "");
				on.put("Name", js.get("Name").asText() != null ? js.get("Name").asText() : "");
				on.put("IP", js.get("IP").asText() != null ? js.get("IP").asText() : "");
				on.put("HConf", js.get("HConf").asText() != null ? js.get("HConf").asText() : "");
				on.put("OS", js.get("OS").asText() != null ? js.get("OS").asText() : "");
			//	on.put("HVisor", js.get("HVisor").asText() != null ? js.get("HVisor").asText() : "");
				on.put("Status", js.get("Status").asText() != null ? js.get("Status").asText() : "");
				on.put("edit", "edit");
				retnode.add(on);
			}
		} else {
			for (JsonNode js : lists) {

				ObjectNode on = om.createObjectNode();// 创建一个ojbectnode
				Matcher ipmatcher = p.matcher(js.get("IP").asText());
				Matcher hostnamematcher = p.matcher(js.get("Name").asText());
				if (ipmatcher.find() || hostnamematcher.find()) {
					/*
					 * ahb.set_id(js.get("_id").asText() != null ?
					 * js.get("_id").asText() : "");
					 * ahb.setName(js.get("Name").asText() != null ?
					 * js.get("Name").asText() : "");
					 * ahb.setIP(js.get("IP").asText() != null ?
					 * js.get("IP").asText() : "");
					 * ahb.setHConf(js.get("HConf").asText() != null ?
					 * js.get("HConf").asText() : "");
					 * ahb.setOS(js.get("OS").asText() != null ?
					 * js.get("OS").asText() : "");
					 * ahb.setHVisor(js.get("HVisor").asText() != null ?
					 * js.get("HVisor").asText() : "");
					 * ahb.setStatus(js.get("Status").asText() != null ?
					 * js.get("Status").asText() : ""); lahb.add(ahb);
					 */
					on.put("_id", js.get("_id").asText() != null ? js.get("_id").asText() : "");
					on.put("Name", js.get("Name").asText() != null ? js.get("Name").asText() : "");
					on.put("IP", js.get("IP").asText() != null ? js.get("IP").asText() : "");
					on.put("HConf", js.get("HConf").asText() != null ? js.get("HConf").asText() : "");
					on.put("OS", js.get("OS").asText() != null ? js.get("OS").asText() : "");
					on.put("HVisor", js.get("HVisor").asText() != null ? js.get("HVisor").asText() : "");
					on.put("Status", js.get("Status").asText() != null ? js.get("Status").asText() : "");
					on.put("edit", "edit");
					retnode.add(on);
				}

			}
		}
		String retStr = retnode.toString();

		return retStr;

	}

	/**
	 * 获取主机列表
	 * 
	 */
	@RequestMapping("/getAllServers")
	public String getAllServers(HttpServletRequest request,HttpServletResponse resp, HttpSession session) {
		String isLogIn =(String) request.getSession().getAttribute("userName");
		if(isLogIn == null || isLogIn.equals(""))//从session中获取userName
		{
			logger.info("请去登入");
			return "login";
		}
		ArrayNode lists = amsRestService.getList(null, null, "odata/servers");
	//	System.out.println(lists);
		// ObjectMapper obj = new ObjectMapper();
		List<AddHostBean> lahb = new ArrayList<AddHostBean>();
		for (JsonNode js : lists) {
			AddHostBean ahb = new AddHostBean();
			ahb.setIP(js.get("IP").asText() != null ? js.get("IP").asText() : "");
			ahb.setName(js.get("Name").asText() != null ? js.get("Name").asText() : "");
			ahb.setOS(js.get("OS").asText() != null ? js.get("OS").asText() : "");
			ahb.setPassword(js.get("Password").asText() != null ? js.get("Password").asText() : "");
			ahb.setUserID(js.get("UserID").asText() != null ? js.get("UserID").asText() : "");
			ahb.setStatus(js.get("Status").asText() != null ? js.get("Status").asText() : "");
			ahb.set_id(js.get("_id").asText() != null ? js.get("_id").asText() : "");
			ahb.setHConf(js.get("HConf").asText() != null ? js.get("HConf").asText() : "");
			ahb.setHVisor(js.get("HVisor").asText() != null ? js.get("HVisor").asText() : "");
			
			lahb.add(ahb);
		}
		Collections.sort(lahb);
	//	System.out.println(lahb);
		request.setAttribute("servers", lahb);
		request.setAttribute("total", lahb.size());
		return "instance_aix_add_newlist";
	}

	/**
	 * responsebody 表示要返回字符串，不返回页面
	 */
	@RequestMapping("/IPCheck")
	@ResponseBody
	public String ipcheck(HttpServletRequest request, HttpSession session) {
		String ip = request.getParameter("IP");
		int status = addhostserivce.HostCheck(ip, "IP");
		if (status == 1) {
			JSONObject json = new JSONObject();
			json.put("msg", 1);
			return json.toString();// 已经存在ip
		} else {
			JSONObject json = new JSONObject();
			json.put("msg", 0);
			return json.toString();// 不存在IP
		}
	}

	/**
	 * 根据Excel批量导入主机信息
	 * 
	 */
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	@ResponseBody
	public String importExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		ObjectMapper om = new ObjectMapper();
		// String org= file.getOriginalFilename();
		// System.out.println("the original file is ::"+org);
		try {
			InputStream is = file.getInputStream();
			Workbook rwb = Workbook.getWorkbook(is);
			Sheet oFirstSheet = rwb.getSheet(0);
			int rows = oFirstSheet.getRows();// 获取工作表中的总行数
			int cols = oFirstSheet.getColumns();// 获取工作表中的总列数
			StringBuffer total = new StringBuffer();

			for (int i = 1; i < rows; i++) {
				String temp = "";

				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < cols; j++) {
					Cell oCell = oFirstSheet.getCell(j, i);
					temp = oCell.getContents();
					sb.append(temp).append(",");
				}
				total.append(sb).append(";");

			}
			String finalStr = new String(total);
			finalStr = finalStr.replaceAll(",;", ";"); // 把,;变成;
			// System.out.println(finalStr);
			ArrayNode objArray = om.createArrayNode();
			ObjectNode totalObj = om.createObjectNode();// 总的节点
			String[] finalArr = finalStr.split(";");
			for (String s1 : finalArr) {
				String[] s2 = s1.split(",");
				ObjectNode inner = om.createObjectNode();
				inner.put("Name", "Default");
				inner.put("IP", s2[0]);
				inner.put("UserID", s2[1]);
				inner.put("Password", SecurityUtil.encrypt(s2[2], amsCfg.getProperty("key")));
				inner.put("OS", "DefaultOS");
				inner.put("HVisor", "DefaultHVisor");
				objArray.add(inner);
				/*
				 * AddHostBean ahb = new AddHostBean();
				 * ahb.setType("createServer"); ahb.setName(s2[0]);
				 * ahb.setIP(s2[1]); ahb.setUserID(s2[2]);
				 * ahb.setPassword(s2[3]); ahb.setOS(s2[4]);
				 * ahb.setHVisor(s2[5]); // int stat =
				 * addhostserivce.create(ahb); // System.out.println(stat);
				 * 
				 */
			}
			totalObj.put("type", "createServer");
			totalObj.putPOJO("servers", objArray);
			int stat = addhostserivce.createOne(totalObj);
			if (stat == 1) {
				JSONObject json = new JSONObject();
				json.put("msg", "success");
				request.setAttribute("status", "success");
				return json.toString();// 成功
			} else {
				request.setAttribute("status", "failure");
				JSONObject json = new JSONObject();
				json.put("msg", "failure");
				return json.toString();//

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject json = new JSONObject();
		json.put("msg", "failure");
		return json.toString();
		// return "1";

	}

	/**
	 * -1 其他异常 1 已经存在账号，无法添加 0 未存在账号，已经帮你添加到mongodb
	 */
	@RequestMapping("/addHost")
	@ResponseBody
	public String addHost(HttpServletRequest request, HttpSession session) {
		// log.debug("debug::"+request);
		ObjectMapper obj = new ObjectMapper();
		String name = request.getParameter("Name");
		String ip = request.getParameter("IP");
		String os = request.getParameter("OS");
		String userid = request.getParameter("UserID");
		String password = request.getParameter("Password");
		String type = request.getParameter("type");
		String HVisor = request.getParameter("HVisor");
		/*
		 * AddHostBean ahb = new AddHostBean(); ahb.setIP(ip);
		 * ahb.setName(name); ahb.setOS(os); ahb.setPassword(password);
		 * ahb.setUserID(userid); ahb.setType(type); ahb.setHVisor(HVisor);
		 */
		ObjectNode totalNodes = obj.createObjectNode();
		ObjectNode innerNode = obj.createObjectNode();
		ArrayNode an = obj.createArrayNode();
		totalNodes.put("type", type);
		innerNode.put("Name", name);
		innerNode.put("IP", ip);
		innerNode.put("UserID", userid);
		String pass=SecurityUtil.encrypt(password, amsCfg.getProperty("key"));  //加密字符串
		
		innerNode.put("Password", pass);
		innerNode.put("OS", os);
		innerNode.put("HVisor", HVisor);
		an.add(innerNode);
		totalNodes.putPOJO("servers", an);
		int stat = addhostserivce.createOne(totalNodes);
		if (stat == 1) {
			JSONObject json = new JSONObject();
			json.put("msg", "success");
			request.setAttribute("status", "success");
			return json.toString();// 成功
		} else {
			/*
			 * request.setAttribute("IP", ahb.getIP());
			 * request.setAttribute("Name", ahb.getName());
			 * request.setAttribute("OS", ahb.getOS());
			 * request.setAttribute("type", ahb.getType());
			 * request.setAttribute("UserID", ahb.getUserID());
			 * request.setAttribute("Password", ahb.getPassword());
			 */
			request.setAttribute("status", "failure");
			JSONObject json = new JSONObject();
			json.put("msg", "failure");
			return json.toString();//
		}
	}
	/**
	 * 获取需要编辑的服务器的信息
	 * */
	@RequestMapping("/getModifyHost")
	@ResponseBody
	public JsonNode editHost(HttpServletRequest request, HttpSession session)
	{
		
		String _id = request.getParameter("_id");
		ArrayNode lists = amsRestService.getList(null, null, "odata/servers?_id="+_id);
		
		JsonNode jn = lists.get(lists.size()-1);
		
		return jn;
	}
	/**
	 * 修改服务器信息
	 * */
	@RequestMapping("/postEditForm")
	@ResponseBody
	public ObjectNode postEditForm(HttpServletRequest request, HttpSession session)
	{
		ObjectMapper obj = new ObjectMapper();
		String _id = request.getParameter("_id");
		String ip = request.getParameter("IP");
		//String os = request.getParameter("OS");
		String userid = request.getParameter("UserID");
		String password = request.getParameter("Password");
		//String type = request.getParameter("type");
		//String HVisor = request.getParameter("HVisor");
		ObjectNode on  = obj.createObjectNode();
		on.put("type", "modifyServer");
		on.put("_id", _id);
		on.put("IP", ip);
		on.put("UserID", userid);
		on.put("Password", SecurityUtil.encrypt(password, amsCfg.getProperty("key")));
		int stat = addhostserivce.modifyOne(on);
		if(stat == 1)
		{
			ObjectNode temp  = obj.createObjectNode();
			temp.put("msg", "success");
			return temp;
		}else{
			ObjectNode temp  = obj.createObjectNode();
			temp.put("msg", "failure");
			return temp;
		}
	}
}
