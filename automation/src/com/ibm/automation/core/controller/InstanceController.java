package com.ibm.automation.core.controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.automation.ams.service.AmsRestService;
import com.ibm.automation.core.bean.AddHostBean;
import com.ibm.automation.core.bean.MqClusterLogBean;
import com.ibm.automation.core.bean.WasClusterLogBean;
import com.ibm.automation.core.util.FormatUtil;
import com.ibm.automation.core.util.PropertyUtil;

/**
 * @Title:InstanceController
 * @Description:实例管理控制类
 * @Auth:LiangRui
 * @CreateTime:2015年6月10日 下午4:22:00
 * @version V1.0
 * 
 */
@Controller
public class InstanceController extends BaseController {
	public static Logger logger = Logger.getLogger(InstanceController.class);
	@Autowired
	private AmsRestService amsRestService;

	Properties p = PropertyUtil.getResourceFile("config/properties/cloud.properties");

	Properties amsprop = PropertyUtil.getResourceFile("config/properties/ams2.properties");

	/**
	 * @param pro
	 * @author hujin
	 * @see 判断用户购买了哪些产品服务
	 */
	private String judgeProductIsExist(String pro) {
		String allProduct = amsprop.getProperty("product");
		if (allProduct.contains(pro.toLowerCase())) {
			String ifExists = "com.ibm.automation." + pro + ".controller." + pro.toUpperCase() + "Controller";
			// System.out.println(ifExists);
			try {
				Class.forName(ifExists);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				logger.debug("该产品不存在，请联系IBM购买");
				return "IBM_NJ_TSS_Product";
			}

		}
		return "success";
	}

	@RequestMapping("/getIBMAllInstance")
	public String getIBMAllInstance(HttpServletRequest request, HttpServletResponse resp, HttpSession session)
			throws Exception {

		String isLogIn = (String) request.getSession().getAttribute("userName");
		if (isLogIn == null || isLogIn.equals("")) // 从session中获取userName
		{
			return "login";
		}

		String pro = request.getParameter("ptype");// 当前进入的是哪个产品

		if (pro == null || pro.equals("null")) {
			return "redirect:/getAllServers";
		}
		String exists = judgeProductIsExist(pro);
		if (exists.equals("success")) {
			// 买了的话nothing to do .
		} else {
			return exists;// 不存在该产品
		}
		ArrayNode lists = amsRestService.getList(null, null, "odata/servers");
		String platform = request.getParameter("platform");// 对应的产品只装需要的平台
		List<AddHostBean> lahb = new ArrayList<AddHostBean>();
		if (platform.equals("all")) // 适配所有
		{
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
		} else if (platform.equalsIgnoreCase("aix")) // 适配aix平台
		{
			// Pattern p = Pattern.compile("aix");

			for (JsonNode js : lists) {

				if (js.get("OS").asText().toLowerCase().contains("aix")) {
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
			}

		} else if (platform.equalsIgnoreCase("linux")) // 适配Linux
		{
			for (JsonNode js : lists) {

				String ostemp = js.get("OS").asText().toLowerCase();
				if (ostemp.contains("red hat") || ostemp.contains("redhat") || ostemp.contains("suse")
						|| ostemp.contains("linux") || ostemp.contains("centeros")) {
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
			}
		} else {

		}

		Collections.sort(lahb);

		// System.out.println(lahb);
		request.setAttribute("servers", lahb);
		request.setAttribute("total", lahb.size());

		String type = request.getParameter("ptype");// db2 -db2ha -was - mq
		request.setAttribute("ptype", type);
		if (type != null && type != "" && type.equals("was")) {
			return "instance_linux_was_list";
		} else if (type != null && type != "" && type.equals("wascluster")) {
			return "instance_linux_wascluster_list";
		} else if (type != null && type != "" && type.equals("db2ha")) {
			return "instance_aix_db2ha_list";
		} else if (type != null && type != "" && type.equals("db2")) {
			return "instance_aix_db2_list";
		} else if (type != null && type != "" && type.equals("mq")) {
			return "instance_aix_linux_mq_list";
		} else if (type != null && type != "" && type.equals("mqcluster")){
			return "instance_aix_linux_mqcluster_list";
		} else if (type != null && type != "" && type.equals("osagent")){
			return "instance_linux_os_agent_list";
		}
		return null;
	}

	/**
	 * @Title: getInstanceDetial
	 * @Description:查询虚机详细信息
	 * @param HttpServletRequest
	 *            request
	 * @param @param
	 *            session
	 * @param @return
	 * @param @throws
	 *            Exception
	 * @author LiangRui
	 * @throws @Time
	 *             2015年6月16日 上午11:08:32
	 */
	// @SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/getInstanceDetial")
	public String getInstanceDetial(HttpServletRequest request, HttpServletResponse resp, HttpSession session)
			throws Exception {

		String isLogIn = (String) request.getSession().getAttribute("userName");
		if (isLogIn == null || isLogIn.equals("")) // 从session中获取userName
		{
			return "login";
		}
		ObjectMapper om = new ObjectMapper();

		String serId = request.getParameter("serId");

		String ptype = request.getParameter("ptype");// db2 -db2ha -was - mq - os agent
		logger.debug("getInstanceDetial::获取serId 和ptype " + serId + ";" + ptype);
		List serIds = new ArrayList();
		if (serId != null && serId != "") {
			String[] ss = serId.split(",");
			for (int i = 0; i < ss.length; i++) {
				serIds.add(ss[i]);
			}
		}
		ArrayNode lists = amsRestService.getList(null, null, "odata/servers");
		Log.debug("getInstanceDetial::amsRestService.getList");
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
		List<AddHostBean> listDetial = new ArrayList<AddHostBean>();
		for (int i = 0; i < lahb.size(); i++) {
			String serverId = lahb.get(i).get_id();
			for (int j = 0; j < serIds.size(); j++) {
				if (serverId.equals(serIds.get(j))) {
					listDetial.add(lahb.get(i));
				}
			}
		}
		if (ptype != null && ptype != "" && ptype.equals("was")) {
			Iterator<AddHostBean> iter = listDetial.iterator();
			AddHostBean sb = null;
			while (iter.hasNext()) {
				sb = (AddHostBean) iter.next();
			}
			Collections.sort(listDetial);
			request.setAttribute("total", lahb.size());
			request.setAttribute("serId", serId);
			request.setAttribute("servers", listDetial);
			request.setAttribute("ptype", ptype);
			request.setAttribute("hostId", sb.get_id());
			request.setAttribute("was_ip", sb.getIP());
			request.setAttribute("was_hostname", sb.getName());
			return "instance_linux_was_config";
		}
		if (ptype != null && ptype != "" && ptype.equals("wascluster")) {
			/*
			 * Iterator<AddHostBean> iter = listDetial.iterator(); AddHostBean
			 * sb = null; while (iter.hasNext()) { sb = (AddHostBean)
			 * iter.next();
			 * 
			 * }
			 */
			Collections.sort(listDetial);
			request.setAttribute("total", lahb.size());
			request.setAttribute("serId", serId);
			request.setAttribute("servers", listDetial);
			request.setAttribute("ptype", ptype);
			request.setAttribute("hostId", serId);
			// request.setAttribute("was_ip", sb.getIP());
			return "instance_linux_wascluster_config";
		} 
		if(ptype != null && ptype != "" && ptype.equals("osagent")){
			Iterator<AddHostBean> iter = listDetial.iterator();
			AddHostBean sb = null;
			while (iter.hasNext()) {
				sb = (AddHostBean) iter.next();
			}
			Collections.sort(listDetial);
			request.setAttribute("total", lahb.size());
			request.setAttribute("serId", serId);
			request.setAttribute("servers", listDetial);
			request.setAttribute("ptype", ptype);
			request.setAttribute("hostId", serId);
			request.setAttribute("osagent_hostname", sb.getName());
			request.setAttribute("osagent_ip", sb.getIP());
			return "instance_linux_os_agent_config";
		}
		if(ptype != null && ptype != "" && ptype.equals("mqcluster")){
			Iterator<AddHostBean> iter = listDetial.iterator();
			AddHostBean sb = null;
			while (iter.hasNext()) {
				sb = (AddHostBean) iter.next();
			}
			Collections.sort(listDetial);
			request.setAttribute("total", lahb.size());
			request.setAttribute("serId", serId);
			request.setAttribute("servers", listDetial);
			request.setAttribute("ptype", ptype);
			request.setAttribute("hostId", serId);
			request.setAttribute("mq_hostname", sb.getName());
			request.setAttribute("mq_ip", sb.getIP());
			return "instance_aix_linux_mqcluster_config";
		}
		else if (ptype != null && ptype != "" && ptype.equals("mq")) {
			ArrayNode aixorlinux = amsRestService.getList(null, null, "odata/servers?_id=" + serId);
			for (JsonNode jn : aixorlinux) {
				String os = jn.get("OS").textValue().toString();// 读取是哪个os
				if (os.toLowerCase().contains("aix")) {
					request.setAttribute("os", "aix");
				} else if (os.toLowerCase().contains("red hat") || os.toLowerCase().contains("redhat")
						|| os.toLowerCase().contains("suse") || os.toLowerCase().contains("centeros")
						|| os.toLowerCase().contains("linux")) {
					request.setAttribute("os", "linux"); // 由于MQ可以在linux 或者aix
															// 平台所以页面需要动态化
				} else {
					request.setAttribute("os", "all");
				}
			}
			Iterator<AddHostBean> iter = listDetial.iterator();
			AddHostBean sb = null;
			while (iter.hasNext()) {
				sb = (AddHostBean) iter.next();

			}
			Collections.sort(listDetial);
			request.setAttribute("total", lahb.size());
			request.setAttribute("serId", serId);
			request.setAttribute("servers", listDetial);
			request.setAttribute("ptype", ptype);
			request.setAttribute("hostId", serId);
			request.setAttribute("mq_hostname", sb.getName());
			request.setAttribute("mq_ip", sb.getIP());
			return "instance_aix_linux_mq_config";
		} 
		else {
			Iterator<AddHostBean> iter = listDetial.iterator();
			AddHostBean sb = null;
			while (iter.hasNext()) {
				sb = (AddHostBean) iter.next();
			}
			Collections.sort(listDetial);
			request.setAttribute("total", lahb.size());
			request.setAttribute("serId", serId);
			request.setAttribute("servers", listDetial);
			request.setAttribute("ptype", ptype);
			request.setAttribute("hostId", sb.get_id());
			request.setAttribute("was_ip", sb.getIP());
			String sername = amsprop.getProperty("server_name");
			String serip = amsprop.getProperty("server_ip");
			String pername = amsprop.getProperty("permanent_name");
			String perip = amsprop.getProperty("permanent_ip");

			ArrayNode objnode = om.createArrayNode();
			for (AddHostBean ser : listDetial) {
				String addr = ser.getIP() != null ? ser.getIP() : "";
				String addsub = addr.substring(addr.indexOf(".", addr.indexOf(".", addr.indexOf(".", 0) + 1) + 1) + 1,
						addr.length());
				ObjectNode node = om.createObjectNode();
				node.put("serip", serip.replace("#", addsub));
				node.put("sername", sername.replace("#", ser.getName()));
				node.put("perip", perip.replace("#", addsub));
				node.put("pername", pername.replace("#", ser.getName()));
				node.put("hostname", ser.getName());
				node.put("addr", addr);
				node.put("hostId", ser.get_id());
				objnode.add(node);
			}
			request.setAttribute("hosts", objnode);
			request.setAttribute("ha_primaryNode", request.getParameter("ha_primaryNode"));
			return "instance_aix_db2ha_host";
		}
	}

	public static String[] convertStrToArray(String str) {
		String[] strArray = null;
		strArray = str.split(",");
		return strArray;
	}

	// 创建一个host
	public static ObjectNode createHostObjNode(String type, String hostname, String addr) {
		// { "host": "aix71was0.dc", "addr": "10.58.0.134", "conf": { "was":
		// "cell" } }
		ObjectMapper om = new ObjectMapper();
		ObjectNode host = om.createObjectNode();
		host.put("host", hostname);
		// ip地址可能有多网卡,现在暂时只取第一个
		addr = addr.split(";")[0];
		host.put("addr", addr);
		host.set("conf", om.createObjectNode().put("was", type));
		return host;
	}

	// 创建pvcnode
	private String createPvcNodeObjNode(String hostname, String addr, String hostId, String type, String role) {
		ObjectMapper om = new ObjectMapper();
		ObjectNode pvcnode = om.createObjectNode();
		String pvcnodeId = UUID.randomUUID().toString();
		pvcnode.put("name", hostname);
		pvcnode.put("uuid", pvcnodeId);
		pvcnode.put("pvcid", hostId);
		pvcnode.put("addr", addr);
		pvcnode.put("type", type);
		pvcnode.put("role", role);
		pvcnode.put("status", 0);
		ObjectNode obj = amsRestService.savePVCNode(pvcnode, "odata/pvcnodes");
		// System.out.println("pvcnode:::" + obj);
		return pvcnodeId;
	}

	// 创建pvcclusters-nodes-node
	private ObjectNode createPvcClusterNode(String hostname, String addr, String hostId, String pvcnodeId,
			String type) {
		ObjectMapper om = new ObjectMapper();
		ObjectNode cluster = om.createObjectNode();
		cluster.put("hostname", hostname);
		cluster.put("uuid", pvcnodeId);
		cluster.put("addr", addr);
		cluster.put("role", type);
		cluster.put("pvcid", hostId);
		return cluster;
	}

	// 创建pvcclusters
	private ObjectNode createPvcClustersObjNode(String uuid, ArrayNode pvcclusternodes, ObjectNode info, String ptype) {
		ObjectMapper om = new ObjectMapper();
		ObjectNode pvccluster = om.createObjectNode();
		pvccluster.put("uuid", uuid);
		pvccluster.put("type", ptype);
		pvccluster.put("status", 0);
		pvccluster.put("name", "pvccluster");
		pvccluster.set("nodes", pvcclusternodes);
		pvccluster.set("options", info);
		ObjectNode obj = amsRestService.savePVCNode(pvccluster, "odata/pvcclusters");
		System.out.println("pvccluster:::" + obj);
		return obj;
	}

	private ObjectNode createDB2HostObjNode(String hostname, String addr, String role) {
		// { "host": "aix71was0.dc", "addr": "10.58.0.134", "role": "ha1" }
		ObjectMapper om = new ObjectMapper();
		ObjectNode host = om.createObjectNode();
		host.put("host", hostname);
		// ip地址可能有多网卡,现在暂时只取第一个
		addr = addr.split(";")[0];
		host.put("addr", addr);
		host.put("role", "ha" + role);
		return host;
	}

	/*
	 * public void showRequest(HttpServletRequest request) {
	 * System.out.println(request.getParameter("ip"));
	 * System.out.println(request.getParameter("bootip"));
	 * System.out.println(request.getParameter("hostname"));
	 * System.out.println(request.getParameter("vgdb2home"));
	 * System.out.println(request.getParameter("vgdb2log"));
	 * System.out.println(request.getParameter("vgdb2archlog"));
	 * System.out.println(request.getParameter("vgdataspace"));
	 * System.out.println(request.getParameter("db2homepv"));
	 * System.out.println(request.getParameter("db2logpv"));
	 * System.out.println(request.getParameter("db2archlogpv"));
	 * System.out.println(request.getParameter("dataspacepv"));
	 * System.out.println(request.getParameter("db2homemode"));
	 * System.out.println(request.getParameter("db2logmode"));
	 * System.out.println(request.getParameter("db2archlogmode"));
	 * System.out.println(request.getParameter("dataspacemode"));
	 * System.out.println(request.getParameter("db2_version"));
	 * System.out.println(request.getParameter("db2_db2base"));
	 * System.out.println(request.getParameter("db2_dbpath"));
	 * System.out.println(request.getParameter("db2_svcename"));
	 * System.out.println(request.getParameter("db2_db2insusr"));
	 * System.out.println(request.getParameter("db2_dbname"));
	 * System.out.println(request.getParameter("db2_codeset"));
	 * System.out.println(request.getParameter("db2_dbdatapath"));
	 * System.out.println(request.getParameter("db2_db2insgrp"));
	 * System.out.println(request.getParameter("db2_db2fenusr"));
	 * System.out.println(request.getParameter("db2_db2fengrp"));
	 * System.out.println(request.getParameter("db2_db2comm"));
	 * System.out.println(request.getParameter("db2_db2codepage"));
	 * System.out.println(request.getParameter("db2_initagents"));
	 * System.out.println(request.getParameter("db2_max_coordagents"));
	 * System.out.println(request.getParameter("db2_max_connectings"));
	 * System.out.println(request.getParameter("db2_poolagents"));
	 * System.out.println(request.getParameter("db2_diagsize"));
	 * System.out.println(request.getParameter("db2_mon_buf"));
	 * System.out.println(request.getParameter("db2_mon_lock"));
	 * System.out.println(request.getParameter("db2_mon_sort"));
	 * System.out.println(request.getParameter("db2_mon_stmt"));
	 * System.out.println(request.getParameter("db2_mon_table"));
	 * System.out.println(request.getParameter("db2_mon_uow"));
	 * System.out.println(request.getParameter("db2_health_mon"));
	 * System.out.println(request.getParameter("db2_mon_heap"));
	 * System.out.println(request.getParameter("db2_db2log"));
	 * System.out.println(request.getParameter("db2_backuppath"));
	 * System.out.println(request.getParameter("db2_logarchpath"));
	 * System.out.println(request.getParameter("db2_stmm"));
	 * System.out.println(request.getParameter("db2_locklist"));
	 * System.out.println(request.getParameter("db2_maxlocks"));
	 * System.out.println(request.getParameter("db2_locktimeout"));
	 * System.out.println(request.getParameter("db2_sortheap"));
	 * System.out.println(request.getParameter("db2_logfilesize"));
	 * System.out.println(request.getParameter("db2_logprimary"));
	 * System.out.println(request.getParameter("db2_logsecond"));
	 * System.out.println(request.getParameter("db2_logbuff"));
	 * System.out.println(request.getParameter("db2_softmax"));
	 * System.out.println(request.getParameter("db2_trackmod"));
	 * System.out.println(request.getParameter("db2_pagesize"));
	 * System.out.println(request.getParameter("db2homelv"));
	 * System.out.println(request.getParameter("db2loglv"));
	 * System.out.println(request.getParameter("db2archloglv"));
	 * System.out.println(request.getParameter("dataspacelv"));
	 * System.out.println(request.getParameter("db2homefs"));
	 * System.out.println(request.getParameter("db2logfs"));
	 * System.out.println(request.getParameter("db2archlogfs"));
	 * System.out.println(request.getParameter("db2backupfs"));
	 * System.out.println(request.getParameter("dataspacefs"));
	 * System.out.println(request.getParameter("db2path"));
	 * System.out.println(request.getParameter("db2base"));
	 * System.out.println(request.getParameter("nfsON"));
	 * System.out.println(request.getParameter("nfsIP1"));
	 * System.out.println(request.getParameter("nfsSPoint1"));
	 * System.out.println(request.getParameter("nfsCPoint1"));
	 * System.out.println(request.getParameter("nfsIP2"));
	 * System.out.println(request.getParameter("nfsSPoint2"));
	 * System.out.println(request.getParameter("nfsCPoint2"));
	 * System.out.println(request.getParameter("nfsIP3"));
	 * System.out.println(request.getParameter("nfsSPoint3"));
	 * System.out.println(request.getParameter("nfsCPoint3"));
	 * System.out.println(request.getParameter("nfsIP4"));
	 * System.out.println(request.getParameter("nfsSPoint4"));
	 * System.out.println(request.getParameter("nfsCPoint4"));
	 * System.out.println(request.getParameter("nfsIP5"));
	 * System.out.println(request.getParameter("nfsSPoint5"));
	 * System.out.println(request.getParameter("nfsCPoint5"));
	 * System.out.println(request.getParameter("nfsIP6"));
	 * System.out.println(request.getParameter("nfsSPoint6"));
	 * System.out.println(request.getParameter("nfsCPoint6"));
	 * System.out.println(request.getParameter("nfsIP7"));
	 * System.out.println(request.getParameter("nfsSPoint7"));
	 * System.out.println(request.getParameter("nfsCPoint7"));
	 * System.out.println(request.getParameter("nfsIP8"));
	 * System.out.println(request.getParameter("nfsSPoint8"));
	 * System.out.println(request.getParameter("nfsCPoint8"));
	 * System.out.println(request.getParameter("nfsIP9"));
	 * System.out.println(request.getParameter("nfsSPoint9"));
	 * System.out.println(request.getParameter("nfsCPoint9"));
	 * System.out.println(request.getParameter("nfsIP10"));
	 * System.out.println(request.getParameter("nfsSPoint10"));
	 * System.out.println(request.getParameter("nfsCPoint10"));
	 * System.out.println(request.getParameter("nfsIP11"));
	 * System.out.println(request.getParameter("nfsSPoint11"));
	 * System.out.println(request.getParameter("nfsCPoint11"));
	 * System.out.println(request.getParameter("nfsIP12"));
	 * System.out.println(request.getParameter("nfsSPoint12"));
	 * System.out.println(request.getParameter("nfsCPoint12"));
	 * System.out.println(request.getParameter("nfsIP13"));
	 * System.out.println(request.getParameter("nfsSPoint13"));
	 * System.out.println(request.getParameter("nfsCPoint13"));
	 * System.out.println(request.getParameter("nfsIP14"));
	 * System.out.println(request.getParameter("nfsSPoint14"));
	 * System.out.println(request.getParameter("nfsCPoint14"));
	 * System.out.println(request.getParameter("nfsIP15"));
	 * System.out.println(request.getParameter("nfsSPoint15"));
	 * System.out.println(request.getParameter("nfsCPoint15")); }
	 */
	// @SuppressWarnings({ "rawtypes", "unchecked" })
	/*
	 * @RequestMapping("/toWasClusterNextPage") public String
	 * toWasCluterNextPage(HttpServletRequest request, HttpSession session) {
	 * String serId = request.getParameter("serId");
	 * 
	 * String ptype = request.getParameter("ptype");
	 * 
	 * String[] allips = request.getParameterValues("all_ips"); String[]
	 * allhostnames = request.getParameterValues("all_hostnames"); String[]
	 * allprofiletypes = request.getParameterValues("all_profile_types");
	 * String[] allprofilenames =
	 * request.getParameterValues("all_profile_names");
	 * 
	 * for (int i = 0; i < allprofiletypes.length; i++) { if
	 * (allprofiletypes[i].equals("cell")) { if (i == 0) { break; } else {
	 * String typetemp = allprofiletypes[0]; String nametemp =
	 * allprofilenames[0]; String hosttemp = allhostnames[0]; String iptemp =
	 * allips[0];
	 * 
	 * allprofiletypes[0] = allprofiletypes[i]; allprofilenames[0] =
	 * allprofilenames[i]; allhostnames[0] = allhostnames[i]; allips[0] =
	 * allips[i];
	 * 
	 * allprofiletypes[i] = typetemp; allprofilenames[i] = nametemp;
	 * allhostnames[i] = hosttemp; allips[i] = iptemp;
	 * 
	 * } } }
	 * 
	 * List<WasClusterBean> arrList = new ArrayList<WasClusterBean>(); for (int
	 * i = 0; i < allips.length; i++) { WasClusterBean bean = new
	 * WasClusterBean(); bean.setIp(allips[i]); bean.setName(allhostnames[i]);
	 * bean.setProfilename(allprofilenames[i]);
	 * bean.setProfiletype(allprofiletypes[i]); arrList.add(bean); }
	 * 
	 * request.setAttribute("allservers", arrList); // 显示多个IP 主机名 概要情况 // String
	 * str1 = String.Join(",", allips);
	 * 
	 * StringBuffer sbhostnames = new StringBuffer(); StringBuffer sbhostips =
	 * new StringBuffer(); StringBuffer sbprofiletypes = new StringBuffer();
	 * StringBuffer sballprofilenames = new StringBuffer(); for (int i = 0; i <
	 * allhostnames.length; i++) {
	 * sbhostnames.append(allhostnames[i]).append(",");
	 * sbhostips.append(allips[i]).append(",");
	 * sbprofiletypes.append(allprofiletypes[i]).append(",");
	 * sballprofilenames.append(allprofilenames[i]).append(","); } String
	 * allhostname = sbhostnames.substring(0, sbhostnames.length() - 1);//
	 * 截取末尾的, String allip = sbhostips.substring(0, sbhostips.length() - 1);//
	 * 截取获取ip String allprofiletype = sbprofiletypes.substring(0,
	 * sbprofiletypes.length() - 1);// 截取profiletype String allprofilename =
	 * sballprofilenames.substring(0, sballprofilenames.length() - 1);//
	 * 截取profilename
	 * 
	 * request.setAttribute("allip", allip); request.setAttribute("allhostname",
	 * allhostname); request.setAttribute("allprofiletype", allprofiletype);
	 * request.setAttribute("allprofilename", allprofilename);
	 * 
	 * List serIds = new ArrayList(); if (serId != null && serId != "") {
	 * String[] ss = serId.split(","); for (int i = 0; i < ss.length; i++) {
	 * serIds.add(ss[i]); } } ArrayNode lists = amsRestService.getList(null,
	 * null, "odata/servers"); // System.out.println(lists); List<AddHostBean>
	 * lahb = new ArrayList<AddHostBean>(); for (JsonNode js : lists) {
	 * AddHostBean ahb = new AddHostBean(); ahb.setIP(js.get("IP").asText() !=
	 * null ? js.get("IP").asText() : ""); ahb.setName(js.get("Name").asText()
	 * != null ? js.get("Name").asText() : ""); ahb.setOS(js.get("OS").asText()
	 * != null ? js.get("OS").asText() : "");
	 * ahb.setPassword(js.get("Password").asText() != null ?
	 * js.get("Password").asText() : "");
	 * ahb.setUserID(js.get("UserID").asText() != null ?
	 * js.get("UserID").asText() : ""); ahb.setStatus(js.get("Status").asText()
	 * != null ? js.get("Status").asText() : "");
	 * ahb.set_id(js.get("_id").asText() != null ? js.get("_id").asText() : "");
	 * ahb.setHConf(js.get("HConf").asText() != null ? js.get("HConf").asText()
	 * : ""); ahb.setHVisor(js.get("HVisor").asText() != null ?
	 * js.get("HVisor").asText() : ""); lahb.add(ahb); } Collections.sort(lahb);
	 * // System.out.println(lahb);
	 * 
	 * List<AddHostBean> listDetial = new ArrayList<AddHostBean>();
	 * 
	 * for (int i = 0; i < lahb.size(); i++) { String serverId =
	 * lahb.get(i).get_id(); for (int j = 0; j < serIds.size(); j++) { if
	 * (serverId.equals(serIds.get(j))) { listDetial.add(lahb.get(i)); } } }
	 * Collections.sort(listDetial); Iterator<AddHostBean> iter =
	 * listDetial.iterator(); AddHostBean sb = null; while (iter.hasNext()) { sb
	 * = (AddHostBean) iter.next(); } request.setAttribute("total",
	 * lahb.size()); request.setAttribute("serId", serId);
	 * request.setAttribute("servers", listDetial);
	 * request.setAttribute("ptype", ptype); // request.setAttribute("hostId",
	 * sb.get_id());
	 * 
	 * String status = request.getParameter("status"); // was参数
	 * request.setAttribute("was_hostname", sb.getName());
	 * request.setAttribute("was_version", request.getParameter("was_version"));
	 * request.setAttribute("was_fp", request.getParameter("was_fp"));
	 * request.setAttribute("was_user", request.getParameter("was_user"));
	 * request.setAttribute("was_im_path", request.getParameter("was_im_path"));
	 * request.setAttribute("was_inst_path",
	 * request.getParameter("was_inst_path")); request.setAttribute("was_jdk7",
	 * request.getParameter("was_jdk7")); request.setAttribute("was_ip",
	 * request.getParameter("was_ip")); request.setAttribute("was_profile_type",
	 * request.getParameter("was_profile_type"));
	 * request.setAttribute("was_profile_path",
	 * request.getParameter("was_profile_path"));
	 * request.setAttribute("was_profile_name",
	 * request.getParameter("was_profile_name"));
	 * request.setAttribute("was_security",
	 * request.getParameter("was_security")); request.setAttribute("was_userid",
	 * request.getParameter("was_userid")); request.setAttribute("was_password",
	 * request.getParameter("was_password"));
	 * request.setAttribute("was_nofile_soft",
	 * request.getParameter("was_nofile_soft"));
	 * request.setAttribute("was_nofile_hard",
	 * request.getParameter("was_nofile_hard"));
	 * request.setAttribute("was_fsize_soft",
	 * request.getParameter("was_fsize_soft"));
	 * request.setAttribute("was_fsize_hard",
	 * request.getParameter("was_fsize_hard"));
	 * request.setAttribute("was_core_soft",
	 * request.getParameter("was_core_soft"));
	 * request.setAttribute("was_core_hard",
	 * request.getParameter("was_core_hard")); request.setAttribute("hostId",
	 * request.getParameter("hostId")); request.setAttribute("wasfix",
	 * request.getParameter("wasfix")); if (status.equals("confirm")) { return
	 * "instance_linux_wascluster_confirm"; } return null; }
	 */

	/*
	 * @RequestMapping("/toMqNextPage") public String
	 * toMqNextPage(HttpServletRequest request, HttpSession session) throws
	 * Exception { String qmgr_method=request.getParameter("qmgr_method");
	 * System.out.println(qmgr_method); String os =
	 * request.getParameter("os");//判断是AIX 还是linux 平台 String
	 * status=request.getParameter("status");//得到哪个页面的状态 String
	 * hostId=request.getParameter("hostId"); String ptype =
	 * request.getParameter("ptype"); String mqfix =
	 * request.getParameter("mqfix");//mq 版本补丁的简单形式
	 * request.setAttribute("mqfix", mqfix);
	 * request.setAttribute("mq_version",request.getParameter("mq_version"));
	 * request.setAttribute("mq_fp",request.getParameter("mq_fp"));
	 * request.setAttribute("mq_inst_path",request.getParameter("mq_inst_path"))
	 * ; request.setAttribute("mq_user",request.getParameter("mq_user"));
	 * request.setAttribute("mq_hostname",request.getParameter("mq_hostname"));
	 * request.setAttribute("mq_ip",request.getParameter("mq_ip"));
	 * request.setAttribute("qmgr_method",request.getParameter("qmgr_method"));
	 * request.setAttribute("qmgr_script",request.getParameter("qmgr_script"));
	 * request.setAttribute("mq_qmgr_name",request.getParameter("mq_qmgr_name"))
	 * ;
	 * request.setAttribute("mq_data_path",request.getParameter("mq_data_path"))
	 * ;
	 * request.setAttribute("mq_log_path",request.getParameter("mq_log_path"));
	 * request.setAttribute("mq_qmgr_plog",request.getParameter("mq_qmgr_plog"))
	 * ;
	 * request.setAttribute("mq_qmgr_slog",request.getParameter("mq_qmgr_slog"))
	 * ;
	 * request.setAttribute("mq_log_psize",request.getParameter("mq_log_psize"))
	 * ; request.setAttribute("mq_chl_max",request.getParameter("mq_chl_max"));
	 * request.setAttribute("mq_chl_kalive",request.getParameter("mq_chl_kalive"
	 * )); if(os.equals("aix")) {
	 * request.setAttribute("sys_semmni",request.getParameter("sys_semmni"));
	 * request.setAttribute("sys_semmns",request.getParameter("sys_semmns"));
	 * request.setAttribute("sys_shmmni",request.getParameter("sys_shmmni"));
	 * request.setAttribute("sys_maxuproc",request.getParameter("sys_maxuproc"))
	 * ;
	 * request.setAttribute("sys_nofiles",request.getParameter("sys_nofiles"));
	 * request.setAttribute("sys_data",request.getParameter("sys_data"));
	 * request.setAttribute("sys_stack",request.getParameter("sys_stack"));
	 * request.setAttribute("os", "aix"); }else if(os.equals("linux")) {
	 * request.setAttribute("sys_semmsl",request.getParameter("sys_semmsl"));
	 * request.setAttribute("sys_semmns",request.getParameter("sys_semmns"));
	 * request.setAttribute("sys_semopm",request.getParameter("sys_semopm"));
	 * request.setAttribute("sys_semmni",request.getParameter("sys_semmni"));
	 * request.setAttribute("sys_shmax",request.getParameter("sys_shmax"));
	 * request.setAttribute("sys_shmni",request.getParameter("sys_shmni"));
	 * request.setAttribute("sys_shmall",request.getParameter("sys_shmall"));
	 * request.setAttribute("sys_filemax",request.getParameter("sys_filemax"));
	 * request.setAttribute("sys_nofile",request.getParameter("sys_nofile"));
	 * request.setAttribute("sys_nproc",request.getParameter("sys_nproc"));
	 * request.setAttribute("os", "linux"); }else {
	 * 
	 * } String serId = request.getParameter("serId"); ObjectMapper om = new
	 * ObjectMapper();
	 * 
	 * List serIds = new ArrayList(); if (serId != null && serId != "") {
	 * String[] ss = serId.split(","); for (int i = 0; i < ss.length; i++) {
	 * serIds.add(ss[i]); } } ArrayNode lists = amsRestService.getList(null,
	 * null, "odata/servers"); System.out.println(lists);
	 * 
	 * List<AddHostBean> lahb = new ArrayList<AddHostBean>(); for (JsonNode js :
	 * lists) { AddHostBean ahb = new AddHostBean();
	 * ahb.setIP(js.get("IP").asText() != null ? js.get("IP").asText() : "");
	 * ahb.setName(js.get("Name").asText() != null ? js.get("Name").asText() :
	 * ""); ahb.setOS(js.get("OS").asText() != null ? js.get("OS").asText() :
	 * ""); ahb.setPassword(js.get("Password").asText() != null ?
	 * js.get("Password").asText() : "");
	 * ahb.setUserID(js.get("UserID").asText() != null ?
	 * js.get("UserID").asText() : ""); ahb.setStatus(js.get("Status").asText()
	 * != null ? js.get("Status").asText() : "");
	 * ahb.set_id(js.get("_id").asText() != null ? js.get("_id").asText() : "");
	 * ahb.setHConf(js.get("HConf").asText() != null ? js.get("HConf").asText()
	 * : ""); ahb.setHVisor(js.get("HVisor").asText() != null ?
	 * js.get("HVisor").asText() : ""); lahb.add(ahb); } Collections.sort(lahb);
	 * System.out.println(lahb);
	 * 
	 * request.setAttribute("serId", serId); request.setAttribute("total",
	 * lahb.size()); request.setAttribute("ptype", ptype);
	 * request.setAttribute("hostId", hostId); List<AddHostBean> listDetial =
	 * new ArrayList<AddHostBean>();
	 * 
	 * for (int i = 0; i < lahb.size(); i++) { String serverId =
	 * lahb.get(i).get_id(); for (int j = 0; j < serIds.size(); j++) { if
	 * (serverId.equals(serIds.get(j))) { listDetial.add(lahb.get(i)); } } }
	 * 
	 * request.setAttribute("servers", listDetial);
	 * 
	 * 
	 * if (status.equals("confirm")) { return
	 * "instance_aix_linux_mq_standalone_comfirm"; } return null;
	 * 
	 * //request.setAttribute("",request.getParameter("")); }
	 */
	// @SuppressWarnings({ "rawtypes", "unchecked" })

	/*
	 * @SuppressWarnings({ "rawtypes", "unchecked" })
	 * 
	 * @RequestMapping("/toDb2NextPage") public String
	 * toDb2NextPage(HttpServletRequest request, HttpSession session) throws
	 * Exception {
	 * 
	 * // 获取跳转页面信息 String status = request.getParameter("status"); // 获取ID
	 * String serId = request.getParameter("serId"); String ptype =
	 * request.getParameter("ptype"); // showRequest(request);
	 * System.out.println(serId); // 基本 // 节点名称 request.setAttribute("hostname",
	 * request.getParameter("hostname")); // 单节点IP request.setAttribute("ip",
	 * request.getParameter("ip")); request.setAttribute("bootip",
	 * request.getParameter("bootip")); request.setAttribute("hostId",
	 * request.getParameter("hostId")); request.setAttribute("db2_fixpack",
	 * request.getParameter("db2_fixpack")); // VG
	 * request.setAttribute("vgdb2home", request.getParameter("vgdb2home"));
	 * request.setAttribute("vgdb2log", request.getParameter("vgdb2log"));
	 * request.setAttribute("vgdb2archlog",
	 * request.getParameter("vgdb2archlog"));
	 * request.setAttribute("vgdataspace", request.getParameter("vgdataspace"));
	 * 
	 * // PV request.setAttribute("db2homepv",
	 * request.getParameter("db2homepv")); request.setAttribute("db2logpv",
	 * request.getParameter("db2logpv")); request.setAttribute("db2archlogpv",
	 * request.getParameter("db2archlogpv"));
	 * request.setAttribute("dataspacepv", request.getParameter("dataspacepv"));
	 * // VG创建方式 request.setAttribute("db2homemode",
	 * request.getParameter("db2homemode")); request.setAttribute("db2logmode",
	 * request.getParameter("db2logmode"));
	 * request.setAttribute("db2archlogmode",
	 * request.getParameter("db2archlogmode"));
	 * request.setAttribute("dataspacemode",
	 * request.getParameter("dataspacemode")); // NFS BEGIN -->
	 * request.setAttribute("nfsON", request.getParameter("nfsON"));
	 * 
	 * request.setAttribute("nfsIP1", request.getParameter("nfsIP1"));
	 * request.setAttribute("nfsSPoint1", request.getParameter("nfsSPoint1"));
	 * request.setAttribute("nfsCPoint1", request.getParameter("nfsCPoint1"));
	 * 
	 * request.setAttribute("nfsIP2", request.getParameter("nfsIP2"));
	 * request.setAttribute("nfsSPoint2", request.getParameter("nfsSPoint2"));
	 * request.setAttribute("nfsCPoint2", request.getParameter("nfsCPoint2"));
	 * 
	 * request.setAttribute("nfsIP3", request.getParameter("nfsIP3"));
	 * request.setAttribute("nfsSPoint3", request.getParameter("nfsSPoint3"));
	 * request.setAttribute("nfsCPoint3", request.getParameter("nfsCPoint3"));
	 * 
	 * request.setAttribute("nfsIP4", request.getParameter("nfsIP4"));
	 * request.setAttribute("nfsSPoint4", request.getParameter("nfsSPoint4"));
	 * request.setAttribute("nfsCPoint4", request.getParameter("nfsCPoint4"));
	 * 
	 * request.setAttribute("nfsIP5", request.getParameter("nfsIP5"));
	 * request.setAttribute("nfsSPoint5", request.getParameter("nfsSPoint5"));
	 * request.setAttribute("nfsCPoint5", request.getParameter("nfsCPoint5"));
	 * 
	 * request.setAttribute("nfsIP6", request.getParameter("nfsIP6"));
	 * request.setAttribute("nfsSPoint6", request.getParameter("nfsSPoint6"));
	 * request.setAttribute("nfsCPoint6", request.getParameter("nfsCPoint6"));
	 * 
	 * request.setAttribute("nfsIP7", request.getParameter("nfsIP7"));
	 * request.setAttribute("nfsSPoint7", request.getParameter("nfsSPoint7"));
	 * request.setAttribute("nfsCPoint7", request.getParameter("nfsCPoint7"));
	 * 
	 * request.setAttribute("nfsIP8", request.getParameter("nfsIP8"));
	 * request.setAttribute("nfsSPoint8", request.getParameter("nfsSPoint8"));
	 * request.setAttribute("nfsCPoint8", request.getParameter("nfsCPoint8"));
	 * 
	 * request.setAttribute("nfsIP9", request.getParameter("nfsIP9"));
	 * request.setAttribute("nfsSPoint9", request.getParameter("nfsSPoint9"));
	 * request.setAttribute("nfsCPoint9", request.getParameter("nfsCPoint9"));
	 * 
	 * request.setAttribute("nfsIP10", request.getParameter("nfsIP10"));
	 * request.setAttribute("nfsSPoint10", request.getParameter("nfsSPoint10"));
	 * request.setAttribute("nfsCPoint10", request.getParameter("nfsCPoint10"));
	 * 
	 * request.setAttribute("nfsIP11", request.getParameter("nfsIP11"));
	 * request.setAttribute("nfsSPoint11", request.getParameter("nfsSPoint11"));
	 * request.setAttribute("nfsCPoint11", request.getParameter("nfsCPoint11"));
	 * 
	 * request.setAttribute("nfsIP12", request.getParameter("nfsIP12"));
	 * request.setAttribute("nfsSPoint12", request.getParameter("nfsSPoint12"));
	 * request.setAttribute("nfsCPoint12", request.getParameter("nfsCPoint12"));
	 * 
	 * request.setAttribute("nfsIP13", request.getParameter("nfsIP13"));
	 * request.setAttribute("nfsSPoint13", request.getParameter("nfsSPoint13"));
	 * request.setAttribute("nfsCPoint13", request.getParameter("nfsCPoint13"));
	 * 
	 * request.setAttribute("nfsIP14", request.getParameter("nfsIP14"));
	 * request.setAttribute("nfsSPoint14", request.getParameter("nfsSPoint14"));
	 * request.setAttribute("nfsCPoint14", request.getParameter("nfsCPoint14"));
	 * 
	 * request.setAttribute("nfsIP15", request.getParameter("nfsIP15"));
	 * request.setAttribute("nfsSPoint15", request.getParameter("nfsSPoint15"));
	 * request.setAttribute("nfsCPoint15", request.getParameter("nfsCPoint15"));
	 * 
	 * // DB2信息 基本信息 request.setAttribute("db2_version",
	 * request.getParameter("db2_version")); request.setAttribute("db2_db2base",
	 * request.getParameter("db2_db2base")); request.setAttribute("db2_dbpath",
	 * request.getParameter("db2_dbpath"));
	 * request.setAttribute("db2_db2insusr",
	 * request.getParameter("db2_db2insusr"));
	 * request.setAttribute("db2_svcename",
	 * request.getParameter("db2_svcename")); request.setAttribute("db2_dbname",
	 * request.getParameter("db2_dbname")); request.setAttribute("db2_codeset",
	 * request.getParameter("db2_codeset"));
	 * request.setAttribute("db2_dbdatapath",
	 * request.getParameter("db2_dbdatapath"));
	 * 
	 * // DB2信息 实例高级属性 request.setAttribute("db2_db2insgrp",
	 * request.getParameter("db2_db2insgrp"));
	 * request.setAttribute("db2_db2fenusr",
	 * request.getParameter("db2_db2fenusr"));
	 * request.setAttribute("db2_db2fengrp",
	 * request.getParameter("db2_db2fengrp"));
	 * request.setAttribute("db2_db2comm", request.getParameter("db2_db2comm"));
	 * request.setAttribute("db2_db2codepage",
	 * request.getParameter("db2_db2codepage"));
	 * request.setAttribute("db2_initagents",
	 * request.getParameter("db2_initagents"));
	 * request.setAttribute("db2_poolagents",
	 * request.getParameter("db2_poolagents"));
	 * request.setAttribute("db2_max_coordagents",
	 * request.getParameter("db2_max_coordagents"));
	 * request.setAttribute("db2_max_connectings",
	 * request.getParameter("db2_max_connectings"));
	 * request.setAttribute("db2_diagsize",
	 * request.getParameter("db2_diagsize"));
	 * request.setAttribute("db2_mon_buf", request.getParameter("db2_mon_buf"));
	 * request.setAttribute("db2_mon_lock",
	 * request.getParameter("db2_mon_lock"));
	 * request.setAttribute("db2_mon_sort",
	 * request.getParameter("db2_mon_sort"));
	 * request.setAttribute("db2_mon_stmt",
	 * request.getParameter("db2_mon_stmt"));
	 * request.setAttribute("db2_mon_table",
	 * request.getParameter("db2_mon_table"));
	 * request.setAttribute("db2_mon_uow", request.getParameter("db2_mon_uow"));
	 * request.setAttribute("db2_health_mon",
	 * request.getParameter("db2_health_mon"));
	 * request.setAttribute("db2_mon_heap",
	 * request.getParameter("db2_mon_heap")); // DB2信息 实例高级属性
	 * request.setAttribute("db2_db2log", request.getParameter("db2_db2log"));
	 * request.setAttribute("db2_logarchpath",
	 * request.getParameter("db2_logarchpath"));
	 * request.setAttribute("db2_backuppath",
	 * request.getParameter("db2_backuppath")); request.setAttribute("db2_stmm",
	 * request.getParameter("db2_stmm")); request.setAttribute("db2_locklist",
	 * request.getParameter("db2_locklist"));
	 * request.setAttribute("db2_maxlocks",
	 * request.getParameter("db2_maxlocks"));
	 * request.setAttribute("db2_locktimeout",
	 * request.getParameter("db2_locktimeout"));
	 * request.setAttribute("db2_sortheap",
	 * request.getParameter("db2_sortheap"));
	 * request.setAttribute("db2_logfilesize",
	 * request.getParameter("db2_logfilesize"));
	 * request.setAttribute("db2_logprimary",
	 * request.getParameter("db2_logprimary"));
	 * request.setAttribute("db2_logsecond",
	 * request.getParameter("db2_logsecond"));
	 * request.setAttribute("db2_logbuff", request.getParameter("db2_logbuff"));
	 * request.setAttribute("db2_softmax", request.getParameter("db2_softmax"));
	 * request.setAttribute("db2_trackmod",
	 * request.getParameter("db2_trackmod"));
	 * request.setAttribute("db2_pagesize",
	 * request.getParameter("db2_pagesize")); ObjectMapper om = new
	 * ObjectMapper();
	 * 
	 * List serIds = new ArrayList(); if (serId != null && serId != "") {
	 * String[] ss = serId.split(","); for (int i = 0; i < ss.length; i++) {
	 * serIds.add(ss[i]); } } ArrayNode lists = amsRestService.getList(null,
	 * null, "odata/servers"); System.out.println(lists);
	 * 
	 * List<AddHostBean> lahb = new ArrayList<AddHostBean>(); for (JsonNode js :
	 * lists) { AddHostBean ahb = new AddHostBean();
	 * ahb.setIP(js.get("IP").asText() != null ? js.get("IP").asText() : "");
	 * ahb.setName(js.get("Name").asText() != null ? js.get("Name").asText() :
	 * ""); ahb.setOS(js.get("OS").asText() != null ? js.get("OS").asText() :
	 * ""); ahb.setPassword(js.get("Password").asText() != null ?
	 * js.get("Password").asText() : "");
	 * ahb.setUserID(js.get("UserID").asText() != null ?
	 * js.get("UserID").asText() : ""); ahb.setStatus(js.get("Status").asText()
	 * != null ? js.get("Status").asText() : "");
	 * ahb.set_id(js.get("_id").asText() != null ? js.get("_id").asText() : "");
	 * ahb.setHConf(js.get("HConf").asText() != null ? js.get("HConf").asText()
	 * : ""); ahb.setHVisor(js.get("HVisor").asText() != null ?
	 * js.get("HVisor").asText() : ""); lahb.add(ahb); } Collections.sort(lahb);
	 * System.out.println(lahb);
	 * 
	 * request.setAttribute("serId", serId); request.setAttribute("total",
	 * lahb.size()); request.setAttribute("ptype", ptype); List<AddHostBean>
	 * listDetial = new ArrayList<AddHostBean>();
	 * 
	 * for (int i = 0; i < lahb.size(); i++) { String serverId =
	 * lahb.get(i).get_id(); for (int j = 0; j < serIds.size(); j++) { if
	 * (serverId.equals(serIds.get(j))) { listDetial.add(lahb.get(i)); } } }
	 * 
	 * request.setAttribute("servers", listDetial);
	 * 
	 * 
	 * List<ServerBean> listDetial = new ArrayList<ServerBean>();
	 * List<ServerBean> list; List<FlavorBean> fList; List<ImagesBean>
	 * imageList; try { list = instanceService.getServerListV2(tenantId,
	 * tokenId); for (int i = 0; i < list.size(); i++) { String serverId =
	 * list.get(i).getId(); for (int j = 0; j < serIds.size(); j++) { if
	 * (serverId.equals(serIds.get(j))) { listDetial.add(list.get(i)); } } }
	 * fList = flavorsService.getAllFlavorsList(tenantId, tokenId); imageList =
	 * imageService.getAllImageList(tenantId, tokenId); } catch
	 * (LoginTimeOutException e) { return this.setExceptionTologin(e, request);
	 * } catch (BaseException e) { e.printStackTrace(); throw new
	 * ReturnToMainPageException(e.getMessage()); }
	 * 
	 * Collections.sort(listDetial); request.setAttribute("serId", serId);
	 * request.setAttribute("servers", listDetial);
	 * request.setAttribute("flavors", fList); request.setAttribute("imageList",
	 * imageList);
	 * 
	 * if (status.equals("")) { } else if (status.equals("installConfirmNew")) {
	 * 
	 * System.out.println("=======new_confirm======");
	 * System.out.println("db2base=:" + request.getParameter("db2base"));
	 * request.setAttribute("db2base", request.getParameter("db2base"));
	 * 
	 * System.out.println("*********new_confirm*******"); return
	 * "instance_aix_db2_config_new_confirm"; } else if
	 * (status.equals("installPageNew")) {
	 * System.out.println("=============ceshixinyemian================");
	 * ObjectNode basicInfo = AMS2KeyUtil.getBasicInfo();
	 * request.setAttribute("basicInfo", basicInfo); ObjectNode instProp =
	 * AMS2KeyUtil.getInstAdvProp(); request.setAttribute("instProp", instProp);
	 * ObjectNode dbProp = AMS2KeyUtil.getDB2AdvProp();
	 * request.setAttribute("dbProp", dbProp);
	 * 
	 * String ha_dataspace1pv = request.getParameter("dataspacepv");
	 * System.out.println("=========ha_dataspace1pv=========" +
	 * ha_dataspace1pv);
	 * 
	 * String[] dataspcepvs = ha_dataspace1pv.split(","); String db2_dbdatapath
	 * = ""; for (int i = 1; i <= dataspcepvs.length; i++) { db2_dbdatapath =
	 * db2_dbdatapath + "/db2dataspace" + i + ","; } if ("" != db2_dbdatapath) {
	 * db2_dbdatapath = db2_dbdatapath.substring(0, db2_dbdatapath.length() -
	 * 1); } System.out.println("====db2_dbdatapath====" + db2_dbdatapath);
	 * request.setAttribute("db2_dbdatapath", db2_dbdatapath); return
	 * "instance_aix_db2_config_new"; } else if (status.equals("makevg")) {
	 * ArrayNode allHdisk = om.createArrayNode(); for (int i = 0; i <
	 * listDetial.size(); i++) { ArrayNode hdisks = om.createArrayNode(); String
	 * instname = listDetial.get(i).getName(); String instaddr =
	 * listDetial.get(i).getIP(); // 获取选中实例的HDiskInfo String cmd_cfgmgr =
	 * amsprop.getProperty(AMS2PropertyKeyConst.cmd_cfgmgr); List<String> info =
	 * amsRestService.getCmdInfoByAddr(instname, instaddr, cmd_cfgmgr); for
	 * (String line : info) {
	 * 
	 * ObjectNode hd = om.createObjectNode(); hd.put("hdiskname", line);
	 * hdisks.add(hd); } allHdisk.add(hdisks); }
	 * request.setAttribute("allHdisk", allHdisk);
	 * 
	 * Iterator<AddHostBean> iter = listDetial.iterator(); AddHostBean sb =
	 * null; while (iter.hasNext()) { sb = (AddHostBean) iter.next(); }
	 * 
	 * request.setAttribute("hostId", sb.get_id());// hostId
	 * request.setAttribute("ip", sb.getIP());// ip设置为IP
	 * request.setAttribute("hostname", sb.getName());// 主机名 return
	 * "instance_aix_db2_makevg"; } return null; }
	 */

	/**
	 * @Title: toConfirmPage
	 * @Description: 到配置确认页面
	 * @param @param
	 *            request
	 * @param @param
	 *            session
	 * @param @return
	 * @param @throws
	 *            Exception
	 * @author LiangRui
	 * @throws @Time
	 *             2015年6月16日 下午12:25:18
	 *//*
		 * @SuppressWarnings({ "rawtypes", "unchecked" })
		 * 
		 * @RequestMapping("/toNextPage") public String
		 * toNextPage(HttpServletRequest request, HttpSession session) throws
		 * Exception { ObjectMapper om = new ObjectMapper();
		 * 
		 * String tenantId = ""; String tokenId = ""; try { tenantId =
		 * OpenStackUtil.getTenantId(); tokenId = OpenStackUtil.getTokenId(); }
		 * catch (LoginTimeOutException e) { return this.setExceptionTologin(e,
		 * request); }
		 * 
		 * // 获取跳转页面信息 String status = request.getParameter("status"); // 获取ID
		 * String serId = request.getParameter("serId"); String ptype =
		 * request.getParameter("ptype"); // 第一步host信息 获取输入的HOST参数.
		 * System.out.println("hostNames=" +
		 * request.getParameterValues("hostNames"));
		 * request.setAttribute("hostNames",
		 * request.getParameterValues("hostNames") == null ? "" :
		 * Arrays.asList(request.getParameterValues("hostNames")));
		 * request.setAttribute("hostIps", request.getParameterValues("hostIps")
		 * == null ? "" : Arrays.asList(request.getParameterValues("hostIps")));
		 * request.setAttribute("serNames",
		 * request.getParameterValues("serNames") == null ? "" :
		 * Arrays.asList(request.getParameterValues("serNames")));
		 * request.setAttribute("serIps", request.getParameterValues("serIps")
		 * == null ? "" : Arrays.asList(request.getParameterValues("serIps")));
		 * request.setAttribute("perNames",
		 * request.getParameterValues("perNames") == null ? "" :
		 * Arrays.asList(request.getParameterValues("perNames")));
		 * request.setAttribute("perIps", request.getParameterValues("perIps")
		 * == null ? "" : Arrays.asList(request.getParameterValues("perIps")));
		 * 
		 * // 基本 request.setAttribute("haname", request.getParameter("haname"));
		 * request.setAttribute("ha_RGNmae", request.getParameter("ha_RGNmae"));
		 * request.setAttribute("ha_ASName", request.getParameter("ha_ASName"));
		 * request.setAttribute("ha_primaryNode",
		 * request.getParameter("ha_primaryNode"));
		 * 
		 * // IP
		 * 
		 * request.setAttribute("hostId1", request.getParameter("hostId1"));
		 * request.setAttribute("hostId2", request.getParameter("hostId2"));
		 * request.setAttribute("ha_ip1", request.getParameter("ha_ip1"));
		 * request.setAttribute("ha_ip2", request.getParameter("ha_ip2"));
		 * request.setAttribute("ha_bootip1",
		 * request.getParameter("ha_bootip1"));
		 * request.setAttribute("ha_bootip2",
		 * request.getParameter("ha_bootip2")); request.setAttribute("ha_svcip",
		 * request.getParameter("ha_svcip"));
		 * 
		 * // 主机别名 request.setAttribute("ha_hostname1",
		 * request.getParameter("ha_hostname1"));
		 * request.setAttribute("ha_hostname2",
		 * request.getParameter("ha_hostname2"));
		 * request.setAttribute("ha_bootalias1",
		 * request.getParameter("ha_bootalias1"));
		 * request.setAttribute("ha_bootalias2",
		 * request.getParameter("ha_bootalias2"));
		 * request.setAttribute("ha_svcalias",
		 * request.getParameter("ha_svcalias"));
		 * 
		 * request.setAttribute("hostName", request.getParameter("hostName"));
		 * request.setAttribute("hostIp", request.getParameter("hostIp"));
		 * request.setAttribute("serName", request.getParameter("serName"));
		 * request.setAttribute("serIp", request.getParameter("serIp"));
		 * request.setAttribute("perName", request.getParameter("perName"));
		 * request.setAttribute("perIp", request.getParameter("perIp"));
		 * 
		 * // 旧VG信息 request.setAttribute("hdisknames",
		 * request.getParameterValues("hdisknames") == null ? "" :
		 * Arrays.asList(request.getParameterValues("hdisknames")));
		 * request.setAttribute("hdiskids",
		 * request.getParameterValues("hdiskids") == null ? "" :
		 * Arrays.asList(request.getParameterValues("hdiskids")));
		 * request.setAttribute("vgtypes", request.getParameterValues("vgtypes")
		 * == null ? "" : Arrays.asList(request.getParameterValues("vgtypes")));
		 * 
		 * request.setAttribute("hdiskname", request.getParameter("hdiskname"));
		 * request.setAttribute("hdiskid", request.getParameter("hdiskid"));
		 * request.setAttribute("vgtype", request.getParameter("vgtype")); // VG
		 * request.setAttribute("ha_vgdb2home",
		 * request.getParameter("ha_vgdb2home"));
		 * request.setAttribute("ha_vgdb2log",
		 * request.getParameter("ha_vgdb2log"));
		 * request.setAttribute("ha_vgdb2archlog",
		 * request.getParameter("ha_vgdb2archlog")); //
		 * request.setAttribute("ha_vgdb2backup", //
		 * request.getParameter("ha_vgdb2backup"));
		 * request.setAttribute("ha_vgdataspace",
		 * request.getParameter("ha_vgdataspace")); //
		 * request.setAttribute("ha_vgdataspace2", //
		 * request.getParameter("ha_vgdataspace2")); //
		 * request.setAttribute("ha_vgdataspace3", //
		 * request.getParameter("ha_vgdataspace3")); //
		 * request.setAttribute("ha_vgdataspace4", //
		 * request.getParameter("ha_vgdataspace4"));
		 * request.setAttribute("ha_vgcaap", request.getParameter("ha_vgcaap"));
		 * 
		 * // PV request.setAttribute("ha_db2homepv",
		 * request.getParameter("ha_db2homepv"));
		 * request.setAttribute("ha_db2logpv",
		 * request.getParameter("ha_db2logpv"));
		 * request.setAttribute("ha_db2archlogpv",
		 * request.getParameter("ha_db2archlogpv")); //
		 * request.setAttribute("ha_db2backuppv", //
		 * request.getParameter("ha_db2backuppv"));
		 * request.setAttribute("ha_dataspace1pv",
		 * request.getParameter("ha_dataspace1pv")); //
		 * request.setAttribute("ha_dataspace2pv", //
		 * request.getParameter("ha_dataspace2pv")); //
		 * request.setAttribute("ha_dataspace3pv", //
		 * request.getParameter("ha_dataspace3pv")); //
		 * request.setAttribute("ha_dataspace4pv", //
		 * request.getParameter("ha_dataspace4pv"));
		 * request.setAttribute("ha_caappv", request.getParameter("ha_caappv"));
		 * 
		 * // VG创建方式 request.setAttribute("ha_db2homemode",
		 * request.getParameter("ha_db2homemode"));
		 * request.setAttribute("ha_db2logmode",
		 * request.getParameter("ha_db2logmode"));
		 * request.setAttribute("ha_db2archlogmode",
		 * request.getParameter("ha_db2archlogmode")); //
		 * request.setAttribute("ha_db2backupmode", //
		 * request.getParameter("ha_db2backupmode"));
		 * request.setAttribute("ha_dataspacemode",
		 * request.getParameter("ha_dataspacemode")); //
		 * request.setAttribute("ha_dataspace2mode", //
		 * request.getParameter("ha_dataspace2mode")); //
		 * request.setAttribute("ha_dataspace3mode", //
		 * request.getParameter("ha_dataspace3mode")); //
		 * request.setAttribute("ha_dataspace4mode", //
		 * request.getParameter("ha_dataspace4mode"));
		 * request.setAttribute("ha_caapmode",
		 * request.getParameter("ha_caapmode"));
		 * 
		 * // HA切换策略 BEGIN request.setAttribute("ha_startpolicy",
		 * request.getParameter("ha_startpolicy"));
		 * request.setAttribute("ha_fopolicy",
		 * request.getParameter("ha_fopolicy"));
		 * request.setAttribute("ha_fbpolicy",
		 * request.getParameter("ha_fbpolicy"));
		 * 
		 * // NFS BEGIN --> request.setAttribute("ha_nfsON",
		 * request.getParameter("ha_nfsON"));
		 * 
		 * request.setAttribute("ha_nfsIP1", request.getParameter("ha_nfsIP1"));
		 * request.setAttribute("ha_nfsSPoint1",
		 * request.getParameter("ha_nfsSPoint1"));
		 * request.setAttribute("ha_nfsCPoint1",
		 * request.getParameter("ha_nfsCPoint1"));
		 * 
		 * request.setAttribute("ha_nfsIP2", request.getParameter("ha_nfsIP2"));
		 * request.setAttribute("ha_nfsSPoint2",
		 * request.getParameter("ha_nfsSPoint2"));
		 * request.setAttribute("ha_nfsCPoint2",
		 * request.getParameter("ha_nfsCPoint2"));
		 * 
		 * request.setAttribute("ha_nfsIP3", request.getParameter("ha_nfsIP3"));
		 * request.setAttribute("ha_nfsSPoint3",
		 * request.getParameter("ha_nfsSPoint3"));
		 * request.setAttribute("ha_nfsCPoint3",
		 * request.getParameter("ha_nfsCPoint3"));
		 * 
		 * request.setAttribute("ha_nfsIP4", request.getParameter("ha_nfsIP4"));
		 * request.setAttribute("ha_nfsSPoint4",
		 * request.getParameter("ha_nfsSPoint4"));
		 * request.setAttribute("ha_nfsCPoint4",
		 * request.getParameter("ha_nfsCPoint4"));
		 * 
		 * request.setAttribute("ha_nfsIP5", request.getParameter("ha_nfsIP5"));
		 * request.setAttribute("ha_nfsSPoint5",
		 * request.getParameter("ha_nfsSPoint5"));
		 * request.setAttribute("ha_nfsCPoint5",
		 * request.getParameter("ha_nfsCPoint5"));
		 * 
		 * request.setAttribute("ha_nfsIP6", request.getParameter("ha_nfsIP6"));
		 * request.setAttribute("ha_nfsSPoint6",
		 * request.getParameter("ha_nfsSPoint6"));
		 * request.setAttribute("ha_nfsCPoint6",
		 * request.getParameter("ha_nfsCPoint6"));
		 * 
		 * request.setAttribute("ha_nfsIP7", request.getParameter("ha_nfsIP7"));
		 * request.setAttribute("ha_nfsSPoint7",
		 * request.getParameter("ha_nfsSPoint7"));
		 * request.setAttribute("ha_nfsCPoint7",
		 * request.getParameter("ha_nfsCPoint7"));
		 * 
		 * request.setAttribute("ha_nfsIP8", request.getParameter("ha_nfsIP8"));
		 * request.setAttribute("ha_nfsSPoint8",
		 * request.getParameter("ha_nfsSPoint8"));
		 * request.setAttribute("ha_nfsCPoint8",
		 * request.getParameter("ha_nfsCPoint8"));
		 * 
		 * request.setAttribute("ha_nfsIP9", request.getParameter("ha_nfsIP9"));
		 * request.setAttribute("ha_nfsSPoint9",
		 * request.getParameter("ha_nfsSPoint9"));
		 * request.setAttribute("ha_nfsCPoint9",
		 * request.getParameter("ha_nfsCPoint9"));
		 * 
		 * request.setAttribute("ha_nfsIP10",
		 * request.getParameter("ha_nfsIP10"));
		 * request.setAttribute("ha_nfsSPoint10",
		 * request.getParameter("ha_nfsSPoint10"));
		 * request.setAttribute("ha_nfsCPoint10",
		 * request.getParameter("ha_nfsCPoint10"));
		 * 
		 * request.setAttribute("ha_nfsIP11",
		 * request.getParameter("ha_nfsIP11"));
		 * request.setAttribute("ha_nfsSPoint11",
		 * request.getParameter("ha_nfsSPoint11"));
		 * request.setAttribute("ha_nfsCPoint11",
		 * request.getParameter("ha_nfsCPoint11"));
		 * 
		 * request.setAttribute("ha_nfsIP12",
		 * request.getParameter("ha_nfsIP12"));
		 * request.setAttribute("ha_nfsSPoint12",
		 * request.getParameter("ha_nfsSPoint12"));
		 * request.setAttribute("ha_nfsCPoint12",
		 * request.getParameter("ha_nfsCPoint12"));
		 * 
		 * request.setAttribute("ha_nfsIP13",
		 * request.getParameter("ha_nfsIP13"));
		 * request.setAttribute("ha_nfsSPoint13",
		 * request.getParameter("ha_nfsSPoint13"));
		 * request.setAttribute("ha_nfsCPoint13",
		 * request.getParameter("ha_nfsCPoint13"));
		 * 
		 * request.setAttribute("ha_nfsIP14",
		 * request.getParameter("ha_nfsIP14"));
		 * request.setAttribute("ha_nfsSPoint14",
		 * request.getParameter("ha_nfsSPoint14"));
		 * request.setAttribute("ha_nfsCPoint14",
		 * request.getParameter("ha_nfsCPoint14"));
		 * 
		 * request.setAttribute("ha_nfsIP15",
		 * request.getParameter("ha_nfsIP15"));
		 * request.setAttribute("ha_nfsSPoint15",
		 * request.getParameter("ha_nfsSPoint15"));
		 * request.setAttribute("ha_nfsCPoint15",
		 * request.getParameter("ha_nfsCPoint15"));
		 * 
		 * // DB2信息 基本信息 request.setAttribute("db2_version",
		 * request.getParameter("db2_version"));
		 * request.setAttribute("db2_db2base",
		 * request.getParameter("db2_db2base"));
		 * request.setAttribute("db2_dbpath",
		 * request.getParameter("db2_dbpath"));
		 * request.setAttribute("db2_db2insusr",
		 * request.getParameter("db2_db2insusr"));
		 * request.setAttribute("db2_svcename",
		 * request.getParameter("db2_svcename"));
		 * request.setAttribute("db2_dbname",
		 * request.getParameter("db2_dbname"));
		 * request.setAttribute("db2_codeset",
		 * request.getParameter("db2_codeset"));
		 * request.setAttribute("db2_dbdatapath",
		 * request.getParameter("db2_dbdatapath"));
		 * 
		 * // DB2信息 实例高级属性 request.setAttribute("db2_db2insgrp",
		 * request.getParameter("db2_db2insgrp"));
		 * request.setAttribute("db2_db2fenusr",
		 * request.getParameter("db2_db2fenusr"));
		 * request.setAttribute("db2_db2fengrp",
		 * request.getParameter("db2_db2fengrp"));
		 * request.setAttribute("db2_db2comm",
		 * request.getParameter("db2_db2comm"));
		 * request.setAttribute("db2_db2codepage",
		 * request.getParameter("db2_db2codepage"));
		 * request.setAttribute("db2_initagents",
		 * request.getParameter("db2_initagents"));
		 * request.setAttribute("db2_poolagents",
		 * request.getParameter("db2_poolagents"));
		 * request.setAttribute("db2_max_coordagents",
		 * request.getParameter("db2_max_coordagents"));
		 * request.setAttribute("db2_max_connectings",
		 * request.getParameter("db2_max_connectings"));
		 * request.setAttribute("db2_diagsize",
		 * request.getParameter("db2_diagsize"));
		 * request.setAttribute("db2_mon_buf",
		 * request.getParameter("db2_mon_buf"));
		 * request.setAttribute("db2_mon_lock",
		 * request.getParameter("db2_mon_lock"));
		 * request.setAttribute("db2_mon_sort",
		 * request.getParameter("db2_mon_sort"));
		 * request.setAttribute("db2_mon_stmt",
		 * request.getParameter("db2_mon_stmt"));
		 * request.setAttribute("db2_mon_table",
		 * request.getParameter("db2_mon_table"));
		 * request.setAttribute("db2_mon_uow",
		 * request.getParameter("db2_mon_uow"));
		 * request.setAttribute("db2_health_mon",
		 * request.getParameter("db2_health_mon"));
		 * request.setAttribute("db2_mon_heap",
		 * request.getParameter("db2_mon_heap")); // DB2信息 实例高级属性
		 * request.setAttribute("db2_db2log",
		 * request.getParameter("db2_db2log"));
		 * request.setAttribute("db2_logarchpath",
		 * request.getParameter("db2_logarchpath"));
		 * request.setAttribute("db2_backuppath",
		 * request.getParameter("db2_backuppath"));
		 * request.setAttribute("db2_stmm", request.getParameter("db2_stmm"));
		 * request.setAttribute("db2_locklist",
		 * request.getParameter("db2_locklist"));
		 * request.setAttribute("db2_maxlocks",
		 * request.getParameter("db2_maxlocks"));
		 * request.setAttribute("db2_locktimeout",
		 * request.getParameter("db2_locktimeout"));
		 * request.setAttribute("db2_sortheap",
		 * request.getParameter("db2_sortheap"));
		 * request.setAttribute("db2_logfilesize",
		 * request.getParameter("db2_logfilesize"));
		 * request.setAttribute("db2_logprimary",
		 * request.getParameter("db2_logprimary"));
		 * request.setAttribute("db2_logsecond",
		 * request.getParameter("db2_logsecond"));
		 * request.setAttribute("db2_logbuff",
		 * request.getParameter("db2_logbuff"));
		 * request.setAttribute("db2_softmax",
		 * request.getParameter("db2_softmax"));
		 * request.setAttribute("db2_trackmod",
		 * request.getParameter("db2_trackmod"));
		 * request.setAttribute("db2_pagesize",
		 * request.getParameter("db2_pagesize"));
		 * 
		 * List serIds = new ArrayList(); if (serId != null && serId != "") {
		 * String[] ss = serId.split(","); for (int i = 0; i < ss.length; i++) {
		 * serIds.add(ss[i]); } }
		 * 
		 * ArrayNode lists = amsRestService.getList(null, null,
		 * "odata/servers"); System.out.println(lists);
		 * 
		 * List<AddHostBean> lahb = new ArrayList<AddHostBean>(); for (JsonNode
		 * js : lists) { AddHostBean ahb = new AddHostBean();
		 * ahb.setIP(js.get("IP").asText() != null ? js.get("IP").asText() :
		 * ""); ahb.setName(js.get("Name").asText() != null ?
		 * js.get("Name").asText() : ""); ahb.setOS(js.get("OS").asText() !=
		 * null ? js.get("OS").asText() : "");
		 * ahb.setPassword(js.get("Password").asText() != null ?
		 * js.get("Password").asText() : "");
		 * ahb.setUserID(js.get("UserID").asText() != null ?
		 * js.get("UserID").asText() : "");
		 * ahb.setStatus(js.get("Status").asText() != null ?
		 * js.get("Status").asText() : ""); ahb.set_id(js.get("_id").asText() !=
		 * null ? js.get("_id").asText() : "");
		 * ahb.setHConf(js.get("HConf").asText() != null ?
		 * js.get("HConf").asText() : "");
		 * ahb.setHVisor(js.get("HVisor").asText() != null ?
		 * js.get("HVisor").asText() : ""); lahb.add(ahb); }
		 * Collections.sort(lahb); System.out.println(lahb);
		 * 
		 * List<AddHostBean> listDetial = new ArrayList<AddHostBean>();
		 * 
		 * for (int i = 0; i < lahb.size(); i++) { String serverId =
		 * lahb.get(i).get_id(); for (int j = 0; j < serIds.size(); j++) { if
		 * (serverId.equals(serIds.get(j))) { listDetial.add(lahb.get(i)); } } }
		 * Collections.sort(listDetial); request.setAttribute("serId", serId);
		 * request.setAttribute("servers", listDetial);
		 * request.setAttribute("total", lahb.size());
		 * request.setAttribute("ptype", ptype); if
		 * (status.equals("configConfirm")) { return
		 * "instance_aix_db2ha_config_confirm"; } else if
		 * (status.equals("installPage")) { ObjectNode db2Config =
		 * AMS2KeyUtil.getDb2ConfigInfo(); request.setAttribute("db2Config",
		 * db2Config); return "instance_aix_db2ha_install"; } else if
		 * (status.equals("installConfirm")) { return
		 * "instance_aix_db2ha_install_confirm"; }
		 * 
		 * // 新增页面Star========= else if (status.equals("installPageNew")) {
		 * System.out.println("=============ceshixinyemian================");
		 * ObjectNode basicInfo = AMS2KeyUtil.getBasicInfo();
		 * request.setAttribute("basicInfo", basicInfo); ObjectNode instProp =
		 * AMS2KeyUtil.getInstAdvProp(); request.setAttribute("instProp",
		 * instProp); ObjectNode dbProp = AMS2KeyUtil.getDB2AdvProp();
		 * request.setAttribute("dbProp", dbProp);
		 * 
		 * String ha_dataspace1pv = request.getParameter("ha_dataspace1pv");
		 * System.out.println("=========ha_dataspace1pv=========" +
		 * ha_dataspace1pv);
		 * 
		 * String[] dataspcepvs = ha_dataspace1pv.split(","); String
		 * db2_dbdatapath = ""; for (int i = 1; i <= dataspcepvs.length; i++) {
		 * db2_dbdatapath = db2_dbdatapath + "/db2dataspace" + i + ","; } if (""
		 * != db2_dbdatapath) { db2_dbdatapath = db2_dbdatapath.substring(0,
		 * db2_dbdatapath.length() - 1); }
		 * System.out.println("====db2_dbdatapath====" + db2_dbdatapath);
		 * request.setAttribute("db2_dbdatapath", db2_dbdatapath); return
		 * "instance_aix_db2ha_config_new"; } else if
		 * (status.equals("installConfirmNew")) {
		 * 
		 * System.out.println("=======new_confirm======");
		 * System.out.println("db2base=:" + request.getParameter("db2base"));
		 * request.setAttribute("db2base", request.getParameter("db2base"));
		 * 
		 * System.out.println("*********new_confirm*******"); return
		 * "instance_aix_db2ha_config_new_confirm"; } // 新增页面End=========
		 * 
		 * else if (status.equals("makeVgPage")) {
		 * 
		 * ArrayNode allHdisk = om.createArrayNode(); for (int i = 0; i <
		 * listDetial.size(); i++) { ArrayNode hdisks = om.createArrayNode();
		 * String instname = listDetial.get(i).getName(); String instaddr =
		 * listDetial.get(i).getIP(); // 获取选中实例的HDiskInfo String cmd_cfgmgr =
		 * amsprop.getProperty(AMS2PropertyKeyConst.cmd_cfgmgr); List<String>
		 * info = amsRestService.getCmdInfoByAddr(instname, instaddr,
		 * cmd_cfgmgr);
		 * 
		 * for (String line : info) {
		 * 
		 * ObjectNode hd = om.createObjectNode(); hd.put("hdiskname", line); //
		 * hd.put("hdiskid", cells[1]); hdisks.add(hd); // } } String hpn =
		 * request.getParameter("ha_primaryNode"); String hi1 =
		 * request.getParameter("ha_ip1"); String hi2 =
		 * request.getParameter("ha_ip2"); String hh1 =
		 * request.getParameter("ha_hostname1"); String hh2 =
		 * request.getParameter("ha_hostname2"); if (hpn != null && hi1 != null
		 * && hh1 != null && hh1.equals(hpn)) { if (instaddr != null &&
		 * instaddr.equals(hi1)) { allHdisk.add(hdisks); } } if (hpn != null &&
		 * hi2 != null && hh2 != null && hh2.equals(hpn)) { if (instaddr != null
		 * && instaddr.equals(hi2)) { allHdisk.add(hdisks); } } }
		 * request.setAttribute("allHdisk", allHdisk); return
		 * "instance_aix_db2ha_makevg";
		 * 
		 * } else if (status.equals("db2haConfirm")) { String haname =
		 * request.getParameter("haname");
		 * 
		 * String[] hostName = getStrArr(request.getParameter("hostName"));
		 * String[] serName = getStrArr(request.getParameter("serName"));
		 * System.out.println(hostName);
		 * 
		 * String[] hdiskname = getStrArr(request.getParameter("hdiskname"));
		 * String[] vgtype = getStrArr(request.getParameter("vgtype"));
		 * 
		 * String caappv = getArrayNumByName(vgtype, hdiskname,
		 * "caavg-private"); String datavg2 = getArrayNumByName(vgtype,
		 * hdiskname, "datavg2"); String datavg1 = getArrayNumByName(vgtype,
		 * hdiskname, "datavg1");
		 * 
		 * String hasetup = getHaSetup(haname, hostName, serName[0], caappv);
		 * request.setAttribute("hasetup", hasetup); String mkvg =
		 * getMkvg(hostName[0], datavg1, datavg2); request.setAttribute("mkvg",
		 * mkvg); String importvg = getImportvg(hostName[1], datavg1, datavg2);
		 * request.setAttribute("importvg", importvg); return
		 * "instance_aix_db2ha_confirm"; } return null; }
		 */

	/*
	 * private String[] getStrArr(String string) { string = string.substring(1,
	 * string.length() - 1); return string.split(","); }
	 */

	/*
	 * private String getHaSetup(String haname, String[] hostName, String
	 * serName, String caappv) { String ha_setup_info =
	 * amsprop.getProperty("ha_setup_info"); // 字符串拼接 String lineSep = "\n";
	 * String strInfo = "CLUSTER;;" + haname + lineSep + "NODE;;" + hostName[0]
	 * + "," + hostName[1] + "" + lineSep +
	 * "ENET;;net_ether_01;;255.255.255.0;;yes" + lineSep + "REPDEV;;" + caappv
	 * + "" + lineSep + "HAMODE;;AA" + lineSep + "RGNUM;;1" + lineSep +
	 * "APSERV1;;ap01;;/script/db2start.sh;;/script/db2stop.sh" + lineSep +
	 * "SERVIP1;;aix7svc;;net_ether_01;;255.255.255.0" + lineSep +
	 * "PERIP1;;perip1;;net_ether_01;;255.255.255.0" + lineSep +
	 * "RGROUP1;;rg_hln01;;" + hostName[0] + "," + hostName[1] + ";;normal;;" +
	 * serName + ";;ap01;;datavg1,datavg2;;fsck;;sequential;;true"; String str =
	 * ha_setup_info + lineSep + lineSep + strInfo; return str.replace("\n",
	 * "<br />"); }
	 */

	/*
	 * private String getMkvg(String hostName, String datavg1, String datavg2) {
	 * String mkvg_info = amsprop.getProperty("mkvg_info"); String th =
	 * amsprop.getProperty("mkvg_info_table_th");
	 * 
	 * String lineSep = "\n"; String t = "\t\t\t\t\t"; String td = "" + hostName
	 * + t + "datavg1" + t + "64" + t + "301" + t + "1" + t + "S" + t + "y" + t
	 * + datavg1 + lineSep + hostName + "\t" + "datavg2" + t + "64" + t + "302"
	 * + t + "1" + t + "S" + t + "y" + t + datavg2 + lineSep;
	 * 
	 * String str = mkvg_info + lineSep + lineSep + th + td; return
	 * str.replace("\n", "<br />"); }
	 */

	/*
	 * private String getImportvg(String hostName, String datavg1, String
	 * datavg2) { String importvg_info = amsprop.getProperty("importvg_info");
	 * String th = amsprop.getProperty("importvg_info_table_th");
	 * 
	 * String lineSep = "\n"; String t = "\t\t\t\t\t"; String td = "" + hostName
	 * + t + "datavg1\t\t\t\t\t302" + datavg1 + lineSep + hostName + t +
	 * "datavg2\t\t\t\t\t" + datavg2 + lineSep; String str = importvg_info +
	 * lineSep + lineSep + th + td; return str.replace("\n", "<br />"); }
	 */

	/*
	 * private String getArrayNumByName(String[] vgtype, String[] hdiskname,
	 * String name) { String restr = ""; for (int i = 0; i < vgtype.length / 2;
	 * i++) { if (vgtype[i].trim().equals(name)) { restr = restr +
	 * hdiskname[i].trim() + ","; } } return restr.substring(0, restr.length() -
	 * 1); }
	 */

	/*	*//**
			 * db2 单节点设计
			 * 
			 * @throws OPSTBaseException
			 * 
			 */
	/*
	 * @SuppressWarnings({ "rawtypes", "unchecked" })
	 * 
	 * @RequestMapping("/installDb2StandAloneInfo") public String
	 * installDb2StandAloneInfo(HttpServletRequest request, HttpSession session)
	 * throws OPSTBaseException { ObjectMapper om = new ObjectMapper(); String
	 * retstr = setDb2ConfigMsg(request); ArrayNode hosts =
	 * om.createArrayNode(); String hostname =
	 * request.getParameter("hostname");// 主机名 String ip =
	 * request.getParameter("ip");// 主机IP String hostId =
	 * request.getParameter("hostId");// hostId String ptype =
	 * request.getParameter("ptype"); ObjectNode host =
	 * createDB2HostObjNode(hostname, ip, String.valueOf(1)); hosts.add(host);
	 * ArrayNode pvcclusternodes = om.createArrayNode(); if (ip != null &&
	 * (!ip.equals(""))) { // 如果ip不空 String pvcnodeId =
	 * createPvcNodeObjNode(hostname, ip, hostId, "DB2", String.valueOf(1));
	 * ObjectNode clusterNode = createPvcClusterNode(hostname, ip, hostId,
	 * pvcnodeId, String.valueOf(1)); pvcclusternodes.add(clusterNode); }
	 * 
	 * String type = request.getParameter("type");
	 * System.out.println("=========type(yes:立即创建,no:手动创建)=====:[" + type +
	 * "]"); String nfsON = request.getParameter("nfsON"); System.out.println(
	 * "=========nfsON 是否挂载NFS文件系统(yes:是,no:否)=====:[" + nfsON + "]");
	 * ObjectNode dbnode = amsRestService.postDB2StandAloneRun(hosts, null,
	 * retstr, type, nfsON);
	 * 
	 * if (dbnode != null && dbnode.get("uuid") != null) {
	 * 
	 * ObjectNode optionsInfo = om.createObjectNode();
	 * setDb2OptionsInfo(request, optionsInfo);
	 * createPvcClustersObjNode(dbnode.get("uuid").asText(), pvcclusternodes,
	 * optionsInfo, ptype); }
	 * 
	 * if ("yes".equals(type)) { return "redirect:/getLogInfo"; } else { String
	 * serId = request.getParameter("serId");
	 * 
	 * List serIds = new ArrayList(); if (serId != null && serId != "") {
	 * String[] ss = serId.split(","); for (int i = 0; i < ss.length; i++) {
	 * serIds.add(ss[i]); } } ArrayNode lists = amsRestService.getList(null,
	 * null, "odata/servers"); System.out.println(lists);
	 * 
	 * List<AddHostBean> lahb = new ArrayList<AddHostBean>(); for (JsonNode js :
	 * lists) { AddHostBean ahb = new AddHostBean();
	 * ahb.setIP(js.get("IP").asText() != null ? js.get("IP").asText() : "");
	 * ahb.setName(js.get("Name").asText() != null ? js.get("Name").asText() :
	 * ""); ahb.setOS(js.get("OS").asText() != null ? js.get("OS").asText() :
	 * ""); ahb.setPassword(js.get("Password").asText() != null ?
	 * js.get("Password").asText() : "");
	 * ahb.setUserID(js.get("UserID").asText() != null ?
	 * js.get("UserID").asText() : ""); ahb.setStatus(js.get("Status").asText()
	 * != null ? js.get("Status").asText() : "");
	 * ahb.set_id(js.get("_id").asText() != null ? js.get("_id").asText() : "");
	 * ahb.setHConf(js.get("HConf").asText() != null ? js.get("HConf").asText()
	 * : ""); ahb.setHVisor(js.get("HVisor").asText() != null ?
	 * js.get("HVisor").asText() : ""); lahb.add(ahb); } Collections.sort(lahb);
	 * System.out.println(lahb);
	 * 
	 * request.setAttribute("serId", serId); request.setAttribute("total",
	 * lahb.size()); List<AddHostBean> listDetial = new
	 * ArrayList<AddHostBean>();
	 * 
	 * for (int i = 0; i < lahb.size(); i++) { String serverId =
	 * lahb.get(i).get_id(); for (int j = 0; j < serIds.size(); j++) { if
	 * (serverId.equals(serIds.get(j))) { listDetial.add(lahb.get(i)); } } }
	 * 
	 * request.setAttribute("servers", listDetial); request.setAttribute("ip",
	 * request.getParameter("ip")); request.setAttribute("bootip",
	 * request.getParameter("bootip")); request.setAttribute("hostname",
	 * request.getParameter("hostname")); return
	 * "instance_aix_db2_uninstall_script"; } }
	 */
	/*	*//**
			 * 
			 * @Title: installDb2haInfo
			 * @Description: db2ha安装确认
			 * @param @param
			 *            request
			 * @param @param
			 *            session
			 * @param @return
			 * @param @throws
			 *            Exception
			 * @return String
			 * @author liwj
			 * @throws @Time
			 *             2015年7月3日 下午3:06:40
			 *//*
			 * @SuppressWarnings({ "rawtypes", "unchecked" })
			 * 
			 * @RequestMapping("/installDb2haInfo") public String
			 * installDb2haInfo(HttpServletRequest request, HttpSession session)
			 * throws Exception {
			 * 
			 * ObjectMapper om = new ObjectMapper(); // HOST
			 * 
			 * String haname = request.getParameter("haname"); String[] hostName
			 * = getStrArr(request.getParameter("hostName")); String[] hostIp =
			 * getStrArr(request.getParameter("hostIp")); String[] serName =
			 * getStrArr(request.getParameter("serName")); String[] serIp =
			 * getStrArr(request.getParameter("serIp")); String[] perName =
			 * getStrArr(request.getParameter("perName")); String[] perIp =
			 * getStrArr(request.getParameter("perIp"));
			 * 
			 * 
			 * // 获取所有参数 String retstr = setConfigMsg(request); String ptype =
			 * request.getParameter("ptype"); ArrayNode hosts =
			 * om.createArrayNode(); ArrayNode pvcclusternodes =
			 * om.createArrayNode();
			 * 
			 * String ha_clusterName = request.getParameter("ha_clusterName");
			 * String ha_hostname1 = request.getParameter("ha_hostname1");
			 * String ha_hostname2 = request.getParameter("ha_hostname2");
			 * String ha_ip1 = request.getParameter("ha_ip1"); String ha_ip2 =
			 * request.getParameter("ha_ip2"); String ha_bootip1 =
			 * request.getParameter("ha_bootip1"); String ha_bootip2 =
			 * request.getParameter("ha_bootip2"); String ha_bootalias1 =
			 * request.getParameter("ha_bootalias1"); String ha_bootalias2 =
			 * request.getParameter("ha_bootalias2"); String ha_svcip =
			 * request.getParameter("ha_svcip"); String ha_svcalias =
			 * request.getParameter("ha_svcalias"); String hostId1 =
			 * request.getParameter("hostId1"); String hostId2 =
			 * request.getParameter("hostId2"); String ha_primaryNode =
			 * request.getParameter("ha_primaryNode");
			 * 
			 * if (ha_primaryNode != null && ha_hostname1 != null &&
			 * ha_primaryNode.equals(ha_hostname1)) { ObjectNode host =
			 * createDB2HostObjNode(ha_hostname1, ha_ip1, String.valueOf(1)); //
			 * {"host":"wangzeyao1","addr":"10.58.0.120","role":"ha1"}
			 * hosts.add(host); String pvcnodeId =
			 * createPvcNodeObjNode(ha_hostname1, ha_ip1, hostId1, "DB2",
			 * String.valueOf(1)); // ObjectNode clusterNode =
			 * createPvcClusterNode(String.valueOf(1), // ha_hostname1, ha_ip1,
			 * ha_ip1, pvcnodeId); ObjectNode clusterNode =
			 * createPvcClusterNode(ha_hostname1, ha_ip1, hostId1, pvcnodeId,
			 * String.valueOf(1)); pvcclusternodes.add(clusterNode);
			 * 
			 * host = createDB2HostObjNode(ha_hostname2, ha_ip2,
			 * String.valueOf(1)); hosts.add(host); pvcnodeId =
			 * createPvcNodeObjNode(ha_hostname2, ha_ip2, hostId2, "DB2",
			 * String.valueOf(2)); // clusterNode =
			 * createPvcClusterNode(String.valueOf(2), // ha_hostname2, ha_ip2,
			 * ha_ip2, pvcnodeId); clusterNode =
			 * createPvcClusterNode(ha_hostname2, ha_ip2, hostId2, pvcnodeId,
			 * String.valueOf(2)); pvcclusternodes.add(clusterNode);
			 * request.setAttribute("ha_subNode", ha_hostname2); } else {
			 * ObjectNode host = createDB2HostObjNode(ha_hostname2, ha_ip2,
			 * String.valueOf(1)); hosts.add(host); String pvcnodeId =
			 * createPvcNodeObjNode(ha_hostname2, ha_ip2, hostId2, "DB2",
			 * String.valueOf(1)); // ObjectNode clusterNode =
			 * createPvcClusterNode(String.valueOf(2), // ha_hostname2, ha_ip2,
			 * ha_ip2, pvcnodeId); ObjectNode clusterNode =
			 * createPvcClusterNode(ha_hostname2, ha_ip2, hostId2, pvcnodeId,
			 * String.valueOf(1)); pvcclusternodes.add(clusterNode);
			 * 
			 * host = createDB2HostObjNode(ha_hostname1, ha_ip1,
			 * String.valueOf(1)); hosts.add(host); pvcnodeId =
			 * createPvcNodeObjNode(ha_hostname1, ha_ip1, hostId1, "DB2",
			 * String.valueOf(2)); // clusterNode =
			 * createPvcClusterNode(String.valueOf(1), // ha_hostname1, ha_ip1,
			 * ha_ip1, pvcnodeId); clusterNode =
			 * createPvcClusterNode(ha_hostname1, ha_ip1, hostId1, pvcnodeId,
			 * String.valueOf(2)); pvcclusternodes.add(clusterNode);
			 * request.setAttribute("ha_subNode", ha_hostname1); }
			 * 
			 * String type = request.getParameter("type");
			 * System.out.println("=========type(yes:立即创建,no:手动创建)=====:[" +
			 * type + "]"); String nfsON = request.getParameter("ha_nfsON");
			 * System.out.println(
			 * "=========ha_nfsON 是否挂载NFS文件系统(yes:是,no:否)=====:[" + nfsON +
			 * "]"); ObjectNode dbnode = amsRestService.postDB2Run(hosts, null,
			 * retstr, type, nfsON);
			 * 
			 * if (dbnode != null && dbnode.get("uuid") != null) {
			 * 
			 * ObjectNode optionsInfo = om.createObjectNode();
			 * setOptionsInfo(request, optionsInfo); // 创建pvcclusters
			 * createPvcClustersObjNode(dbnode.get("uuid").asText(),
			 * pvcclusternodes, optionsInfo, ptype); }
			 * 
			 * if ("yes".equals(type)) {
			 * 
			 * return "redirect:/getLogInfo"; } else {
			 * 
			 * // 获取ID String serId = request.getParameter("serId");
			 * 
			 * List serIds = new ArrayList(); if (serId != null && serId != "")
			 * { String[] ss = serId.split(","); for (int i = 0; i < ss.length;
			 * i++) { serIds.add(ss[i]); } }
			 * 
			 * ArrayNode lists = amsRestService.getList(null, null,
			 * "odata/servers"); System.out.println(lists);
			 * 
			 * List<AddHostBean> lahb = new ArrayList<AddHostBean>(); for
			 * (JsonNode js : lists) { AddHostBean ahb = new AddHostBean();
			 * ahb.setIP(js.get("IP").asText() != null ? js.get("IP").asText() :
			 * ""); ahb.setName(js.get("Name").asText() != null ?
			 * js.get("Name").asText() : ""); ahb.setOS(js.get("OS").asText() !=
			 * null ? js.get("OS").asText() : "");
			 * ahb.setPassword(js.get("Password").asText() != null ?
			 * js.get("Password").asText() : "");
			 * ahb.setUserID(js.get("UserID").asText() != null ?
			 * js.get("UserID").asText() : "");
			 * ahb.setStatus(js.get("Status").asText() != null ?
			 * js.get("Status").asText() : "");
			 * ahb.set_id(js.get("_id").asText() != null ?
			 * js.get("_id").asText() : "");
			 * ahb.setHConf(js.get("HConf").asText() != null ?
			 * js.get("HConf").asText() : "");
			 * ahb.setHVisor(js.get("HVisor").asText() != null ?
			 * js.get("HVisor").asText() : ""); lahb.add(ahb); }
			 * Collections.sort(lahb); System.out.println(lahb);
			 * 
			 * request.setAttribute("serId", serId);
			 * request.setAttribute("total", lahb.size()); List<AddHostBean>
			 * listDetial = new ArrayList<AddHostBean>();
			 * 
			 * for (int i = 0; i < lahb.size(); i++) { String serverId =
			 * lahb.get(i).get_id(); for (int j = 0; j < serIds.size(); j++) {
			 * if (serverId.equals(serIds.get(j))) {
			 * listDetial.add(lahb.get(i)); } } }
			 * 
			 * request.setAttribute("serId", serId);
			 * request.setAttribute("servers", listDetial);
			 * 
			 * // 基本 request.setAttribute("ha_RGNmae",
			 * request.getParameter("ha_RGNmae"));
			 * request.setAttribute("ha_ASName",
			 * request.getParameter("ha_ASName"));
			 * request.setAttribute("ha_primaryNode",
			 * request.getParameter("ha_primaryNode"));
			 * 
			 * // IP
			 * 
			 * request.setAttribute("ha_ip1", request.getParameter("ha_ip1"));
			 * request.setAttribute("ha_ip2", request.getParameter("ha_ip2"));
			 * request.setAttribute("ha_svcip",
			 * request.getParameter("ha_svcip"));
			 * 
			 * // 主机别名 request.setAttribute("ha_hostname1",
			 * request.getParameter("ha_hostname1"));
			 * request.setAttribute("ha_hostname2",
			 * request.getParameter("ha_hostname2"));
			 * 
			 * // HA名称(集群名) request.setAttribute("ha_clusterName",
			 * request.getParameter("ha_clusterName"));
			 * 
			 * return "instance_aix_db2ha_uninstall_script"; }
			 * 
			 * }
			 */

	/**
	 * 设置was cluster 的属性
	 *//*
		 * private void setWasClusterOptionsInfo(HttpServletRequest request,
		 * ObjectNode optionsInfo) { optionsInfo.put("was_hostname",
		 * request.getParameter("allhostname")); optionsInfo.put("was_ip",
		 * request.getParameter("allip")); optionsInfo.put("was_profile_type",
		 * request.getParameter("allprofiletype"));
		 * optionsInfo.put("was_profile_name",
		 * request.getParameter("allprofilename"));
		 * optionsInfo.put("was_version", request.getParameter("was_version"));
		 * optionsInfo.put("was_fp", request.getParameter("was_fp"));
		 * optionsInfo.put("was_user", request.getParameter("was_user"));
		 * optionsInfo.put("was_im_path", request.getParameter("was_im_path"));
		 * optionsInfo.put("was_inst_path",
		 * request.getParameter("was_inst_path")); optionsInfo.put("was_jdk7",
		 * request.getParameter("was_jdk7"));
		 * 
		 * optionsInfo.put("was_profile_path",
		 * request.getParameter("was_profile_path"));
		 * 
		 * optionsInfo.put("was_security",
		 * request.getParameter("was_security")); optionsInfo.put("was_userid",
		 * request.getParameter("was_userid")); optionsInfo.put("was_password",
		 * request.getParameter("was_password"));
		 * optionsInfo.put("was_nofile_soft",
		 * request.getParameter("was_nofile_soft"));
		 * optionsInfo.put("was_nofile_hard",
		 * request.getParameter("was_nofile_hard"));
		 * optionsInfo.put("was_fsize_soft",
		 * request.getParameter("was_fsize_soft"));
		 * optionsInfo.put("was_fsize_hard",
		 * request.getParameter("was_fsize_hard"));
		 * optionsInfo.put("was_core_soft",
		 * request.getParameter("was_core_soft"));
		 * optionsInfo.put("was_core_hard",
		 * request.getParameter("was_core_hard")); }
		 */

	/*	*//**
			 * 设置db2 单节点的参数
			 */
	/*
	 * private void setDb2OptionsInfo(HttpServletRequest request, ObjectNode
	 * optionsInfo) { // 基本 optionsInfo.put("hostname",
	 * request.getParameter("hostname")); // 单节点IP optionsInfo.put("ip",
	 * request.getParameter("ip")); optionsInfo.put("bootip",
	 * request.getParameter("bootip")); optionsInfo.put("hostId",
	 * request.getParameter("hostId")); // VG optionsInfo.put("vgdb2home",
	 * request.getParameter("vgdb2home")); optionsInfo.put("vgdb2log",
	 * request.getParameter("vgdb2log")); optionsInfo.put("vgdb2archlog",
	 * request.getParameter("vgdb2archlog")); optionsInfo.put("vgdataspace",
	 * request.getParameter("vgdataspace"));
	 * 
	 * // PV optionsInfo.put("db2homepv", request.getParameter("db2homepv"));
	 * optionsInfo.put("db2logpv", request.getParameter("db2logpv"));
	 * optionsInfo.put("db2archlogpv", request.getParameter("db2archlogpv"));
	 * optionsInfo.put("dataspacepv", request.getParameter("dataspacepv")); //
	 * VG创建方式 optionsInfo.put("db2homemode",
	 * request.getParameter("db2homemode")); optionsInfo.put("db2logmode",
	 * request.getParameter("db2logmode")); optionsInfo.put("db2archlogmode",
	 * request.getParameter("db2archlogmode")); optionsInfo.put("dataspacemode",
	 * request.getParameter("dataspacemode")); // NFS BEGIN -->
	 * optionsInfo.put("nfsON", request.getParameter("nfsON"));
	 * 
	 * optionsInfo.put("nfsIP1", request.getParameter("nfsIP1"));
	 * optionsInfo.put("nfsSPoint1", request.getParameter("nfsSPoint1"));
	 * optionsInfo.put("nfsCPoint1", request.getParameter("nfsCPoint1"));
	 * 
	 * optionsInfo.put("nfsIP2", request.getParameter("nfsIP2"));
	 * optionsInfo.put("nfsSPoint2", request.getParameter("nfsSPoint2"));
	 * optionsInfo.put("nfsCPoint2", request.getParameter("nfsCPoint2"));
	 * 
	 * optionsInfo.put("nfsIP3", request.getParameter("nfsIP3"));
	 * optionsInfo.put("nfsSPoint3", request.getParameter("nfsSPoint3"));
	 * optionsInfo.put("nfsCPoint3", request.getParameter("nfsCPoint3"));
	 * 
	 * optionsInfo.put("nfsIP4", request.getParameter("nfsIP4"));
	 * optionsInfo.put("nfsSPoint4", request.getParameter("nfsSPoint4"));
	 * optionsInfo.put("nfsCPoint4", request.getParameter("nfsCPoint4"));
	 * 
	 * optionsInfo.put("nfsIP5", request.getParameter("nfsIP5"));
	 * optionsInfo.put("nfsSPoint5", request.getParameter("nfsSPoint5"));
	 * optionsInfo.put("nfsCPoint5", request.getParameter("nfsCPoint5"));
	 * 
	 * optionsInfo.put("nfsIP6", request.getParameter("nfsIP6"));
	 * optionsInfo.put("nfsSPoint6", request.getParameter("nfsSPoint6"));
	 * optionsInfo.put("nfsCPoint6", request.getParameter("nfsCPoint6"));
	 * 
	 * optionsInfo.put("nfsIP7", request.getParameter("nfsIP7"));
	 * optionsInfo.put("nfsSPoint7", request.getParameter("nfsSPoint7"));
	 * optionsInfo.put("nfsCPoint7", request.getParameter("nfsCPoint7"));
	 * 
	 * optionsInfo.put("nfsIP8", request.getParameter("nfsIP8"));
	 * optionsInfo.put("nfsSPoint8", request.getParameter("nfsSPoint8"));
	 * optionsInfo.put("nfsCPoint8", request.getParameter("nfsCPoint8"));
	 * 
	 * optionsInfo.put("nfsIP9", request.getParameter("nfsIP9"));
	 * optionsInfo.put("nfsSPoint9", request.getParameter("nfsSPoint9"));
	 * optionsInfo.put("nfsCPoint9", request.getParameter("nfsCPoint9"));
	 * 
	 * optionsInfo.put("nfsIP10", request.getParameter("nfsIP10"));
	 * optionsInfo.put("nfsSPoint10", request.getParameter("nfsSPoint10"));
	 * optionsInfo.put("nfsCPoint10", request.getParameter("nfsCPoint10"));
	 * 
	 * optionsInfo.put("nfsIP11", request.getParameter("nfsIP11"));
	 * optionsInfo.put("nfsSPoint11", request.getParameter("nfsSPoint11"));
	 * optionsInfo.put("nfsCPoint11", request.getParameter("nfsCPoint11"));
	 * 
	 * optionsInfo.put("nfsIP12", request.getParameter("nfsIP12"));
	 * optionsInfo.put("nfsSPoint12", request.getParameter("nfsSPoint12"));
	 * optionsInfo.put("nfsCPoint12", request.getParameter("nfsCPoint12"));
	 * 
	 * optionsInfo.put("nfsIP13", request.getParameter("nfsIP13"));
	 * optionsInfo.put("nfsSPoint13", request.getParameter("nfsSPoint13"));
	 * optionsInfo.put("nfsCPoint13", request.getParameter("nfsCPoint13"));
	 * 
	 * optionsInfo.put("nfsIP14", request.getParameter("nfsIP14"));
	 * optionsInfo.put("nfsSPoint14", request.getParameter("nfsSPoint14"));
	 * optionsInfo.put("nfsCPoint14", request.getParameter("nfsCPoint14"));
	 * 
	 * optionsInfo.put("nfsIP15", request.getParameter("nfsIP15"));
	 * optionsInfo.put("nfsSPoint15", request.getParameter("nfsSPoint15"));
	 * optionsInfo.put("nfsCPoint15", request.getParameter("nfsCPoint15"));
	 * 
	 * // DB2信息 基本信息 optionsInfo.put("db2_version",
	 * request.getParameter("db2_version")); optionsInfo.put("db2_db2base",
	 * request.getParameter("db2_db2base")); optionsInfo.put("db2_dbpath",
	 * request.getParameter("db2_dbpath")); optionsInfo.put("db2_db2insusr",
	 * request.getParameter("db2_db2insusr")); optionsInfo.put("db2_svcename",
	 * request.getParameter("db2_svcename")); optionsInfo.put("db2_dbname",
	 * request.getParameter("db2_dbname")); optionsInfo.put("db2_codeset",
	 * request.getParameter("db2_codeset")); optionsInfo.put("db2_dbdatapath",
	 * request.getParameter("db2_dbdatapath"));
	 * 
	 * // DB2信息 实例高级属性 optionsInfo.put("db2_db2insgrp",
	 * request.getParameter("db2_db2insgrp")); optionsInfo.put("db2_db2fenusr",
	 * request.getParameter("db2_db2fenusr")); optionsInfo.put("db2_db2fengrp",
	 * request.getParameter("db2_db2fengrp")); optionsInfo.put("db2_db2comm",
	 * request.getParameter("db2_db2comm")); optionsInfo.put("db2_db2codepage",
	 * request.getParameter("db2_db2codepage"));
	 * optionsInfo.put("db2_initagents",
	 * request.getParameter("db2_initagents"));
	 * optionsInfo.put("db2_poolagents",
	 * request.getParameter("db2_poolagents"));
	 * optionsInfo.put("db2_max_coordagents",
	 * request.getParameter("db2_max_coordagents"));
	 * optionsInfo.put("db2_max_connectings",
	 * request.getParameter("db2_max_connectings"));
	 * optionsInfo.put("db2_diagsize", request.getParameter("db2_diagsize"));
	 * optionsInfo.put("db2_mon_buf", request.getParameter("db2_mon_buf"));
	 * optionsInfo.put("db2_mon_lock", request.getParameter("db2_mon_lock"));
	 * optionsInfo.put("db2_mon_sort", request.getParameter("db2_mon_sort"));
	 * optionsInfo.put("db2_mon_stmt", request.getParameter("db2_mon_stmt"));
	 * optionsInfo.put("db2_mon_table", request.getParameter("db2_mon_table"));
	 * optionsInfo.put("db2_mon_uow", request.getParameter("db2_mon_uow"));
	 * optionsInfo.put("db2_health_mon",
	 * request.getParameter("db2_health_mon")); optionsInfo.put("db2_mon_heap",
	 * request.getParameter("db2_mon_heap")); // DB2信息 实例高级属性
	 * optionsInfo.put("db2_db2log", request.getParameter("db2_db2log"));
	 * optionsInfo.put("db2_logarchpath",
	 * request.getParameter("db2_logarchpath"));
	 * optionsInfo.put("db2_backuppath",
	 * request.getParameter("db2_backuppath")); optionsInfo.put("db2_stmm",
	 * request.getParameter("db2_stmm")); optionsInfo.put("db2_locklist",
	 * request.getParameter("db2_locklist")); optionsInfo.put("db2_maxlocks",
	 * request.getParameter("db2_maxlocks")); optionsInfo.put("db2_locktimeout",
	 * request.getParameter("db2_locktimeout")); optionsInfo.put("db2_sortheap",
	 * request.getParameter("db2_sortheap")); optionsInfo.put("db2_logfilesize",
	 * request.getParameter("db2_logfilesize"));
	 * optionsInfo.put("db2_logprimary",
	 * request.getParameter("db2_logprimary")); optionsInfo.put("db2_logsecond",
	 * request.getParameter("db2_logsecond")); optionsInfo.put("db2_logbuff",
	 * request.getParameter("db2_logbuff")); optionsInfo.put("db2_softmax",
	 * request.getParameter("db2_softmax")); optionsInfo.put("db2_trackmod",
	 * request.getParameter("db2_trackmod")); optionsInfo.put("db2_pagesize",
	 * request.getParameter("db2_pagesize"));
	 * 
	 * }
	 */
	/*	*//**
			 * 构造cluster的options属性
			 * 
			 * @param request
			 * @param optionsInfo
			 *//*
			 * private void setOptionsInfo(HttpServletRequest request,
			 * ObjectNode optionsInfo) {
			 * 
			 * // -------基本------ // ha_clusterName
			 * optionsInfo.put("ha_clusterName",
			 * request.getParameter("ha_clusterName")); // ha_RGNmae
			 * optionsInfo.put("ha_RGNmae", request.getParameter("ha_RGNmae"));
			 * // ha_ASName optionsInfo.put("ha_ASName",
			 * request.getParameter("ha_ASName")); // ha_primaryNode
			 * optionsInfo.put("ha_primaryNode",
			 * request.getParameter("ha_primaryNode")); // --------IP----- //
			 * ha_ip1 optionsInfo.put("ha_ip1", request.getParameter("ha_ip1"));
			 * // ha_ip2 optionsInfo.put("ha_ip2",
			 * request.getParameter("ha_ip2")); // ha_bootip1
			 * optionsInfo.put("ha_bootip1",
			 * request.getParameter("ha_bootip1")); // ha_bootip2
			 * optionsInfo.put("ha_bootip2",
			 * request.getParameter("ha_bootip2")); // ha_svcip
			 * optionsInfo.put("ha_svcip", request.getParameter("ha_svcip"));
			 * 
			 * // --------主机别名----- // ha_hostname1
			 * optionsInfo.put("ha_hostname1",
			 * request.getParameter("ha_hostname1")); // ha_hostname2
			 * optionsInfo.put("ha_hostname2",
			 * request.getParameter("ha_hostname2")); // ha_bootalias1
			 * optionsInfo.put("ha_bootalias1",
			 * request.getParameter("ha_bootalias1")); // ha_bootalias2
			 * optionsInfo.put("ha_bootalias2",
			 * request.getParameter("ha_bootalias2")); // ha_svcalias
			 * optionsInfo.put("ha_svcalias",
			 * request.getParameter("ha_svcalias"));
			 * 
			 * // ---------VG------ // ha_vgdb2home
			 * optionsInfo.put("ha_vgdb2home",
			 * request.getParameter("ha_vgdb2home")); // ha_vgdb2log
			 * optionsInfo.put("ha_vgdb2log",
			 * request.getParameter("ha_vgdb2log")); // ha_vgdb2archlog
			 * optionsInfo.put("ha_vgdb2archlog",
			 * request.getParameter("ha_vgdb2archlog")); // ha_vgdataspace1
			 * optionsInfo.put("ha_vgdataspace",
			 * request.getParameter("ha_vgdataspace")); // ha_vgcaap
			 * optionsInfo.put("ha_vgcaap", request.getParameter("ha_vgcaap"));
			 * 
			 * // -------PV----- // ha_db2homepv optionsInfo.put("ha_db2homepv",
			 * request.getParameter("ha_db2homepv")); // ha_db2logpv
			 * optionsInfo.put("ha_db2logpv",
			 * request.getParameter("ha_db2logpv")); // ha_db2archlogpv
			 * optionsInfo.put("ha_db2archlogpv",
			 * request.getParameter("ha_db2archlogpv")); // ha_dataspace1pv
			 * optionsInfo.put("ha_dataspacepv",
			 * request.getParameter("ha_dataspace1pv")); // ha_caappv
			 * optionsInfo.put("ha_caappv", request.getParameter("ha_caappv"));
			 * 
			 * // ------VG创建方式----- // ha_db2homemode
			 * optionsInfo.put("ha_db2homemode",
			 * request.getParameter("ha_db2homemode")); // ha_db2logmode
			 * optionsInfo.put("ha_db2logmode",
			 * request.getParameter("ha_db2logmode")); // ha_db2archlogmode
			 * optionsInfo.put("ha_db2archlogmode",
			 * request.getParameter("ha_db2archlogmode")); // ha_dataspace1mode
			 * optionsInfo.put("ha_dataspacemode",
			 * request.getParameter("ha_dataspacemode")); // ha_caapmode
			 * optionsInfo.put("ha_caapmode",
			 * request.getParameter("ha_caapmode"));
			 * 
			 * // ------HA切换配置----- // ha_startpolicy
			 * optionsInfo.put("ha_startpolicy",
			 * request.getParameter("ha_startpolicy")); // ha_fopolicy
			 * optionsInfo.put("ha_fopolicy",
			 * request.getParameter("ha_fopolicy")); // ha_fbpolicy
			 * optionsInfo.put("ha_fbpolicy",
			 * request.getParameter("ha_fbpolicy"));
			 * 
			 * // --------NFS--------- // ha_nfsON optionsInfo.put("ha_nfsON",
			 * request.getParameter("ha_nfsON"));
			 * 
			 * // ha_nfsIP1 optionsInfo.put("ha_nfsIP1",
			 * request.getParameter("ha_nfsIP1")); // ha_nfsSPoint1
			 * optionsInfo.put("ha_nfsSPoint1",
			 * request.getParameter("ha_nfsSPoint1")); // ha_nfsCPoint1
			 * optionsInfo.put("ha_nfsCPoint1",
			 * request.getParameter("ha_nfsCPoint1")); // ha_nfsIP2
			 * optionsInfo.put("ha_nfsIP2", request.getParameter("ha_nfsIP2"));
			 * // ha_nfsSPoint2 optionsInfo.put("ha_nfsSPoint2",
			 * request.getParameter("ha_nfsSPoint2")); // ha_nfsCPoint2
			 * optionsInfo.put("ha_nfsCPoint2",
			 * request.getParameter("ha_nfsCPoint2")); // ha_nfsIP3
			 * optionsInfo.put("ha_nfsIP3", request.getParameter("ha_nfsIP3"));
			 * // ha_nfsSPoint3 optionsInfo.put("ha_nfsSPoint3",
			 * request.getParameter("ha_nfsSPoint3")); // ha_nfsCPoint3
			 * optionsInfo.put("ha_nfsCPoint3",
			 * request.getParameter("ha_nfsCPoint3")); // ha_nfsIP4
			 * optionsInfo.put("ha_nfsIP4", request.getParameter("ha_nfsIP4"));
			 * // ha_nfsSPoint4 optionsInfo.put("ha_nfsSPoint4",
			 * request.getParameter("ha_nfsSPoint4")); // ha_nfsCPoint4
			 * optionsInfo.put("ha_nfsCPoint4",
			 * request.getParameter("ha_nfsCPoint4")); // ha_nfsIP5
			 * optionsInfo.put("ha_nfsIP5", request.getParameter("ha_nfsIP5"));
			 * // ha_nfsSPoint5 optionsInfo.put("ha_nfsSPoint5",
			 * request.getParameter("ha_nfsSPoint5")); // ha_nfsCPoint5
			 * optionsInfo.put("ha_nfsCPoint5",
			 * request.getParameter("ha_nfsCPoint5")); // ha_nfsIP6
			 * optionsInfo.put("ha_nfsIP6", request.getParameter("ha_nfsIP6"));
			 * // ha_nfsSPoint6 optionsInfo.put("ha_nfsSPoint6",
			 * request.getParameter("ha_nfsSPoint6")); // ha_nfsCPoint6
			 * optionsInfo.put("ha_nfsCPoint6",
			 * request.getParameter("ha_nfsCPoint6")); // ha_nfsIP7
			 * optionsInfo.put("ha_nfsIP7", request.getParameter("ha_nfsIP7"));
			 * // ha_nfsSPoint7 optionsInfo.put("ha_nfsSPoint7",
			 * request.getParameter("ha_nfsSPoint7")); // ha_nfsCPoint7
			 * optionsInfo.put("ha_nfsCPoint7",
			 * request.getParameter("ha_nfsCPoint7")); // ha_nfsIP8
			 * optionsInfo.put("ha_nfsIP8", request.getParameter("ha_nfsIP8"));
			 * // ha_nfsSPoint8 optionsInfo.put("ha_nfsSPoint8",
			 * request.getParameter("ha_nfsSPoint8")); // ha_nfsCPoint8
			 * optionsInfo.put("ha_nfsCPoint8",
			 * request.getParameter("ha_nfsCPoint8")); // ha_nfsIP9
			 * optionsInfo.put("ha_nfsIP9", request.getParameter("ha_nfsIP9"));
			 * // ha_nfsSPoint9 optionsInfo.put("ha_nfsSPoint9",
			 * request.getParameter("ha_nfsSPoint9")); // ha_nfsCPoint9
			 * optionsInfo.put("ha_nfsCPoint9",
			 * request.getParameter("ha_nfsCPoint9")); // ha_nfsIP10
			 * optionsInfo.put("ha_nfsIP10",
			 * request.getParameter("ha_nfsIP10")); // ha_nfsSPoint10
			 * optionsInfo.put("ha_nfsSPoint10",
			 * request.getParameter("ha_nfsSPoint10")); // ha_nfsCPoint10
			 * optionsInfo.put("ha_nfsCPoint10",
			 * request.getParameter("ha_nfsCPoint10")); // ha_nfsIP11
			 * optionsInfo.put("ha_nfsIP11",
			 * request.getParameter("ha_nfsIP11")); // ha_nfsSPoint11
			 * optionsInfo.put("ha_nfsSPoint11",
			 * request.getParameter("ha_nfsSPoint11")); // ha_nfsCPoint11
			 * optionsInfo.put("ha_nfsCPoint11",
			 * request.getParameter("ha_nfsCPoint11")); // ha_nfsIP12
			 * optionsInfo.put("ha_nfsIP12",
			 * request.getParameter("ha_nfsIP12")); // ha_nfsSPoint12
			 * optionsInfo.put("ha_nfsSPoint12",
			 * request.getParameter("ha_nfsSPoint12")); // ha_nfsCPoint12
			 * optionsInfo.put("ha_nfsCPoint12",
			 * request.getParameter("ha_nfsCPoint12")); // ha_nfsIP13
			 * optionsInfo.put("ha_nfsIP13",
			 * request.getParameter("ha_nfsIP13")); // ha_nfsSPoint13
			 * optionsInfo.put("ha_nfsSPoint13",
			 * request.getParameter("ha_nfsSPoint13")); // ha_nfsCPoint13
			 * optionsInfo.put("ha_nfsCPoint13",
			 * request.getParameter("ha_nfsCPoint13")); // ha_nfsIP14
			 * optionsInfo.put("ha_nfsIP14",
			 * request.getParameter("ha_nfsIP14")); // ha_nfsSPoint14
			 * optionsInfo.put("ha_nfsSPoint14",
			 * request.getParameter("ha_nfsSPoint14")); // ha_nfsCPoint14
			 * optionsInfo.put("ha_nfsCPoint14",
			 * request.getParameter("ha_nfsCPoint14")); // ha_nfsIP15
			 * optionsInfo.put("ha_nfsIP15",
			 * request.getParameter("ha_nfsIP15")); // ha_nfsSPoint15
			 * optionsInfo.put("ha_nfsSPoint15",
			 * request.getParameter("ha_nfsSPoint15")); // ha_nfsCPoint15
			 * optionsInfo.put("ha_nfsCPoint15",
			 * request.getParameter("ha_nfsCPoint15"));
			 * 
			 * // ------基本信息----- // db2_version optionsInfo.put("db2_version",
			 * request.getParameter("db2_version")); // db2_db2base
			 * optionsInfo.put("db2_db2base",
			 * request.getParameter("db2_db2base")); // db2_dbpath
			 * optionsInfo.put("db2_dbpath",
			 * request.getParameter("db2_dbpath")); // db2_svcename
			 * optionsInfo.put("db2_svcename",
			 * request.getParameter("db2_svcename")); // db2_db2insusr
			 * optionsInfo.put("db2_db2insusr",
			 * request.getParameter("db2_db2insusr")); // db2_dbname
			 * optionsInfo.put("db2_dbname",
			 * request.getParameter("db2_dbname")); // db2_codeset
			 * optionsInfo.put("db2_codeset",
			 * request.getParameter("db2_codeset")); // db2_dbdatapath
			 * optionsInfo.put("db2_dbdatapath",
			 * request.getParameter("db2_dbdatapath")); // ------实例高级属性-------
			 * // db2_db2insusr 基本信息已有 //
			 * "db2_db2insusr="+request.getParameter("db2_db2insusr")+lineSep +
			 * // db2_db2insgrp optionsInfo.put("db2_db2insgrp",
			 * request.getParameter("db2_db2insgrp")); // db2_db2fenusr
			 * optionsInfo.put("db2_db2fenusr",
			 * request.getParameter("db2_db2fenusr")); // db2_db2fengrp
			 * optionsInfo.put("db2_db2fengrp",
			 * request.getParameter("db2_db2fengrp")); // db2_db2comm
			 * optionsInfo.put("db2_db2comm",
			 * request.getParameter("db2_db2comm")); // db2_db2codepage
			 * optionsInfo.put("db2_db2codepage",
			 * request.getParameter("db2_db2codepage")); // db2_initagents
			 * optionsInfo.put("db2_initagents",
			 * request.getParameter("db2_initagents")); // db2_max_coordagents
			 * optionsInfo.put("db2_max_coordagents",
			 * request.getParameter("db2_max_coordagents")); //
			 * db2_max_connectings optionsInfo.put("db2_max_connectings",
			 * request.getParameter("db2_max_connectings")); // db2_poolagents
			 * optionsInfo.put("db2_poolagents",
			 * request.getParameter("db2_poolagents")); // db2_diagsize
			 * optionsInfo.put("db2_diagsize",
			 * request.getParameter("db2_diagsize")); // db2_mon_buf
			 * optionsInfo.put("db2_mon_buf",
			 * request.getParameter("db2_mon_buf")); // db2_mon_lock
			 * optionsInfo.put("db2_mon_lock",
			 * request.getParameter("db2_mon_lock")); // db2_mon_sort
			 * optionsInfo.put("db2_mon_sort",
			 * request.getParameter("db2_mon_sort")); // db2_mon_stmt
			 * optionsInfo.put("db2_mon_stmt",
			 * request.getParameter("db2_mon_stmt")); // db2_mon_table
			 * optionsInfo.put("db2_mon_table",
			 * request.getParameter("db2_mon_table")); // db2_mon_uow
			 * optionsInfo.put("db2_mon_uow",
			 * request.getParameter("db2_mon_uow")); // db2_health_mon
			 * optionsInfo.put("db2_health_mon",
			 * request.getParameter("db2_health_mon")); // db2_mon_heap
			 * optionsInfo.put("db2_mon_heap",
			 * request.getParameter("db2_mon_heap"));
			 * 
			 * // -------数据库高级属性------ // db2_db2log
			 * optionsInfo.put("db2_db2log",
			 * request.getParameter("db2_db2log")); // db2_backuppath
			 * optionsInfo.put("db2_backuppath",
			 * request.getParameter("db2_backuppath")); // db2_logarchpath
			 * optionsInfo.put("db2_logarchpath",
			 * request.getParameter("db2_logarchpath")); // db2_stmm
			 * optionsInfo.put("db2_stmm", request.getParameter("db2_stmm")); //
			 * db2_locklist optionsInfo.put("db2_locklist",
			 * request.getParameter("db2_locklist")); // db2_maxlocks
			 * optionsInfo.put("db2_maxlocks",
			 * request.getParameter("db2_maxlocks")); // db2_locktimeout
			 * optionsInfo.put("db2_locktimeout",
			 * request.getParameter("db2_locktimeout")); // db2_sortheap
			 * optionsInfo.put("db2_sortheap",
			 * request.getParameter("db2_sortheap")); // db2_logfilesize
			 * optionsInfo.put("db2_logfilesize",
			 * request.getParameter("db2_logfilesize")); // db2_logprimary
			 * optionsInfo.put("db2_logprimary",
			 * request.getParameter("db2_logprimary")); // db2_logsecond
			 * optionsInfo.put("db2_logsecond",
			 * request.getParameter("db2_logsecond")); // db2_logbuff
			 * optionsInfo.put("db2_logbuff",
			 * request.getParameter("db2_logbuff")); // db2_softmax
			 * optionsInfo.put("db2_softmax",
			 * request.getParameter("db2_softmax")); // db2_trackmod
			 * optionsInfo.put("db2_trackmod",
			 * request.getParameter("db2_trackmod")); // db2_pagesize
			 * optionsInfo.put("db2_pagesize",
			 * request.getParameter("db2_pagesize")); }
			 */
	/*
	 * private String setWasClusterConfigMsg(HttpServletRequest request) {
	 * 
	 * 
	 * String[] allips = request.getParameterValues("all_ips"); String[]
	 * allhostnames = request.getParameterValues("all_hostnames"); String[]
	 * allprofiletypes = request.getParameterValues("all_profile_types");
	 * String[] allprofilenames =
	 * request.getParameterValues("all_profile_names"); StringBuffer sbhostnames
	 * = new StringBuffer(); StringBuffer sbhostips = new StringBuffer();
	 * StringBuffer sbprofiletypes = new StringBuffer(); StringBuffer
	 * sballprofilenames = new StringBuffer(); for (int i = 0; i <
	 * allhostnames.length; i++) {
	 * sbhostnames.append(allhostnames[i]).append(",");
	 * sbhostips.append(allips[i]).append(",");
	 * sbprofiletypes.append(allprofiletypes[i]).append(",");
	 * sballprofilenames.append(allprofilenames[i]).append(","); } String
	 * sbhostname = sbhostnames.substring(0, sbhostnames.length() - 1);// 截取末尾的,
	 * String sbhostip = sbhostips.substring(0, sbhostips.length() - 1);//
	 * 截取获取ip String sbprofiletype = sbprofiletypes.substring(0,
	 * sbprofiletypes.length() - 1);// 截取profiletype String sballprofilename =
	 * sballprofilenames.substring(0, sballprofilenames.length() - 1);//
	 * 截取profilename
	 * 
	 * String sbhostip = request.getParameter("allip"); String sbhostname =
	 * request.getParameter("allhostname"); String sbprofiletype =
	 * request.getParameter("allprofiletype"); String sballprofilename =
	 * request.getParameter("allprofilename");
	 * 
	 * String lineSep = "\n"; String retStr = "was_hostname=" + sbhostname +
	 * lineSep + "was_version=" + request.getParameter("was_version") + lineSep
	 * + "was_fp=" + request.getParameter("was_fp") + lineSep + "was_user=" +
	 * request.getParameter("was_user") + lineSep + "was_im_path=" +
	 * request.getParameter("was_im_path") + lineSep + "was_inst_path=" +
	 * request.getParameter("was_inst_path") + lineSep + "was_jdk7=" +
	 * request.getParameter("was_jdk7") + lineSep + "was_ip=" + sbhostip +
	 * lineSep + "was_profile_type=" + sbprofiletype + lineSep +
	 * "was_profile_path=" + request.getParameter("was_profile_path") + lineSep
	 * + "was_profile_name=" + sballprofilename + lineSep + "was_security=" +
	 * request.getParameter("was_security") + lineSep + "was_userid=" +
	 * request.getParameter("was_userid") + lineSep + "was_password=" +
	 * request.getParameter("was_password") + lineSep + "was_nofile_soft=" +
	 * request.getParameter("was_nofile_soft") + lineSep + "was_nofile_hard=" +
	 * request.getParameter("was_nofile_hard") + lineSep + "was_fsize_soft=" +
	 * request.getParameter("was_fsize_soft") + lineSep + "was_fsize_hard=" +
	 * request.getParameter("was_fsize_hard") + lineSep + "was_core_soft=" +
	 * request.getParameter("was_core_soft") + lineSep + "was_core_hard=" +
	 * request.getParameter("was_core_hard") + lineSep + "product=" +
	 * amsprop.getProperty("was_standalone_product") + lineSep + "ftphost=" +
	 * amsprop.getProperty("was_standalone_ftphost") + lineSep + "ftppath=" +
	 * amsprop.getProperty("was_standalone_ftppath") + lineSep + "ftpuser=" +
	 * amsprop.getProperty("was_standalone_ftpuser") + lineSep + "ftppass=" +
	 * amsprop.getProperty("was_standalone_ftppass"); return retStr;
	 * 
	 * }
	 */

	// 拼接was单节点参数
	/*
	 * private String setWasStandAloneConfigMsg(HttpServletRequest request) {
	 * String lineSep = "\n"; String retStr = "was_hostname=" +
	 * request.getParameter("was_hostname") + lineSep + "was_version=" +
	 * request.getParameter("was_version") + lineSep + "was_fp=" +
	 * request.getParameter("was_fp") + lineSep + "was_user=" +
	 * request.getParameter("was_user") + lineSep + "was_im_path=" +
	 * request.getParameter("was_im_path") + lineSep + "was_inst_path=" +
	 * request.getParameter("was_inst_path") + lineSep + "was_jdk7=" +
	 * request.getParameter("was_jdk7") + lineSep + "was_ip=" +
	 * request.getParameter("was_ip") + lineSep + "was_profile_type=default" +
	 * lineSep + "was_profile_path=" + request.getParameter("was_profile_path")
	 * + lineSep + "was_profile_name=" +
	 * request.getParameter("was_profile_name") + lineSep + "was_security=" +
	 * request.getParameter("was_security") + lineSep + "was_userid=" +
	 * request.getParameter("was_userid") + lineSep + "was_password=" +
	 * request.getParameter("was_password") + lineSep + "was_nofile_soft=" +
	 * request.getParameter("was_nofile_soft") + lineSep + "was_nofile_hard=" +
	 * request.getParameter("was_nofile_hard") + lineSep + "was_fsize_soft=" +
	 * request.getParameter("was_fsize_soft") + lineSep + "was_fsize_hard=" +
	 * request.getParameter("was_fsize_hard") + lineSep + "was_core_soft=" +
	 * request.getParameter("was_core_soft") + lineSep + "was_core_hard=" +
	 * request.getParameter("was_core_hard") + lineSep + "product=" +
	 * amsprop.getProperty("was_standalone_product") + lineSep + "ftphost=" +
	 * amsprop.getProperty("was_standalone_ftphost") + lineSep + "ftppath=" +
	 * amsprop.getProperty("was_standalone_ftppath") + lineSep + "ftpuser=" +
	 * amsprop.getProperty("was_standalone_ftpuser") + lineSep + "ftppass=" +
	 * amsprop.getProperty("was_standalone_ftppass"); return retStr;
	 * 
	 * }
	 */
	/*	*//**
			 * 拼接MQ 产生的prepare.mq.lst文件
			 *//*
			 * private String setMQStandAloneConfigMsg(HttpServletRequest
			 * request) { String lineSep="\n"; String
			 * retStr="mq_version="+request.getParameter("mq_version")+lineSep+
			 * "mq_fp="+request.getParameter("mq_fp")+lineSep+
			 * "mq_inst_path="+request.getParameter("mq_inst_path")+lineSep+
			 * "mq_user="+request.getParameter("mq_user")+lineSep+
			 * "mq_hostname="+request.getParameter("mq_hostname")+lineSep+
			 * "mq_ip="+request.getParameter("mq_ip")+lineSep+
			 * "qmgr_method="+request.getParameter("qmgr_method")+lineSep+
			 * "qmgr_script="+request.getParameter("qmgr_script")+lineSep+
			 * "mq_qmgr_name="+request.getParameter("mq_qmgr_name")+lineSep+
			 * "mq_data_path="+request.getParameter("mq_data_path")+lineSep+
			 * "mq_log_path="+request.getParameter("mq_log_path")+lineSep+
			 * "mq_qmgr_plog="+request.getParameter("mq_qmgr_plog")+lineSep+
			 * "mq_qmgr_slog="+request.getParameter("mq_qmgr_slog")+lineSep+
			 * "mq_log_psize="+request.getParameter("mq_log_psize")+lineSep+
			 * "mq_chl_max="+request.getParameter("mq_chl_max")+lineSep+
			 * "mq_chl_kalive="+request.getParameter("mq_chl_kalive")+lineSep+
			 * "sys_semmsl="+request.getParameter("sys_semmsl")+lineSep+
			 * "sys_semmns="+request.getParameter("sys_semmns")+lineSep+
			 * "sys_semopm="+request.getParameter("sys_semopm")+lineSep+
			 * "sys_semmni="+request.getParameter("sys_semmni")+lineSep+
			 * "sys_shmax="+request.getParameter("sys_shmax")+lineSep+
			 * "sys_shmni="+request.getParameter("sys_shmni")+lineSep+
			 * "sys_shmall="+request.getParameter("sys_shmall")+lineSep+
			 * "sys_filemax="+request.getParameter("sys_filemax")+lineSep+
			 * "sys_nofile="+request.getParameter("sys_nofile")+lineSep+
			 * "sys_nproc="+request.getParameter("sys_nproc")+lineSep+
			 * "product="+ amsprop.getProperty("mq_standalone_product") +
			 * lineSep + "ftphost="+
			 * amsprop.getProperty("mq_standalone_ftphost") + lineSep +
			 * "ftppath="+ amsprop.getProperty("mq_standalone_ftppath") +
			 * lineSep + "ftpuser="+
			 * amsprop.getProperty("mq_standalone_ftpuser") + lineSep +
			 * "ftppass="+ amsprop.getProperty("mq_standalone_ftppass"); return
			 * retStr; }
			 */

	/**
	 * 拼接单节点DB2产生的DB2参数
	 *//*
		 * private String setDb2ConfigMsg(HttpServletRequest request) { //
		 * String fixpack = request.getParameter("db2_fixpack"); String lineSep
		 * = "\n"; String retStr = "ip=" + request.getParameter("ip") + lineSep
		 * + "bootip=" + request.getParameter("bootip") + lineSep + "hostname="
		 * + request.getParameter("hostname") + lineSep + "vgdb2home=" +
		 * request.getParameter("vgdb2home") + lineSep + "vgdb2log=" +
		 * request.getParameter("vgdb2log") + lineSep + "vgdb2archlog=" +
		 * request.getParameter("vgdb2archlog") + lineSep + "vgdataspace=" +
		 * request.getParameter("vgdataspace") + lineSep + "db2homepv=" +
		 * request.getParameter("db2homepv") + lineSep + "db2logpv=" +
		 * request.getParameter("db2logpv") + lineSep + "db2archlogpv=" +
		 * request.getParameter("db2archlogpv") + lineSep + "dataspacepv=" +
		 * request.getParameter("dataspacepv") + lineSep + "db2homemode=" +
		 * request.getParameter("db2homemode") + lineSep + "db2logmode=" +
		 * request.getParameter("db2logmode") + lineSep + "db2archlogmode=" +
		 * request.getParameter("db2archlogmode") + lineSep + "dataspacemode=" +
		 * request.getParameter("dataspacemode") + lineSep + "db2_version=" +
		 * request.getParameter("db2_version") + lineSep + "db2_fixpack=" +
		 * request.getParameter("db2_fixpack") + lineSep + "db2_db2base=" +
		 * request.getParameter("db2_db2base") + lineSep + "db2_dbpath=" +
		 * request.getParameter("db2_dbpath") + lineSep + "db2_svcename=" +
		 * request.getParameter("db2_svcename") + lineSep + "db2_db2insusr=" +
		 * request.getParameter("db2_db2insusr") + lineSep + "db2_dbname=" +
		 * request.getParameter("db2_dbname") + lineSep + "db2_codeset=" +
		 * request.getParameter("db2_codeset") + lineSep + "db2_dbdatapath=" +
		 * request.getParameter("db2_dbdatapath") + lineSep + "db2_db2insgrp=" +
		 * request.getParameter("db2_db2insgrp") + lineSep + "db2_db2fenusr=" +
		 * request.getParameter("db2_db2fenusr") + lineSep + "db2_db2fengrp=" +
		 * request.getParameter("db2_db2fengrp") + lineSep + "db2_db2comm=" +
		 * request.getParameter("db2_db2comm") + lineSep + "db2_db2codepage=" +
		 * request.getParameter("db2_db2codepage") + lineSep + "db2_initagents="
		 * + request.getParameter("db2_initagents") + lineSep +
		 * "db2_max_coordagents=" + request.getParameter("db2_max_coordagents")
		 * + lineSep + // db2_max_connectings "db2_max_connectings=" +
		 * request.getParameter("db2_max_connectings") + lineSep + //
		 * db2_poolagents "db2_poolagents=" +
		 * request.getParameter("db2_poolagents") + lineSep + // db2_diagsize
		 * "db2_diagsize=" + request.getParameter("db2_diagsize") + lineSep + //
		 * db2_mon_buf "db2_mon_buf=" + request.getParameter("db2_mon_buf") +
		 * lineSep + // db2_mon_lock "db2_mon_lock=" +
		 * request.getParameter("db2_mon_lock") + lineSep + // db2_mon_sort
		 * "db2_mon_sort=" + request.getParameter("db2_mon_sort") + lineSep + //
		 * db2_mon_stmt "db2_mon_stmt=" + request.getParameter("db2_mon_stmt") +
		 * lineSep + // db2_mon_table "db2_mon_table=" +
		 * request.getParameter("db2_mon_table") + lineSep + // db2_mon_uow
		 * "db2_mon_uow=" + request.getParameter("db2_mon_uow") + lineSep + //
		 * db2_health_mon "db2_health_mon=" +
		 * request.getParameter("db2_health_mon") + lineSep + // db2_mon_heap
		 * "db2_mon_heap=" + request.getParameter("db2_mon_heap") + lineSep +
		 * 
		 * // -------数据库高级属性------ // db2_db2log "db2_db2log=" +
		 * request.getParameter("db2_db2log") + lineSep + // db2_backuppath
		 * "db2_backuppath=" + request.getParameter("db2_backuppath") + lineSep
		 * + // db2_logarchpath "db2_logarchpath=" +
		 * request.getParameter("db2_logarchpath") + lineSep + // db2_stmm
		 * "db2_stmm=" + request.getParameter("db2_stmm") + lineSep + //
		 * db2_locklist "db2_locklist=" + request.getParameter("db2_locklist") +
		 * lineSep + // db2_maxlocks "db2_maxlocks=" +
		 * request.getParameter("db2_maxlocks") + lineSep + // db2_locktimeout
		 * "db2_locktimeout=" + request.getParameter("db2_locktimeout") +
		 * lineSep + // db2_sortheap "db2_sortheap=" +
		 * request.getParameter("db2_sortheap") + lineSep + // db2_logfilesize
		 * "db2_logfilesize=" + request.getParameter("db2_logfilesize") +
		 * lineSep + // db2_logprimary "db2_logprimary=" +
		 * request.getParameter("db2_logprimary") + lineSep + // db2_logsecond
		 * "db2_logsecond=" + request.getParameter("db2_logsecond") + lineSep +
		 * // db2_logbuff "db2_logbuff=" + request.getParameter("db2_logbuff") +
		 * lineSep + // db2_softmax "db2_softmax=" +
		 * request.getParameter("db2_softmax") + lineSep + // db2_trackmod
		 * "db2_trackmod=" + request.getParameter("db2_trackmod") + lineSep + //
		 * db2_pagesize "db2_pagesize=" + request.getParameter("db2_pagesize") +
		 * lineSep + "db2homelv=" + amsprop.getProperty("db2homelv") + lineSep +
		 * "db2loglv=" + amsprop.getProperty("db2loglv") + lineSep +
		 * "db2archloglv=" + amsprop.getProperty("db2archloglv") + lineSep +
		 * "dataspacelv=" + amsprop.getProperty("dataspacelv") + lineSep +
		 * "db2homefs=" + amsprop.getProperty("db2homefs") + lineSep +
		 * "db2logfs=" + amsprop.getProperty("db2logfs") + lineSep +
		 * "db2archlogfs=" + amsprop.getProperty("db2archlogfs") + lineSep +
		 * "db2backupfs=" + amsprop.getProperty("db2backupfs") + lineSep +
		 * "dataspacefs=" + amsprop.getProperty("dataspacefs") + lineSep +
		 * "ftphost=" + amsprop.getProperty("ftphost") + lineSep + "ftpuser=" +
		 * amsprop.getProperty("ftpuser") + lineSep + "ftppass=" +
		 * amsprop.getProperty("ftppass") + lineSep + "ftppath=" +
		 * amsprop.getProperty("ftppath") + lineSep + "db2path=" +
		 * amsprop.getProperty("db2path") + lineSep + "db2base=" +
		 * amsprop.getProperty("db2_path") + lineSep + "nfsON=" +
		 * request.getParameter("nfsON") + lineSep +
		 * 
		 * "nfsIP1=" + request.getParameter("nfsIP1") + lineSep + "nfsSPoint1="
		 * + request.getParameter("nfsSPoint1") + lineSep + "nfsCPoint1=" +
		 * request.getParameter("nfsCPoint1") + lineSep +
		 * 
		 * "nfsIP2=" + request.getParameter("nfsIP2") + lineSep + "nfsSPoint2="
		 * + request.getParameter("nfsSPoint2") + lineSep + "nfsCPoint2=" +
		 * request.getParameter("nfsCPoint2") + lineSep +
		 * 
		 * "nfsIP3=" + request.getParameter("nfsIP3") + lineSep + "nfsSPoint3="
		 * + request.getParameter("nfsSPoint3") + lineSep + "nfsCPoint3=" +
		 * request.getParameter("nfsCPoint3") + lineSep +
		 * 
		 * "nfsIP4=" + request.getParameter("nfsIP4") + lineSep + "nfsSPoint4="
		 * + request.getParameter("nfsSPoint4") + lineSep + "nfsCPoint4=" +
		 * request.getParameter("nfsCPoint4") + lineSep +
		 * 
		 * "nfsIP5=" + request.getParameter("nfsIP5") + lineSep + "nfsSPoint5="
		 * + request.getParameter("nfsSPoint5") + lineSep + "nfsCPoint5=" +
		 * request.getParameter("nfsCPoint5") + lineSep +
		 * 
		 * "nfsIP6=" + request.getParameter("nfsIP6") + lineSep + "nfsSPoint6="
		 * + request.getParameter("nfsSPoint6") + lineSep + "nfsCPoint6=" +
		 * request.getParameter("nfsCPoint6") + lineSep +
		 * 
		 * "nfsIP7=" + request.getParameter("nfsIP7") + lineSep + "nfsSPoint7="
		 * + request.getParameter("nfsSPoint7") + lineSep + "nfsCPoint7=" +
		 * request.getParameter("nfsCPoint7") + lineSep +
		 * 
		 * "nfsIP8=" + request.getParameter("nfsIP8") + lineSep + "nfsSPoint8="
		 * + request.getParameter("nfsSPoint8") + lineSep + "nfsCPoint8=" +
		 * request.getParameter("nfsCPoint8") + lineSep +
		 * 
		 * "nfsIP9=" + request.getParameter("nfsIP9") + lineSep + "nfsSPoint9="
		 * + request.getParameter("nfsSPoint9") + lineSep + "nfsCPoint9=" +
		 * request.getParameter("nfsCPoint9") + lineSep +
		 * 
		 * "nfsIP10=" + request.getParameter("nfsIP10") + lineSep +
		 * "nfsSPoint10=" + request.getParameter("nfsSPoint10") + lineSep +
		 * "nfsCPoint10=" + request.getParameter("nfsCPoint10") + lineSep +
		 * 
		 * "nfsIP11=" + request.getParameter("nfsIP11") + lineSep +
		 * "nfsSPoint11=" + request.getParameter("nfsSPoint11") + lineSep +
		 * "nfsCPoint11=" + request.getParameter("nfsCPoint11") + lineSep +
		 * 
		 * "nfsIP12=" + request.getParameter("nfsIP12") + lineSep +
		 * "nfsSPoint12=" + request.getParameter("nfsSPoint12") + lineSep +
		 * "nfsCPoint12=" + request.getParameter("nfsCPoint12") + lineSep +
		 * 
		 * "nfsIP13=" + request.getParameter("nfsIP13") + lineSep +
		 * "nfsSPoint13=" + request.getParameter("nfsSPoint13") + lineSep +
		 * "nfsCPoint13=" + request.getParameter("nfsCPoint13") + lineSep +
		 * 
		 * "nfsIP14=" + request.getParameter("nfsIP14") + lineSep +
		 * "nfsSPoint14=" + request.getParameter("nfsSPoint14") + lineSep +
		 * "nfsCPoint14=" + request.getParameter("nfsCPoint14") + lineSep +
		 * 
		 * "nfsIP15=" + request.getParameter("nfsIP15") + lineSep +
		 * "nfsSPoint15=" + request.getParameter("nfsSPoint15") + lineSep +
		 * "nfsCPoint15=" + request.getParameter("nfsCPoint15") + lineSep; //
		 * System.out.println("the total string is " + retStr); return retStr; }
		 */

	/**
	 * 拼接前台传过来的db2安装参数
	 * 
	 * @param request
	 * @return
	 *//*
		 * private String setConfigMsg(HttpServletRequest request) { String
		 * lineSep = "\n";
		 * 
		 * String ha_primaryIP = ""; String ha_primaryBootIP = ""; String
		 * ha_primaryBootAlias = ""; if
		 * (request.getParameter("ha_primaryNode").equals(request.getParameter(
		 * "ha_hostname1"))) { ha_primaryIP = request.getParameter("ha_ip1");
		 * ha_primaryBootIP = request.getParameter("ha_ip1");
		 * ha_primaryBootAlias = request.getParameter("ha_bootalias1"); } else {
		 * ha_primaryIP = request.getParameter("ha_ip2"); ha_primaryBootIP =
		 * request.getParameter("ha_ip2"); ha_primaryBootAlias =
		 * request.getParameter("ha_bootalias2"); } String retstr = //
		 * =====HA参数======= // ---基本--- "ha_clusterName=" +
		 * request.getParameter("ha_clusterName") + lineSep + "ha_RGNmae=" +
		 * request.getParameter("ha_RGNmae") + lineSep + "ha_ASName=" +
		 * request.getParameter("ha_ASName") + lineSep + "ha_primaryNode=" +
		 * request.getParameter("ha_primaryNode") + lineSep +
		 * 
		 * // ---基本---
		 * 
		 * // ---IP--- "ha_ip1=" + request.getParameter("ha_ip1") + lineSep +
		 * "ha_ip2=" + request.getParameter("ha_ip2") + lineSep + "ha_bootip1="
		 * + request.getParameter("ha_bootip1") + lineSep + "ha_bootip2=" +
		 * request.getParameter("ha_bootip2") + lineSep + "ha_svcip=" +
		 * request.getParameter("ha_svcip") + lineSep + // 新增主节点的IP和BootIP
		 * "ha_primaryIP=" + ha_primaryIP + lineSep + "ha_primaryBootIP=" +
		 * ha_primaryBootIP + lineSep + // ---IP---
		 * 
		 * // ---主机别名--- "ha_hostname1=" + request.getParameter("ha_hostname1")
		 * + lineSep + "ha_hostname2=" + request.getParameter("ha_hostname2") +
		 * lineSep + "ha_bootalias1=" + request.getParameter("ha_bootalias1") +
		 * lineSep + "ha_bootalias2=" + request.getParameter("ha_bootalias2") +
		 * lineSep + "ha_svcalias=" + request.getParameter("ha_svcalias") +
		 * lineSep + "ha_primaryBootAlias=" + ha_primaryBootAlias + lineSep + //
		 * ---主机别名---
		 * 
		 * // ---VG--- "ha_vgdb2home=" + request.getParameter("ha_vgdb2home") +
		 * lineSep + "ha_vgdb2log=" + request.getParameter("ha_vgdb2log") +
		 * lineSep + "ha_vgdb2archlog=" +
		 * request.getParameter("ha_vgdb2archlog") + lineSep + //
		 * "ha_vgdb2backup="+request.getParameter("ha_vgdb2backup")+lineSep // +
		 * "ha_vgdataspace=" + request.getParameter("ha_vgdataspace") + lineSep
		 * + //
		 * "ha_vgdataspace2="+request.getParameter("ha_vgdataspace2")+lineSep //
		 * + //
		 * "ha_vgdataspace3="+request.getParameter("ha_vgdataspace3")+lineSep //
		 * + //
		 * "ha_vgdataspace4="+request.getParameter("ha_vgdataspace4")+lineSep //
		 * + "ha_vgcaap=" + request.getParameter("ha_vgcaap") + lineSep + //
		 * ---VG---
		 * 
		 * // ---PV--- "ha_db2homepv=" + request.getParameter("ha_db2homepv") +
		 * lineSep + "ha_db2logpv=" + request.getParameter("ha_db2logpv") +
		 * lineSep + "ha_db2archlogpv=" +
		 * request.getParameter("ha_db2archlogpv") + lineSep + //
		 * "ha_db2backuppv="+request.getParameter("ha_db2backuppv")+lineSep // +
		 * "ha_dataspacepv=" + request.getParameter("ha_dataspace1pv") + lineSep
		 * + //
		 * "ha_dataspace2pv="+request.getParameter("ha_dataspace2pv")+lineSep //
		 * + //
		 * "ha_dataspace3pv="+request.getParameter("ha_dataspace3pv")+lineSep //
		 * + //
		 * "ha_dataspace4pv="+request.getParameter("ha_dataspace4pv")+lineSep //
		 * + "ha_caappv=" + request.getParameter("ha_caappv") + lineSep + //
		 * ---PV---
		 * 
		 * // ---VG创建方式--- "ha_db2homemode=" +
		 * request.getParameter("ha_db2homemode") + lineSep + "ha_db2logmode=" +
		 * request.getParameter("ha_db2logmode") + lineSep +
		 * "ha_db2archlogmode=" + request.getParameter("ha_db2archlogmode") +
		 * lineSep + //
		 * "ha_db2backupmode="+request.getParameter("ha_db2backupmode")+lineSep
		 * // + "ha_dataspacemode=" + request.getParameter("ha_dataspacemode") +
		 * lineSep + //
		 * "ha_dataspace2mode="+request.getParameter("ha_dataspace2mode")+
		 * lineSep // + //
		 * "ha_dataspace3mode="+request.getParameter("ha_dataspace3mode")+
		 * lineSep // + //
		 * "ha_dataspace4mode="+request.getParameter("ha_dataspace4mode")+
		 * lineSep // + "ha_caapmode=" + request.getParameter("ha_caapmode") +
		 * lineSep + // ---VG创建方式---
		 * 
		 * // HA切换策略 BEGIN "ha_startpolicy=" +
		 * request.getParameter("ha_startpolicy") + lineSep + "ha_fopolicy=" +
		 * request.getParameter("ha_fopolicy") + lineSep + "ha_fbpolicy=" +
		 * request.getParameter("ha_fbpolicy") + lineSep + // NFS BEGIN -->
		 * "ha_nfsON=" + request.getParameter("ha_nfsON") + lineSep +
		 * 
		 * "ha_nfsIP1=" + request.getParameter("ha_nfsIP1") + lineSep +
		 * "ha_nfsSPoint1=" + request.getParameter("ha_nfsSPoint1") + lineSep +
		 * "ha_nfsCPoint1=" + request.getParameter("ha_nfsCPoint1") + lineSep +
		 * 
		 * "ha_nfsIP2=" + request.getParameter("ha_nfsIP2") + lineSep +
		 * "ha_nfsSPoint2=" + request.getParameter("ha_nfsSPoint2") + lineSep +
		 * "ha_nfsCPoint2=" + request.getParameter("ha_nfsCPoint2") + lineSep +
		 * 
		 * "ha_nfsIP3=" + request.getParameter("ha_nfsIP3") + lineSep +
		 * "ha_nfsSPoint3=" + request.getParameter("ha_nfsSPoint3") + lineSep +
		 * "ha_nfsCPoint3=" + request.getParameter("ha_nfsCPoint3") + lineSep +
		 * 
		 * "ha_nfsIP4=" + request.getParameter("ha_nfsIP4") + lineSep +
		 * "ha_nfsSPoint4=" + request.getParameter("ha_nfsSPoint4") + lineSep +
		 * "ha_nfsCPoint4=" + request.getParameter("ha_nfsCPoint4") + lineSep +
		 * 
		 * "ha_nfsIP5=" + request.getParameter("ha_nfsIP5") + lineSep +
		 * "ha_nfsSPoint5=" + request.getParameter("ha_nfsSPoint5") + lineSep +
		 * "ha_nfsCPoint5=" + request.getParameter("ha_nfsCPoint5") + lineSep +
		 * 
		 * "ha_nfsIP6=" + request.getParameter("ha_nfsIP6") + lineSep +
		 * "ha_nfsSPoint6=" + request.getParameter("ha_nfsSPoint6") + lineSep +
		 * "ha_nfsCPoint6=" + request.getParameter("ha_nfsCPoint6") + lineSep +
		 * 
		 * "ha_nfsIP7=" + request.getParameter("ha_nfsIP7") + lineSep +
		 * "ha_nfsSPoint7=" + request.getParameter("ha_nfsSPoint7") + lineSep +
		 * "ha_nfsCPoint7=" + request.getParameter("ha_nfsCPoint7") + lineSep +
		 * 
		 * "ha_nfsIP8=" + request.getParameter("ha_nfsIP8") + lineSep +
		 * "ha_nfsSPoint8=" + request.getParameter("ha_nfsSPoint8") + lineSep +
		 * "ha_nfsCPoint8=" + request.getParameter("ha_nfsCPoint8") + lineSep +
		 * 
		 * "ha_nfsIP9=" + request.getParameter("ha_nfsIP9") + lineSep +
		 * "ha_nfsSPoint9=" + request.getParameter("ha_nfsSPoint9") + lineSep +
		 * "ha_nfsCPoint9=" + request.getParameter("ha_nfsCPoint9") + lineSep +
		 * 
		 * "ha_nfsIP10=" + request.getParameter("ha_nfsIP10") + lineSep +
		 * "ha_nfsSPoint10=" + request.getParameter("ha_nfsSPoint10") + lineSep
		 * + "ha_nfsCPoint10=" + request.getParameter("ha_nfsCPoint10") +
		 * lineSep +
		 * 
		 * "ha_nfsIP11=" + request.getParameter("ha_nfsIP11") + lineSep +
		 * "ha_nfsSPoint11=" + request.getParameter("ha_nfsSPoint11") + lineSep
		 * + "ha_nfsCPoint11=" + request.getParameter("ha_nfsCPoint11") +
		 * lineSep +
		 * 
		 * "ha_nfsIP12=" + request.getParameter("ha_nfsIP12") + lineSep +
		 * "ha_nfsSPoint12=" + request.getParameter("ha_nfsSPoint12") + lineSep
		 * + "ha_nfsCPoint12=" + request.getParameter("ha_nfsCPoint12") +
		 * lineSep +
		 * 
		 * "ha_nfsIP13=" + request.getParameter("ha_nfsIP13") + lineSep +
		 * "ha_nfsSPoint13=" + request.getParameter("ha_nfsSPoint13") + lineSep
		 * + "ha_nfsCPoint13=" + request.getParameter("ha_nfsCPoint13") +
		 * lineSep +
		 * 
		 * "ha_nfsIP14=" + request.getParameter("ha_nfsIP14") + lineSep +
		 * "ha_nfsSPoint14=" + request.getParameter("ha_nfsSPoint14") + lineSep
		 * + "ha_nfsCPoint14=" + request.getParameter("ha_nfsCPoint14") +
		 * lineSep +
		 * 
		 * "ha_nfsIP15=" + request.getParameter("ha_nfsIP15") + lineSep +
		 * "ha_nfsSPoint15=" + request.getParameter("ha_nfsSPoint15") + lineSep
		 * + "ha_nfsCPoint15=" + request.getParameter("ha_nfsCPoint15") +
		 * lineSep +
		 * 
		 * // =====DB2 页面参数 star======= // ------基本信息------ // db2_version
		 * "db2_version=" + request.getParameter("db2_version") + lineSep + //
		 * db2_db2base "db2_db2base=" + request.getParameter("db2_db2base") +
		 * lineSep + // db2_dbpath "db2_dbpath=" +
		 * request.getParameter("db2_dbpath") + lineSep + // db2_svcename
		 * "db2_svcename=" + request.getParameter("db2_svcename") + lineSep + //
		 * db2_db2insusr "db2_db2insusr=" +
		 * request.getParameter("db2_db2insusr") + lineSep + // db2_dbname
		 * "db2_dbname=" + request.getParameter("db2_dbname") + lineSep + //
		 * db2_codeset "db2_codeset=" + request.getParameter("db2_codeset") +
		 * lineSep + // db2_dbdatapath "db2_dbdatapath=" +
		 * request.getParameter("db2_dbdatapath") + lineSep +
		 * 
		 * // ------实例高级属性------- // db2_db2insusr 基本信息已有 //
		 * "db2_db2insusr="+request.getParameter("db2_db2insusr")+lineSep + //
		 * db2_db2insgrp "db2_db2insgrp=" +
		 * request.getParameter("db2_db2insgrp") + lineSep + // db2_db2fenusr
		 * "db2_db2fenusr=" + request.getParameter("db2_db2fenusr") + lineSep +
		 * // db2_db2fengrp "db2_db2fengrp=" +
		 * request.getParameter("db2_db2fengrp") + lineSep + // db2_db2comm
		 * "db2_db2comm=" + request.getParameter("db2_db2comm") + lineSep + //
		 * db2_db2codepage "db2_db2codepage=" +
		 * request.getParameter("db2_db2codepage") + lineSep + // db2_initagents
		 * "db2_initagents=" + request.getParameter("db2_initagents") + lineSep
		 * + // db2_max_coordagents "db2_max_coordagents=" +
		 * request.getParameter("db2_max_coordagents") + lineSep + //
		 * db2_max_connectings "db2_max_connectings=" +
		 * request.getParameter("db2_max_connectings") + lineSep + //
		 * db2_poolagents "db2_poolagents=" +
		 * request.getParameter("db2_poolagents") + lineSep + // db2_diagsize
		 * "db2_diagsize=" + request.getParameter("db2_diagsize") + lineSep + //
		 * db2_mon_buf "db2_mon_buf=" + request.getParameter("db2_mon_buf") +
		 * lineSep + // db2_mon_lock "db2_mon_lock=" +
		 * request.getParameter("db2_mon_lock") + lineSep + // db2_mon_sort
		 * "db2_mon_sort=" + request.getParameter("db2_mon_sort") + lineSep + //
		 * db2_mon_stmt "db2_mon_stmt=" + request.getParameter("db2_mon_stmt") +
		 * lineSep + // db2_mon_table "db2_mon_table=" +
		 * request.getParameter("db2_mon_table") + lineSep + // db2_mon_uow
		 * "db2_mon_uow=" + request.getParameter("db2_mon_uow") + lineSep + //
		 * db2_health_mon "db2_health_mon=" +
		 * request.getParameter("db2_health_mon") + lineSep + // db2_mon_heap
		 * "db2_mon_heap=" + request.getParameter("db2_mon_heap") + lineSep +
		 * 
		 * // -------数据库高级属性------ // db2_db2log "db2_db2log=" +
		 * request.getParameter("db2_db2log") + lineSep + // db2_backuppath
		 * "db2_backuppath=" + request.getParameter("db2_backuppath") + lineSep
		 * + // db2_logarchpath "db2_logarchpath=" +
		 * request.getParameter("db2_logarchpath") + lineSep + // db2_stmm
		 * "db2_stmm=" + request.getParameter("db2_stmm") + lineSep + //
		 * db2_locklist "db2_locklist=" + request.getParameter("db2_locklist") +
		 * lineSep + // db2_maxlocks "db2_maxlocks=" +
		 * request.getParameter("db2_maxlocks") + lineSep + // db2_locktimeout
		 * "db2_locktimeout=" + request.getParameter("db2_locktimeout") +
		 * lineSep + // db2_sortheap "db2_sortheap=" +
		 * request.getParameter("db2_sortheap") + lineSep + // db2_logfilesize
		 * "db2_logfilesize=" + request.getParameter("db2_logfilesize") +
		 * lineSep + // db2_logprimary "db2_logprimary=" +
		 * request.getParameter("db2_logprimary") + lineSep + // db2_logsecond
		 * "db2_logsecond=" + request.getParameter("db2_logsecond") + lineSep +
		 * // db2_logbuff "db2_logbuff=" + request.getParameter("db2_logbuff") +
		 * lineSep + // db2_softmax "db2_softmax=" +
		 * request.getParameter("db2_softmax") + lineSep + // db2_trackmod
		 * "db2_trackmod=" + request.getParameter("db2_trackmod") + lineSep + //
		 * db2_pagesize "db2_pagesize=" + request.getParameter("db2_pagesize") +
		 * lineSep +
		 * 
		 * // =====DB2 页面参数 end======= // =====新增静态参数star======
		 * 
		 * // db2homelv=db2homelv //
		 * "db2homelv="+amsprop.getProperty("db2homelv")+lineSep + //
		 * db2loglv=db2loglv //
		 * "db2loglv="+amsprop.getProperty("db2loglv")+lineSep + //
		 * db2archloglv=db2archloglv //
		 * "db2archloglv="+amsprop.getProperty("db2archloglv")+lineSep + //
		 * db2backuplv=db2backuplv //
		 * "db2backuplv="+amsprop.getProperty("db2backuplv")+lineSep + //
		 * dataspace1lv=dataspace1lv //
		 * "dataspace1lv="+amsprop.getProperty("dataspace1lv")+lineSep + //
		 * dataspace2lv=dataspace2lv //
		 * "dataspace2lv="+amsprop.getProperty("dataspace2lv")+lineSep + //
		 * dataspace3lv=dataspace3lv //
		 * "dataspace3lv="+amsprop.getProperty("dataspace3lv")+lineSep + //
		 * dataspace4lv=dataspace4lv //
		 * "dataspace4lv="+amsprop.getProperty("dataspace4lv")+lineSep + //
		 * db2homefs=/db2home //
		 * "db2homefs="+amsprop.getProperty("db2homefs")+lineSep + //
		 * db2logfs=/db2log //
		 * "db2logfs="+amsprop.getProperty("db2logfs")+lineSep + //
		 * db2archlogfs=/db2archlog //
		 * "db2archlogfs="+amsprop.getProperty("db2archlogfs")+lineSep + //
		 * db2backupfs=/db2backup //
		 * "db2backupfs="+amsprop.getProperty("db2backupfs")+lineSep + //
		 * dataspace1fs=/db2dataspace1 //
		 * "dataspace1fs="+amsprop.getProperty("dataspace1fs")+lineSep + //
		 * dataspace2fs=/db2dataspace2 //
		 * "dataspace2fs="+amsprop.getProperty("dataspace2fs")+lineSep + //
		 * dataspace3fs=/db2dataspace3 //
		 * "dataspace3fs="+amsprop.getProperty("dataspace3fs")+lineSep + //
		 * dataspace4fs=/db2dataspace4 //
		 * "dataspace4fs="+amsprop.getProperty("dataspace4fs")+lineSep + //
		 * harg1vgs=$ha_vgdb2home,$ha_vgdb2log,$ha_vgdb2archlog,$ha_vgdb2backup,
		 * $ha_vgdataspace1,$ha_vgdataspace2,$ha_vgdataspace3,$ha_vgdataspace4
		 * "harg1vgs=" + amsprop.getProperty("harg1vgs") + lineSep + //
		 * hamode=AA "hamode=" + amsprop.getProperty("hamode") + lineSep + //
		 * hargnum=1 "hargnum=" + amsprop.getProperty("hargnum") + lineSep + //
		 * ftphost=10.48.0.210 "ftphost=" + amsprop.getProperty("ftphost") +
		 * lineSep + // ftpuser=htzhang "ftpuser=" +
		 * amsprop.getProperty("ftpuser") + lineSep + // ftppass=8uhbvgy7
		 * "ftppass=" + amsprop.getProperty("ftppass") + lineSep + //
		 * ftppath=/pub/ahrccb/DB2/ "ftppath=" + amsprop.getProperty("ftppath")
		 * + lineSep + // db2path=$db2homefs/install "db2path=" +
		 * amsprop.getProperty("db2path") + lineSep +
		 * 
		 * // ======新增静态参数end=======
		 * 
		 * "db2base=" + amsprop.getProperty("db2_path") + lineSep;
		 * System.out.println("====retstr:" + retstr); return retstr; }
		 */

	/**
	 * @Title: toWasNextPage
	 * @Description: 到Was配置确认页面
	 * @param @param
	 *            request
	 * @param @param
	 *            session
	 * @param @return
	 * @param @throws
	 *            Exception
	 * @author LiangRui
	 * @throws @Time
	 *             2015年6月16日 下午12:25:18
	 */
	/*
	 * @SuppressWarnings({ "rawtypes", "unchecked" })
	 * 
	 * @RequestMapping("/toWasNextPage") public String
	 * toWasInstallPage(HttpServletRequest request, HttpSession session) throws
	 * Exception { String tenantId = ""; String tokenId = ""; try { tenantId =
	 * OpenStackUtil.getTenantId(); tokenId = OpenStackUtil.getTokenId(); }
	 * catch (LoginTimeOutException e) { return this.setExceptionTologin(e,
	 * request); } // 获取输入的参数. request.setAttribute("wasUrl",
	 * request.getParameter("wasUrl")); request.setAttribute("wasDateUrl",
	 * request.getParameter("wasDateUrl")); request.setAttribute("wasCellNode",
	 * request.getParameter("wasCellNode")); request.setAttribute("wasAccount",
	 * request.getParameter("wasAccount")); request.setAttribute("wasPss",
	 * request.getParameter("wasPss")); // 获取ID String serId =
	 * request.getParameter("serId"); List serIds = new ArrayList(); if (serId
	 * != null && serId != "") { String[] ss = serId.split(","); for (int i = 0;
	 * i < ss.length; i++) { serIds.add(ss[i]); } } List<ServerBean> listDetial
	 * = new ArrayList<ServerBean>(); List<ServerBean> list; List<FlavorBean>
	 * fList; List<ImagesBean> imageList; try { list =
	 * instanceService.getServerListV2(tenantId, tokenId); for (int i = 0; i <
	 * list.size(); i++) { String serverId = list.get(i).getId(); for (int j =
	 * 0; j < serIds.size(); j++) { if (serverId.equals(serIds.get(j))) {
	 * listDetial.add(list.get(i)); } } } fList =
	 * flavorsService.getAllFlavorsList(tenantId, tokenId); imageList =
	 * imageService.getAllImageList(tenantId, tokenId); } catch
	 * (LoginTimeOutException e) { return this.setExceptionTologin(e, request);
	 * } catch (BaseException e) { e.printStackTrace(); throw new
	 * ReturnToMainPageException(e.getMessage()); }
	 * request.setAttribute("serId", serId); request.setAttribute("servers",
	 * listDetial); request.setAttribute("flavors", fList);
	 * request.setAttribute("imageList", imageList); return
	 * "instance_aix_was_install_confirm"; }
	 */
	/**
	 * @Title: getLogInfo
	 * @Description: 查询所有日志信息
	 * @param @param
	 *            request
	 * @param @param
	 *            session
	 * @param @return
	 * @param @throws
	 *            Exception
	 * @author LiangRui
	 * @throws @Time
	 *             2015年6月16日 下午7:19:00
	 */
	@RequestMapping("/getLogInfo")
	public String getLogInfo(HttpServletRequest request, HttpServletResponse resp, HttpSession session)
			throws Exception {
		String isLogIn = (String) request.getSession().getAttribute("userName");

		if (isLogIn == null || isLogIn.equals("")) // 从session中获取userName { try
		{
			return "login";
		}

		ArrayNode list = amsRestService.getList(null, null, "odata/pvcclusters");

		ObjectMapper om = new ObjectMapper();

		ArrayNode logAryNode = om.createArrayNode();

		for (JsonNode jn : list)

		{
			// ObjectMapper om1 = new ObjectMapper();
			ObjectNode logObjNode = om.createObjectNode();

			ObjectNode nodeJN = (ObjectNode) jn;

			logObjNode.put("uuid", nodeJN.get("uuid").asText());
			logObjNode.put("type", nodeJN.get("type").asText());
			logObjNode.put("created_at", nodeJN.get("created_at").asText());
			// System.out.println("nodes size is " +
			// nodeJN.get("nodes").size());
			int size = nodeJN.get("nodes").size();
			logObjNode.put("nodesnum", String.valueOf(size));
			// 查出运行结果 目标:http://10.28.0.235:3000/odata/tasks?uuid=... 参数uuid

			String uuid = nodeJN.get("uuid").asText();
			String table = "odata/tasks?uuid=";
			String url = table + uuid;
			ArrayNode tasks = amsRestService.getList(null, null, url);
			ObjectNode task = null;
			if (tasks != null && tasks.size() > 0) {
				task = (ObjectNode) tasks.get(0);
				String status = task.get("status").asText();
				String steps = task.get("steps").asText();
				String total = task.get("total").asText();
				// String nodesnum = task.get(index)
				if ("0".equals(status)) {
					// 未执行
					logObjNode.put("status", "0");
				} else if ("1".equals(status)) {
					// 执行中
					logObjNode.put("status", "1");
				} else if ("2".equals(status) && steps.equals(total)) {
					// 成功
					logObjNode.put("status", "2");
				} else {
					// 失败
					logObjNode.put("status", "3");
				}
			} else {
				logger.info("根据uuid获取tasks信息有误");
			}

			JsonNode nodes = jn.get("nodes");
			String iswas = jn.get("type").textValue(); // 得到操作类型是was\wascluster\mq\mqcluster\db2

			if (iswas.equals("WAS Cluster")) 
			{
				JsonNode optionnode = jn.get("options");
				String profiletypenode = optionnode.get("was_profile_type").textValue();// 概要类型
				if (profiletypenode == null) // 这个属性没有值
				{
					logger.info("wascluster::was_profile_type has no value.");
				} else {
					if (profiletypenode.contains("cell")) // 如果包含集群的cell字符串
					{
						/*
						 * for (JsonNode node : nodes) { String hostname =
						 * node.get("hostname").textValue(); String addr =
						 * node.get("addr").textValue(); String role =
						 * node.get("role").textValue(); logObjNode.put(role,
						 * hostname + "(" + addr + ")"); // 主节点dmgr
						 * logObjNode.put("2", hostname + "(" + addr + ")"); //
						 * 主节点dmgr if ("1".equals(role)) {
						 * logObjNode.put("mainaddr", addr); }
						 * 
						 * }
						 */
						String hostnames = optionnode.get("was_hostname").textValue();
						String hostips = optionnode.get("was_ip").textValue();

						String[] hosts = hostnames.split(",");
						String[] ips = hostips.split(",");
						logObjNode.put("1", hosts[0] + "(" + ips[0] + ")");// 主节点dmgr
						logObjNode.put("mainaddr", ips[0]);
						if (hosts.length == 1) {
							logAryNode.add(logObjNode);
						} else {
							StringBuffer sb = new StringBuffer();
							for (int i = 1; i < hosts.length; i++) {
								sb.append(hosts[i] + "(" + ips[i] + ")").append(",");
							}

							logObjNode.put("2", sb.substring(0, sb.length() - 1).toString());
							logAryNode.add(logObjNode);
						}
					}
				}
			} 
			if (iswas.equals("MQ Cluster")) 
			{
				JsonNode optionnode = jn.get("options");
				String completesavenode = optionnode.get("mq_complete_save").textValue();// 概要类型
				if (completesavenode == null) // 这个属性没有值
				{
					logger.info("mqcluster::mq_complete_save has no value.");
				} else {
					if (completesavenode.contains("yes")) // 如果包含集群的yes字符串
					{
						String hostnames = optionnode.get("mq_hostname").textValue();
						String hostips = optionnode.get("mq_ip").textValue();

						String[] hosts = hostnames.split(",");
						String[] ips = hostips.split(",");
						
						logObjNode.put("1", hosts[0] + "(" + ips[0] + ")");// 主节点dmgr
						logObjNode.put("mainaddr", ips[0]);
						if (hosts.length == 1) 
						{
							logAryNode.add(logObjNode);
						} 
						else 
						{
							StringBuffer sb = new StringBuffer();
							for (int i = 1; i < hosts.length; i++) 
							{
								sb.append(hosts[i] + "(" + ips[i] + ")").append(",");
							}
							logObjNode.put("2", sb.substring(0, sb.length() - 1).toString());
							logAryNode.add(logObjNode);
						}
					}
				}
			} 
			else {
				// 所有非集群操作如下分支
				for (JsonNode node : nodes) {
					String hostname = node.get("hostname").textValue();
					String addr = node.get("addr").textValue();
					String role = node.get("role").textValue();
					logObjNode.put(role, hostname + "(" + addr + ")"); // 主节点dmgr
																		// 放1 HA
																		// CLUSTER
																		// 副节点放2
					if ("1".equals(role)) {
						logObjNode.put("mainaddr", addr);
					}

				}
				logAryNode.add(logObjNode);
			}
		}
		// logAryNode;
		// System.out.println("logAryNode" + logAryNode);
		logger.info("InstanceController::logAryNode" + logAryNode);
		request.setAttribute("items", logAryNode);

		return "instance_aix_log";

	}

	/**
	 * 获取was集群日志情况
	 *//*
		 * public String getWasClusterInfoDetail(HttpServletRequest request,
		 * HttpSession session) { String uuid = request.getParameter("uuid"); //
		 * String serid=request.getParameter("serids"); ArrayNode pvcclusters =
		 * amsRestService.getList(null, null, "odata/pvcclusters?uuid=" + uuid);
		 * ObjectNode cluster = null; if (pvcclusters != null &&
		 * pvcclusters.size() > 0) { cluster = (ObjectNode) pvcclusters.get(0);
		 * } List<String> serIds = new ArrayList<String>(); if (cluster != null
		 * && cluster.get("nodes") != null) { ArrayNode nodes = (ArrayNode)
		 * cluster.get("nodes"); for (JsonNode jn : nodes) {
		 * serIds.add(jn.get("pvcid") == null ? "" : jn.get("pvcid").asText());
		 * } } if (cluster != null && cluster.get("options") != null) {
		 * ObjectNode options = (ObjectNode) cluster.get("options"); // 单节点IP
		 * 
		 * request.setAttribute("was_core_hard",
		 * options.get("was_core_hard").asText());
		 * request.setAttribute("was_core_soft",
		 * options.get("was_core_soft").asText());
		 * request.setAttribute("was_fsize_hard",
		 * options.get("was_fsize_hard").asText());
		 * request.setAttribute("was_fsize_soft",
		 * options.get("was_fsize_soft").asText());
		 * request.setAttribute("was_nofile_hard",
		 * options.get("was_nofile_hard").asText());
		 * request.setAttribute("was_nofile_soft",
		 * options.get("was_nofile_soft").asText());
		 * request.setAttribute("was_password",
		 * options.get("was_password").asText());
		 * request.setAttribute("was_userid",
		 * options.get("was_userid").asText());
		 * request.setAttribute("was_security",
		 * options.get("was_security").asText());
		 * request.setAttribute("was_profile_name",
		 * options.get("was_profile_name").asText());
		 * request.setAttribute("was_profile_path",
		 * options.get("was_profile_path").asText());
		 * request.setAttribute("was_profile_type",
		 * options.get("was_profile_type").asText());
		 * request.setAttribute("was_ip", options.get("was_ip").asText());
		 * request.setAttribute("was_jdk7", options.get("was_jdk7").asText());
		 * request.setAttribute("was_inst_path",
		 * options.get("was_inst_path").asText());
		 * request.setAttribute("was_im_path",
		 * options.get("was_im_path").asText());
		 * request.setAttribute("was_user", options.get("was_user").asText());
		 * request.setAttribute("was_fp", options.get("was_fp").asText());
		 * request.setAttribute("was_version",
		 * options.get("was_version").asText());
		 * request.setAttribute("was_hostname",
		 * options.get("was_hostname").asText()); //
		 * request.setAttribute("wasfix", options.get("wasfix").asText()); }
		 * ArrayNode lists = amsRestService.getList(null, null,
		 * "odata/servers"); // System.out.println(lists);
		 * 
		 * List<AddHostBean> lahb = new ArrayList<AddHostBean>(); for (JsonNode
		 * js : lists) { AddHostBean ahb = new AddHostBean();
		 * ahb.setIP(js.get("IP").asText() != null ? js.get("IP").asText() :
		 * ""); ahb.setName(js.get("Name").asText() != null ?
		 * js.get("Name").asText() : ""); ahb.setOS(js.get("OS").asText() !=
		 * null ? js.get("OS").asText() : "");
		 * ahb.setPassword(js.get("Password").asText() != null ?
		 * js.get("Password").asText() : "");
		 * ahb.setUserID(js.get("UserID").asText() != null ?
		 * js.get("UserID").asText() : "");
		 * ahb.setStatus(js.get("Status").asText() != null ?
		 * js.get("Status").asText() : ""); ahb.set_id(js.get("_id").asText() !=
		 * null ? js.get("_id").asText() : "");
		 * ahb.setHConf(js.get("HConf").asText() != null ?
		 * js.get("HConf").asText() : "");
		 * ahb.setHVisor(js.get("HVisor").asText() != null ?
		 * js.get("HVisor").asText() : ""); lahb.add(ahb); }
		 * Collections.sort(lahb); // request.setAttribute("serId", serid); //
		 * request.setAttribute("total", lahb.size()); List<AddHostBean>
		 * listDetial = new ArrayList<AddHostBean>();
		 * 
		 * for (int i = 0; i < lahb.size(); i++) { String serverId =
		 * lahb.get(i).get_id(); for (int j = 0; j < serIds.size(); j++) { if
		 * (serverId.equals(serIds.get(j))) { listDetial.add(lahb.get(i)); } } }
		 * request.setAttribute("servers", listDetial);
		 * request.setAttribute("cluster", cluster);
		 * request.setAttribute("mainaddr", request.getParameter("mainaddr"));
		 * request.setAttribute("uuid", request.getParameter("uuid"));
		 * 
		 * // 查询安装执行日志信息 // 查出运行结果
		 * 目标:http://10.28.0.235:3000/odata/tasks?uuid=... 参数uuid String table =
		 * "odata/tasks?uuid="; String url = table + uuid; ArrayNode tasks =
		 * amsRestService.getList(null, null, url); ObjectNode task = null; Map
		 * map = new TreeMap(); if (tasks != null && tasks.size() > 0) { task =
		 * (ObjectNode) tasks.get(0); // total String total =
		 * task.get("total").asText(); // steps String stepsNb =
		 * task.get("steps").asText(); // status String status =
		 * task.get("status").asText(); String progress = stepsNb + " / " +
		 * total; map.put("progress", progress);
		 * request.setAttribute("progress", progress); String percent =
		 * FormatUtil.getPercent(Integer.parseInt(stepsNb),
		 * Integer.parseInt(total)); map.put("percent", percent);
		 * request.setAttribute("percent", percent); map.put("status", status);
		 * request.setAttribute("status", status); List<WasClusterLogBean>
		 * wasclusterlist = new ArrayList<WasClusterLogBean>();
		 * WasClusterLogBean wclb = null;
		 * 
		 * ArrayNode jobsNode = (ArrayNode) task.get("jobs"); for (JsonNode
		 * jsonNode : jobsNode) { ObjectNode jobnode = (ObjectNode) jsonNode;
		 * String name = jobnode.get("name").asText(); if
		 * (name.equals("make-directory")) { wclb = new WasClusterLogBean(); }
		 * ArrayNode stepsNodes = (ArrayNode) jobnode.get("steps"); for (int i =
		 * 0; i < stepsNodes.size(); i++) { ObjectNode stepsNode = (ObjectNode)
		 * stepsNodes.get(i); String step_uuid = stepsNode.get("uuid").asText();
		 * String steps_url = "odata/steps?uuid=" + step_uuid; ArrayNode steps =
		 * amsRestService.getList(null, null, steps_url); ObjectNode step =
		 * null; if (steps != null && steps.size() > 0) { step = (ObjectNode)
		 * steps.get(0); } String script_status = step.get("status").asText();
		 * if ("2".equals(script_status)) { String script_retnum =
		 * step.get("retnum").asText(); if ("0".equals(script_retnum)) { // 成功
		 * // request.setAttribute((name + "_" + // i).replace('-',
		 * '_').replace('.', '_'), "成功"); // System.out.println((name + "_" +
		 * i).replace('-', // '_').replace('.', '_')); String successstep =
		 * (name + "_" + i).replace('-', '_').replace('.', '_'); map.put((name +
		 * "_" + i).replace('-', '_').replace('.', '_'), "成功");
		 * 
		 * setWasClusterLogDetail(wclb, name, i, "成功");
		 * 
		 * } else { // 失败 // request.setAttribute((name + "_" + //
		 * i).replace('-', '_').replace('.', '_'), "失败"); map.put((name + "_" +
		 * i).replace('-', '_').replace('.', '_'), "失败");
		 * setWasClusterLogDetail(wclb, name, i, "失败"); } } else if
		 * ("1".equals(script_status)) { // request.setAttribute((name + "_" +
		 * i).replace('-', // '_').replace('.', '_'), "执行中"); map.put((name +
		 * "_" + i).replace('-', '_').replace('.', '_'), "执行中");
		 * setWasClusterLogDetail(wclb, name, i, "执行中"); } else { // 未执行 ///
		 * request.setAttribute((name + "_" + i).replace('-', //
		 * '_').replace('.', '_'), "未开始"); map.put((name + "_" + i).replace('-',
		 * '_').replace('.', '_'), "未开始"); setWasClusterLogDetail(wclb, name, i,
		 * "未执行"); } String script_addr = step.get("node").get("addr").asText();
		 * // request.setAttribute((name + "_addr_" + i).replace('-', //
		 * '_').replace('.', '_'), script_addr); // System.out.println((name +
		 * "_addr_" + i).replace('-', // '_').replace('.', '_')); map.put((name
		 * + "_addr_" + i).replace('-', '_').replace('.', '_'), script_addr);
		 * setWasClusterLogAddressDetail(wclb, name, i, script_addr);
		 * 
		 * } if ("start-was".equals(name)) wasclusterlist.add(wclb); }
		 * 
		 * request.setAttribute("wasclusterservers", wasclusterlist);
		 * 
		 * }
		 * 
		 * return "instance_linux_wascluster_log_details"; }
		 */

	/**
	 * 
	 * 获取was 单节点日志情况
	 * 
	 *//*
		 * public String getWasStandAloneInfoDetail(HttpServletRequest request,
		 * HttpSession session) { String uuid = request.getParameter("uuid"); //
		 * String serid=request.getParameter("serids"); ArrayNode pvcclusters =
		 * amsRestService.getList(null, null, "odata/pvcclusters?uuid=" + uuid);
		 * ObjectNode cluster = null; if (pvcclusters != null &&
		 * pvcclusters.size() > 0) { cluster = (ObjectNode) pvcclusters.get(0);
		 * } List<String> serIds = new ArrayList<String>(); if (cluster != null
		 * && cluster.get("nodes") != null) { ArrayNode nodes = (ArrayNode)
		 * cluster.get("nodes"); for (JsonNode jn : nodes) {
		 * serIds.add(jn.get("pvcid") == null ? "" : jn.get("pvcid").asText());
		 * } } if (cluster != null && cluster.get("options") != null) {
		 * ObjectNode options = (ObjectNode) cluster.get("options"); // 单节点IP
		 * 
		 * request.setAttribute("was_core_hard",
		 * options.get("was_core_hard").asText());
		 * request.setAttribute("was_core_soft",
		 * options.get("was_core_soft").asText());
		 * request.setAttribute("was_fsize_hard",
		 * options.get("was_fsize_hard").asText());
		 * request.setAttribute("was_fsize_soft",
		 * options.get("was_fsize_soft").asText());
		 * request.setAttribute("was_nofile_hard",
		 * options.get("was_nofile_hard").asText());
		 * request.setAttribute("was_nofile_soft",
		 * options.get("was_nofile_soft").asText());
		 * request.setAttribute("was_password",
		 * options.get("was_password").asText());
		 * request.setAttribute("was_userid",
		 * options.get("was_userid").asText());
		 * request.setAttribute("was_security",
		 * options.get("was_security").asText());
		 * request.setAttribute("was_profile_name",
		 * options.get("was_profile_name").asText());
		 * request.setAttribute("was_profile_path",
		 * options.get("was_profile_path").asText());
		 * request.setAttribute("was_profile_type",
		 * options.get("was_profile_type").asText());
		 * request.setAttribute("was_ip", options.get("was_ip").asText());
		 * request.setAttribute("was_jdk7", options.get("was_jdk7").asText());
		 * request.setAttribute("was_inst_path",
		 * options.get("was_inst_path").asText());
		 * request.setAttribute("was_im_path",
		 * options.get("was_im_path").asText());
		 * request.setAttribute("was_user", options.get("was_user").asText());
		 * request.setAttribute("was_fp", options.get("was_fp").asText());
		 * request.setAttribute("was_version",
		 * options.get("was_version").asText());
		 * request.setAttribute("was_hostname",
		 * options.get("was_hostname").asText()); //
		 * request.setAttribute("wasfix", options.get("wasfix").asText()); }
		 * ArrayNode lists = amsRestService.getList(null, null,
		 * "odata/servers"); System.out.println(lists);
		 * 
		 * List<AddHostBean> lahb = new ArrayList<AddHostBean>(); for (JsonNode
		 * js : lists) { AddHostBean ahb = new AddHostBean();
		 * ahb.setIP(js.get("IP").asText() != null ? js.get("IP").asText() :
		 * ""); ahb.setName(js.get("Name").asText() != null ?
		 * js.get("Name").asText() : ""); ahb.setOS(js.get("OS").asText() !=
		 * null ? js.get("OS").asText() : "");
		 * ahb.setPassword(js.get("Password").asText() != null ?
		 * js.get("Password").asText() : "");
		 * ahb.setUserID(js.get("UserID").asText() != null ?
		 * js.get("UserID").asText() : "");
		 * ahb.setStatus(js.get("Status").asText() != null ?
		 * js.get("Status").asText() : ""); ahb.set_id(js.get("_id").asText() !=
		 * null ? js.get("_id").asText() : "");
		 * ahb.setHConf(js.get("HConf").asText() != null ?
		 * js.get("HConf").asText() : "");
		 * ahb.setHVisor(js.get("HVisor").asText() != null ?
		 * js.get("HVisor").asText() : ""); lahb.add(ahb); }
		 * Collections.sort(lahb); System.out.println(lahb);
		 * 
		 * // request.setAttribute("serId", serid); //
		 * request.setAttribute("total", lahb.size()); List<AddHostBean>
		 * listDetial = new ArrayList<AddHostBean>();
		 * 
		 * for (int i = 0; i < lahb.size(); i++) { String serverId =
		 * lahb.get(i).get_id(); for (int j = 0; j < serIds.size(); j++) { if
		 * (serverId.equals(serIds.get(j))) { listDetial.add(lahb.get(i)); } } }
		 * request.setAttribute("servers", listDetial);
		 * request.setAttribute("cluster", cluster);
		 * request.setAttribute("mainaddr", request.getParameter("mainaddr"));
		 * request.setAttribute("uuid", request.getParameter("uuid"));
		 * 
		 * // 查询安装执行日志信息 // 查出运行结果
		 * 目标:http://10.28.0.235:3000/odata/tasks?uuid=... 参数uuid String table =
		 * "odata/tasks?uuid="; String url = table + uuid; ArrayNode tasks =
		 * amsRestService.getList(null, null, url); ObjectNode task = null; Map
		 * map = new TreeMap(); if (tasks != null && tasks.size() > 0) { task =
		 * (ObjectNode) tasks.get(0); // total String total =
		 * task.get("total").asText(); // steps String stepsNb =
		 * task.get("steps").asText(); // status String status =
		 * task.get("status").asText(); String progress = stepsNb + " / " +
		 * total; map.put("progress", progress);
		 * request.setAttribute("progress", progress); String percent =
		 * FormatUtil.getPercent(Integer.parseInt(stepsNb),
		 * Integer.parseInt(total)); map.put("percent", percent);
		 * request.setAttribute("percent", percent); map.put("status", status);
		 * request.setAttribute("status", status);
		 * 
		 * ArrayNode jobsNode = (ArrayNode) task.get("jobs"); for (JsonNode
		 * jsonNode : jobsNode) { ObjectNode jobnode = (ObjectNode) jsonNode;
		 * String name = jobnode.get("name").asText(); ArrayNode stepsNodes =
		 * (ArrayNode) jobnode.get("steps"); for (int i = 0; i <
		 * stepsNodes.size(); i++) { ObjectNode stepsNode = (ObjectNode)
		 * stepsNodes.get(i); String step_uuid = stepsNode.get("uuid").asText();
		 * String steps_url = "odata/steps?uuid=" + step_uuid; ArrayNode steps =
		 * amsRestService.getList(null, null, steps_url); ObjectNode step =
		 * null; if (steps != null && steps.size() > 0) { step = (ObjectNode)
		 * steps.get(0); } String script_status = step.get("status").asText();
		 * if ("2".equals(script_status)) { String script_retnum =
		 * step.get("retnum").asText(); if ("0".equals(script_retnum)) { // 成功
		 * request.setAttribute((name + "_" + i).replace('-', '_').replace('.',
		 * '_'), "成功"); // System.out.println((name + "_" + i).replace('-', //
		 * '_').replace('.', '_')); map.put((name + "_" + i).replace('-',
		 * '_').replace('.', '_'), "成功");
		 * 
		 * } else { // 失败 request.setAttribute((name + "_" + i).replace('-',
		 * '_').replace('.', '_'), "失败"); map.put((name + "_" + i).replace('-',
		 * '_').replace('.', '_'), "失败"); } } else if
		 * ("1".equals(script_status)) { request.setAttribute((name + "_" +
		 * i).replace('-', '_').replace('.', '_'), "执行中"); map.put((name + "_" +
		 * i).replace('-', '_').replace('.', '_'), "执行中"); } else { // 未执行
		 * request.setAttribute((name + "_" + i).replace('-', '_').replace('.',
		 * '_'), "未开始"); map.put((name + "_" + i).replace('-', '_').replace('.',
		 * '_'), "未开始"); } String script_addr =
		 * step.get("node").get("addr").asText(); request.setAttribute((name +
		 * "_addr_" + i).replace('-', '_').replace('.', '_'), script_addr); //
		 * System.out.println((name + "_addr_" + i).replace('-', //
		 * '_').replace('.', '_')); map.put((name + "_addr_" + i).replace('-',
		 * '_').replace('.', '_'), script_addr);
		 * 
		 * } } }
		 * 
		 * return "instance_linux_was_standalone_log_details";
		 * 
		 * }
		 */

	/*	*//**
			 * db2 单节点日志
			 * 
			 * @throws OPSTBaseException
			 * 
			 *//*
			 * public String getDb2StandAloneInfoDetail(HttpServletRequest
			 * request, HttpSession session) throws OPSTBaseException {
			 * 
			 * String uuid = request.getParameter("uuid"); // String
			 * serid=request.getParameter("serids"); ArrayNode pvcclusters =
			 * amsRestService.getList(null, null, "odata/pvcclusters?uuid=" +
			 * uuid); ObjectNode cluster = null; if (pvcclusters != null &&
			 * pvcclusters.size() > 0) { cluster = (ObjectNode)
			 * pvcclusters.get(0); } List<String> serIds = new
			 * ArrayList<String>(); if (cluster != null && cluster.get("nodes")
			 * != null) { ArrayNode nodes = (ArrayNode) cluster.get("nodes");
			 * for (JsonNode jn : nodes) { serIds.add(jn.get("pvcid") == null ?
			 * "" : jn.get("pvcid").asText()); } } if (cluster != null &&
			 * cluster.get("options") != null) { ObjectNode options =
			 * (ObjectNode) cluster.get("options");
			 * 
			 * request.setAttribute("hostname",
			 * options.get("hostname").asText()); // 单节点IP
			 * request.setAttribute("ip", options.get("ip").asText());
			 * request.setAttribute("bootip", options.get("bootip").asText());
			 * request.setAttribute("hostId", options.get("hostId").asText());
			 * // VG request.setAttribute("vgdb2home",
			 * options.get("vgdb2home").asText());
			 * request.setAttribute("vgdb2log",
			 * options.get("vgdb2log").asText());
			 * request.setAttribute("vgdb2archlog",
			 * options.get("vgdb2archlog").asText());
			 * request.setAttribute("vgdataspace",
			 * options.get("vgdataspace").asText());
			 * 
			 * // PV request.setAttribute("db2homepv",
			 * options.get("db2homepv").asText());
			 * request.setAttribute("db2logpv",
			 * options.get("db2logpv").asText());
			 * request.setAttribute("db2archlogpv",
			 * options.get("db2archlogpv").asText());
			 * request.setAttribute("dataspacepv",
			 * options.get("dataspacepv").asText()); // VG创建方式
			 * request.setAttribute("db2homemode",
			 * options.get("db2homemode").asText());
			 * request.setAttribute("db2logmode",
			 * options.get("db2logmode").asText());
			 * request.setAttribute("db2archlogmode",
			 * options.get("db2archlogmode").asText());
			 * request.setAttribute("dataspacemode",
			 * options.get("dataspacemode").asText()); // NFS BEGIN -->
			 * request.setAttribute("nfsON", options.get("nfsON").asText());
			 * request.setAttribute("ison", "yes");
			 * request.setAttribute("nfsIP1", options.get("nfsIP1").asText());
			 * request.setAttribute("nfsSPoint1",
			 * options.get("nfsSPoint1").asText());
			 * request.setAttribute("nfsCPoint1",
			 * options.get("nfsCPoint1").asText());
			 * 
			 * request.setAttribute("nfsIP2", options.get("nfsIP2").asText());
			 * request.setAttribute("nfsSPoint2",
			 * options.get("nfsSPoint2").asText());
			 * request.setAttribute("nfsCPoint2",
			 * options.get("nfsCPoint2").asText());
			 * 
			 * request.setAttribute("nfsIP3", options.get("nfsIP3").asText());
			 * request.setAttribute("nfsSPoint3",
			 * options.get("nfsSPoint3").asText());
			 * request.setAttribute("nfsCPoint3",
			 * options.get("nfsCPoint3").asText());
			 * 
			 * request.setAttribute("nfsIP4", options.get("nfsIP4").asText());
			 * request.setAttribute("nfsSPoint4",
			 * options.get("nfsSPoint4").asText());
			 * request.setAttribute("nfsCPoint4",
			 * options.get("nfsCPoint4").asText());
			 * 
			 * request.setAttribute("nfsIP5", options.get("nfsIP5").asText());
			 * request.setAttribute("nfsSPoint5",
			 * options.get("nfsSPoint5").asText());
			 * request.setAttribute("nfsCPoint5",
			 * options.get("nfsCPoint5").asText());
			 * 
			 * request.setAttribute("nfsIP6", options.get("nfsIP6").asText());
			 * request.setAttribute("nfsSPoint6",
			 * options.get("nfsSPoint6").asText());
			 * request.setAttribute("nfsCPoint6",
			 * options.get("nfsCPoint6").asText());
			 * 
			 * request.setAttribute("nfsIP7", options.get("nfsIP7").asText());
			 * request.setAttribute("nfsSPoint7",
			 * options.get("nfsSPoint7").asText());
			 * request.setAttribute("nfsCPoint7",
			 * options.get("nfsCPoint7").asText());
			 * 
			 * request.setAttribute("nfsIP8", options.get("nfsIP8").asText());
			 * request.setAttribute("nfsSPoint8",
			 * options.get("nfsSPoint8").asText());
			 * request.setAttribute("nfsCPoint8",
			 * options.get("nfsCPoint8").asText());
			 * 
			 * request.setAttribute("nfsIP9", options.get("nfsIP9").asText());
			 * request.setAttribute("nfsSPoint9",
			 * options.get("nfsSPoint9").asText());
			 * request.setAttribute("nfsCPoint9",
			 * options.get("nfsCPoint9").asText());
			 * 
			 * request.setAttribute("nfsIP10", options.get("nfsIP10").asText());
			 * request.setAttribute("nfsSPoint10",
			 * options.get("nfsSPoint10").asText());
			 * request.setAttribute("nfsCPoint10",
			 * options.get("nfsCPoint10").asText());
			 * 
			 * request.setAttribute("nfsIP11", options.get("nfsIP11").asText());
			 * request.setAttribute("nfsSPoint11",
			 * options.get("nfsSPoint11").asText());
			 * request.setAttribute("nfsCPoint11",
			 * options.get("nfsCPoint11").asText());
			 * 
			 * request.setAttribute("nfsIP12", options.get("nfsIP12").asText());
			 * request.setAttribute("nfsSPoint12",
			 * options.get("nfsSPoint12").asText());
			 * request.setAttribute("nfsCPoint12",
			 * options.get("nfsCPoint12").asText());
			 * 
			 * request.setAttribute("nfsIP13", options.get("nfsIP13").asText());
			 * request.setAttribute("nfsSPoint13",
			 * options.get("nfsSPoint13").asText());
			 * request.setAttribute("nfsCPoint13",
			 * options.get("nfsCPoint13").asText());
			 * 
			 * request.setAttribute("nfsIP14", options.get("nfsIP14").asText());
			 * request.setAttribute("nfsSPoint14",
			 * options.get("nfsSPoint14").asText());
			 * request.setAttribute("nfsCPoint14",
			 * options.get("nfsCPoint14").asText());
			 * 
			 * request.setAttribute("nfsIP15", options.get("nfsIP15").asText());
			 * request.setAttribute("nfsSPoint15",
			 * options.get("nfsSPoint15").asText());
			 * request.setAttribute("nfsCPoint15",
			 * options.get("nfsCPoint15").asText());
			 * 
			 * // DB2信息 基本信息 request.setAttribute("db2_version",
			 * options.get("db2_version").asText());
			 * request.setAttribute("db2_db2base",
			 * options.get("db2_db2base").asText());
			 * request.setAttribute("db2_dbpath",
			 * options.get("db2_dbpath").asText());
			 * request.setAttribute("db2_db2insusr",
			 * options.get("db2_db2insusr").asText());
			 * request.setAttribute("db2_svcename",
			 * options.get("db2_svcename").asText());
			 * request.setAttribute("db2_dbname",
			 * options.get("db2_dbname").asText());
			 * request.setAttribute("db2_codeset",
			 * options.get("db2_codeset").asText());
			 * request.setAttribute("db2_dbdatapath",
			 * options.get("db2_dbdatapath").asText());
			 * 
			 * // DB2信息 实例高级属性 request.setAttribute("db2_db2insgrp",
			 * options.get("db2_db2insgrp").asText());
			 * request.setAttribute("db2_db2fenusr",
			 * options.get("db2_db2fenusr").asText());
			 * request.setAttribute("db2_db2fengrp",
			 * options.get("db2_db2fengrp").asText());
			 * request.setAttribute("db2_db2comm",
			 * options.get("db2_db2comm").asText());
			 * request.setAttribute("db2_db2codepage",
			 * options.get("db2_db2codepage").asText());
			 * request.setAttribute("db2_initagents",
			 * options.get("db2_initagents").asText());
			 * request.setAttribute("db2_poolagents",
			 * options.get("db2_poolagents").asText());
			 * request.setAttribute("db2_max_coordagents",
			 * options.get("db2_max_coordagents").asText());
			 * request.setAttribute("db2_max_connectings",
			 * options.get("db2_max_connectings").asText());
			 * request.setAttribute("db2_diagsize",
			 * options.get("db2_diagsize").asText());
			 * request.setAttribute("db2_mon_buf",
			 * options.get("db2_mon_buf").asText());
			 * request.setAttribute("db2_mon_lock",
			 * options.get("db2_mon_lock").asText());
			 * request.setAttribute("db2_mon_sort",
			 * options.get("db2_mon_sort").asText());
			 * request.setAttribute("db2_mon_stmt",
			 * options.get("db2_mon_stmt").asText());
			 * request.setAttribute("db2_mon_table",
			 * options.get("db2_mon_table").asText());
			 * request.setAttribute("db2_mon_uow",
			 * options.get("db2_mon_uow").asText());
			 * request.setAttribute("db2_health_mon",
			 * options.get("db2_health_mon").asText());
			 * request.setAttribute("db2_mon_heap",
			 * options.get("db2_mon_heap").asText()); // DB2信息 实例高级属性
			 * request.setAttribute("db2_db2log",
			 * options.get("db2_db2log").asText());
			 * request.setAttribute("db2_logarchpath",
			 * options.get("db2_logarchpath").asText());
			 * request.setAttribute("db2_backuppath",
			 * options.get("db2_backuppath").asText());
			 * request.setAttribute("db2_stmm",
			 * options.get("db2_stmm").asText());
			 * request.setAttribute("db2_locklist",
			 * options.get("db2_locklist").asText());
			 * request.setAttribute("db2_maxlocks",
			 * options.get("db2_maxlocks").asText());
			 * request.setAttribute("db2_locktimeout",
			 * options.get("db2_locktimeout").asText());
			 * request.setAttribute("db2_sortheap",
			 * options.get("db2_sortheap").asText());
			 * request.setAttribute("db2_logfilesize",
			 * options.get("db2_logfilesize").asText());
			 * request.setAttribute("db2_logprimary",
			 * options.get("db2_logprimary").asText());
			 * request.setAttribute("db2_logsecond",
			 * options.get("db2_logsecond").asText());
			 * request.setAttribute("db2_logbuff",
			 * options.get("db2_logbuff").asText());
			 * request.setAttribute("db2_softmax",
			 * options.get("db2_softmax").asText());
			 * request.setAttribute("db2_trackmod",
			 * options.get("db2_trackmod").asText());
			 * request.setAttribute("db2_pagesize",
			 * options.get("db2_pagesize").asText()); } ArrayNode lists =
			 * amsRestService.getList(null, null, "odata/servers");
			 * System.out.println(lists);
			 * 
			 * List<AddHostBean> lahb = new ArrayList<AddHostBean>(); for
			 * (JsonNode js : lists) { AddHostBean ahb = new AddHostBean();
			 * ahb.setIP(js.get("IP").asText() != null ? js.get("IP").asText() :
			 * ""); ahb.setName(js.get("Name").asText() != null ?
			 * js.get("Name").asText() : ""); ahb.setOS(js.get("OS").asText() !=
			 * null ? js.get("OS").asText() : "");
			 * ahb.setPassword(js.get("Password").asText() != null ?
			 * js.get("Password").asText() : "");
			 * ahb.setUserID(js.get("UserID").asText() != null ?
			 * js.get("UserID").asText() : "");
			 * ahb.setStatus(js.get("Status").asText() != null ?
			 * js.get("Status").asText() : "");
			 * ahb.set_id(js.get("_id").asText() != null ?
			 * js.get("_id").asText() : "");
			 * ahb.setHConf(js.get("HConf").asText() != null ?
			 * js.get("HConf").asText() : "");
			 * ahb.setHVisor(js.get("HVisor").asText() != null ?
			 * js.get("HVisor").asText() : ""); lahb.add(ahb); }
			 * Collections.sort(lahb); System.out.println(lahb);
			 * 
			 * // request.setAttribute("serId", serid); //
			 * request.setAttribute("total", lahb.size()); List<AddHostBean>
			 * listDetial = new ArrayList<AddHostBean>();
			 * 
			 * for (int i = 0; i < lahb.size(); i++) { String serverId =
			 * lahb.get(i).get_id(); for (int j = 0; j < serIds.size(); j++) {
			 * if (serverId.equals(serIds.get(j))) {
			 * listDetial.add(lahb.get(i)); } } }
			 * request.setAttribute("servers", listDetial);
			 * request.setAttribute("cluster", cluster);
			 * request.setAttribute("mainaddr",
			 * request.getParameter("mainaddr")); request.setAttribute("uuid",
			 * request.getParameter("uuid"));
			 * 
			 * // 查询安装执行日志信息 // 查出运行结果
			 * 目标:http://10.28.0.235:3000/odata/tasks?uuid=... 参数uuid String
			 * table = "odata/tasks?uuid="; String url = table + uuid; ArrayNode
			 * tasks = amsRestService.getList(null, null, url); ObjectNode task
			 * = null; Map map = new TreeMap(); if (tasks != null &&
			 * tasks.size() > 0) { task = (ObjectNode) tasks.get(0); // total
			 * String total = task.get("total").asText(); // steps String
			 * stepsNb = task.get("steps").asText(); // status String status =
			 * task.get("status").asText(); String progress = stepsNb + " / " +
			 * total; map.put("progress", progress);
			 * request.setAttribute("progress", progress); String percent =
			 * FormatUtil.getPercent(Integer.parseInt(stepsNb),
			 * Integer.parseInt(total)); map.put("percent", percent);
			 * request.setAttribute("percent", percent); map.put("status",
			 * status); request.setAttribute("status", status);
			 * 
			 * ArrayNode jobsNode = (ArrayNode) task.get("jobs"); for (JsonNode
			 * jsonNode : jobsNode) { ObjectNode jobnode = (ObjectNode)
			 * jsonNode; String name = jobnode.get("name").asText(); ArrayNode
			 * stepsNodes = (ArrayNode) jobnode.get("steps"); for (int i = 0; i
			 * < stepsNodes.size(); i++) { ObjectNode stepsNode = (ObjectNode)
			 * stepsNodes.get(i); String step_uuid =
			 * stepsNode.get("uuid").asText(); String steps_url =
			 * "odata/steps?uuid=" + step_uuid; ArrayNode steps =
			 * amsRestService.getList(null, null, steps_url); ObjectNode step =
			 * null; if (steps != null && steps.size() > 0) { step =
			 * (ObjectNode) steps.get(0); } String script_status =
			 * step.get("status").asText(); if ("2".equals(script_status)) {
			 * String script_retnum = step.get("retnum").asText(); if
			 * ("0".equals(script_retnum)) { // 成功 request.setAttribute((name +
			 * "_" + i).replace('-', '_').replace('.', '_'), "成功");
			 * map.put((name + "_" + i).replace('-', '_').replace('.', '_'),
			 * "成功");
			 * 
			 * } else { // 失败 request.setAttribute((name + "_" + i).replace('-',
			 * '_').replace('.', '_'), "失败"); map.put((name + "_" +
			 * i).replace('-', '_').replace('.', '_'), "失败"); } } else if
			 * ("1".equals(script_status)) { request.setAttribute((name + "_" +
			 * i).replace('-', '_').replace('.', '_'), "执行中"); map.put((name +
			 * "_" + i).replace('-', '_').replace('.', '_'), "执行中"); } else { //
			 * 未执行 request.setAttribute((name + "_" + i).replace('-',
			 * '_').replace('.', '_'), "未开始"); map.put((name + "_" +
			 * i).replace('-', '_').replace('.', '_'), "未开始"); } String
			 * script_addr = step.get("node").get("addr").asText();
			 * request.setAttribute((name + "_addr_" + i).replace('-',
			 * '_').replace('.', '_'), script_addr); map.put((name + "_addr_" +
			 * i).replace('-', '_').replace('.', '_'), script_addr);
			 * 
			 * } } } return "instance_aix_db2_standalone_log_details"; }
			 */

	/**
	 * @Title: getLogInfoDetial
	 * @Description:查询所有日志详细
	 * @param @param
	 *            request
	 * @param @param
	 *            session
	 * @param @return
	 * @param @throws
	 *            Exception
	 * @author LiangRui
	 * @throws @Time
	 *             2015年6月16日 下午7:23:18
	 */
	@RequestMapping("/getLogInfoDetial")
	public String getLogInfoDetial(HttpServletRequest request, HttpSession session) throws Exception {

		String type = request.getParameter("type");

		// return "forward:"+"/get"+type+"LogInfoDetail"; 将会被启动的代码
		// int n = Integer.valueOf(num);
		if (type.equalsIgnoreCase("db2")) // db2 1表示单节点
		{
			// return getDb2StandAloneInfoDetail(request, session);
			return "forward:" + "/getdb2LogInfoDetial";
		} 
		else if (type.equalsIgnoreCase("was")) // was 单节点
		{
			return "forward:" + "/getwasLogInfoDetial";
			// return getWasStandAloneInfoDetail(request, session);
		} 
		else if (type.equalsIgnoreCase("WAS Cluster")) 
		{
			// return getWasClusterInfoDetail(request, session);// was cluster
			return "forward:" + "/getwasclusterLogInfoDetial";
		} 
		else if (type.equalsIgnoreCase("mq")) 
		{
			return "forward:" + "/getmqLogInfoDetial";
		} 
		else if (type.equalsIgnoreCase("MQ Cluster")) 
		{
			return "forward:" + "/getmqclusterLogInfoDetial";
		} 
		else if (type.equalsIgnoreCase("db2ha")) // 2表示DB2 HA
		{
			return "forward:" + "/getdb2haLogInfoDetial";
			/*
			 * String uuid = request.getParameter("uuid"); ArrayNode pvcclusters
			 * = amsRestService.getList(null, null, "odata/pvcclusters?uuid=" +
			 * uuid); ObjectNode cluster = null; if (pvcclusters != null &&
			 * pvcclusters.size() > 0) { cluster = (ObjectNode)
			 * pvcclusters.get(0); } List<String> serIds = new
			 * ArrayList<String>(); if (cluster != null && cluster.get("nodes")
			 * != null) { ArrayNode nodes = (ArrayNode) cluster.get("nodes");
			 * for (JsonNode jn : nodes) { serIds.add(jn.get("pvcid") == null ?
			 * "" : jn.get("pvcid").asText()); } } if (cluster != null &&
			 * cluster.get("options") != null) { ObjectNode options =
			 * (ObjectNode) cluster.get("options");
			 * 
			 * // -------基本------ // ha_clusterName
			 * request.setAttribute("ha_clusterName",
			 * options.get("ha_clusterName").asText()); // ha_RGNmae
			 * request.setAttribute("ha_RGNmae",
			 * options.get("ha_RGNmae").asText()); // ha_ASName
			 * request.setAttribute("ha_ASName",
			 * options.get("ha_ASName").asText()); // ha_primaryNode
			 * request.setAttribute("ha_primaryNode",
			 * options.get("ha_primaryNode").asText()); // --------IP----- //
			 * ha_ip1 request.setAttribute("ha_ip1",
			 * options.get("ha_ip1").asText()); // ha_ip2
			 * request.setAttribute("ha_ip2", options.get("ha_ip2").asText());
			 * // ha_bootip1 request.setAttribute("ha_bootip1",
			 * options.get("ha_bootip1").asText()); // ha_bootip2
			 * request.setAttribute("ha_bootip2",
			 * options.get("ha_bootip2").asText()); // ha_svcip
			 * request.setAttribute("ha_svcip",
			 * options.get("ha_svcip").asText());
			 * 
			 * // --------主机别名----- // ha_hostname1
			 * request.setAttribute("ha_hostname1",
			 * options.get("ha_hostname1").asText()); // ha_hostname2
			 * request.setAttribute("ha_hostname2",
			 * options.get("ha_hostname2").asText()); // ha_bootalias1
			 * request.setAttribute("ha_bootalias1",
			 * options.get("ha_bootalias1").asText()); // ha_bootalias2
			 * request.setAttribute("ha_bootalias2",
			 * options.get("ha_bootalias2").asText()); // ha_svcalias
			 * request.setAttribute("ha_svcalias",
			 * options.get("ha_svcalias").asText());
			 * 
			 * if (options.get("ha_primaryNode") != null &&
			 * options.get("ha_hostname1") != null &&
			 * options.get("ha_primaryNode").equals(options.get("ha_hostname1"))
			 * ) { request.setAttribute("ha_subNode",
			 * options.get("ha_hostname2").asText()); } else {
			 * request.setAttribute("ha_subNode",
			 * options.get("ha_hostname1").asText()); }
			 * 
			 * // ---------VG------ // ha_vgdb2home
			 * request.setAttribute("ha_vgdb2home",
			 * options.get("ha_vgdb2home").asText()); // ha_vgdb2log
			 * request.setAttribute("ha_vgdb2log",
			 * options.get("ha_vgdb2log").asText()); // ha_vgdb2archlog
			 * request.setAttribute("ha_vgdb2archlog",
			 * options.get("ha_vgdb2archlog").asText()); // ha_vgdataspace1
			 * request.setAttribute("ha_vgdataspace",
			 * options.get("ha_vgdataspace").asText()); // ha_vgcaap
			 * request.setAttribute("ha_vgcaap",
			 * options.get("ha_vgcaap").asText());
			 * 
			 * // -------PV----- // ha_db2homepv
			 * request.setAttribute("ha_db2homepv",
			 * options.get("ha_db2homepv").asText()); // ha_db2logpv
			 * request.setAttribute("ha_db2logpv",
			 * options.get("ha_db2logpv").asText()); // ha_db2archlogpv
			 * request.setAttribute("ha_db2archlogpv",
			 * options.get("ha_db2archlogpv").asText()); // ha_dataspace1pv
			 * request.setAttribute("ha_dataspacepv",
			 * options.get("ha_dataspacepv").asText()); // ha_caappv
			 * request.setAttribute("ha_caappv",
			 * options.get("ha_caappv").asText());
			 * 
			 * // ------VG创建方式----- // ha_db2homemode
			 * request.setAttribute("ha_db2homemode",
			 * options.get("ha_db2homemode").asText()); // ha_db2logmode
			 * request.setAttribute("ha_db2logmode",
			 * options.get("ha_db2logmode").asText()); // ha_db2archlogmode
			 * request.setAttribute("ha_db2archlogmode",
			 * options.get("ha_db2archlogmode").asText()); // ha_dataspace1mode
			 * request.setAttribute("ha_dataspacemode",
			 * options.get("ha_dataspacemode").asText()); // ha_caapmode
			 * request.setAttribute("ha_caapmode",
			 * options.get("ha_caapmode").asText());
			 * 
			 * // ------HA切换配置----- // ha_startpolicy String ha_startpolicy =
			 * ""; if (options.get("ha_startpolicy").asText().equals("OHN")) {
			 * ha_startpolicy = "Online On Home Node Only"; } else if
			 * (options.get("ha_startpolicy").asText().equals("OFAN")) {
			 * ha_startpolicy = "Online On First Available Node"; } else if
			 * (options.get("ha_startpolicy").asText().equals("OUDP")) {
			 * ha_startpolicy = "Online Using Node Distribution Policy"; } else
			 * if (options.get("ha_startpolicy").asText().equals("OAAN")) {
			 * ha_startpolicy = "Online On All Available Nodes"; }
			 * request.setAttribute("ha_startpolicy", ha_startpolicy);
			 * 
			 * // ha_fopolicy String ha_fopolicy = ""; if
			 * (options.get("ha_fopolicy").asText().equals("FNPN")) {
			 * ha_fopolicy = "Fallover To Next Priority Node In The List"; }
			 * else if (options.get("ha_fopolicy").asText().equals("FUDNP")) {
			 * ha_fopolicy = "Fallover Using Dynamic Node Priority"; } else if
			 * (options.get("ha_fopolicy").asText().equals("BO")) { ha_fopolicy
			 * = "Bring Offline (On Error Node Only)"; }
			 * request.setAttribute("ha_fopolicy", ha_fopolicy);
			 * 
			 * // ha_fbpolicy String ha_fbpolicy = ""; if
			 * (options.get("ha_fbpolicy").asText().equals("FBHPN")) {
			 * ha_fbpolicy = "Fallback To Higher Priority Node In The List"; }
			 * else if (options.get("ha_fbpolicy").asText().equals("NFB")) {
			 * ha_fbpolicy = "Never Fallback"; }
			 * request.setAttribute("ha_fbpolicy", ha_fbpolicy);
			 * 
			 * // --------NFS--------- // ha_nfsON
			 * request.setAttribute("ha_nfsON",
			 * options.get("ha_nfsON").asText()); request.setAttribute("ison",
			 * "yes"); // ha_nfsIP1 request.setAttribute("ha_nfsIP1",
			 * options.get("ha_nfsIP1").asText()); // ha_nfsSPoint1
			 * request.setAttribute("ha_nfsSPoint1",
			 * options.get("ha_nfsSPoint1").asText()); // ha_nfsCPoint1
			 * request.setAttribute("ha_nfsCPoint1",
			 * options.get("ha_nfsCPoint1").asText()); // ha_nfsIP2
			 * request.setAttribute("ha_nfsIP2",
			 * options.get("ha_nfsIP2").asText()); // ha_nfsSPoint2
			 * request.setAttribute("ha_nfsSPoint2",
			 * options.get("ha_nfsSPoint2").asText()); // ha_nfsCPoint2
			 * request.setAttribute("ha_nfsCPoint2",
			 * options.get("ha_nfsCPoint2").asText()); // ha_nfsIP3
			 * request.setAttribute("ha_nfsIP3",
			 * options.get("ha_nfsIP3").asText()); // ha_nfsSPoint3
			 * request.setAttribute("ha_nfsSPoint3",
			 * options.get("ha_nfsSPoint3").asText()); // ha_nfsCPoint3
			 * request.setAttribute("ha_nfsCPoint3",
			 * options.get("ha_nfsCPoint3").asText()); // ha_nfsIP4
			 * request.setAttribute("ha_nfsIP4",
			 * options.get("ha_nfsIP4").asText()); // ha_nfsSPoint4
			 * request.setAttribute("ha_nfsSPoint4",
			 * options.get("ha_nfsSPoint4").asText()); // ha_nfsCPoint4
			 * request.setAttribute("ha_nfsCPoint4",
			 * options.get("ha_nfsCPoint4").asText()); // ha_nfsIP5
			 * request.setAttribute("ha_nfsIP5",
			 * options.get("ha_nfsIP5").asText()); // ha_nfsSPoint5
			 * request.setAttribute("ha_nfsSPoint5",
			 * options.get("ha_nfsSPoint5").asText()); // ha_nfsCPoint5
			 * request.setAttribute("ha_nfsCPoint5",
			 * options.get("ha_nfsCPoint5").asText()); // ha_nfsIP6
			 * request.setAttribute("ha_nfsIP6",
			 * options.get("ha_nfsIP6").asText()); // ha_nfsSPoint6
			 * request.setAttribute("ha_nfsSPoint6",
			 * options.get("ha_nfsSPoint6").asText()); // ha_nfsCPoint6
			 * request.setAttribute("ha_nfsCPoint6",
			 * options.get("ha_nfsCPoint6").asText()); // ha_nfsIP7
			 * request.setAttribute("ha_nfsIP7",
			 * options.get("ha_nfsIP7").asText()); // ha_nfsSPoint7
			 * request.setAttribute("ha_nfsSPoint7",
			 * options.get("ha_nfsSPoint7").asText()); // ha_nfsCPoint7
			 * request.setAttribute("ha_nfsCPoint7",
			 * options.get("ha_nfsCPoint7").asText()); // ha_nfsIP8
			 * request.setAttribute("ha_nfsIP8",
			 * options.get("ha_nfsIP8").asText()); // ha_nfsSPoint8
			 * request.setAttribute("ha_nfsSPoint8",
			 * options.get("ha_nfsSPoint8").asText()); // ha_nfsCPoint8
			 * request.setAttribute("ha_nfsCPoint8",
			 * options.get("ha_nfsCPoint8").asText()); // ha_nfsIP9
			 * request.setAttribute("ha_nfsIP9",
			 * options.get("ha_nfsIP9").asText()); // ha_nfsSPoint9
			 * request.setAttribute("ha_nfsSPoint9",
			 * options.get("ha_nfsSPoint9").asText()); // ha_nfsCPoint9
			 * request.setAttribute("ha_nfsCPoint9",
			 * options.get("ha_nfsCPoint9").asText()); // ha_nfsIP10
			 * request.setAttribute("ha_nfsIP10",
			 * options.get("ha_nfsIP10").asText()); // ha_nfsSPoint10
			 * request.setAttribute("ha_nfsSPoint10",
			 * options.get("ha_nfsSPoint10").asText()); // ha_nfsCPoint10
			 * request.setAttribute("ha_nfsCPoint10",
			 * options.get("ha_nfsCPoint10").asText()); // ha_nfsIP11
			 * request.setAttribute("ha_nfsIP11",
			 * options.get("ha_nfsIP11").asText()); // ha_nfsSPoint11
			 * request.setAttribute("ha_nfsSPoint11",
			 * options.get("ha_nfsSPoint11").asText()); // ha_nfsCPoint11
			 * request.setAttribute("ha_nfsCPoint11",
			 * options.get("ha_nfsCPoint11").asText()); // ha_nfsIP12
			 * request.setAttribute("ha_nfsIP12",
			 * options.get("ha_nfsIP12").asText()); // ha_nfsSPoint12
			 * request.setAttribute("ha_nfsSPoint12",
			 * options.get("ha_nfsSPoint12").asText()); // ha_nfsCPoint12
			 * request.setAttribute("ha_nfsCPoint12",
			 * options.get("ha_nfsCPoint12").asText()); // ha_nfsIP13
			 * request.setAttribute("ha_nfsIP13",
			 * options.get("ha_nfsIP13").asText()); // ha_nfsSPoint13
			 * request.setAttribute("ha_nfsSPoint13",
			 * options.get("ha_nfsSPoint13").asText()); // ha_nfsCPoint13
			 * request.setAttribute("ha_nfsCPoint13",
			 * options.get("ha_nfsCPoint13").asText()); // ha_nfsIP14
			 * request.setAttribute("ha_nfsIP14",
			 * options.get("ha_nfsIP14").asText()); // ha_nfsSPoint14
			 * request.setAttribute("ha_nfsSPoint14",
			 * options.get("ha_nfsSPoint14").asText()); // ha_nfsCPoint14
			 * request.setAttribute("ha_nfsCPoint14",
			 * options.get("ha_nfsCPoint14").asText()); // ha_nfsIP15
			 * request.setAttribute("ha_nfsIP15",
			 * options.get("ha_nfsIP15").asText()); // ha_nfsSPoint15
			 * request.setAttribute("ha_nfsSPoint15",
			 * options.get("ha_nfsSPoint15").asText()); // ha_nfsCPoint15
			 * request.setAttribute("ha_nfsCPoint15",
			 * options.get("ha_nfsCPoint15").asText());
			 * 
			 * // -------基本信息------ // db2_version
			 * request.setAttribute("db2_version",
			 * options.get("db2_version").asText()); // db2_db2base
			 * request.setAttribute("db2_db2base",
			 * options.get("db2_db2base").asText()); // db2_dbpath
			 * request.setAttribute("db2_dbpath",
			 * options.get("db2_dbpath").asText()); // db2_svcename
			 * request.setAttribute("db2_svcename",
			 * options.get("db2_svcename").asText()); // db2_db2insusr
			 * request.setAttribute("db2_db2insusr",
			 * options.get("db2_db2insusr").asText()); // db2_dbname
			 * request.setAttribute("db2_dbname",
			 * options.get("db2_dbname").asText()); // db2_codeset
			 * request.setAttribute("db2_codeset",
			 * options.get("db2_codeset").asText()); // db2_dbdatapath
			 * request.setAttribute("db2_dbdatapath",
			 * options.get("db2_dbdatapath").asText()); // ------实例高级属性-------
			 * // db2_db2insgrp request.setAttribute("db2_db2insgrp",
			 * options.get("db2_db2insgrp").asText()); // db2_db2fenusr
			 * request.setAttribute("db2_db2fenusr",
			 * options.get("db2_db2fenusr").asText()); // db2_db2fengrp
			 * request.setAttribute("db2_db2fengrp",
			 * options.get("db2_db2fengrp").asText()); // db2_db2comm
			 * request.setAttribute("db2_db2comm",
			 * options.get("db2_db2comm").asText()); // db2_db2codepage
			 * request.setAttribute("db2_db2codepage",
			 * options.get("db2_db2codepage").asText()); // db2_initagents
			 * request.setAttribute("db2_initagents",
			 * options.get("db2_initagents").asText()); // db2_max_coordagents
			 * request.setAttribute("db2_max_coordagents",
			 * options.get("db2_max_coordagents").asText()); //
			 * db2_max_connectings request.setAttribute("db2_max_connectings",
			 * options.get("db2_max_connectings").asText()); // db2_poolagents
			 * request.setAttribute("db2_poolagents",
			 * options.get("db2_poolagents").asText()); // db2_diagsize
			 * request.setAttribute("db2_diagsize",
			 * options.get("db2_diagsize").asText()); // db2_mon_buf
			 * request.setAttribute("db2_mon_buf",
			 * options.get("db2_mon_buf").asText()); // db2_mon_lock
			 * request.setAttribute("db2_mon_lock",
			 * options.get("db2_mon_lock").asText()); // db2_mon_sort
			 * request.setAttribute("db2_mon_sort",
			 * options.get("db2_mon_sort").asText()); // db2_mon_stmt
			 * request.setAttribute("db2_mon_stmt",
			 * options.get("db2_mon_stmt").asText()); // db2_mon_table
			 * request.setAttribute("db2_mon_table",
			 * options.get("db2_mon_table").asText()); // db2_mon_uow
			 * request.setAttribute("db2_mon_uow",
			 * options.get("db2_mon_uow").asText()); // db2_health_mon
			 * request.setAttribute("db2_health_mon",
			 * options.get("db2_health_mon").asText()); // db2_mon_heap
			 * request.setAttribute("db2_mon_heap",
			 * options.get("db2_mon_heap").asText());
			 * 
			 * // -------数据库高级属性------ // db2_db2log
			 * request.setAttribute("db2_db2log",
			 * options.get("db2_db2log").asText()); // db2_backuppath
			 * request.setAttribute("db2_backuppath",
			 * options.get("db2_backuppath").asText()); // db2_logarchpath
			 * request.setAttribute("db2_logarchpath",
			 * options.get("db2_logarchpath").asText()); // db2_stmm
			 * request.setAttribute("db2_stmm",
			 * options.get("db2_stmm").asText()); // db2_locklist
			 * request.setAttribute("db2_locklist",
			 * options.get("db2_locklist").asText()); // db2_maxlocks
			 * request.setAttribute("db2_maxlocks",
			 * options.get("db2_maxlocks").asText()); // db2_locktimeout
			 * request.setAttribute("db2_locktimeout",
			 * options.get("db2_locktimeout").asText()); // db2_sortheap
			 * request.setAttribute("db2_sortheap",
			 * options.get("db2_sortheap").asText()); // db2_logfilesize
			 * request.setAttribute("db2_logfilesize",
			 * options.get("db2_logfilesize").asText()); // db2_logprimary
			 * request.setAttribute("db2_logprimary",
			 * options.get("db2_logprimary").asText()); // db2_logsecond
			 * request.setAttribute("db2_logsecond",
			 * options.get("db2_logsecond").asText()); // db2_logbuff
			 * request.setAttribute("db2_logbuff",
			 * options.get("db2_logbuff").asText()); // db2_softmax
			 * request.setAttribute("db2_softmax",
			 * options.get("db2_softmax").asText()); // db2_trackmod
			 * request.setAttribute("db2_trackmod",
			 * options.get("db2_trackmod").asText()); // db2_pagesize
			 * request.setAttribute("db2_pagesize",
			 * options.get("db2_pagesize").asText());
			 * 
			 * } // List<ServerBean> listDetial = new ArrayList<ServerBean>();
			 * 
			 * // request.setAttribute("servers", listDetial);
			 * 
			 * // request.setAttribute("flavors", fList); //
			 * request.setAttribute("imageList", imageList);
			 * request.setAttribute("cluster", cluster);
			 * request.setAttribute("mainaddr",
			 * request.getParameter("mainaddr")); request.setAttribute("uuid",
			 * request.getParameter("uuid"));
			 * 
			 * // 查询安装执行日志信息 // 查出运行结果
			 * 目标:http://10.28.0.235:3000/odata/tasks?uuid=... 参数uuid String
			 * table = "odata/tasks?uuid="; String url = table + uuid; ArrayNode
			 * tasks = amsRestService.getList(null, null, url); ObjectNode task
			 * = null; Map map = new TreeMap(); if (tasks != null &&
			 * tasks.size() > 0) { task = (ObjectNode) tasks.get(0); // total
			 * String total = task.get("total").asText(); // steps String
			 * stepsNb = task.get("steps").asText(); // status String status =
			 * task.get("status").asText(); String progress = stepsNb + " / " +
			 * total; map.put("progress", progress);
			 * request.setAttribute("progress", progress); String percent =
			 * FormatUtil.getPercent(Integer.parseInt(stepsNb),
			 * Integer.parseInt(total)); map.put("percent", percent);
			 * request.setAttribute("percent", percent); map.put("status",
			 * status); request.setAttribute("status", status);
			 * 
			 * ArrayNode jobsNode = (ArrayNode) task.get("jobs"); for (JsonNode
			 * jsonNode : jobsNode) { ObjectNode jobnode = (ObjectNode)
			 * jsonNode; String name = jobnode.get("name").asText(); ArrayNode
			 * stepsNodes = (ArrayNode) jobnode.get("steps"); for (int i = 0; i
			 * < stepsNodes.size(); i++) { ObjectNode stepsNode = (ObjectNode)
			 * stepsNodes.get(i); String step_uuid =
			 * stepsNode.get("uuid").asText(); String steps_url =
			 * "odata/steps?uuid=" + step_uuid; ArrayNode steps =
			 * amsRestService.getList(null, null, steps_url); ObjectNode step =
			 * null; if (steps != null && steps.size() > 0) { step =
			 * (ObjectNode) steps.get(0); } String script_status =
			 * step.get("status").asText(); if ("2".equals(script_status)) {
			 * String script_retnum = step.get("retnum").asText(); if
			 * ("0".equals(script_retnum)) { // 成功 request.setAttribute((name +
			 * "_" + i).replace('-', '_').replace('.', '_'), "成功");
			 * map.put((name + "_" + i).replace('-', '_').replace('.', '_'),
			 * "成功");
			 * 
			 * } else { // 失败 request.setAttribute((name + "_" + i).replace('-',
			 * '_').replace('.', '_'), "失败"); map.put((name + "_" +
			 * i).replace('-', '_').replace('.', '_'), "失败"); } } else if
			 * ("1".equals(script_status)) { request.setAttribute((name + "_" +
			 * i).replace('-', '_').replace('.', '_'), "执行中"); map.put((name +
			 * "_" + i).replace('-', '_').replace('.', '_'), "执行中"); } else { //
			 * 未执行 request.setAttribute((name + "_" + i).replace('-',
			 * '_').replace('.', '_'), "未开始"); map.put((name + "_" +
			 * i).replace('-', '_').replace('.', '_'), "未开始"); } String
			 * script_addr = step.get("node").get("addr").asText();
			 * request.setAttribute((name + "_addr_" + i).replace('-',
			 * '_').replace('.', '_'), script_addr); map.put((name + "_addr_" +
			 * i).replace('-', '_').replace('.', '_'), script_addr);
			 * 
			 * } } }
			 * 
			 * System.out.println("map:" + map);
			 * 
			 * return "instance_aix_log_details2"; }
			 */
		}
		return null;
	}

	/**
	 * 获得安装进度
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/nodeInstall")
	public void nodeInstall(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws Exception {

		response.setContentType("text/html;charset=utf-8");

		// response.setContentType("application/json;charset=utf-8");
		response.setCharacterEncoding("utf-8");

		PrintWriter out = response.getWriter();

		String uuid = request.getParameter("uuid");
		ObjectMapper om = new ObjectMapper();
		ObjectNode respJson = om.createObjectNode();
		// 查询安装执行日志信息
		// 查出运行结果 目标:http://10.28.0.235:3000/odata/tasks?uuid=... 参数uuid
		String table = "odata/tasks?uuid=";
		String url = table + uuid;
		ArrayNode tasks = amsRestService.getList(null, null, url);
		ObjectNode task = null;
		Map map = new TreeMap();
		if (tasks != null && tasks.size() > 0) {
			task = (ObjectNode) tasks.get(0);
			// total
			String total = task.get("total").asText();
			// steps
			String stepsNb = task.get("steps").asText();
			// status
			String status = task.get("status").asText();
			String progress = stepsNb + " / " + total;
			map.put("progress", progress);
			respJson.put("progress", progress);
			String percent = FormatUtil.getPercent(Integer.parseInt(stepsNb), Integer.parseInt(total));
			map.put("percent", percent);
			respJson.put("percent", percent);
			map.put("status", status);
			respJson.put("status", status);

			ArrayNode jobsNode = (ArrayNode) task.get("jobs");
			for (JsonNode jsonNode : jobsNode) {
				ObjectNode jobnode = (ObjectNode) jsonNode;
				String name = jobnode.get("name").asText();
				ArrayNode stepsNodes = (ArrayNode) jobnode.get("steps");
				for (int i = 0; i < stepsNodes.size(); i++) {
					ObjectNode stepsNode = (ObjectNode) stepsNodes.get(i);
					String step_uuid = stepsNode.get("uuid").asText();
					String steps_url = "odata/steps?uuid=" + step_uuid;
					ArrayNode steps = amsRestService.getList(null, null, steps_url);
					ObjectNode step = null;
					if (steps != null && steps.size() > 0) {
						step = (ObjectNode) steps.get(0);
					}
					WasClusterLogBean wclb = new WasClusterLogBean();
					String script_status = step.get("status").asText();
					if ("2".equals(script_status)) {
						String script_retnum = step.get("retnum").asText();
						if ("0".equals(script_retnum)) {
							// 成功
							map.put((name + "_" + i).replace('-', '_').replace('.', '_'), "成功");
							respJson.put((name + "_" + i).replace('-', '_').replace('.', '_'), "成功");
							// setWasClusterLogDetail(wclb,name,i,"成功");
						} else {
							// 失败
							map.put((name + "_" + i).replace('-', '_').replace('.', '_'), "失败");
							respJson.put((name + "_" + i).replace('-', '_').replace('.', '_'), "失败");
							// setWasClusterLogDetail(wclb,name,i,"失败");
						}
					} else if ("1".equals(script_status)) {
						map.put((name + "_" + i).replace('-', '_').replace('.', '_'), "执行中");
						respJson.put((name + "_" + i).replace('-', '_').replace('.', '_'), "执行中");
					} else {
						// 未执行
						map.put((name + "_" + i).replace('-', '_').replace('.', '_'), "未开始");
						respJson.put((name + "_" + i).replace('-', '_').replace('.', '_'), "未开始");
					}
					String script_addr = step.get("node").get("addr").asText();
					map.put((name + "_addr_" + i).replace('-', '_').replace('.', '_'), script_addr);
					respJson.put((name + "_addr_" + i).replace('-', '_').replace('.', '_'), script_addr);

				}
			}
		}

		logger.info("map:" + map);
		System.out.println("resp::" + respJson);
		logger.info("resp" + respJson);
		out.print(respJson);
		out.close();
	}

	/**
	 * 针对wascluster获得安装进度
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/wasclusternodeInstall")
	public void wasclusternodeInstall(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws Exception {

		response.setContentType("text/html;charset=utf-8");

		// response.setContentType("application/json;charset=utf-8");
		response.setCharacterEncoding("utf-8");

		PrintWriter out = response.getWriter();

		String uuid = request.getParameter("uuid");
		ObjectMapper om = new ObjectMapper();
		ObjectNode respJson = om.createObjectNode();
		// 查询安装执行日志信息
		// 查出运行结果 目标:http://10.28.0.235:3000/odata/tasks?uuid=... 参数uuid
		String table = "odata/tasks?uuid=";
		String url = table + uuid;
		ArrayNode tasks = amsRestService.getList(null, null, url);
		ObjectNode task = null;
		Map map = new TreeMap();
		List<WasClusterLogBean> wasclusterlist = new ArrayList<WasClusterLogBean>();
		if (tasks != null && tasks.size() > 0) {
			task = (ObjectNode) tasks.get(0);
			// total
			String total = task.get("total").asText();
			// steps
			String stepsNb = task.get("steps").asText();
			// status
			String status = task.get("status").asText();
			String progress = stepsNb + " / " + total;
	//		map.put("progress", progress);
	//		respJson.put("progress", progress);
			String percent = FormatUtil.getPercent(Integer.parseInt(stepsNb), Integer.parseInt(total));
	//		map.put("percent", percent);
	//		respJson.put("percent", percent);
	//		map.put("status", status);
	//		respJson.put("status", status);
			
			WasClusterLogBean wclb = null;

			ArrayNode jobsNode = (ArrayNode) task.get("jobs");
			for (JsonNode jsonNode : jobsNode) {
				ObjectNode jobnode = (ObjectNode) jsonNode;
				String name = jobnode.get("name").asText();
				if (name.equals("make-directory")) {
					wclb = new WasClusterLogBean();
				}
				ArrayNode stepsNodes = (ArrayNode) jobnode.get("steps");
				for (int i = 0; i < stepsNodes.size(); i++) {
					ObjectNode stepsNode = (ObjectNode) stepsNodes.get(i);
					String step_uuid = stepsNode.get("uuid").asText();
					String steps_url = "odata/steps?uuid=" + step_uuid;
					ArrayNode steps = amsRestService.getList(null, null, steps_url);
					ObjectNode step = null;
					if (steps != null && steps.size() > 0) {
						step = (ObjectNode) steps.get(0);
					}
					String script_status = step.get("status").asText();
					if ("2".equals(script_status)) {
						String script_retnum = step.get("retnum").asText();
						if ("0".equals(script_retnum)) {
							// 成功
							// request.setAttribute((name + "_" +
							// i).replace('-', '_').replace('.', '_'), "成功");
							// System.out.println((name + "_" + i).replace('-',
							// '_').replace('.', '_'));
			//				String successstep = (name + "_" + i).replace('-', '_').replace('.', '_');
			//				map.put((name + "_" + i).replace('-', '_').replace('.', '_'), "成功");

							setWasClusterLogDetail(wclb, name, i, "成功");

						} else {
							// 失败
							// request.setAttribute((name + "_" +
							// i).replace('-', '_').replace('.', '_'), "失败");
			//				map.put((name + "_" + i).replace('-', '_').replace('.', '_'), "失败");
							setWasClusterLogDetail(wclb, name, i, "失败");
						}
					} else if ("1".equals(script_status)) {
						// request.setAttribute((name + "_" + i).replace('-',
						// '_').replace('.', '_'), "执行中");
		//				map.put((name + "_" + i).replace('-', '_').replace('.', '_'), "执行中");
						setWasClusterLogDetail(wclb, name, i, "执行中");
					} else {
						// 未执行
						/// request.setAttribute((name + "_" + i).replace('-',
						// '_').replace('.', '_'), "未开始");
			//			map.put((name + "_" + i).replace('-', '_').replace('.', '_'), "未开始");
						setWasClusterLogDetail(wclb, name, i, "未执行");
					}
					String script_addr = step.get("node").get("addr").asText();
					// request.setAttribute((name + "_addr_" + i).replace('-',
					// '_').replace('.', '_'), script_addr);
					// System.out.println((name + "_addr_" + i).replace('-',
					// '_').replace('.', '_'));
			//		map.put((name + "_addr_" + i).replace('-', '_').replace('.', '_'), script_addr);
					setWasClusterLogAddressDetail(wclb, name, i, script_addr);

				}
				if ("start-was".equals(name))
					wasclusterlist.add(wclb);
			}

			
		}
		net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(wasclusterlist);
		logger.info(jsonArray.toString());
		out.print(jsonArray.toString());
		out.close();
	}

	public void setWasClusterLogDetail(WasClusterLogBean wclb, String name, int i, String status) {

		if ("make_directory_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setMake_directory_0(status);
		} else if ("propagate_scripts_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setPropagate_scripts_0(status);
		} else if ("propagate_scripts_1".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setPropagate_scripts_1(status);
		} else if ("propagate_scripts_2".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setPropagate_scripts_2(status);
		} else if ("propagate_scripts_3".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setPropagate_scripts_3(status);
		} else if ("manipulate_was_config_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setManipulate_was_config_0(status);
		} else if ("prepare_chmod_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setPrepare_chmod_0(status);
		} else if ("prepare_set_hostname_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setPrepare_set_hostname_0(status);
		} else if ("download_files_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setDownload_files_0(status);
		} else if ("install_im_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setInstall_im_0(status);
		} else if ("install_was_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setInstall_was_0(status);
		} else if ("update_was_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setUpdate_was_0(status);
		}else if ("install_jdk_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setInstall_jdk_0(status);
		} else if ("build_was_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setBuild_was_0(status);
		} else if ("start_was_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setStart_was_0(status);
		}

	}

	public void setWasClusterLogAddressDetail(WasClusterLogBean wclb, String name, int i, String address) {
		if ("make_directory_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setMake_directory_addr_0(address);
		} else if ("propagate_scripts_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setPropagate_scripts_addr_0(address);
		} else if ("propagate_scripts_addr_1".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setPropagate_scripts_addr_1(address);
		} else if ("propagate_scripts_addr_2".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setPropagate_scripts_addr_2(address);
		} else if ("propagate_scripts_addr_3".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setPropagate_scripts_addr_3(address);
		} else if ("manipulate_was_config_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setManipulate_was_config_addr_0(address);
		} else if ("prepare_chmod_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setPrepare_chmod_addr_0(address);
		} else if ("prepare_set_hostname_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setPrepare_set_hostname_addr_0(address);
		} else if ("download_files_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setDownload_files_addr_0(address);
		} else if ("install_im_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setInstall_im_addr_0(address);
		} else if ("install_was_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setInstall_was_addr_0(address);
		} else if ("update_was_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setUpdate_was_addr_0(address);
		}else if("install_jdk_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setInstall_jdk_addr_0(address);
		}
		else if ("build_was_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setBuild_was_addr_0(address);
		} else if ("start_was_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) {
			wclb.setStart_was_addr_0(address);
		}
	}
	
		
	
	/**
	 * 获得安装进度
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/wasclusterProgressnodeInstall")
	public void wasclusterProgressnodeInstall(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws Exception {

		response.setContentType("text/html;charset=utf-8");

		// response.setContentType("application/json;charset=utf-8");
		response.setCharacterEncoding("utf-8");

		PrintWriter out = response.getWriter();

		String uuid = request.getParameter("uuid");
		ObjectMapper om = new ObjectMapper();
		ObjectNode respJson = om.createObjectNode();
		// 查询安装执行日志信息
		// 查出运行结果 目标:http://10.28.0.235:3000/odata/tasks?uuid=... 参数uuid
		String table = "odata/tasks?uuid=";
		String url = table + uuid;
		ArrayNode tasks = amsRestService.getList(null, null, url);
		ObjectNode task = null;
		Map map = new TreeMap();
		if (tasks != null && tasks.size() > 0) {
			task = (ObjectNode) tasks.get(0);
			// total
			String total = task.get("total").asText();
			// steps
			String stepsNb = task.get("steps").asText();
			// status
			String status = task.get("status").asText();
			String progress = stepsNb + " / " + total;
			map.put("progress", progress);
			respJson.put("progress", progress);
			String percent = FormatUtil.getPercent(Integer.parseInt(stepsNb), Integer.parseInt(total));
			map.put("percent", percent);
			respJson.put("percent", percent);
			map.put("status", status);
			respJson.put("status", status);			
		}

		logger.info("map:" + map);
		System.out.println("resp::" + respJson);
		logger.info("resp" + respJson);
		out.print(respJson);
		out.close();
	}
	
	
	/**
	 * 针对mqcluster获得安装进度
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */	
	@RequestMapping("/mqclusternodeInstall")
	public void mqclusternodeInstall(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws Exception{
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		
		String uuid = request.getParameter("uuid");
		ObjectMapper om = new ObjectMapper();
		ObjectNode respJson = om.createObjectNode();
		
		String table = "odata/tasks?uuid=";
		String url = table + uuid;
		ArrayNode tasks = amsRestService.getList(null, null, url);
		ObjectNode task = null;
		Map map = new TreeMap();
		List<MqClusterLogBean> mqclusterlist = new ArrayList<MqClusterLogBean>();
		if (tasks != null && tasks.size() > 0) 
		{
			task = (ObjectNode) tasks.get(0);
			// total
			String total = task.get("total").asText();
			// steps
			String stepsNb = task.get("steps").asText();
			// status
			String status = task.get("status").asText();
			String progress = stepsNb + " / " + total;
			String percent = FormatUtil.getPercent(Integer.parseInt(stepsNb), Integer.parseInt(total));
			MqClusterLogBean mclb = null;
			ArrayNode jobsNode = (ArrayNode) task.get("jobs");
			for (JsonNode jsonNode : jobsNode)
			{
				ObjectNode jobnode = (ObjectNode) jsonNode;
				String name = jobnode.get("name").asText();
				if (name.equals("make-directory")) 
				{
					mclb = new MqClusterLogBean();
				}
				ArrayNode stepsNodes = (ArrayNode) jobnode.get("steps");
				for (int i = 0; i < stepsNodes.size(); i++)
				{
					ObjectNode stepsNode = (ObjectNode) stepsNodes.get(i);
					String step_uuid = stepsNode.get("uuid").asText();
					String steps_url = "odata/steps?uuid=" + step_uuid;
					ArrayNode steps = amsRestService.getList(null, null, steps_url);
					ObjectNode step = null;
					if (steps != null && steps.size() > 0) 
					{
						step = (ObjectNode) steps.get(0);
					}
					String script_status = step.get("status").asText();
					if ("2".equals(script_status)) 
					{
						String script_retnum = step.get("retnum").asText();
						if ("0".equals(script_retnum)) 
						{
							setMqClusterLogDetail(mclb, name, i, "成功");
						} 
						else 
						{
							setMqClusterLogDetail(mclb, name, i, "失败");
						}
					}
					else if ("1".equals(script_status)) 
					{
						setMqClusterLogDetail(mclb, name, i, "执行中");
					}
					else 
					{
						setMqClusterLogDetail(mclb, name, i, "未执行");
					}
					String script_addr = step.get("node").get("addr").asText();
					setMqClusterLogAddressDetail(mclb, name, i, script_addr);
				}
				if ("start-mq".equals(name))
					mqclusterlist.add(mclb);
			}
		}
		net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(mqclusterlist);
		logger.info(jsonArray.toString());
		out.print(jsonArray.toString());
		out.close();
	}
	
	public void setMqClusterLogDetail(MqClusterLogBean mclb, String name, int i, String status) 
	{
		if ("make_directory_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setMake_directory_0(status);
		} 
		else if ("propagate_host_script_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setPropagate_host_script_0(status);
		} 
		else if ("propagate_prepare_script_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setPropagate_prepare_script_0(status);
		} 
		else if ("propagate_install_script_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setPropagate_install_script_0(status);
		} 
		else if ("propagate_build_script_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setPropagate_build_script_0(status);
		} 
		else if ("manipulate_mq_config_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setManipulate_mq_config_0(status);
		} 
		else if ("prepare_chmod_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setPrepare_chmod_0(status);
		} 
		else if ("prepare_set_hostname_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setPrepare_set_hostname_0(status);
		} 
		else if ("download_files_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setDownload_files_0(status);
		} 
		else if ("install_mq_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setInstall_mq_0(status);
		} 
		else if ("update_mq_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setUpdate_mq_0(status);
		} 
		else if ("build_mq_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setBuild_mq_0(status);
		}
		else if ("start_mq_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setStart_mq_0(status);
		} 
	}
	
	public void setMqClusterLogAddressDetail(MqClusterLogBean mclb, String name, int i, String address) 
	{
		if ("make_directory_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setMake_directory_addr_0(address);
		} 
		else if ("propagate_host_script_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setPropagate_host_script_addr_0(address);
		} 
		else if ("propagate_prepare_script_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setPropagate_prepare_script_addr_0(address);
		} 
		else if ("propagate_install_script_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setPropagate_install_script_addr_0(address);
		} 
		else if ("propagate_build_script_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setPropagate_build_script_addr_0(address);
		} 
		else if ("manipulate_mq_config_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setManipulate_mq_config_addr_0(address);
		} 
		else if ("prepare_chmod_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setPrepare_chmod_addr_0(address);
		} 
		else if ("prepare_set_hostname_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setPrepare_set_hostname_addr_0(address);
		} 
		else if ("download_files_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setDownload_files_addr_0(address);
		} 
		else if ("install_mq_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setInstall_mq_addr_0(address);
		} 
		else if ("update_mq_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setUpdate_mq_addr_0(address);
		} 
		else if ("build_mq_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setBuild_mq_addr_0(address);
		} 
		else if ("start_mq_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			mclb.setStart_mq_addr_0(address);
		}
	}
	
	/**
	 * 获得mq cluster安装进度
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("/mqclusterProgressnodeInstall")
	public void mqclusterProgressnodeInstall(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws Exception 
	{
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");

		PrintWriter out = response.getWriter();

		String uuid = request.getParameter("uuid");
		ObjectMapper om = new ObjectMapper();
		ObjectNode respJson = om.createObjectNode();

		String table = "odata/tasks?uuid=";
		String url = table + uuid;
		ArrayNode tasks = amsRestService.getList(null, null, url);
		ObjectNode task = null;
		Map map = new TreeMap();
		if (tasks != null && tasks.size() > 0) 
		{
			task = (ObjectNode) tasks.get(0);
			// total
			String total = task.get("total").asText();
			// steps
			String stepsNb = task.get("steps").asText();
			// status
			String status = task.get("status").asText();
			String progress = stepsNb + " / " + total;
			map.put("progress", progress);
			respJson.put("progress", progress);
			String percent = FormatUtil.getPercent(Integer.parseInt(stepsNb), Integer.parseInt(total));
			map.put("percent", percent);
			respJson.put("percent", percent);
			map.put("status", status);
			respJson.put("status", status);			
		}
		logger.info("map:" + map);
		System.out.println("resp::" + respJson);
		logger.info("resp" + respJson);
		out.print(respJson);
		out.close();
	}

}
