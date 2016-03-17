package com.ibm.automation.db2.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.automation.ams.constants.AMS2KeyUtil;
import com.ibm.automation.ams.constants.AMS2PropertyKeyConst;
import com.ibm.automation.ams.service.AmsRestService;
import com.ibm.automation.ams.util.AmsClient;
import com.ibm.automation.ams.util.AmsV2ClientHttpClient4Impl;
import com.ibm.automation.core.bean.AddHostBean;
import com.ibm.automation.core.constants.OPSTPropertyKeyConst;
import com.ibm.automation.core.exception.OPSTBaseException;
import com.ibm.automation.core.util.EncodeUtil;
import com.ibm.automation.core.util.FormatUtil;
import com.ibm.automation.core.util.HttpClientUtil;
import com.ibm.automation.core.util.PropertyUtil;



@Controller
public class DB2Controller {
	@Autowired
	private AmsRestService amsRestService;
	AmsClient amsClient = new AmsV2ClientHttpClient4Impl();
	private static Logger logger = Logger.getLogger(DB2Controller.class);

	private ArrayNode serverlists; // 放入认证信息

	public ArrayNode getServerList() // 获取servers 的列表
	{
		ArrayNode lists = getList(null, null, "odata/servers"); // 获取server表数据
		return lists;
	}
	
	public ArrayNode getList(ArrayNode sort, JsonNode query, String url) {
		try {
			return amsClient.list(sort, query, url);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("DB2Controller::"+e.getMessage());
		}
		return null;
	}

	ObjectMapper om = new ObjectMapper();
	Properties p = PropertyUtil.getResourceFile("config/properties/cloud.properties");
	Properties amsprop = PropertyUtil.getResourceFile("config/properties/ams2.properties");
	
	
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
		logger.info("pvccluster:::" + obj);
		return obj;
	}
	// 创建pvcnode
	private  String createPvcNodeObjNode(String hostname, String addr, String hostId, String type, String role) {
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
	
	/*// 创建一个host
	private ObjectNode createHostObjNode(String type, String hostname, String addr) {
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
	}*/
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/toDb2NextPage")
	public String toDb2NextPage(HttpServletRequest request, HttpSession session) throws Exception {
		// 获取跳转页面信息
		String status = request.getParameter("status");
		// 获取ID
		String serId = request.getParameter("serId");
		String ptype = request.getParameter("ptype");
		// showRequest(request);
	    // System.out.println(serId);
		// 基本
		// 节点名称
		request.setAttribute("hostname", request.getParameter("hostname"));
		// 单节点IP
		request.setAttribute("ip", request.getParameter("ip"));
		request.setAttribute("bootip", request.getParameter("bootip"));
		request.setAttribute("hostId", request.getParameter("hostId"));
		request.setAttribute("db2_fixpack", request.getParameter("db2_fixpack"));
		// VG
		request.setAttribute("vgdb2home", request.getParameter("vgdb2home"));
		request.setAttribute("vgdb2log", request.getParameter("vgdb2log"));
		request.setAttribute("vgdb2archlog", request.getParameter("vgdb2archlog"));
		request.setAttribute("vgdataspace", request.getParameter("vgdataspace"));

		// PV
		request.setAttribute("db2homepv", request.getParameter("db2homepv"));
		request.setAttribute("db2logpv", request.getParameter("db2logpv"));
		request.setAttribute("db2archlogpv", request.getParameter("db2archlogpv"));
		request.setAttribute("dataspacepv", request.getParameter("dataspacepv"));
		// VG创建方式
		request.setAttribute("db2homemode", request.getParameter("db2homemode"));
		request.setAttribute("db2logmode", request.getParameter("db2logmode"));
		request.setAttribute("db2archlogmode", request.getParameter("db2archlogmode"));
		request.setAttribute("dataspacemode", request.getParameter("dataspacemode"));
		// NFS BEGIN -->
		request.setAttribute("nfsON", request.getParameter("nfsON"));

		request.setAttribute("nfsIP1", request.getParameter("nfsIP1"));
		request.setAttribute("nfsSPoint1", request.getParameter("nfsSPoint1"));
		request.setAttribute("nfsCPoint1", request.getParameter("nfsCPoint1"));

		request.setAttribute("nfsIP2", request.getParameter("nfsIP2"));
		request.setAttribute("nfsSPoint2", request.getParameter("nfsSPoint2"));
		request.setAttribute("nfsCPoint2", request.getParameter("nfsCPoint2"));

		request.setAttribute("nfsIP3", request.getParameter("nfsIP3"));
		request.setAttribute("nfsSPoint3", request.getParameter("nfsSPoint3"));
		request.setAttribute("nfsCPoint3", request.getParameter("nfsCPoint3"));

		request.setAttribute("nfsIP4", request.getParameter("nfsIP4"));
		request.setAttribute("nfsSPoint4", request.getParameter("nfsSPoint4"));
		request.setAttribute("nfsCPoint4", request.getParameter("nfsCPoint4"));

		request.setAttribute("nfsIP5", request.getParameter("nfsIP5"));
		request.setAttribute("nfsSPoint5", request.getParameter("nfsSPoint5"));
		request.setAttribute("nfsCPoint5", request.getParameter("nfsCPoint5"));

		request.setAttribute("nfsIP6", request.getParameter("nfsIP6"));
		request.setAttribute("nfsSPoint6", request.getParameter("nfsSPoint6"));
		request.setAttribute("nfsCPoint6", request.getParameter("nfsCPoint6"));

		request.setAttribute("nfsIP7", request.getParameter("nfsIP7"));
		request.setAttribute("nfsSPoint7", request.getParameter("nfsSPoint7"));
		request.setAttribute("nfsCPoint7", request.getParameter("nfsCPoint7"));

		request.setAttribute("nfsIP8", request.getParameter("nfsIP8"));
		request.setAttribute("nfsSPoint8", request.getParameter("nfsSPoint8"));
		request.setAttribute("nfsCPoint8", request.getParameter("nfsCPoint8"));

		request.setAttribute("nfsIP9", request.getParameter("nfsIP9"));
		request.setAttribute("nfsSPoint9", request.getParameter("nfsSPoint9"));
		request.setAttribute("nfsCPoint9", request.getParameter("nfsCPoint9"));

		request.setAttribute("nfsIP10", request.getParameter("nfsIP10"));
		request.setAttribute("nfsSPoint10", request.getParameter("nfsSPoint10"));
		request.setAttribute("nfsCPoint10", request.getParameter("nfsCPoint10"));

		request.setAttribute("nfsIP11", request.getParameter("nfsIP11"));
		request.setAttribute("nfsSPoint11", request.getParameter("nfsSPoint11"));
		request.setAttribute("nfsCPoint11", request.getParameter("nfsCPoint11"));

		request.setAttribute("nfsIP12", request.getParameter("nfsIP12"));
		request.setAttribute("nfsSPoint12", request.getParameter("nfsSPoint12"));
		request.setAttribute("nfsCPoint12", request.getParameter("nfsCPoint12"));

		request.setAttribute("nfsIP13", request.getParameter("nfsIP13"));
		request.setAttribute("nfsSPoint13", request.getParameter("nfsSPoint13"));
		request.setAttribute("nfsCPoint13", request.getParameter("nfsCPoint13"));

		request.setAttribute("nfsIP14", request.getParameter("nfsIP14"));
		request.setAttribute("nfsSPoint14", request.getParameter("nfsSPoint14"));
		request.setAttribute("nfsCPoint14", request.getParameter("nfsCPoint14"));

		request.setAttribute("nfsIP15", request.getParameter("nfsIP15"));
		request.setAttribute("nfsSPoint15", request.getParameter("nfsSPoint15"));
		request.setAttribute("nfsCPoint15", request.getParameter("nfsCPoint15"));

		// DB2信息 基本信息
		request.setAttribute("db2_version", request.getParameter("db2_version"));
		request.setAttribute("db2_db2base", request.getParameter("db2_db2base"));
		request.setAttribute("db2_dbpath", request.getParameter("db2_dbpath"));
		request.setAttribute("db2_db2insusr", request.getParameter("db2_db2insusr"));
		request.setAttribute("db2_svcename", request.getParameter("db2_svcename"));
		request.setAttribute("db2_dbname", request.getParameter("db2_dbname"));
		request.setAttribute("db2_codeset", request.getParameter("db2_codeset"));
		request.setAttribute("db2_dbdatapath", request.getParameter("db2_dbdatapath"));

		// DB2信息 实例高级属性
		request.setAttribute("db2_db2insgrp", request.getParameter("db2_db2insgrp"));
		request.setAttribute("db2_db2fenusr", request.getParameter("db2_db2fenusr"));
		request.setAttribute("db2_db2fengrp", request.getParameter("db2_db2fengrp"));
		request.setAttribute("db2_db2comm", request.getParameter("db2_db2comm"));
		request.setAttribute("db2_db2codepage", request.getParameter("db2_db2codepage"));
		request.setAttribute("db2_initagents", request.getParameter("db2_initagents"));
		request.setAttribute("db2_poolagents", request.getParameter("db2_poolagents"));
		request.setAttribute("db2_max_coordagents", request.getParameter("db2_max_coordagents"));
		request.setAttribute("db2_max_connectings", request.getParameter("db2_max_connectings"));
		request.setAttribute("db2_diagsize", request.getParameter("db2_diagsize"));
		request.setAttribute("db2_mon_buf", request.getParameter("db2_mon_buf"));
		request.setAttribute("db2_mon_lock", request.getParameter("db2_mon_lock"));
		request.setAttribute("db2_mon_sort", request.getParameter("db2_mon_sort"));
		request.setAttribute("db2_mon_stmt", request.getParameter("db2_mon_stmt"));
		request.setAttribute("db2_mon_table", request.getParameter("db2_mon_table"));
		request.setAttribute("db2_mon_uow", request.getParameter("db2_mon_uow"));
		request.setAttribute("db2_health_mon", request.getParameter("db2_health_mon"));
		request.setAttribute("db2_mon_heap", request.getParameter("db2_mon_heap"));
		// DB2信息 实例高级属性
		request.setAttribute("db2_db2log", request.getParameter("db2_db2log"));
		request.setAttribute("db2_logarchpath", request.getParameter("db2_logarchpath"));
		request.setAttribute("db2_backuppath", request.getParameter("db2_backuppath"));
		request.setAttribute("db2_stmm", request.getParameter("db2_stmm"));
		request.setAttribute("db2_locklist", request.getParameter("db2_locklist"));
		request.setAttribute("db2_maxlocks", request.getParameter("db2_maxlocks"));
		request.setAttribute("db2_locktimeout", request.getParameter("db2_locktimeout"));
		request.setAttribute("db2_sortheap", request.getParameter("db2_sortheap"));
		request.setAttribute("db2_logfilesize", request.getParameter("db2_logfilesize"));
		request.setAttribute("db2_logprimary", request.getParameter("db2_logprimary"));
		request.setAttribute("db2_logsecond", request.getParameter("db2_logsecond"));
		request.setAttribute("db2_logbuff", request.getParameter("db2_logbuff"));
		request.setAttribute("db2_softmax", request.getParameter("db2_softmax"));
		request.setAttribute("db2_trackmod", request.getParameter("db2_trackmod"));
		request.setAttribute("db2_pagesize", request.getParameter("db2_pagesize"));
		
		ObjectMapper om = new ObjectMapper();

		List serIds = new ArrayList();
		if (serId != null && serId != "") {
			String[] ss = serId.split(",");
			for (int i = 0; i < ss.length; i++) {
				serIds.add(ss[i]);
			}
		}
		ArrayNode lists = amsRestService.getList(null, null, "odata/servers");
	//	System.out.println(lists);

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
		//System.out.println(lahb);

		request.setAttribute("serId", serId);
		request.setAttribute("total", lahb.size());
		request.setAttribute("ptype", ptype);
		List<AddHostBean> listDetial = new ArrayList<AddHostBean>();

		for (int i = 0; i < lahb.size(); i++) {
			String serverId = lahb.get(i).get_id();
			for (int j = 0; j < serIds.size(); j++) {
				if (serverId.equals(serIds.get(j))) {
					listDetial.add(lahb.get(i));
				}
			}
		}

		request.setAttribute("servers", listDetial);

		/*
		 * List<ServerBean> listDetial = new ArrayList<ServerBean>();
		 * List<ServerBean> list; List<FlavorBean> fList; List<ImagesBean>
		 * imageList; try { list = instanceService.getServerListV2(tenantId,
		 * tokenId); for (int i = 0; i < list.size(); i++) { String serverId =
		 * list.get(i).getId(); for (int j = 0; j < serIds.size(); j++) { if
		 * (serverId.equals(serIds.get(j))) { listDetial.add(list.get(i)); } } }
		 * fList = flavorsService.getAllFlavorsList(tenantId, tokenId);
		 * imageList = imageService.getAllImageList(tenantId, tokenId); } catch
		 * (LoginTimeOutException e) { return this.setExceptionTologin(e,
		 * request); } catch (BaseException e) { e.printStackTrace(); throw new
		 * ReturnToMainPageException(e.getMessage()); }
		 * 
		 * Collections.sort(listDetial); request.setAttribute("serId", serId);
		 * request.setAttribute("servers", listDetial);
		 * request.setAttribute("flavors", fList);
		 * request.setAttribute("imageList", imageList);
		 */
		if (status.equals("")) {
		} else if (status.equals("installConfirmNew")) {

		//	System.out.println("=======new_confirm======");
		//	System.out.println("db2base=:" + request.getParameter("db2base"));
			request.setAttribute("db2base", request.getParameter("db2base"));

		//	System.out.println("*********new_confirm*******");
			return "instance_aix_db2_config_new_confirm";
		} else if (status.equals("installPageNew")) {
		//	System.out.println("=============ceshixinyemian================");
			ObjectNode basicInfo = AMS2KeyUtil.getBasicInfo();
			request.setAttribute("basicInfo", basicInfo);
			ObjectNode instProp = AMS2KeyUtil.getInstAdvProp();
			request.setAttribute("instProp", instProp);
			ObjectNode dbProp = AMS2KeyUtil.getDB2AdvProp();
			request.setAttribute("dbProp", dbProp);

			String ha_dataspace1pv = request.getParameter("dataspacepv");
			logger.info("=========ha_dataspace1pv=========" + ha_dataspace1pv);

			String[] dataspcepvs = ha_dataspace1pv.split(",");
			String db2_dbdatapath = "";
			for (int i = 1; i <= dataspcepvs.length; i++) {
				db2_dbdatapath = db2_dbdatapath + "/db2dataspace" + i + ",";
			}
			if ("" != db2_dbdatapath) {
				db2_dbdatapath = db2_dbdatapath.substring(0, db2_dbdatapath.length() - 1);
			}
		//	System.out.println("====db2_dbdatapath====" + db2_dbdatapath);
			request.setAttribute("db2_dbdatapath", db2_dbdatapath);
			return "instance_aix_db2_config_new";
		} else if (status.equals("makevg")) {
			ArrayNode allHdisk = om.createArrayNode();
			for (int i = 0; i < listDetial.size(); i++) {
				ArrayNode hdisks = om.createArrayNode();
				String instname = listDetial.get(i).getName();
				String instaddr = listDetial.get(i).getIP();
				// 获取选中实例的HDiskInfo
				String cmd_cfgmgr = amsprop.getProperty(AMS2PropertyKeyConst.cmd_cfgmgr);
				List<String> info = amsRestService.getCmdInfoByAddr(instname, instaddr, cmd_cfgmgr);
				for (String line : info) {

					ObjectNode hd = om.createObjectNode();
					hd.put("hdiskname", line);
					hdisks.add(hd);
				}
				allHdisk.add(hdisks);
			}
			request.setAttribute("allHdisk", allHdisk);

			Iterator<AddHostBean> iter = listDetial.iterator();
			AddHostBean sb = null;
			while (iter.hasNext()) {
				sb = (AddHostBean) iter.next();
			}

			request.setAttribute("hostId", sb.get_id());// hostId
			request.setAttribute("ip", sb.getIP());// ip设置为IP
			request.setAttribute("hostname", sb.getName());// 主机名
			return "instance_aix_db2_makevg";
		}
		return null;
	}
	/**
	 * 拼接单节点DB2产生的DB2参数
	 */
	private String setDb2ConfigMsg(HttpServletRequest request) {
		// String fixpack = request.getParameter("db2_fixpack");
		String lineSep = "\n";
		String retStr = "ip=" + request.getParameter("ip") + lineSep 
				+ "bootip=" + request.getParameter("bootip") + lineSep 
				+ "hostname=" + request.getParameter("hostname") + lineSep 
				+ "vgdb2home=" + request.getParameter("vgdb2home") + lineSep 
				+ "vgdb2log=" + request.getParameter("vgdb2log") + lineSep
				+ "vgdb2archlog=" + request.getParameter("vgdb2archlog") + lineSep 
				+ "vgdataspace=" + request.getParameter("vgdataspace") + lineSep 
				+ "db2homepv=" + request.getParameter("db2homepv") + lineSep 
				+ "db2logpv=" + request.getParameter("db2logpv") + lineSep 
				+ "db2archlogpv=" + request.getParameter("db2archlogpv") + lineSep 
				+ "dataspacepv=" + request.getParameter("dataspacepv") + lineSep 
				+ "db2homemode=" + request.getParameter("db2homemode") + lineSep 
				+ "db2logmode=" + request.getParameter("db2logmode") + lineSep 
				+ "db2archlogmode=" + request.getParameter("db2archlogmode") + lineSep 
				+ "dataspacemode=" + request.getParameter("dataspacemode") + lineSep 
				+ "db2_version=" + request.getParameter("db2_version") + lineSep 
				+ "db2_fixpack=" + request.getParameter("db2_fixpack") + lineSep 
				+ "db2_db2base=" + request.getParameter("db2_db2base") + lineSep 
				+ "db2_dbpath=" + request.getParameter("db2_dbpath") + lineSep 
				+ "db2_svcename=" + request.getParameter("db2_svcename") + lineSep 
				+ "db2_db2insusr=" + request.getParameter("db2_db2insusr") + lineSep 
				+ "db2_dbname=" + request.getParameter("db2_dbname") + lineSep 
				+ "db2_codeset=" + request.getParameter("db2_codeset") + lineSep 
				+ "db2_dbdatapath=" + request.getParameter("db2_dbdatapath") + lineSep 
				+ "db2_db2insgrp=" + request.getParameter("db2_db2insgrp") + lineSep 
				+ "db2_db2fenusr=" + request.getParameter("db2_db2fenusr") + lineSep 
				+ "db2_db2fengrp=" + request.getParameter("db2_db2fengrp") + lineSep 
				+ "db2_db2comm=" + request.getParameter("db2_db2comm") + lineSep 
				+ "db2_db2codepage=" + request.getParameter("db2_db2codepage") + lineSep 
				+ "db2_initagents=" + request.getParameter("db2_initagents") + lineSep 
				+ "db2_max_coordagents=" + request.getParameter("db2_max_coordagents") + lineSep 
				// db2_max_connectings
				+ "db2_max_connectings=" + request.getParameter("db2_max_connectings") + lineSep 
				// db2_poolagents
				+ "db2_poolagents=" + request.getParameter("db2_poolagents") + lineSep 
				// db2_diagsize
				+ "db2_diagsize=" + request.getParameter("db2_diagsize") + lineSep 
				// db2_mon_buf
				+ "db2_mon_buf=" + request.getParameter("db2_mon_buf") + lineSep 
				// db2_mon_lock
				+ "db2_mon_lock=" + request.getParameter("db2_mon_lock") + lineSep 
				// db2_mon_sort
				+ "db2_mon_sort=" + request.getParameter("db2_mon_sort") + lineSep 
				// db2_mon_stmt
				+ "db2_mon_stmt=" + request.getParameter("db2_mon_stmt") + lineSep 
				// db2_mon_table
				+ "db2_mon_table=" + request.getParameter("db2_mon_table") + lineSep 
				// db2_mon_uow
				+ "db2_mon_uow=" + request.getParameter("db2_mon_uow") + lineSep 
				// db2_health_mon
				+ "db2_health_mon=" + request.getParameter("db2_health_mon") + lineSep 
				// db2_mon_heap
				+ "db2_mon_heap=" + request.getParameter("db2_mon_heap") + lineSep 

		        // -------数据库高级属性------
		        // db2_db2log
		        + "db2_db2log=" + request.getParameter("db2_db2log") + lineSep 
				// db2_backuppath
				+ "db2_backuppath=" + request.getParameter("db2_backuppath") + lineSep 
				// db2_logarchpath
				+ "db2_logarchpath=" + request.getParameter("db2_logarchpath") + lineSep 
				// db2_stmm
				+ "db2_stmm=" + request.getParameter("db2_stmm") + lineSep 
				// db2_locklist
				+ "db2_locklist=" + request.getParameter("db2_locklist") + lineSep 
				// db2_maxlocks
				+ "db2_maxlocks=" + request.getParameter("db2_maxlocks") + lineSep 
				// db2_locktimeout
				+ "db2_locktimeout=" + request.getParameter("db2_locktimeout") + lineSep 
				// db2_sortheap
				+ "db2_sortheap=" + request.getParameter("db2_sortheap") + lineSep 
				// db2_logfilesize
				+ "db2_logfilesize=" + request.getParameter("db2_logfilesize") + lineSep 
				// db2_logprimary
				+ "db2_logprimary=" + request.getParameter("db2_logprimary") + lineSep 
				// db2_logsecond
				+ "db2_logsecond=" + request.getParameter("db2_logsecond") + lineSep 
				// db2_logbuff
				+ "db2_logbuff=" + request.getParameter("db2_logbuff") + lineSep 
				// db2_softmax
				+ "db2_softmax=" + request.getParameter("db2_softmax") + lineSep 
				// db2_trackmod
				+ "db2_trackmod=" + request.getParameter("db2_trackmod") + lineSep 
				
				// db2_pagesize
				+ "db2_pagesize=" + request.getParameter("db2_pagesize") + lineSep 
				+ "db2homelv=" + amsprop.getProperty("db2homelv") + lineSep 
				+ "db2loglv=" + amsprop.getProperty("db2loglv") + lineSep
				+ "db2archloglv=" + amsprop.getProperty("db2archloglv") + lineSep 
				+ "dataspacelv=" + amsprop.getProperty("dataspacelv") + lineSep 
				+ "db2homefs=" + amsprop.getProperty("db2homefs") + lineSep 
				+ "db2logfs=" + amsprop.getProperty("db2logfs") + lineSep 
				+ "db2archlogfs=" + amsprop.getProperty("db2archlogfs") + lineSep 
				+ "db2backupfs=" + amsprop.getProperty("db2backupfs") + lineSep 
				+ "dataspacefs=" + amsprop.getProperty("dataspacefs") + lineSep 
				+ "ftphost=" + amsprop.getProperty("ftphost") + lineSep 
				+ "ftpuser=" + amsprop.getProperty("ftpuser") + lineSep
				+ "ftppass=" + amsprop.getProperty("ftppass") + lineSep 
				+ "ftppath=" + amsprop.getProperty("ftppath") + lineSep 
				+ "db2path=" + amsprop.getProperty("db2path") + lineSep 
				+ "db2base=" + amsprop.getProperty("db2_path") + lineSep 
				
				+ "nfsON=" + request.getParameter("nfsON") + lineSep 
				+ "nfsIP1=" + request.getParameter("nfsIP1") + lineSep 
				+ "nfsSPoint1=" + request.getParameter("nfsSPoint1") + lineSep 
				+ "nfsCPoint1=" + request.getParameter("nfsCPoint1") + lineSep 
				+ "nfsIP2=" + request.getParameter("nfsIP2") + lineSep 
				+ "nfsSPoint2=" + request.getParameter("nfsSPoint2") + lineSep 
				+ "nfsCPoint2=" + request.getParameter("nfsCPoint2") + lineSep 
				+ "nfsIP3=" + request.getParameter("nfsIP3") + lineSep 
				+ "nfsSPoint3=" + request.getParameter("nfsSPoint3") + lineSep 
				+ "nfsCPoint3=" + request.getParameter("nfsCPoint3") + lineSep 
				+ "nfsIP4=" + request.getParameter("nfsIP4") + lineSep 
				+ "nfsSPoint4=" + request.getParameter("nfsSPoint4") + lineSep 
				+ "nfsCPoint4=" + request.getParameter("nfsCPoint4") + lineSep 
				+ "nfsIP5=" + request.getParameter("nfsIP5") + lineSep 
				+ "nfsSPoint5=" + request.getParameter("nfsSPoint5") + lineSep 
				+ "nfsCPoint5=" + request.getParameter("nfsCPoint5") + lineSep 
				+ "nfsIP6=" + request.getParameter("nfsIP6") + lineSep 
				+ "nfsSPoint6=" + request.getParameter("nfsSPoint6") + lineSep 
				+ "nfsCPoint6=" + request.getParameter("nfsCPoint6") + lineSep 
				+ "nfsIP7=" + request.getParameter("nfsIP7") + lineSep 
				+ "nfsSPoint7=" + request.getParameter("nfsSPoint7") + lineSep 
				+ "nfsCPoint7=" + request.getParameter("nfsCPoint7") + lineSep 
				+ "nfsIP8=" + request.getParameter("nfsIP8") + lineSep 
				+ "nfsSPoint8=" + request.getParameter("nfsSPoint8") + lineSep 
				+ "nfsCPoint8=" + request.getParameter("nfsCPoint8") + lineSep 
				+ "nfsIP9=" + request.getParameter("nfsIP9") + lineSep
				+ "nfsSPoint9=" + request.getParameter("nfsSPoint9") + lineSep 
				+ "nfsCPoint9=" + request.getParameter("nfsCPoint9") + lineSep 
				+ "nfsIP10=" + request.getParameter("nfsIP10") + lineSep 
				+ "nfsSPoint10=" + request.getParameter("nfsSPoint10") + lineSep 
				+ "nfsCPoint10=" + request.getParameter("nfsCPoint10") + lineSep 
				+ "nfsIP11=" + request.getParameter("nfsIP11") + lineSep 
				+ "nfsSPoint11=" + request.getParameter("nfsSPoint11") + lineSep 
				+ "nfsCPoint11=" + request.getParameter("nfsCPoint11") + lineSep 
				+ "nfsIP12=" + request.getParameter("nfsIP12") + lineSep 
				+ "nfsSPoint12=" + request.getParameter("nfsSPoint12") + lineSep 
				+ "nfsCPoint12=" + request.getParameter("nfsCPoint12") + lineSep 
				+ "nfsIP13=" + request.getParameter("nfsIP13") + lineSep 
				+ "nfsSPoint13=" + request.getParameter("nfsSPoint13") + lineSep 
				+ "nfsCPoint13=" + request.getParameter("nfsCPoint13") + lineSep 
				+ "nfsIP14=" + request.getParameter("nfsIP14") + lineSep 
				+ "nfsSPoint14=" + request.getParameter("nfsSPoint14") + lineSep 
				+ "nfsCPoint14=" + request.getParameter("nfsCPoint14") + lineSep 
				+ "nfsIP15=" + request.getParameter("nfsIP15") + lineSep 
				+ "nfsSPoint15=" + request.getParameter("nfsSPoint15") + lineSep 
				+ "nfsCPoint15=" + request.getParameter("nfsCPoint15") + lineSep;
		// System.out.println("the total string is " + retStr);
		return retStr;
	}
	
	
	/**
	 * db2 单节点设计
	 * 
	 * @throws OPSTBaseException
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/installDb2StandAloneInfo")
	public String installDb2StandAloneInfo(HttpServletRequest request, HttpSession session) throws OPSTBaseException {
		ObjectMapper om = new ObjectMapper();
		String retstr = setDb2ConfigMsg(request);
		ArrayNode hosts = om.createArrayNode();
		String hostname = request.getParameter("hostname");// 主机名
		String ip = request.getParameter("ip");// 主机IP
		String hostId = request.getParameter("hostId");// hostId
		String ptype = request.getParameter("ptype");
		ObjectNode host = createDB2HostObjNode(hostname, ip, String.valueOf(1));
		hosts.add(host);
		ArrayNode pvcclusternodes = om.createArrayNode();
		if (ip != null && (!ip.equals(""))) {
			// 如果ip不空
			String pvcnodeId = createPvcNodeObjNode(hostname, ip, hostId, "DB2", String.valueOf(1));
			ObjectNode clusterNode = createPvcClusterNode(hostname, ip, hostId, pvcnodeId, String.valueOf(1));
			pvcclusternodes.add(clusterNode);
		}

		String type = request.getParameter("type");
	//	System.out.println("=========type(yes:立即创建,no:手动创建)=====:[" + type + "]");
		String nfsON = request.getParameter("nfsON");
	//	System.out.println("=========nfsON 是否挂载NFS文件系统(yes:是,no:否)=====:[" + nfsON + "]");
		ObjectNode dbnode = this.postDB2StandAloneRun(hosts, null, retstr, type, nfsON);

		if (dbnode != null && dbnode.get("uuid") != null) {

			ObjectNode optionsInfo = om.createObjectNode();
			setDb2OptionsInfo(request, optionsInfo);
			createPvcClustersObjNode(dbnode.get("uuid").asText(), pvcclusternodes, optionsInfo, ptype);
		}
		//logger.info("自动执行DB2单节点");
		if ("yes".equals(type)) {
			return "redirect:/getLogInfo";
		} else {
			String serId = request.getParameter("serId");

			List serIds = new ArrayList();
			if (serId != null && serId != "") {
				String[] ss = serId.split(",");
				for (int i = 0; i < ss.length; i++) {
					serIds.add(ss[i]);
				}
			}
			ArrayNode lists = amsRestService.getList(null, null, "odata/servers");
		//	System.out.println(lists);

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

			request.setAttribute("serId", serId);
			request.setAttribute("total", lahb.size());
			List<AddHostBean> listDetial = new ArrayList<AddHostBean>();

			for (int i = 0; i < lahb.size(); i++) {
				String serverId = lahb.get(i).get_id();
				for (int j = 0; j < serIds.size(); j++) {
					if (serverId.equals(serIds.get(j))) {
						listDetial.add(lahb.get(i));
					}
				}
			}

			request.setAttribute("servers", listDetial);
			request.setAttribute("ip", request.getParameter("ip"));
			request.setAttribute("bootip", request.getParameter("bootip"));
			request.setAttribute("hostname", request.getParameter("hostname"));
			return "instance_aix_db2_uninstall_script";
		}
	}
	
	
	
	/**
	 * db2 单节点日志
	 * 
	 * @throws OPSTBaseException
	 * 
	 */
	public String getDb2StandAloneInfoDetail(HttpServletRequest request, HttpSession session) throws OPSTBaseException {

		String uuid = request.getParameter("uuid");
		// String serid=request.getParameter("serids");
		ArrayNode pvcclusters = amsRestService.getList(null, null, "odata/pvcclusters?uuid=" + uuid);
		ObjectNode cluster = null;
		if (pvcclusters != null && pvcclusters.size() > 0) {
			cluster = (ObjectNode) pvcclusters.get(0);
		}
		List<String> serIds = new ArrayList<String>();
		if (cluster != null && cluster.get("nodes") != null) {
			ArrayNode nodes = (ArrayNode) cluster.get("nodes");
			for (JsonNode jn : nodes) {
				serIds.add(jn.get("pvcid") == null ? "" : jn.get("pvcid").asText());
			}
		}
		if (cluster != null && cluster.get("options") != null) {
			ObjectNode options = (ObjectNode) cluster.get("options");

			request.setAttribute("hostname", options.get("hostname").asText());
			// 单节点IP
			request.setAttribute("ip", options.get("ip").asText());
			request.setAttribute("bootip", options.get("bootip").asText());
			request.setAttribute("hostId", options.get("hostId").asText());
			// VG
			request.setAttribute("vgdb2home", options.get("vgdb2home").asText());
			request.setAttribute("vgdb2log", options.get("vgdb2log").asText());
			request.setAttribute("vgdb2archlog", options.get("vgdb2archlog").asText());
			request.setAttribute("vgdataspace", options.get("vgdataspace").asText());

			// PV
			request.setAttribute("db2homepv", options.get("db2homepv").asText());
			request.setAttribute("db2logpv", options.get("db2logpv").asText());
			request.setAttribute("db2archlogpv", options.get("db2archlogpv").asText());
			request.setAttribute("dataspacepv", options.get("dataspacepv").asText());
			// VG创建方式
			request.setAttribute("db2homemode", options.get("db2homemode").asText());
			request.setAttribute("db2logmode", options.get("db2logmode").asText());
			request.setAttribute("db2archlogmode", options.get("db2archlogmode").asText());
			request.setAttribute("dataspacemode", options.get("dataspacemode").asText());
			// NFS BEGIN -->
			request.setAttribute("nfsON", options.get("nfsON").asText());
			request.setAttribute("ison", "yes");
			request.setAttribute("nfsIP1", options.get("nfsIP1").asText());
			request.setAttribute("nfsSPoint1", options.get("nfsSPoint1").asText());
			request.setAttribute("nfsCPoint1", options.get("nfsCPoint1").asText());

			request.setAttribute("nfsIP2", options.get("nfsIP2").asText());
			request.setAttribute("nfsSPoint2", options.get("nfsSPoint2").asText());
			request.setAttribute("nfsCPoint2", options.get("nfsCPoint2").asText());

			request.setAttribute("nfsIP3", options.get("nfsIP3").asText());
			request.setAttribute("nfsSPoint3", options.get("nfsSPoint3").asText());
			request.setAttribute("nfsCPoint3", options.get("nfsCPoint3").asText());

			request.setAttribute("nfsIP4", options.get("nfsIP4").asText());
			request.setAttribute("nfsSPoint4", options.get("nfsSPoint4").asText());
			request.setAttribute("nfsCPoint4", options.get("nfsCPoint4").asText());

			request.setAttribute("nfsIP5", options.get("nfsIP5").asText());
			request.setAttribute("nfsSPoint5", options.get("nfsSPoint5").asText());
			request.setAttribute("nfsCPoint5", options.get("nfsCPoint5").asText());

			request.setAttribute("nfsIP6", options.get("nfsIP6").asText());
			request.setAttribute("nfsSPoint6", options.get("nfsSPoint6").asText());
			request.setAttribute("nfsCPoint6", options.get("nfsCPoint6").asText());

			request.setAttribute("nfsIP7", options.get("nfsIP7").asText());
			request.setAttribute("nfsSPoint7", options.get("nfsSPoint7").asText());
			request.setAttribute("nfsCPoint7", options.get("nfsCPoint7").asText());

			request.setAttribute("nfsIP8", options.get("nfsIP8").asText());
			request.setAttribute("nfsSPoint8", options.get("nfsSPoint8").asText());
			request.setAttribute("nfsCPoint8", options.get("nfsCPoint8").asText());

			request.setAttribute("nfsIP9", options.get("nfsIP9").asText());
			request.setAttribute("nfsSPoint9", options.get("nfsSPoint9").asText());
			request.setAttribute("nfsCPoint9", options.get("nfsCPoint9").asText());

			request.setAttribute("nfsIP10", options.get("nfsIP10").asText());
			request.setAttribute("nfsSPoint10", options.get("nfsSPoint10").asText());
			request.setAttribute("nfsCPoint10", options.get("nfsCPoint10").asText());

			request.setAttribute("nfsIP11", options.get("nfsIP11").asText());
			request.setAttribute("nfsSPoint11", options.get("nfsSPoint11").asText());
			request.setAttribute("nfsCPoint11", options.get("nfsCPoint11").asText());

			request.setAttribute("nfsIP12", options.get("nfsIP12").asText());
			request.setAttribute("nfsSPoint12", options.get("nfsSPoint12").asText());
			request.setAttribute("nfsCPoint12", options.get("nfsCPoint12").asText());

			request.setAttribute("nfsIP13", options.get("nfsIP13").asText());
			request.setAttribute("nfsSPoint13", options.get("nfsSPoint13").asText());
			request.setAttribute("nfsCPoint13", options.get("nfsCPoint13").asText());

			request.setAttribute("nfsIP14", options.get("nfsIP14").asText());
			request.setAttribute("nfsSPoint14", options.get("nfsSPoint14").asText());
			request.setAttribute("nfsCPoint14", options.get("nfsCPoint14").asText());

			request.setAttribute("nfsIP15", options.get("nfsIP15").asText());
			request.setAttribute("nfsSPoint15", options.get("nfsSPoint15").asText());
			request.setAttribute("nfsCPoint15", options.get("nfsCPoint15").asText());

			// DB2信息 基本信息
			request.setAttribute("db2_version", options.get("db2_version").asText());
			request.setAttribute("db2_db2base", options.get("db2_db2base").asText());
			request.setAttribute("db2_dbpath", options.get("db2_dbpath").asText());
			request.setAttribute("db2_db2insusr", options.get("db2_db2insusr").asText());
			request.setAttribute("db2_svcename", options.get("db2_svcename").asText());
			request.setAttribute("db2_dbname", options.get("db2_dbname").asText());
			request.setAttribute("db2_codeset", options.get("db2_codeset").asText());
			request.setAttribute("db2_dbdatapath", options.get("db2_dbdatapath").asText());

			// DB2信息 实例高级属性
			request.setAttribute("db2_db2insgrp", options.get("db2_db2insgrp").asText());
			request.setAttribute("db2_db2fenusr", options.get("db2_db2fenusr").asText());
			request.setAttribute("db2_db2fengrp", options.get("db2_db2fengrp").asText());
			request.setAttribute("db2_db2comm", options.get("db2_db2comm").asText());
			request.setAttribute("db2_db2codepage", options.get("db2_db2codepage").asText());
			request.setAttribute("db2_initagents", options.get("db2_initagents").asText());
			request.setAttribute("db2_poolagents", options.get("db2_poolagents").asText());
			request.setAttribute("db2_max_coordagents", options.get("db2_max_coordagents").asText());
			request.setAttribute("db2_max_connectings", options.get("db2_max_connectings").asText());
			request.setAttribute("db2_diagsize", options.get("db2_diagsize").asText());
			request.setAttribute("db2_mon_buf", options.get("db2_mon_buf").asText());
			request.setAttribute("db2_mon_lock", options.get("db2_mon_lock").asText());
			request.setAttribute("db2_mon_sort", options.get("db2_mon_sort").asText());
			request.setAttribute("db2_mon_stmt", options.get("db2_mon_stmt").asText());
			request.setAttribute("db2_mon_table", options.get("db2_mon_table").asText());
			request.setAttribute("db2_mon_uow", options.get("db2_mon_uow").asText());
			request.setAttribute("db2_health_mon", options.get("db2_health_mon").asText());
			request.setAttribute("db2_mon_heap", options.get("db2_mon_heap").asText());
			// DB2信息 实例高级属性
			request.setAttribute("db2_db2log", options.get("db2_db2log").asText());
			request.setAttribute("db2_logarchpath", options.get("db2_logarchpath").asText());
			request.setAttribute("db2_backuppath", options.get("db2_backuppath").asText());
			request.setAttribute("db2_stmm", options.get("db2_stmm").asText());
			request.setAttribute("db2_locklist", options.get("db2_locklist").asText());
			request.setAttribute("db2_maxlocks", options.get("db2_maxlocks").asText());
			request.setAttribute("db2_locktimeout", options.get("db2_locktimeout").asText());
			request.setAttribute("db2_sortheap", options.get("db2_sortheap").asText());
			request.setAttribute("db2_logfilesize", options.get("db2_logfilesize").asText());
			request.setAttribute("db2_logprimary", options.get("db2_logprimary").asText());
			request.setAttribute("db2_logsecond", options.get("db2_logsecond").asText());
			request.setAttribute("db2_logbuff", options.get("db2_logbuff").asText());
			request.setAttribute("db2_softmax", options.get("db2_softmax").asText());
			request.setAttribute("db2_trackmod", options.get("db2_trackmod").asText());
			request.setAttribute("db2_pagesize", options.get("db2_pagesize").asText());
		}
		ArrayNode lists = amsRestService.getList(null, null, "odata/servers");
	//	System.out.println(lists);

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

		// request.setAttribute("serId", serid);
		// request.setAttribute("total", lahb.size());
		List<AddHostBean> listDetial = new ArrayList<AddHostBean>();

		for (int i = 0; i < lahb.size(); i++) {
			String serverId = lahb.get(i).get_id();
			for (int j = 0; j < serIds.size(); j++) {
				if (serverId.equals(serIds.get(j))) {
					listDetial.add(lahb.get(i));
				}
			}
		}
		request.setAttribute("servers", listDetial);
		request.setAttribute("cluster", cluster);
		request.setAttribute("mainaddr", request.getParameter("mainaddr"));
		request.setAttribute("uuid", request.getParameter("uuid"));

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
			request.setAttribute("progress", progress);
			String percent = FormatUtil.getPercent(Integer.parseInt(stepsNb), Integer.parseInt(total));
			map.put("percent", percent);
			request.setAttribute("percent", percent);
			map.put("status", status);
			request.setAttribute("status", status);

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
					String script_status = step.get("status").asText();
					if ("2".equals(script_status)) {
						String script_retnum = step.get("retnum").asText();
						if ("0".equals(script_retnum)) {
							// 成功
							request.setAttribute((name + "_" + i).replace('-', '_').replace('.', '_'), "成功");
							map.put((name + "_" + i).replace('-', '_').replace('.', '_'), "成功");

						} else {
							// 失败
							request.setAttribute((name + "_" + i).replace('-', '_').replace('.', '_'), "失败");
							map.put((name + "_" + i).replace('-', '_').replace('.', '_'), "失败");
						}
					} else if ("1".equals(script_status)) {
						request.setAttribute((name + "_" + i).replace('-', '_').replace('.', '_'), "执行中");
						map.put((name + "_" + i).replace('-', '_').replace('.', '_'), "执行中");
					} else {
						// 未执行
						request.setAttribute((name + "_" + i).replace('-', '_').replace('.', '_'), "未开始");
						map.put((name + "_" + i).replace('-', '_').replace('.', '_'), "未开始");
					}
					String script_addr = step.get("node").get("addr").asText();
					request.setAttribute((name + "_addr_" + i).replace('-', '_').replace('.', '_'), script_addr);
					map.put((name + "_addr_" + i).replace('-', '_').replace('.', '_'), script_addr);

				}
			}
		}
		return "instance_aix_db2_standalone_log_details";
	}
	/**
	 * 设置db2 单节点的参数
	 */
	private void setDb2OptionsInfo(HttpServletRequest request, ObjectNode optionsInfo) {
		// 基本
		optionsInfo.put("hostname", request.getParameter("hostname"));
		// 单节点IP
		optionsInfo.put("ip", request.getParameter("ip"));
		optionsInfo.put("bootip", request.getParameter("bootip"));
		optionsInfo.put("hostId", request.getParameter("hostId"));
		// VG
		optionsInfo.put("vgdb2home", request.getParameter("vgdb2home"));
		optionsInfo.put("vgdb2log", request.getParameter("vgdb2log"));
		optionsInfo.put("vgdb2archlog", request.getParameter("vgdb2archlog"));
		optionsInfo.put("vgdataspace", request.getParameter("vgdataspace"));

		// PV
		optionsInfo.put("db2homepv", request.getParameter("db2homepv"));
		optionsInfo.put("db2logpv", request.getParameter("db2logpv"));
		optionsInfo.put("db2archlogpv", request.getParameter("db2archlogpv"));
		optionsInfo.put("dataspacepv", request.getParameter("dataspacepv"));
		// VG创建方式
		optionsInfo.put("db2homemode", request.getParameter("db2homemode"));
		optionsInfo.put("db2logmode", request.getParameter("db2logmode"));
		optionsInfo.put("db2archlogmode", request.getParameter("db2archlogmode"));
		optionsInfo.put("dataspacemode", request.getParameter("dataspacemode"));
		// NFS BEGIN -->
		optionsInfo.put("nfsON", request.getParameter("nfsON"));

		optionsInfo.put("nfsIP1", request.getParameter("nfsIP1"));
		optionsInfo.put("nfsSPoint1", request.getParameter("nfsSPoint1"));
		optionsInfo.put("nfsCPoint1", request.getParameter("nfsCPoint1"));

		optionsInfo.put("nfsIP2", request.getParameter("nfsIP2"));
		optionsInfo.put("nfsSPoint2", request.getParameter("nfsSPoint2"));
		optionsInfo.put("nfsCPoint2", request.getParameter("nfsCPoint2"));

		optionsInfo.put("nfsIP3", request.getParameter("nfsIP3"));
		optionsInfo.put("nfsSPoint3", request.getParameter("nfsSPoint3"));
		optionsInfo.put("nfsCPoint3", request.getParameter("nfsCPoint3"));

		optionsInfo.put("nfsIP4", request.getParameter("nfsIP4"));
		optionsInfo.put("nfsSPoint4", request.getParameter("nfsSPoint4"));
		optionsInfo.put("nfsCPoint4", request.getParameter("nfsCPoint4"));

		optionsInfo.put("nfsIP5", request.getParameter("nfsIP5"));
		optionsInfo.put("nfsSPoint5", request.getParameter("nfsSPoint5"));
		optionsInfo.put("nfsCPoint5", request.getParameter("nfsCPoint5"));

		optionsInfo.put("nfsIP6", request.getParameter("nfsIP6"));
		optionsInfo.put("nfsSPoint6", request.getParameter("nfsSPoint6"));
		optionsInfo.put("nfsCPoint6", request.getParameter("nfsCPoint6"));

		optionsInfo.put("nfsIP7", request.getParameter("nfsIP7"));
		optionsInfo.put("nfsSPoint7", request.getParameter("nfsSPoint7"));
		optionsInfo.put("nfsCPoint7", request.getParameter("nfsCPoint7"));

		optionsInfo.put("nfsIP8", request.getParameter("nfsIP8"));
		optionsInfo.put("nfsSPoint8", request.getParameter("nfsSPoint8"));
		optionsInfo.put("nfsCPoint8", request.getParameter("nfsCPoint8"));

		optionsInfo.put("nfsIP9", request.getParameter("nfsIP9"));
		optionsInfo.put("nfsSPoint9", request.getParameter("nfsSPoint9"));
		optionsInfo.put("nfsCPoint9", request.getParameter("nfsCPoint9"));

		optionsInfo.put("nfsIP10", request.getParameter("nfsIP10"));
		optionsInfo.put("nfsSPoint10", request.getParameter("nfsSPoint10"));
		optionsInfo.put("nfsCPoint10", request.getParameter("nfsCPoint10"));

		optionsInfo.put("nfsIP11", request.getParameter("nfsIP11"));
		optionsInfo.put("nfsSPoint11", request.getParameter("nfsSPoint11"));
		optionsInfo.put("nfsCPoint11", request.getParameter("nfsCPoint11"));

		optionsInfo.put("nfsIP12", request.getParameter("nfsIP12"));
		optionsInfo.put("nfsSPoint12", request.getParameter("nfsSPoint12"));
		optionsInfo.put("nfsCPoint12", request.getParameter("nfsCPoint12"));

		optionsInfo.put("nfsIP13", request.getParameter("nfsIP13"));
		optionsInfo.put("nfsSPoint13", request.getParameter("nfsSPoint13"));
		optionsInfo.put("nfsCPoint13", request.getParameter("nfsCPoint13"));

		optionsInfo.put("nfsIP14", request.getParameter("nfsIP14"));
		optionsInfo.put("nfsSPoint14", request.getParameter("nfsSPoint14"));
		optionsInfo.put("nfsCPoint14", request.getParameter("nfsCPoint14"));

		optionsInfo.put("nfsIP15", request.getParameter("nfsIP15"));
		optionsInfo.put("nfsSPoint15", request.getParameter("nfsSPoint15"));
		optionsInfo.put("nfsCPoint15", request.getParameter("nfsCPoint15"));

		// DB2信息 基本信息
		optionsInfo.put("db2_version", request.getParameter("db2_version"));
		optionsInfo.put("db2_db2base", request.getParameter("db2_db2base"));
		optionsInfo.put("db2_dbpath", request.getParameter("db2_dbpath"));
		optionsInfo.put("db2_db2insusr", request.getParameter("db2_db2insusr"));
		optionsInfo.put("db2_svcename", request.getParameter("db2_svcename"));
		optionsInfo.put("db2_dbname", request.getParameter("db2_dbname"));
		optionsInfo.put("db2_codeset", request.getParameter("db2_codeset"));
		optionsInfo.put("db2_dbdatapath", request.getParameter("db2_dbdatapath"));

		// DB2信息 实例高级属性
		optionsInfo.put("db2_db2insgrp", request.getParameter("db2_db2insgrp"));
		optionsInfo.put("db2_db2fenusr", request.getParameter("db2_db2fenusr"));
		optionsInfo.put("db2_db2fengrp", request.getParameter("db2_db2fengrp"));
		optionsInfo.put("db2_db2comm", request.getParameter("db2_db2comm"));
		optionsInfo.put("db2_db2codepage", request.getParameter("db2_db2codepage"));
		optionsInfo.put("db2_initagents", request.getParameter("db2_initagents"));
		optionsInfo.put("db2_poolagents", request.getParameter("db2_poolagents"));
		optionsInfo.put("db2_max_coordagents", request.getParameter("db2_max_coordagents"));
		optionsInfo.put("db2_max_connectings", request.getParameter("db2_max_connectings"));
		optionsInfo.put("db2_diagsize", request.getParameter("db2_diagsize"));
		optionsInfo.put("db2_mon_buf", request.getParameter("db2_mon_buf"));
		optionsInfo.put("db2_mon_lock", request.getParameter("db2_mon_lock"));
		optionsInfo.put("db2_mon_sort", request.getParameter("db2_mon_sort"));
		optionsInfo.put("db2_mon_stmt", request.getParameter("db2_mon_stmt"));
		optionsInfo.put("db2_mon_table", request.getParameter("db2_mon_table"));
		optionsInfo.put("db2_mon_uow", request.getParameter("db2_mon_uow"));
		optionsInfo.put("db2_health_mon", request.getParameter("db2_health_mon"));
		optionsInfo.put("db2_mon_heap", request.getParameter("db2_mon_heap"));
		// DB2信息 实例高级属性
		optionsInfo.put("db2_db2log", request.getParameter("db2_db2log"));
		optionsInfo.put("db2_logarchpath", request.getParameter("db2_logarchpath"));
		optionsInfo.put("db2_backuppath", request.getParameter("db2_backuppath"));
		optionsInfo.put("db2_stmm", request.getParameter("db2_stmm"));
		optionsInfo.put("db2_locklist", request.getParameter("db2_locklist"));
		optionsInfo.put("db2_maxlocks", request.getParameter("db2_maxlocks"));
		optionsInfo.put("db2_locktimeout", request.getParameter("db2_locktimeout"));
		optionsInfo.put("db2_sortheap", request.getParameter("db2_sortheap"));
		optionsInfo.put("db2_logfilesize", request.getParameter("db2_logfilesize"));
		optionsInfo.put("db2_logprimary", request.getParameter("db2_logprimary"));
		optionsInfo.put("db2_logsecond", request.getParameter("db2_logsecond"));
		optionsInfo.put("db2_logbuff", request.getParameter("db2_logbuff"));
		optionsInfo.put("db2_softmax", request.getParameter("db2_softmax"));
		optionsInfo.put("db2_trackmod", request.getParameter("db2_trackmod"));
		optionsInfo.put("db2_pagesize", request.getParameter("db2_pagesize"));

	}
	private ArrayNode getDb2StandAlonePutHostname(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "/script/hostname.helper.sh");
			steps.put("exec", amsprop.getProperty("db2_standalone_script_path")
					+ amsprop.getProperty("db2_standalone_hostname_sh"));
			steps.put("type", "sput");
			// steps.put("file",
			// "/home/cloudm/works/tcloud2-ams/shell/hostname.helper.sh");
			steps.put("file", amsprop.getProperty("db2_standalone_helpers_file_path")
					+ amsprop.getProperty("db2_standalone_hostname_sh"));
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}
	private ArrayNode getDb2StandAlonePutHosts(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "/script/hosts.helper.sh");
			steps.put("exec",
					amsprop.getProperty("db2_standalone_script_path") + amsprop.getProperty("db2_standalone_hosts_sh"));
			steps.put("type", "sput");
			// steps.put("file",
			// "/home/cloudm/works/tcloud2-ams/shell/hosts.helper.sh");
			steps.put("file", amsprop.getProperty("db2_standalone_helpers_file_path")
					+ amsprop.getProperty("db2_standalone_hosts_sh"));
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}
	public ObjectNode postDB2StandAloneRun(ArrayNode hostnode, ObjectNode crednode, String info, String type,
			String nfsON) {
		// TODO Auto-generated method stub
		// 设值是否立即执行标识
		serverlists = getServerList(); // 为获取serverlist 的加密认证信息

		boolean flag = false;
		if ("yes".equals(type)) {
			flag = true;
		} else if ("no".equals(type)) {
			flag = false;
		}
		// 设值是否立即执行标识
		/*
		 * boolean nfsFlag = false ; if("yes".equals(nfsON)){ nfsFlag = true;
		 * }else if("no".equals(nfsON)){ nfsFlag = false; }
		 */
		// 获取参数信息
		String hdiskHost = p.getProperty(OPSTPropertyKeyConst.AMS2_HOST);
		String hdiskApi = p.getProperty(OPSTPropertyKeyConst.POST_ams2_service_run);
		// 拼接URL
		String strOrgUrl = hdiskHost + hdiskApi;
	//	System.out.println("url::" + strOrgUrl);
		// 构建json
		ObjectNode task = om.createObjectNode();
		ArrayNode jobs = om.createArrayNode();
		task.put("name", "aix71-db2");
		task.put("immediate", true);
		task.set("jobs", jobs);
		ArrayNode names;
		if (flag) {
			names = AMS2KeyUtil.getArrayNodeByKey("db2_standalone_job_name"); // 自动执行,job名称的集合
		} else {
			names = AMS2KeyUtil.getArrayNodeByKey("db2_standalone_job_name_sub"); // 手动执行job名称的集合
		}
		ObjectNode stepobj = this.getDb2StandAloneStepObjectNode();// step集合
		for (JsonNode jnode : names) {
			ObjectNode job = (ObjectNode) jnode;
			String name = job.get("name").asText();
			ArrayNode stepnames = (ArrayNode) stepobj.get(name);
			for (JsonNode sn : stepnames) {
				ObjectNode jobnode = om.createObjectNode();
				ArrayNode steps = null;
				String sname = sn.get("name").asText();
				String jobname = name + "-" + sname;
				jobnode.put("name", jobname);
				jobnode.put("type", "series");
				if (sname.equals("scripts")) {
					steps = getScripts(hostnode, crednode, jobname);
				} else if (sname.equals("put-hosts")) {
					steps = getDb2StandAlonePutHosts(hostnode, crednode, jobname);
				} else if (sname.equals("put-hostname")) {
					steps = getDb2StandAlonePutHostname(hostnode, crednode, jobname);
				} else if (sname.equals("files")) {
					steps = getFiles(hostnode, crednode, jobname, "db2_standalone_file_name",
							"db2_standalone_file_path");
				} else if (sname.equals("prepare.db2.lst")) {
					steps = getPrepareDb2Lst(hostnode, crednode, jobname, info);
				} else if (sname.equals("chmod")) {
					steps = getChmod(hostnode, crednode, jobname);
				} else if (sname.equals("set-hostname")) {
					steps = getSetHostname(hostnode, crednode, jobname);
				} else if (flag && sname.equals("prepare.db2.ksh")) {
					steps = getPrepareDb2Ksh(hostnode, crednode, jobname);
				} else if (flag && sname.equals("mount.nfs.ksh")) {
					steps = getNFSDb2Ksh(hostnode, crednode, jobname);
				}
				jobnode.set("steps", steps);
				jobs.add(jobnode);

			}
		}
	//	System.out.println("jobs::" + jobs);
		logger.info("aixdb2standalone task::"+task);
	//	System.out.println("task::" + task);
		String response = "";
		try {
			response = HttpClientUtil.postMethod(strOrgUrl, task.toString());
			logger.info("aixdb2standalone response::" + response);
	//		System.out.println("response::" + response);
			return (ObjectNode) om.readTree(response);
		} catch (Exception e1) {
			e1.printStackTrace();
			logger.error("aix db2 standalone error ::"+e1.getMessage());
			//return null;
		}
		return null;
	}
	/** 提供封装json的拼装方法 结束 **/
	private ObjectNode getDb2StandAloneStepObjectNode() {
		ObjectNode stepobj = om.createObjectNode();
		ArrayNode prepare_steps = AMS2KeyUtil.getArrayNodeByKey("db2_standalone_prepare_step_name");
		ArrayNode install_steps = AMS2KeyUtil.getArrayNodeByKey("db2_standalone_install_step_name");
		// ArrayNode cluster_steps =
		// AMS2KeyUtil.getArrayNodeByKey("db2_cluster_step_name");
		ArrayNode nfs_steps = AMS2KeyUtil.getArrayNodeByKey("db2_standalone_nfs_step_name");
		stepobj.set("prepare", prepare_steps);
		stepobj.set("install", install_steps);
		// stepobj.set("cluster", cluster_steps);
		stepobj.set("nfs", nfs_steps);
		return stepobj;
	}
	private ArrayNode getScripts(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "mkdir -p /script");
			steps.put("exec", "mkdir -p " + amsprop.getProperty("db2_standalone_script_path_sub"));
			steps.put("type", "scmd");
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}
	private ArrayNode getraws(ArrayNode hostnode, ObjectNode crednode) {
		ArrayNode stephosts = om.createArrayNode();
		for (JsonNode h : hostnode) {
			ObjectNode step = om.createObjectNode();
			step.set("cred", getcred(hostnode, crednode));
			step.set("node",
					om.createObjectNode().put("host", h.get("host").asText()).put("addr", h.get("addr").asText()));

			stephosts.add(step);
		}
		return stephosts;
	}
	/** 提供封装json的拼装方法 开始 **/
	private ObjectNode getcred(ArrayNode an, ObjectNode crednode) {
		// ObjectNode default_cred = AMS2KeyUtil.getCredDefaultUserNode();

		ObjectNode default_cred = om.createObjectNode();

		for (JsonNode jn : serverlists) {
			JsonNode ip = an.get(0).get("addr");// addr
			String ip1 = ip.asText().toString();
			if (ip1.equals(jn.get("IP").asText())) {
				default_cred.put("user", jn.get("UserID").asText());
				default_cred.put("pass", jn.get("Password").asText());
				break;
			}
		}

	/*	ObjectNode cred = crednode == null ? (ObjectNode) default_cred.get("default")
				: (ObjectNode) crednode.get("host");*/
		return default_cred;
	}

	private ArrayNode getPrepareDb2Lst(ArrayNode hostnode, ObjectNode crednode, String name, String info) {
		ArrayNode filesteps = om.createArrayNode();
		ArrayNode raws = getraws(hostnode, crednode);
		String encodestr = EncodeUtil.encode(info.trim());
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "/script/prepare.db2.lst");
			steps.put("exec", amsprop.getProperty("db2_standalone_script_path")
					+ amsprop.getProperty("db2_standalone_prepare_lst"));
			steps.put("type", "scat");
			steps.put("text", "data:text/plain;base64," + encodestr);
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
			filesteps.add(steps);
		}
		return filesteps;
	}
	private ArrayNode getFiles(ArrayNode hostnode, ObjectNode crednode, String name, String filenamekey,
			String filepath) {
		ArrayNode filesteps = om.createArrayNode();
		String filepath1 = amsprop.getProperty(filepath);
		ArrayNode filenames = AMS2KeyUtil.getArrayNodeByKey(filenamekey);
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode fn : filenames) {
			String filename = fn.get("name").asText();
			for (JsonNode rs : raws) {
				ObjectNode step = om.createObjectNode();
				step.set("node", rs.get("node"));
				step.set("cred", rs.get("cred"));
				step.put("async", true);
				// step.put("exec", "/script/" + filename);
				step.put("exec", amsprop.getProperty("db2_standalone_script_path") + filename);
				step.put("type", "sput");
				step.put("file", filepath1 + filename);
				step.put("name", name + "#" + rs.get("node").get("host").asText() + "#" + filename);
				filesteps.add(step);
			}
		}
		return filesteps;
	}
	private ArrayNode getChmod(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "chmod +x /script/*.ksh && chmod +x
			// /script/*.sh");
			steps.put("exec", "chmod +x " + amsprop.getProperty("db2_standalone_script_path") + "*.ksh && chmod +x "
					+ amsprop.getProperty("db2_standalone_script_path") + "*.sh");
			steps.put("type", "scmd");
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}
	private ArrayNode getSetHostname(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "cd /script && sh ./hostname.helper.sh
			// "+rs.get("node").get("host").asText());
			steps.put("exec", "cd " + amsprop.getProperty("db2_standalone_script_path_sub") + " && sh ./"
					+ amsprop.getProperty("db2_standalone_hostname_sh") + " " + rs.get("node").get("host").asText());
			steps.put("type", "scmd");
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}
	private ArrayNode getPrepareDb2Ksh(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode filesteps = om.createArrayNode();
		ArrayNode raws = getraws(hostnode, crednode);

		for (int i = 0; i < raws.size(); i++) {
			ObjectNode rs = (ObjectNode) raws.get(i);
			ObjectNode step = om.createObjectNode();
			step.set("node", rs.get("node"));
			step.set("cred", rs.get("cred"));
			step.put("async", true);
			// step.put("exec", "cd /script && ./prepare.db2.ksh
			// "+hostnode.get(i).get("role").asText());
			step.put("exec",
					"cd " + amsprop.getProperty("db2_standalone_script_path_sub") + " && ./"
							+ amsprop.getProperty("db2_standalone_install_step_name") + " "
							+ hostnode.get(i).get("host").asText());
			step.put("type", "scmd");
			step.put("name", name + "#" + rs.get("node").get("host").asText());
			filesteps.add(step);
		}
		return filesteps;
	}

	private ArrayNode getInstallDb2Ksh(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode filesteps = om.createArrayNode();

		ArrayNode raws = getraws(hostnode, crednode);
		ObjectNode step = om.createObjectNode();
		step.set("node", raws.get(0).get("node"));
		step.set("cred", raws.get(0).get("cred"));
		step.put("async", true);
		// step.put("exec", "cd /script && ./install.db2.ksh ha1");
		step.put("exec", "cd " + amsprop.getProperty("script_path_sub") + " && ./"
				+ amsprop.getProperty("db2_cluster_step_name") + " " + hostnode.get(0).get("host").asText());
		step.put("type", "scmd");
		step.put("name", name + "#" + raws.get(0).get("node").get("host").asText());
		filesteps.add(step);
		return filesteps;
	}
	private ArrayNode getNFSDb2Ksh(ArrayNode hostnode, ObjectNode crednode, String name) {

		ArrayNode filesteps = om.createArrayNode();
		ArrayNode raws = getraws(hostnode, crednode);

		for (int i = 0; i < raws.size(); i++) {
			ObjectNode rs = (ObjectNode) raws.get(i);
			ObjectNode step = om.createObjectNode();
			step.set("node", rs.get("node"));
			step.set("cred", rs.get("cred"));
			step.put("async", true);
			step.put("exec", "cd " + amsprop.getProperty("db2_standalone_script_path_sub") + " && ./"
					+ amsprop.getProperty("db2_standalone_nfs_step_name"));
			step.put("type", "scmd");
			step.put("name", name + "#" + rs.get("node").get("host").asText());
			filesteps.add(step);
		}
		return filesteps;
	}
}
