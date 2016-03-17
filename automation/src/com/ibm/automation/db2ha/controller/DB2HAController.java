package com.ibm.automation.db2ha.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.ibm.automation.core.util.EncodeUtil;
import com.ibm.automation.core.util.FormatUtil;
import com.ibm.automation.core.util.HttpClientUtil;
import com.ibm.automation.core.util.PropertyUtil;



public class DB2HAController {
	@Autowired
	private AmsRestService amsRestService;

	Properties p = PropertyUtil.getResourceFile("config/properties/cloud.properties");
	Properties amsprop = PropertyUtil.getResourceFile("config/properties/ams2.properties");
	AmsClient amsClient = new AmsV2ClientHttpClient4Impl();
	private static final Logger logger = Logger.getLogger(DB2HAController.class);
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
		}
		return null;
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

	ObjectMapper om = new ObjectMapper();
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

	private String[] getStrArr(String string) {
		string = string.substring(1, string.length() - 1);
		return string.split(",");
	}

	private String getArrayNumByName(String[] vgtype, String[] hdiskname, String name) {
		String restr = "";
		for (int i = 0; i < vgtype.length / 2; i++) {
			if (vgtype[i].trim().equals(name)) {
				restr = restr + hdiskname[i].trim() + ",";
			}
		}
		return restr.substring(0, restr.length() - 1);
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
		logger.info("pvcnode:::" + obj);
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
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/toNextPage")
	public String toNextPage(HttpServletRequest request, HttpSession session) throws Exception {
		ObjectMapper om = new ObjectMapper();
		/*
		 * String tenantId = ""; String tokenId = ""; try { tenantId =
		 * OpenStackUtil.getTenantId(); tokenId = OpenStackUtil.getTokenId(); }
		 * catch (LoginTimeOutException e) { return this.setExceptionTologin(e,
		 * request); }
		 */
		// 获取跳转页面信息
		String status = request.getParameter("status");
		// 获取ID
		String serId = request.getParameter("serId");
		String ptype = request.getParameter("ptype");
		// 第一步host信息 获取输入的HOST参数.
	//	System.out.println("hostNames=" + request.getParameterValues("hostNames"));
		request.setAttribute("hostNames", request.getParameterValues("hostNames") == null ? ""
				: Arrays.asList(request.getParameterValues("hostNames")));
		request.setAttribute("hostIps", request.getParameterValues("hostIps") == null ? ""
				: Arrays.asList(request.getParameterValues("hostIps")));
		request.setAttribute("serNames", request.getParameterValues("serNames") == null ? ""
				: Arrays.asList(request.getParameterValues("serNames")));
		request.setAttribute("serIps", request.getParameterValues("serIps") == null ? ""
				: Arrays.asList(request.getParameterValues("serIps")));
		request.setAttribute("perNames", request.getParameterValues("perNames") == null ? ""
				: Arrays.asList(request.getParameterValues("perNames")));
		request.setAttribute("perIps", request.getParameterValues("perIps") == null ? ""
				: Arrays.asList(request.getParameterValues("perIps")));

		// 基本
		request.setAttribute("haname", request.getParameter("haname"));
		request.setAttribute("ha_RGNmae", request.getParameter("ha_RGNmae"));
		request.setAttribute("ha_ASName", request.getParameter("ha_ASName"));
		request.setAttribute("ha_primaryNode", request.getParameter("ha_primaryNode"));

		// IP
		request.setAttribute("hostId1", request.getParameter("hostId1"));
		request.setAttribute("hostId2", request.getParameter("hostId2"));
		request.setAttribute("ha_ip1", request.getParameter("ha_ip1"));
		request.setAttribute("ha_ip2", request.getParameter("ha_ip2"));
		request.setAttribute("ha_bootip1", request.getParameter("ha_bootip1"));
		request.setAttribute("ha_bootip2", request.getParameter("ha_bootip2"));
		request.setAttribute("ha_svcip", request.getParameter("ha_svcip"));

		// 主机别名
		request.setAttribute("ha_hostname1", request.getParameter("ha_hostname1"));
		request.setAttribute("ha_hostname2", request.getParameter("ha_hostname2"));
		request.setAttribute("ha_bootalias1", request.getParameter("ha_bootalias1"));
		request.setAttribute("ha_bootalias2", request.getParameter("ha_bootalias2"));
		request.setAttribute("ha_svcalias", request.getParameter("ha_svcalias"));

		request.setAttribute("hostName", request.getParameter("hostName"));
		request.setAttribute("hostIp", request.getParameter("hostIp"));
		request.setAttribute("serName", request.getParameter("serName"));
		request.setAttribute("serIp", request.getParameter("serIp"));
		request.setAttribute("perName", request.getParameter("perName"));
		request.setAttribute("perIp", request.getParameter("perIp"));

		// 旧VG信息
		request.setAttribute("hdisknames", request.getParameterValues("hdisknames") == null ? ""
				: Arrays.asList(request.getParameterValues("hdisknames")));
		request.setAttribute("hdiskids", request.getParameterValues("hdiskids") == null ? ""
				: Arrays.asList(request.getParameterValues("hdiskids")));
		request.setAttribute("vgtypes", request.getParameterValues("vgtypes") == null ? ""
				: Arrays.asList(request.getParameterValues("vgtypes")));

		request.setAttribute("hdiskname", request.getParameter("hdiskname"));
		request.setAttribute("hdiskid", request.getParameter("hdiskid"));
		request.setAttribute("vgtype", request.getParameter("vgtype"));
		// VG
		request.setAttribute("ha_vgdb2home", request.getParameter("ha_vgdb2home"));
		request.setAttribute("ha_vgdb2log", request.getParameter("ha_vgdb2log"));
		request.setAttribute("ha_vgdb2archlog", request.getParameter("ha_vgdb2archlog"));
		// request.setAttribute("ha_vgdb2backup",
		// request.getParameter("ha_vgdb2backup"));
		request.setAttribute("ha_vgdataspace", request.getParameter("ha_vgdataspace"));
		// request.setAttribute("ha_vgdataspace2",
		// request.getParameter("ha_vgdataspace2"));
		// request.setAttribute("ha_vgdataspace3",
		// request.getParameter("ha_vgdataspace3"));
		// request.setAttribute("ha_vgdataspace4",
		// request.getParameter("ha_vgdataspace4"));
		request.setAttribute("ha_vgcaap", request.getParameter("ha_vgcaap"));

		// PV
		request.setAttribute("ha_db2homepv", request.getParameter("ha_db2homepv"));
		request.setAttribute("ha_db2logpv", request.getParameter("ha_db2logpv"));
		request.setAttribute("ha_db2archlogpv", request.getParameter("ha_db2archlogpv"));
		// request.setAttribute("ha_db2backuppv",
		// request.getParameter("ha_db2backuppv"));
		request.setAttribute("ha_dataspace1pv", request.getParameter("ha_dataspace1pv"));
		// request.setAttribute("ha_dataspace2pv",
		// request.getParameter("ha_dataspace2pv"));
		// request.setAttribute("ha_dataspace3pv",
		// request.getParameter("ha_dataspace3pv"));
		// request.setAttribute("ha_dataspace4pv",
		// request.getParameter("ha_dataspace4pv"));
		request.setAttribute("ha_caappv", request.getParameter("ha_caappv"));

		// VG创建方式
		request.setAttribute("ha_db2homemode", request.getParameter("ha_db2homemode"));
		request.setAttribute("ha_db2logmode", request.getParameter("ha_db2logmode"));
		request.setAttribute("ha_db2archlogmode", request.getParameter("ha_db2archlogmode"));
		// request.setAttribute("ha_db2backupmode",
		// request.getParameter("ha_db2backupmode"));
		request.setAttribute("ha_dataspacemode", request.getParameter("ha_dataspacemode"));
		// request.setAttribute("ha_dataspace2mode",
		// request.getParameter("ha_dataspace2mode"));
		// request.setAttribute("ha_dataspace3mode",
		// request.getParameter("ha_dataspace3mode"));
		// request.setAttribute("ha_dataspace4mode",
		// request.getParameter("ha_dataspace4mode"));
		request.setAttribute("ha_caapmode", request.getParameter("ha_caapmode"));

		// HA切换策略 BEGIN
		request.setAttribute("ha_startpolicy", request.getParameter("ha_startpolicy"));
		request.setAttribute("ha_fopolicy", request.getParameter("ha_fopolicy"));
		request.setAttribute("ha_fbpolicy", request.getParameter("ha_fbpolicy"));

		// NFS BEGIN -->
		request.setAttribute("ha_nfsON", request.getParameter("ha_nfsON"));

		request.setAttribute("ha_nfsIP1", request.getParameter("ha_nfsIP1"));
		request.setAttribute("ha_nfsSPoint1", request.getParameter("ha_nfsSPoint1"));
		request.setAttribute("ha_nfsCPoint1", request.getParameter("ha_nfsCPoint1"));

		request.setAttribute("ha_nfsIP2", request.getParameter("ha_nfsIP2"));
		request.setAttribute("ha_nfsSPoint2", request.getParameter("ha_nfsSPoint2"));
		request.setAttribute("ha_nfsCPoint2", request.getParameter("ha_nfsCPoint2"));

		request.setAttribute("ha_nfsIP3", request.getParameter("ha_nfsIP3"));
		request.setAttribute("ha_nfsSPoint3", request.getParameter("ha_nfsSPoint3"));
		request.setAttribute("ha_nfsCPoint3", request.getParameter("ha_nfsCPoint3"));

		request.setAttribute("ha_nfsIP4", request.getParameter("ha_nfsIP4"));
		request.setAttribute("ha_nfsSPoint4", request.getParameter("ha_nfsSPoint4"));
		request.setAttribute("ha_nfsCPoint4", request.getParameter("ha_nfsCPoint4"));

		request.setAttribute("ha_nfsIP5", request.getParameter("ha_nfsIP5"));
		request.setAttribute("ha_nfsSPoint5", request.getParameter("ha_nfsSPoint5"));
		request.setAttribute("ha_nfsCPoint5", request.getParameter("ha_nfsCPoint5"));

		request.setAttribute("ha_nfsIP6", request.getParameter("ha_nfsIP6"));
		request.setAttribute("ha_nfsSPoint6", request.getParameter("ha_nfsSPoint6"));
		request.setAttribute("ha_nfsCPoint6", request.getParameter("ha_nfsCPoint6"));

		request.setAttribute("ha_nfsIP7", request.getParameter("ha_nfsIP7"));
		request.setAttribute("ha_nfsSPoint7", request.getParameter("ha_nfsSPoint7"));
		request.setAttribute("ha_nfsCPoint7", request.getParameter("ha_nfsCPoint7"));

		request.setAttribute("ha_nfsIP8", request.getParameter("ha_nfsIP8"));
		request.setAttribute("ha_nfsSPoint8", request.getParameter("ha_nfsSPoint8"));
		request.setAttribute("ha_nfsCPoint8", request.getParameter("ha_nfsCPoint8"));

		request.setAttribute("ha_nfsIP9", request.getParameter("ha_nfsIP9"));
		request.setAttribute("ha_nfsSPoint9", request.getParameter("ha_nfsSPoint9"));
		request.setAttribute("ha_nfsCPoint9", request.getParameter("ha_nfsCPoint9"));

		request.setAttribute("ha_nfsIP10", request.getParameter("ha_nfsIP10"));
		request.setAttribute("ha_nfsSPoint10", request.getParameter("ha_nfsSPoint10"));
		request.setAttribute("ha_nfsCPoint10", request.getParameter("ha_nfsCPoint10"));

		request.setAttribute("ha_nfsIP11", request.getParameter("ha_nfsIP11"));
		request.setAttribute("ha_nfsSPoint11", request.getParameter("ha_nfsSPoint11"));
		request.setAttribute("ha_nfsCPoint11", request.getParameter("ha_nfsCPoint11"));

		request.setAttribute("ha_nfsIP12", request.getParameter("ha_nfsIP12"));
		request.setAttribute("ha_nfsSPoint12", request.getParameter("ha_nfsSPoint12"));
		request.setAttribute("ha_nfsCPoint12", request.getParameter("ha_nfsCPoint12"));

		request.setAttribute("ha_nfsIP13", request.getParameter("ha_nfsIP13"));
		request.setAttribute("ha_nfsSPoint13", request.getParameter("ha_nfsSPoint13"));
		request.setAttribute("ha_nfsCPoint13", request.getParameter("ha_nfsCPoint13"));

		request.setAttribute("ha_nfsIP14", request.getParameter("ha_nfsIP14"));
		request.setAttribute("ha_nfsSPoint14", request.getParameter("ha_nfsSPoint14"));
		request.setAttribute("ha_nfsCPoint14", request.getParameter("ha_nfsCPoint14"));

		request.setAttribute("ha_nfsIP15", request.getParameter("ha_nfsIP15"));
		request.setAttribute("ha_nfsSPoint15", request.getParameter("ha_nfsSPoint15"));
		request.setAttribute("ha_nfsCPoint15", request.getParameter("ha_nfsCPoint15"));

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

		List serIds = new ArrayList();
		if (serId != null && serId != "") {
			String[] ss = serId.split(",");
			for (int i = 0; i < ss.length; i++) {
				serIds.add(ss[i]);
			}
		}

		ArrayNode lists = amsRestService.getList(null, null, "odata/servers");
//		System.out.println(lists);

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

		List<AddHostBean> listDetial = new ArrayList<AddHostBean>();

		for (int i = 0; i < lahb.size(); i++) {
			String serverId = lahb.get(i).get_id();
			for (int j = 0; j < serIds.size(); j++) {
				if (serverId.equals(serIds.get(j))) {
					listDetial.add(lahb.get(i));
				}
			}
		}
		Collections.sort(listDetial);
		request.setAttribute("serId", serId);
		request.setAttribute("servers", listDetial);
		request.setAttribute("total", lahb.size());
		request.setAttribute("ptype", ptype);
		if (status.equals("configConfirm")) {
			return "instance_aix_db2ha_config_confirm";
		} else if (status.equals("installPage")) {
			ObjectNode db2Config = AMS2KeyUtil.getDb2ConfigInfo();
			request.setAttribute("db2Config", db2Config);
			return "instance_aix_db2ha_install";
		} else if (status.equals("installConfirm")) {
			return "instance_aix_db2ha_install_confirm";
		}

		// 新增页面Star=========
		else if (status.equals("installPageNew")) {
	//		System.out.println("=============ceshixinyemian================");
			ObjectNode basicInfo = AMS2KeyUtil.getBasicInfo();
			request.setAttribute("basicInfo", basicInfo);
			ObjectNode instProp = AMS2KeyUtil.getInstAdvProp();
			request.setAttribute("instProp", instProp);
			ObjectNode dbProp = AMS2KeyUtil.getDB2AdvProp();
			request.setAttribute("dbProp", dbProp);

			String ha_dataspace1pv = request.getParameter("ha_dataspace1pv");
	//		System.out.println("=========ha_dataspace1pv=========" + ha_dataspace1pv);

			String[] dataspcepvs = ha_dataspace1pv.split(",");
			String db2_dbdatapath = "";
			for (int i = 1; i <= dataspcepvs.length; i++) {
				db2_dbdatapath = db2_dbdatapath + "/db2dataspace" + i + ",";
			}
			if ("" != db2_dbdatapath) {
				db2_dbdatapath = db2_dbdatapath.substring(0, db2_dbdatapath.length() - 1);
			}
	//		System.out.println("====db2_dbdatapath====" + db2_dbdatapath);
			request.setAttribute("db2_dbdatapath", db2_dbdatapath);
			return "instance_aix_db2ha_config_new";
		} else if (status.equals("installConfirmNew")) {

		///	System.out.println("=======new_confirm======");
		//	System.out.println("db2base=:" + request.getParameter("db2base"));
			request.setAttribute("db2base", request.getParameter("db2base"));

		//	System.out.println("*********new_confirm*******");
			return "instance_aix_db2ha_config_new_confirm";
		}
		// 新增页面End=========

		else if (status.equals("makeVgPage")) {

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
					// hd.put("hdiskid", cells[1]);
					hdisks.add(hd);
					// }
				}
				String hpn = request.getParameter("ha_primaryNode");
				String hi1 = request.getParameter("ha_ip1");
				String hi2 = request.getParameter("ha_ip2");
				String hh1 = request.getParameter("ha_hostname1");
				String hh2 = request.getParameter("ha_hostname2");
				if (hpn != null && hi1 != null && hh1 != null && hh1.equals(hpn)) {
					if (instaddr != null && instaddr.equals(hi1)) {
						allHdisk.add(hdisks);
					}
				}
				if (hpn != null && hi2 != null && hh2 != null && hh2.equals(hpn)) {
					if (instaddr != null && instaddr.equals(hi2)) {
						allHdisk.add(hdisks);
					}
				}
			}
			request.setAttribute("allHdisk", allHdisk);
			return "instance_aix_db2ha_makevg";

		} else if (status.equals("db2haConfirm")) {
			String haname = request.getParameter("haname");

			String[] hostName = getStrArr(request.getParameter("hostName"));
			String[] serName = getStrArr(request.getParameter("serName"));
		//	System.out.println(hostName);

			String[] hdiskname = getStrArr(request.getParameter("hdiskname"));
			String[] vgtype = getStrArr(request.getParameter("vgtype"));

			String caappv = getArrayNumByName(vgtype, hdiskname, "caavg-private");
			String datavg2 = getArrayNumByName(vgtype, hdiskname, "datavg2");
			String datavg1 = getArrayNumByName(vgtype, hdiskname, "datavg1");

			String hasetup = getHaSetup(haname, hostName, serName[0], caappv);
			request.setAttribute("hasetup", hasetup);
			String mkvg = getMkvg(hostName[0], datavg1, datavg2);
			request.setAttribute("mkvg", mkvg);
			String importvg = getImportvg(hostName[1], datavg1, datavg2);
			request.setAttribute("importvg", importvg);
			return "instance_aix_db2ha_confirm";
		}
		return null;
	}

	/**
	 * 拼接前台传过来的db2安装参数
	 * 
	 * @param request
	 * @return
	 */
	private String setConfigMsg(HttpServletRequest request) {
		String lineSep = "\n";

		String ha_primaryIP = "";
		String ha_primaryBootIP = "";
		String ha_primaryBootAlias = "";
		if (request.getParameter("ha_primaryNode").equals(request.getParameter("ha_hostname1"))) {
			ha_primaryIP = request.getParameter("ha_ip1");
			ha_primaryBootIP = request.getParameter("ha_ip1");
			ha_primaryBootAlias = request.getParameter("ha_bootalias1");
		} else {
			ha_primaryIP = request.getParameter("ha_ip2");
			ha_primaryBootIP = request.getParameter("ha_ip2");
			ha_primaryBootAlias = request.getParameter("ha_bootalias2");
		}
		String retstr =
		// =====HA参数=======
		// ---基本---
		"ha_clusterName=" + request.getParameter("ha_clusterName") + lineSep + "ha_RGNmae="
				+ request.getParameter("ha_RGNmae") + lineSep + "ha_ASName=" + request.getParameter("ha_ASName")
				+ lineSep + "ha_primaryNode=" + request.getParameter("ha_primaryNode") + lineSep +

		// ---基本---

		// ---IP---
		"ha_ip1=" + request.getParameter("ha_ip1") + lineSep + "ha_ip2=" + request.getParameter("ha_ip2") + lineSep
				+ "ha_bootip1=" + request.getParameter("ha_bootip1") + lineSep + "ha_bootip2="
				+ request.getParameter("ha_bootip2") + lineSep + "ha_svcip=" + request.getParameter("ha_svcip")
				+ lineSep +
				// 新增主节点的IP和BootIP
				"ha_primaryIP=" + ha_primaryIP + lineSep + "ha_primaryBootIP=" + ha_primaryBootIP + lineSep +
				// ---IP---

		// ---主机别名---
		"ha_hostname1=" + request.getParameter("ha_hostname1") + lineSep + "ha_hostname2="
				+ request.getParameter("ha_hostname2") + lineSep + "ha_bootalias1="
				+ request.getParameter("ha_bootalias1") + lineSep + "ha_bootalias2="
				+ request.getParameter("ha_bootalias2") + lineSep + "ha_svcalias=" + request.getParameter("ha_svcalias")
				+ lineSep + "ha_primaryBootAlias=" + ha_primaryBootAlias + lineSep +
				// ---主机别名---

		// ---VG---
		"ha_vgdb2home=" + request.getParameter("ha_vgdb2home") + lineSep + "ha_vgdb2log="
				+ request.getParameter("ha_vgdb2log") + lineSep + "ha_vgdb2archlog="
				+ request.getParameter("ha_vgdb2archlog") + lineSep +
				// "ha_vgdb2backup="+request.getParameter("ha_vgdb2backup")+lineSep
				// +
		"ha_vgdataspace=" + request.getParameter("ha_vgdataspace") + lineSep +
				// "ha_vgdataspace2="+request.getParameter("ha_vgdataspace2")+lineSep
				// +
				// "ha_vgdataspace3="+request.getParameter("ha_vgdataspace3")+lineSep
				// +
				// "ha_vgdataspace4="+request.getParameter("ha_vgdataspace4")+lineSep
				// +
		"ha_vgcaap=" + request.getParameter("ha_vgcaap") + lineSep +
				// ---VG---

		// ---PV---
		"ha_db2homepv=" + request.getParameter("ha_db2homepv") + lineSep + "ha_db2logpv="
				+ request.getParameter("ha_db2logpv") + lineSep + "ha_db2archlogpv="
				+ request.getParameter("ha_db2archlogpv") + lineSep +
				// "ha_db2backuppv="+request.getParameter("ha_db2backuppv")+lineSep
				// +
		"ha_dataspacepv=" + request.getParameter("ha_dataspace1pv") + lineSep +
				// "ha_dataspace2pv="+request.getParameter("ha_dataspace2pv")+lineSep
				// +
				// "ha_dataspace3pv="+request.getParameter("ha_dataspace3pv")+lineSep
				// +
				// "ha_dataspace4pv="+request.getParameter("ha_dataspace4pv")+lineSep
				// +
		"ha_caappv=" + request.getParameter("ha_caappv") + lineSep +
				// ---PV---

		// ---VG创建方式---
		"ha_db2homemode=" + request.getParameter("ha_db2homemode") + lineSep + "ha_db2logmode="
				+ request.getParameter("ha_db2logmode") + lineSep + "ha_db2archlogmode="
				+ request.getParameter("ha_db2archlogmode") + lineSep +
				// "ha_db2backupmode="+request.getParameter("ha_db2backupmode")+lineSep
				// +
		"ha_dataspacemode=" + request.getParameter("ha_dataspacemode") + lineSep +
				// "ha_dataspace2mode="+request.getParameter("ha_dataspace2mode")+lineSep
				// +
				// "ha_dataspace3mode="+request.getParameter("ha_dataspace3mode")+lineSep
				// +
				// "ha_dataspace4mode="+request.getParameter("ha_dataspace4mode")+lineSep
				// +
		"ha_caapmode=" + request.getParameter("ha_caapmode") + lineSep +
				// ---VG创建方式---

		// HA切换策略 BEGIN
		"ha_startpolicy=" + request.getParameter("ha_startpolicy") + lineSep + "ha_fopolicy="
				+ request.getParameter("ha_fopolicy") + lineSep + "ha_fbpolicy=" + request.getParameter("ha_fbpolicy")
				+ lineSep +
				// NFS BEGIN -->
				"ha_nfsON=" + request.getParameter("ha_nfsON") + lineSep +

		"ha_nfsIP1=" + request.getParameter("ha_nfsIP1") + lineSep + "ha_nfsSPoint1="
				+ request.getParameter("ha_nfsSPoint1") + lineSep + "ha_nfsCPoint1="
				+ request.getParameter("ha_nfsCPoint1") + lineSep +

		"ha_nfsIP2=" + request.getParameter("ha_nfsIP2") + lineSep + "ha_nfsSPoint2="
				+ request.getParameter("ha_nfsSPoint2") + lineSep + "ha_nfsCPoint2="
				+ request.getParameter("ha_nfsCPoint2") + lineSep +

		"ha_nfsIP3=" + request.getParameter("ha_nfsIP3") + lineSep + "ha_nfsSPoint3="
				+ request.getParameter("ha_nfsSPoint3") + lineSep + "ha_nfsCPoint3="
				+ request.getParameter("ha_nfsCPoint3") + lineSep +

		"ha_nfsIP4=" + request.getParameter("ha_nfsIP4") + lineSep + "ha_nfsSPoint4="
				+ request.getParameter("ha_nfsSPoint4") + lineSep + "ha_nfsCPoint4="
				+ request.getParameter("ha_nfsCPoint4") + lineSep +

		"ha_nfsIP5=" + request.getParameter("ha_nfsIP5") + lineSep + "ha_nfsSPoint5="
				+ request.getParameter("ha_nfsSPoint5") + lineSep + "ha_nfsCPoint5="
				+ request.getParameter("ha_nfsCPoint5") + lineSep +

		"ha_nfsIP6=" + request.getParameter("ha_nfsIP6") + lineSep + "ha_nfsSPoint6="
				+ request.getParameter("ha_nfsSPoint6") + lineSep + "ha_nfsCPoint6="
				+ request.getParameter("ha_nfsCPoint6") + lineSep +

		"ha_nfsIP7=" + request.getParameter("ha_nfsIP7") + lineSep + "ha_nfsSPoint7="
				+ request.getParameter("ha_nfsSPoint7") + lineSep + "ha_nfsCPoint7="
				+ request.getParameter("ha_nfsCPoint7") + lineSep +

		"ha_nfsIP8=" + request.getParameter("ha_nfsIP8") + lineSep + "ha_nfsSPoint8="
				+ request.getParameter("ha_nfsSPoint8") + lineSep + "ha_nfsCPoint8="
				+ request.getParameter("ha_nfsCPoint8") + lineSep +

		"ha_nfsIP9=" + request.getParameter("ha_nfsIP9") + lineSep + "ha_nfsSPoint9="
				+ request.getParameter("ha_nfsSPoint9") + lineSep + "ha_nfsCPoint9="
				+ request.getParameter("ha_nfsCPoint9") + lineSep +

		"ha_nfsIP10=" + request.getParameter("ha_nfsIP10") + lineSep + "ha_nfsSPoint10="
				+ request.getParameter("ha_nfsSPoint10") + lineSep + "ha_nfsCPoint10="
				+ request.getParameter("ha_nfsCPoint10") + lineSep +

		"ha_nfsIP11=" + request.getParameter("ha_nfsIP11") + lineSep + "ha_nfsSPoint11="
				+ request.getParameter("ha_nfsSPoint11") + lineSep + "ha_nfsCPoint11="
				+ request.getParameter("ha_nfsCPoint11") + lineSep +

		"ha_nfsIP12=" + request.getParameter("ha_nfsIP12") + lineSep + "ha_nfsSPoint12="
				+ request.getParameter("ha_nfsSPoint12") + lineSep + "ha_nfsCPoint12="
				+ request.getParameter("ha_nfsCPoint12") + lineSep +

		"ha_nfsIP13=" + request.getParameter("ha_nfsIP13") + lineSep + "ha_nfsSPoint13="
				+ request.getParameter("ha_nfsSPoint13") + lineSep + "ha_nfsCPoint13="
				+ request.getParameter("ha_nfsCPoint13") + lineSep +

		"ha_nfsIP14=" + request.getParameter("ha_nfsIP14") + lineSep + "ha_nfsSPoint14="
				+ request.getParameter("ha_nfsSPoint14") + lineSep + "ha_nfsCPoint14="
				+ request.getParameter("ha_nfsCPoint14") + lineSep +

		"ha_nfsIP15=" + request.getParameter("ha_nfsIP15") + lineSep + "ha_nfsSPoint15="
				+ request.getParameter("ha_nfsSPoint15") + lineSep + "ha_nfsCPoint15="
				+ request.getParameter("ha_nfsCPoint15") + lineSep +

		// =====DB2 页面参数 star=======
		// ------基本信息------
		// db2_version
		"db2_version=" + request.getParameter("db2_version") + lineSep +
				// db2_db2base
				"db2_db2base=" + request.getParameter("db2_db2base") + lineSep +
				// db2_dbpath
				"db2_dbpath=" + request.getParameter("db2_dbpath") + lineSep +
				// db2_svcename
				"db2_svcename=" + request.getParameter("db2_svcename") + lineSep +
				// db2_db2insusr
				"db2_db2insusr=" + request.getParameter("db2_db2insusr") + lineSep +
				// db2_dbname
				"db2_dbname=" + request.getParameter("db2_dbname") + lineSep +
				// db2_codeset
				"db2_codeset=" + request.getParameter("db2_codeset") + lineSep +
				// db2_dbdatapath
				"db2_dbdatapath=" + request.getParameter("db2_dbdatapath") + lineSep +

		// ------实例高级属性-------
		// db2_db2insusr 基本信息已有
		// "db2_db2insusr="+request.getParameter("db2_db2insusr")+lineSep +
		// db2_db2insgrp
		"db2_db2insgrp=" + request.getParameter("db2_db2insgrp") + lineSep +
				// db2_db2fenusr
				"db2_db2fenusr=" + request.getParameter("db2_db2fenusr") + lineSep +
				// db2_db2fengrp
				"db2_db2fengrp=" + request.getParameter("db2_db2fengrp") + lineSep +
				// db2_db2comm
				"db2_db2comm=" + request.getParameter("db2_db2comm") + lineSep +
				// db2_db2codepage
				"db2_db2codepage=" + request.getParameter("db2_db2codepage") + lineSep +
				// db2_initagents
				"db2_initagents=" + request.getParameter("db2_initagents") + lineSep +
				// db2_max_coordagents
				"db2_max_coordagents=" + request.getParameter("db2_max_coordagents") + lineSep +
				// db2_max_connectings
				"db2_max_connectings=" + request.getParameter("db2_max_connectings") + lineSep +
				// db2_poolagents
				"db2_poolagents=" + request.getParameter("db2_poolagents") + lineSep +
				// db2_diagsize
				"db2_diagsize=" + request.getParameter("db2_diagsize") + lineSep +
				// db2_mon_buf
				"db2_mon_buf=" + request.getParameter("db2_mon_buf") + lineSep +
				// db2_mon_lock
				"db2_mon_lock=" + request.getParameter("db2_mon_lock") + lineSep +
				// db2_mon_sort
				"db2_mon_sort=" + request.getParameter("db2_mon_sort") + lineSep +
				// db2_mon_stmt
				"db2_mon_stmt=" + request.getParameter("db2_mon_stmt") + lineSep +
				// db2_mon_table
				"db2_mon_table=" + request.getParameter("db2_mon_table") + lineSep +
				// db2_mon_uow
				"db2_mon_uow=" + request.getParameter("db2_mon_uow") + lineSep +
				// db2_health_mon
				"db2_health_mon=" + request.getParameter("db2_health_mon") + lineSep +
				// db2_mon_heap
				"db2_mon_heap=" + request.getParameter("db2_mon_heap") + lineSep +

		// -------数据库高级属性------
		// db2_db2log
		"db2_db2log=" + request.getParameter("db2_db2log") + lineSep +
				// db2_backuppath
				"db2_backuppath=" + request.getParameter("db2_backuppath") + lineSep +
				// db2_logarchpath
				"db2_logarchpath=" + request.getParameter("db2_logarchpath") + lineSep +
				// db2_stmm
				"db2_stmm=" + request.getParameter("db2_stmm") + lineSep +
				// db2_locklist
				"db2_locklist=" + request.getParameter("db2_locklist") + lineSep +
				// db2_maxlocks
				"db2_maxlocks=" + request.getParameter("db2_maxlocks") + lineSep +
				// db2_locktimeout
				"db2_locktimeout=" + request.getParameter("db2_locktimeout") + lineSep +
				// db2_sortheap
				"db2_sortheap=" + request.getParameter("db2_sortheap") + lineSep +
				// db2_logfilesize
				"db2_logfilesize=" + request.getParameter("db2_logfilesize") + lineSep +
				// db2_logprimary
				"db2_logprimary=" + request.getParameter("db2_logprimary") + lineSep +
				// db2_logsecond
				"db2_logsecond=" + request.getParameter("db2_logsecond") + lineSep +
				// db2_logbuff
				"db2_logbuff=" + request.getParameter("db2_logbuff") + lineSep +
				// db2_softmax
				"db2_softmax=" + request.getParameter("db2_softmax") + lineSep +
				// db2_trackmod
				"db2_trackmod=" + request.getParameter("db2_trackmod") + lineSep +
				// db2_pagesize
				"db2_pagesize=" + request.getParameter("db2_pagesize") + lineSep +

		// =====DB2 页面参数 end=======
		// =====新增静态参数star======

		// db2homelv=db2homelv
		// "db2homelv="+amsprop.getProperty("db2homelv")+lineSep +
		// db2loglv=db2loglv
		// "db2loglv="+amsprop.getProperty("db2loglv")+lineSep +
		// db2archloglv=db2archloglv
		// "db2archloglv="+amsprop.getProperty("db2archloglv")+lineSep +
		// db2backuplv=db2backuplv
		// "db2backuplv="+amsprop.getProperty("db2backuplv")+lineSep +
		// dataspace1lv=dataspace1lv
		// "dataspace1lv="+amsprop.getProperty("dataspace1lv")+lineSep +
		// dataspace2lv=dataspace2lv
		// "dataspace2lv="+amsprop.getProperty("dataspace2lv")+lineSep +
		// dataspace3lv=dataspace3lv
		// "dataspace3lv="+amsprop.getProperty("dataspace3lv")+lineSep +
		// dataspace4lv=dataspace4lv
		// "dataspace4lv="+amsprop.getProperty("dataspace4lv")+lineSep +
		// db2homefs=/db2home
		// "db2homefs="+amsprop.getProperty("db2homefs")+lineSep +
		// db2logfs=/db2log
		// "db2logfs="+amsprop.getProperty("db2logfs")+lineSep +
		// db2archlogfs=/db2archlog
		// "db2archlogfs="+amsprop.getProperty("db2archlogfs")+lineSep +
		// db2backupfs=/db2backup
		// "db2backupfs="+amsprop.getProperty("db2backupfs")+lineSep +
		// dataspace1fs=/db2dataspace1
		// "dataspace1fs="+amsprop.getProperty("dataspace1fs")+lineSep +
		// dataspace2fs=/db2dataspace2
		// "dataspace2fs="+amsprop.getProperty("dataspace2fs")+lineSep +
		// dataspace3fs=/db2dataspace3
		// "dataspace3fs="+amsprop.getProperty("dataspace3fs")+lineSep +
		// dataspace4fs=/db2dataspace4
		// "dataspace4fs="+amsprop.getProperty("dataspace4fs")+lineSep +
		// harg1vgs=$ha_vgdb2home,$ha_vgdb2log,$ha_vgdb2archlog,$ha_vgdb2backup,$ha_vgdataspace1,$ha_vgdataspace2,$ha_vgdataspace3,$ha_vgdataspace4
		"harg1vgs=" + amsprop.getProperty("harg1vgs") + lineSep +
				// hamode=AA
				"hamode=" + amsprop.getProperty("hamode") + lineSep +
				// hargnum=1
				"hargnum=" + amsprop.getProperty("hargnum") + lineSep +
				// ftphost=10.48.0.210
				"ftphost=" + amsprop.getProperty("ftphost") + lineSep +
				// ftpuser=htzhang
				"ftpuser=" + amsprop.getProperty("ftpuser") + lineSep +
				// ftppass=8uhbvgy7
				"ftppass=" + amsprop.getProperty("ftppass") + lineSep +
				// ftppath=/pub/ahrccb/DB2/
				"ftppath=" + amsprop.getProperty("ftppath") + lineSep +
				// db2path=$db2homefs/install
				"db2path=" + amsprop.getProperty("db2path") + lineSep +

		// ======新增静态参数end=======

		"db2base=" + amsprop.getProperty("db2_path") + lineSep;
	//	System.out.println("====retstr:" + retstr);
		logger.info("db2ha retstr:"+retstr);
		return retstr;
	}

	/**
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
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/installDb2haInfo")
	public String installDb2haInfo(HttpServletRequest request, HttpSession session) throws Exception {

		ObjectMapper om = new ObjectMapper();
		// HOST
		/*
		 * String haname = request.getParameter("haname"); String[] hostName =
		 * getStrArr(request.getParameter("hostName")); String[] hostIp =
		 * getStrArr(request.getParameter("hostIp")); String[] serName =
		 * getStrArr(request.getParameter("serName")); String[] serIp =
		 * getStrArr(request.getParameter("serIp")); String[] perName =
		 * getStrArr(request.getParameter("perName")); String[] perIp =
		 * getStrArr(request.getParameter("perIp"));
		 */

		// 获取所有参数
		String retstr = setConfigMsg(request);
		String ptype = request.getParameter("ptype");
		ArrayNode hosts = om.createArrayNode();
		ArrayNode pvcclusternodes = om.createArrayNode();

		String ha_clusterName = request.getParameter("ha_clusterName");
		String ha_hostname1 = request.getParameter("ha_hostname1");
		String ha_hostname2 = request.getParameter("ha_hostname2");
		String ha_ip1 = request.getParameter("ha_ip1");
		String ha_ip2 = request.getParameter("ha_ip2");
		String ha_bootip1 = request.getParameter("ha_bootip1");
		String ha_bootip2 = request.getParameter("ha_bootip2");
		String ha_bootalias1 = request.getParameter("ha_bootalias1");
		String ha_bootalias2 = request.getParameter("ha_bootalias2");
		String ha_svcip = request.getParameter("ha_svcip");
		String ha_svcalias = request.getParameter("ha_svcalias");
		String hostId1 = request.getParameter("hostId1");
		String hostId2 = request.getParameter("hostId2");
		String ha_primaryNode = request.getParameter("ha_primaryNode");

		if (ha_primaryNode != null && ha_hostname1 != null && ha_primaryNode.equals(ha_hostname1)) {
			ObjectNode host = createDB2HostObjNode(ha_hostname1, ha_ip1, String.valueOf(1));
			// {"host":"wangzeyao1","addr":"10.58.0.120","role":"ha1"}
			hosts.add(host);
			String pvcnodeId = createPvcNodeObjNode(ha_hostname1, ha_ip1, hostId1, "DB2", String.valueOf(1));
			// ObjectNode clusterNode = createPvcClusterNode(String.valueOf(1),
			// ha_hostname1, ha_ip1, ha_ip1, pvcnodeId);
			ObjectNode clusterNode = createPvcClusterNode(ha_hostname1, ha_ip1, hostId1, pvcnodeId, String.valueOf(1));
			pvcclusternodes.add(clusterNode);

			host = createDB2HostObjNode(ha_hostname2, ha_ip2, String.valueOf(1));
			hosts.add(host);
			pvcnodeId = createPvcNodeObjNode(ha_hostname2, ha_ip2, hostId2, "DB2", String.valueOf(2));
			// clusterNode = createPvcClusterNode(String.valueOf(2),
			// ha_hostname2, ha_ip2, ha_ip2, pvcnodeId);
			clusterNode = createPvcClusterNode(ha_hostname2, ha_ip2, hostId2, pvcnodeId, String.valueOf(2));
			pvcclusternodes.add(clusterNode);
			request.setAttribute("ha_subNode", ha_hostname2);
		} else {
			ObjectNode host = createDB2HostObjNode(ha_hostname2, ha_ip2, String.valueOf(1));
			hosts.add(host);
			String pvcnodeId = createPvcNodeObjNode(ha_hostname2, ha_ip2, hostId2, "DB2", String.valueOf(1));
			// ObjectNode clusterNode = createPvcClusterNode(String.valueOf(2),
			// ha_hostname2, ha_ip2, ha_ip2, pvcnodeId);
			ObjectNode clusterNode = createPvcClusterNode(ha_hostname2, ha_ip2, hostId2, pvcnodeId, String.valueOf(1));
			pvcclusternodes.add(clusterNode);

			host = createDB2HostObjNode(ha_hostname1, ha_ip1, String.valueOf(1));
			hosts.add(host);
			pvcnodeId = createPvcNodeObjNode(ha_hostname1, ha_ip1, hostId1, "DB2", String.valueOf(2));
			// clusterNode = createPvcClusterNode(String.valueOf(1),
			// ha_hostname1, ha_ip1, ha_ip1, pvcnodeId);
			clusterNode = createPvcClusterNode(ha_hostname1, ha_ip1, hostId1, pvcnodeId, String.valueOf(2));
			pvcclusternodes.add(clusterNode);
			request.setAttribute("ha_subNode", ha_hostname1);
		}

		String type = request.getParameter("type");
	//	System.out.println("=========type(yes:立即创建,no:手动创建)=====:[" + type + "]");
		String nfsON = request.getParameter("ha_nfsON");
	//	System.out.println("=========ha_nfsON 是否挂载NFS文件系统(yes:是,no:否)=====:[" + nfsON + "]");
		ObjectNode dbnode = this.postDB2Run(hosts, null, retstr, type, nfsON);

		if (dbnode != null && dbnode.get("uuid") != null) {

			ObjectNode optionsInfo = om.createObjectNode();
			setOptionsInfo(request, optionsInfo);
			// 创建pvcclusters
			createPvcClustersObjNode(dbnode.get("uuid").asText(), pvcclusternodes, optionsInfo, ptype);
		}

		if ("yes".equals(type)) {

			return "redirect:/getLogInfo";
		} else {

			// 获取ID
			String serId = request.getParameter("serId");

			List serIds = new ArrayList();
			if (serId != null && serId != "") {
				String[] ss = serId.split(",");
				for (int i = 0; i < ss.length; i++) {
					serIds.add(ss[i]);
				}
			}

			ArrayNode lists = amsRestService.getList(null, null, "odata/servers");
	//		System.out.println(lists);

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
	//		System.out.println(lahb);

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

			request.setAttribute("serId", serId);
			request.setAttribute("servers", listDetial);

			// 基本
			request.setAttribute("ha_RGNmae", request.getParameter("ha_RGNmae"));
			request.setAttribute("ha_ASName", request.getParameter("ha_ASName"));
			request.setAttribute("ha_primaryNode", request.getParameter("ha_primaryNode"));

			// IP

			request.setAttribute("ha_ip1", request.getParameter("ha_ip1"));
			request.setAttribute("ha_ip2", request.getParameter("ha_ip2"));
			request.setAttribute("ha_svcip", request.getParameter("ha_svcip"));

			// 主机别名
			request.setAttribute("ha_hostname1", request.getParameter("ha_hostname1"));
			request.setAttribute("ha_hostname2", request.getParameter("ha_hostname2"));

			// HA名称(集群名)
			request.setAttribute("ha_clusterName", request.getParameter("ha_clusterName"));

			return "instance_aix_db2ha_uninstall_script";
		}

	}

	/**
	 * 构造cluster的options属性
	 * 
	 * @param request
	 * @param optionsInfo
	 */
	private void setOptionsInfo(HttpServletRequest request, ObjectNode optionsInfo) {

		// -------基本------
		// ha_clusterName
		optionsInfo.put("ha_clusterName", request.getParameter("ha_clusterName"));
		// ha_RGNmae
		optionsInfo.put("ha_RGNmae", request.getParameter("ha_RGNmae"));
		// ha_ASName
		optionsInfo.put("ha_ASName", request.getParameter("ha_ASName"));
		// ha_primaryNode
		optionsInfo.put("ha_primaryNode", request.getParameter("ha_primaryNode"));
		// --------IP-----
		// ha_ip1
		optionsInfo.put("ha_ip1", request.getParameter("ha_ip1"));
		// ha_ip2
		optionsInfo.put("ha_ip2", request.getParameter("ha_ip2"));
		// ha_bootip1
		optionsInfo.put("ha_bootip1", request.getParameter("ha_bootip1"));
		// ha_bootip2
		optionsInfo.put("ha_bootip2", request.getParameter("ha_bootip2"));
		// ha_svcip
		optionsInfo.put("ha_svcip", request.getParameter("ha_svcip"));

		// --------主机别名-----
		// ha_hostname1
		optionsInfo.put("ha_hostname1", request.getParameter("ha_hostname1"));
		// ha_hostname2
		optionsInfo.put("ha_hostname2", request.getParameter("ha_hostname2"));
		// ha_bootalias1
		optionsInfo.put("ha_bootalias1", request.getParameter("ha_bootalias1"));
		// ha_bootalias2
		optionsInfo.put("ha_bootalias2", request.getParameter("ha_bootalias2"));
		// ha_svcalias
		optionsInfo.put("ha_svcalias", request.getParameter("ha_svcalias"));

		// ---------VG------
		// ha_vgdb2home
		optionsInfo.put("ha_vgdb2home", request.getParameter("ha_vgdb2home"));
		// ha_vgdb2log
		optionsInfo.put("ha_vgdb2log", request.getParameter("ha_vgdb2log"));
		// ha_vgdb2archlog
		optionsInfo.put("ha_vgdb2archlog", request.getParameter("ha_vgdb2archlog"));
		// ha_vgdataspace1
		optionsInfo.put("ha_vgdataspace", request.getParameter("ha_vgdataspace"));
		// ha_vgcaap
		optionsInfo.put("ha_vgcaap", request.getParameter("ha_vgcaap"));

		// -------PV-----
		// ha_db2homepv
		optionsInfo.put("ha_db2homepv", request.getParameter("ha_db2homepv"));
		// ha_db2logpv
		optionsInfo.put("ha_db2logpv", request.getParameter("ha_db2logpv"));
		// ha_db2archlogpv
		optionsInfo.put("ha_db2archlogpv", request.getParameter("ha_db2archlogpv"));
		// ha_dataspace1pv
		optionsInfo.put("ha_dataspacepv", request.getParameter("ha_dataspace1pv"));
		// ha_caappv
		optionsInfo.put("ha_caappv", request.getParameter("ha_caappv"));

		// ------VG创建方式-----
		// ha_db2homemode
		optionsInfo.put("ha_db2homemode", request.getParameter("ha_db2homemode"));
		// ha_db2logmode
		optionsInfo.put("ha_db2logmode", request.getParameter("ha_db2logmode"));
		// ha_db2archlogmode
		optionsInfo.put("ha_db2archlogmode", request.getParameter("ha_db2archlogmode"));
		// ha_dataspace1mode
		optionsInfo.put("ha_dataspacemode", request.getParameter("ha_dataspacemode"));
		// ha_caapmode
		optionsInfo.put("ha_caapmode", request.getParameter("ha_caapmode"));

		// ------HA切换配置-----
		// ha_startpolicy
		optionsInfo.put("ha_startpolicy", request.getParameter("ha_startpolicy"));
		// ha_fopolicy
		optionsInfo.put("ha_fopolicy", request.getParameter("ha_fopolicy"));
		// ha_fbpolicy
		optionsInfo.put("ha_fbpolicy", request.getParameter("ha_fbpolicy"));

		// --------NFS---------
		// ha_nfsON
		optionsInfo.put("ha_nfsON", request.getParameter("ha_nfsON"));

		// ha_nfsIP1
		optionsInfo.put("ha_nfsIP1", request.getParameter("ha_nfsIP1"));
		// ha_nfsSPoint1
		optionsInfo.put("ha_nfsSPoint1", request.getParameter("ha_nfsSPoint1"));
		// ha_nfsCPoint1
		optionsInfo.put("ha_nfsCPoint1", request.getParameter("ha_nfsCPoint1"));
		// ha_nfsIP2
		optionsInfo.put("ha_nfsIP2", request.getParameter("ha_nfsIP2"));
		// ha_nfsSPoint2
		optionsInfo.put("ha_nfsSPoint2", request.getParameter("ha_nfsSPoint2"));
		// ha_nfsCPoint2
		optionsInfo.put("ha_nfsCPoint2", request.getParameter("ha_nfsCPoint2"));
		// ha_nfsIP3
		optionsInfo.put("ha_nfsIP3", request.getParameter("ha_nfsIP3"));
		// ha_nfsSPoint3
		optionsInfo.put("ha_nfsSPoint3", request.getParameter("ha_nfsSPoint3"));
		// ha_nfsCPoint3
		optionsInfo.put("ha_nfsCPoint3", request.getParameter("ha_nfsCPoint3"));
		// ha_nfsIP4
		optionsInfo.put("ha_nfsIP4", request.getParameter("ha_nfsIP4"));
		// ha_nfsSPoint4
		optionsInfo.put("ha_nfsSPoint4", request.getParameter("ha_nfsSPoint4"));
		// ha_nfsCPoint4
		optionsInfo.put("ha_nfsCPoint4", request.getParameter("ha_nfsCPoint4"));
		// ha_nfsIP5
		optionsInfo.put("ha_nfsIP5", request.getParameter("ha_nfsIP5"));
		// ha_nfsSPoint5
		optionsInfo.put("ha_nfsSPoint5", request.getParameter("ha_nfsSPoint5"));
		// ha_nfsCPoint5
		optionsInfo.put("ha_nfsCPoint5", request.getParameter("ha_nfsCPoint5"));
		// ha_nfsIP6
		optionsInfo.put("ha_nfsIP6", request.getParameter("ha_nfsIP6"));
		// ha_nfsSPoint6
		optionsInfo.put("ha_nfsSPoint6", request.getParameter("ha_nfsSPoint6"));
		// ha_nfsCPoint6
		optionsInfo.put("ha_nfsCPoint6", request.getParameter("ha_nfsCPoint6"));
		// ha_nfsIP7
		optionsInfo.put("ha_nfsIP7", request.getParameter("ha_nfsIP7"));
		// ha_nfsSPoint7
		optionsInfo.put("ha_nfsSPoint7", request.getParameter("ha_nfsSPoint7"));
		// ha_nfsCPoint7
		optionsInfo.put("ha_nfsCPoint7", request.getParameter("ha_nfsCPoint7"));
		// ha_nfsIP8
		optionsInfo.put("ha_nfsIP8", request.getParameter("ha_nfsIP8"));
		// ha_nfsSPoint8
		optionsInfo.put("ha_nfsSPoint8", request.getParameter("ha_nfsSPoint8"));
		// ha_nfsCPoint8
		optionsInfo.put("ha_nfsCPoint8", request.getParameter("ha_nfsCPoint8"));
		// ha_nfsIP9
		optionsInfo.put("ha_nfsIP9", request.getParameter("ha_nfsIP9"));
		// ha_nfsSPoint9
		optionsInfo.put("ha_nfsSPoint9", request.getParameter("ha_nfsSPoint9"));
		// ha_nfsCPoint9
		optionsInfo.put("ha_nfsCPoint9", request.getParameter("ha_nfsCPoint9"));
		// ha_nfsIP10
		optionsInfo.put("ha_nfsIP10", request.getParameter("ha_nfsIP10"));
		// ha_nfsSPoint10
		optionsInfo.put("ha_nfsSPoint10", request.getParameter("ha_nfsSPoint10"));
		// ha_nfsCPoint10
		optionsInfo.put("ha_nfsCPoint10", request.getParameter("ha_nfsCPoint10"));
		// ha_nfsIP11
		optionsInfo.put("ha_nfsIP11", request.getParameter("ha_nfsIP11"));
		// ha_nfsSPoint11
		optionsInfo.put("ha_nfsSPoint11", request.getParameter("ha_nfsSPoint11"));
		// ha_nfsCPoint11
		optionsInfo.put("ha_nfsCPoint11", request.getParameter("ha_nfsCPoint11"));
		// ha_nfsIP12
		optionsInfo.put("ha_nfsIP12", request.getParameter("ha_nfsIP12"));
		// ha_nfsSPoint12
		optionsInfo.put("ha_nfsSPoint12", request.getParameter("ha_nfsSPoint12"));
		// ha_nfsCPoint12
		optionsInfo.put("ha_nfsCPoint12", request.getParameter("ha_nfsCPoint12"));
		// ha_nfsIP13
		optionsInfo.put("ha_nfsIP13", request.getParameter("ha_nfsIP13"));
		// ha_nfsSPoint13
		optionsInfo.put("ha_nfsSPoint13", request.getParameter("ha_nfsSPoint13"));
		// ha_nfsCPoint13
		optionsInfo.put("ha_nfsCPoint13", request.getParameter("ha_nfsCPoint13"));
		// ha_nfsIP14
		optionsInfo.put("ha_nfsIP14", request.getParameter("ha_nfsIP14"));
		// ha_nfsSPoint14
		optionsInfo.put("ha_nfsSPoint14", request.getParameter("ha_nfsSPoint14"));
		// ha_nfsCPoint14
		optionsInfo.put("ha_nfsCPoint14", request.getParameter("ha_nfsCPoint14"));
		// ha_nfsIP15
		optionsInfo.put("ha_nfsIP15", request.getParameter("ha_nfsIP15"));
		// ha_nfsSPoint15
		optionsInfo.put("ha_nfsSPoint15", request.getParameter("ha_nfsSPoint15"));
		// ha_nfsCPoint15
		optionsInfo.put("ha_nfsCPoint15", request.getParameter("ha_nfsCPoint15"));

		// ------基本信息-----
		// db2_version
		optionsInfo.put("db2_version", request.getParameter("db2_version"));
		// db2_db2base
		optionsInfo.put("db2_db2base", request.getParameter("db2_db2base"));
		// db2_dbpath
		optionsInfo.put("db2_dbpath", request.getParameter("db2_dbpath"));
		// db2_svcename
		optionsInfo.put("db2_svcename", request.getParameter("db2_svcename"));
		// db2_db2insusr
		optionsInfo.put("db2_db2insusr", request.getParameter("db2_db2insusr"));
		// db2_dbname
		optionsInfo.put("db2_dbname", request.getParameter("db2_dbname"));
		// db2_codeset
		optionsInfo.put("db2_codeset", request.getParameter("db2_codeset"));
		// db2_dbdatapath
		optionsInfo.put("db2_dbdatapath", request.getParameter("db2_dbdatapath"));
		// ------实例高级属性-------
		// db2_db2insusr 基本信息已有
		// "db2_db2insusr="+request.getParameter("db2_db2insusr")+lineSep +
		// db2_db2insgrp
		optionsInfo.put("db2_db2insgrp", request.getParameter("db2_db2insgrp"));
		// db2_db2fenusr
		optionsInfo.put("db2_db2fenusr", request.getParameter("db2_db2fenusr"));
		// db2_db2fengrp
		optionsInfo.put("db2_db2fengrp", request.getParameter("db2_db2fengrp"));
		// db2_db2comm
		optionsInfo.put("db2_db2comm", request.getParameter("db2_db2comm"));
		// db2_db2codepage
		optionsInfo.put("db2_db2codepage", request.getParameter("db2_db2codepage"));
		// db2_initagents
		optionsInfo.put("db2_initagents", request.getParameter("db2_initagents"));
		// db2_max_coordagents
		optionsInfo.put("db2_max_coordagents", request.getParameter("db2_max_coordagents"));
		// db2_max_connectings
		optionsInfo.put("db2_max_connectings", request.getParameter("db2_max_connectings"));
		// db2_poolagents
		optionsInfo.put("db2_poolagents", request.getParameter("db2_poolagents"));
		// db2_diagsize
		optionsInfo.put("db2_diagsize", request.getParameter("db2_diagsize"));
		// db2_mon_buf
		optionsInfo.put("db2_mon_buf", request.getParameter("db2_mon_buf"));
		// db2_mon_lock
		optionsInfo.put("db2_mon_lock", request.getParameter("db2_mon_lock"));
		// db2_mon_sort
		optionsInfo.put("db2_mon_sort", request.getParameter("db2_mon_sort"));
		// db2_mon_stmt
		optionsInfo.put("db2_mon_stmt", request.getParameter("db2_mon_stmt"));
		// db2_mon_table
		optionsInfo.put("db2_mon_table", request.getParameter("db2_mon_table"));
		// db2_mon_uow
		optionsInfo.put("db2_mon_uow", request.getParameter("db2_mon_uow"));
		// db2_health_mon
		optionsInfo.put("db2_health_mon", request.getParameter("db2_health_mon"));
		// db2_mon_heap
		optionsInfo.put("db2_mon_heap", request.getParameter("db2_mon_heap"));

		// -------数据库高级属性------
		// db2_db2log
		optionsInfo.put("db2_db2log", request.getParameter("db2_db2log"));
		// db2_backuppath
		optionsInfo.put("db2_backuppath", request.getParameter("db2_backuppath"));
		// db2_logarchpath
		optionsInfo.put("db2_logarchpath", request.getParameter("db2_logarchpath"));
		// db2_stmm
		optionsInfo.put("db2_stmm", request.getParameter("db2_stmm"));
		// db2_locklist
		optionsInfo.put("db2_locklist", request.getParameter("db2_locklist"));
		// db2_maxlocks
		optionsInfo.put("db2_maxlocks", request.getParameter("db2_maxlocks"));
		// db2_locktimeout
		optionsInfo.put("db2_locktimeout", request.getParameter("db2_locktimeout"));
		// db2_sortheap
		optionsInfo.put("db2_sortheap", request.getParameter("db2_sortheap"));
		// db2_logfilesize
		optionsInfo.put("db2_logfilesize", request.getParameter("db2_logfilesize"));
		// db2_logprimary
		optionsInfo.put("db2_logprimary", request.getParameter("db2_logprimary"));
		// db2_logsecond
		optionsInfo.put("db2_logsecond", request.getParameter("db2_logsecond"));
		// db2_logbuff
		optionsInfo.put("db2_logbuff", request.getParameter("db2_logbuff"));
		// db2_softmax
		optionsInfo.put("db2_softmax", request.getParameter("db2_softmax"));
		// db2_trackmod
		optionsInfo.put("db2_trackmod", request.getParameter("db2_trackmod"));
		// db2_pagesize
		optionsInfo.put("db2_pagesize", request.getParameter("db2_pagesize"));
	}

	private String getMkvg(String hostName, String datavg1, String datavg2) {
		String mkvg_info = amsprop.getProperty("mkvg_info");
		String th = amsprop.getProperty("mkvg_info_table_th");

		String lineSep = "\n";
		String t = "\t\t\t\t\t";
		String td = "" + hostName + t + "datavg1" + t + "64" + t + "301" + t + "1" + t + "S" + t + "y" + t + datavg1
				+ lineSep + hostName + "\t" + "datavg2" + t + "64" + t + "302" + t + "1" + t + "S" + t + "y" + t
				+ datavg2 + lineSep;

		String str = mkvg_info + lineSep + lineSep + th + td;
		return str.replace("\n", "<br />");
	}

	private String getImportvg(String hostName, String datavg1, String datavg2) {
		String importvg_info = amsprop.getProperty("importvg_info");
		String th = amsprop.getProperty("importvg_info_table_th");

		String lineSep = "\n";
		String t = "\t\t\t\t\t";
		String td = "" + hostName + t + "datavg1\t\t\t\t\t302" + datavg1 + lineSep + hostName + t + "datavg2\t\t\t\t\t"
				+ datavg2 + lineSep;
		String str = importvg_info + lineSep + lineSep + th + td;
		return str.replace("\n", "<br />");
	}

	private String getHaSetup(String haname, String[] hostName, String serName, String caappv) {
		String ha_setup_info = amsprop.getProperty("ha_setup_info");
		// 字符串拼接
		String lineSep = "\n";
		String strInfo = "CLUSTER;;" + haname + lineSep + "NODE;;" + hostName[0] + "," + hostName[1] + "" + lineSep
				+ "ENET;;net_ether_01;;255.255.255.0;;yes" + lineSep + "REPDEV;;" + caappv + "" + lineSep + "HAMODE;;AA"
				+ lineSep + "RGNUM;;1" + lineSep + "APSERV1;;ap01;;/script/db2start.sh;;/script/db2stop.sh" + lineSep
				+ "SERVIP1;;aix7svc;;net_ether_01;;255.255.255.0" + lineSep
				+ "PERIP1;;perip1;;net_ether_01;;255.255.255.0" + lineSep + "RGROUP1;;rg_hln01;;" + hostName[0] + ","
				+ hostName[1] + ";;normal;;" + serName + ";;ap01;;datavg1,datavg2;;fsck;;sequential;;true";
		String str = ha_setup_info + lineSep + lineSep + strInfo;
		return str.replace("\n", "<br />");
	}

	@RequestMapping("/getdb2haLogInfoDetial")
	public String getDB2HALogInfoDetial(HttpServletRequest request, HttpSession session) {
		String uuid = request.getParameter("uuid");
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

			// -------基本------
			// ha_clusterName
			request.setAttribute("ha_clusterName", options.get("ha_clusterName").asText());
			// ha_RGNmae
			request.setAttribute("ha_RGNmae", options.get("ha_RGNmae").asText());
			// ha_ASName
			request.setAttribute("ha_ASName", options.get("ha_ASName").asText());
			// ha_primaryNode
			request.setAttribute("ha_primaryNode", options.get("ha_primaryNode").asText());
			// --------IP-----
			// ha_ip1
			request.setAttribute("ha_ip1", options.get("ha_ip1").asText());
			// ha_ip2
			request.setAttribute("ha_ip2", options.get("ha_ip2").asText());
			// ha_bootip1
			request.setAttribute("ha_bootip1", options.get("ha_bootip1").asText());
			// ha_bootip2
			request.setAttribute("ha_bootip2", options.get("ha_bootip2").asText());
			// ha_svcip
			request.setAttribute("ha_svcip", options.get("ha_svcip").asText());

			// --------主机别名-----
			// ha_hostname1
			request.setAttribute("ha_hostname1", options.get("ha_hostname1").asText());
			// ha_hostname2
			request.setAttribute("ha_hostname2", options.get("ha_hostname2").asText());
			// ha_bootalias1
			request.setAttribute("ha_bootalias1", options.get("ha_bootalias1").asText());
			// ha_bootalias2
			request.setAttribute("ha_bootalias2", options.get("ha_bootalias2").asText());
			// ha_svcalias
			request.setAttribute("ha_svcalias", options.get("ha_svcalias").asText());

			if (options.get("ha_primaryNode") != null && options.get("ha_hostname1") != null
					&& options.get("ha_primaryNode").equals(options.get("ha_hostname1"))) {
				request.setAttribute("ha_subNode", options.get("ha_hostname2").asText());
			} else {
				request.setAttribute("ha_subNode", options.get("ha_hostname1").asText());
			}

			// ---------VG------
			// ha_vgdb2home
			request.setAttribute("ha_vgdb2home", options.get("ha_vgdb2home").asText());
			// ha_vgdb2log
			request.setAttribute("ha_vgdb2log", options.get("ha_vgdb2log").asText());
			// ha_vgdb2archlog
			request.setAttribute("ha_vgdb2archlog", options.get("ha_vgdb2archlog").asText());
			// ha_vgdataspace1
			request.setAttribute("ha_vgdataspace", options.get("ha_vgdataspace").asText());
			// ha_vgcaap
			request.setAttribute("ha_vgcaap", options.get("ha_vgcaap").asText());

			// -------PV-----
			// ha_db2homepv
			request.setAttribute("ha_db2homepv", options.get("ha_db2homepv").asText());
			// ha_db2logpv
			request.setAttribute("ha_db2logpv", options.get("ha_db2logpv").asText());
			// ha_db2archlogpv
			request.setAttribute("ha_db2archlogpv", options.get("ha_db2archlogpv").asText());
			// ha_dataspace1pv
			request.setAttribute("ha_dataspacepv", options.get("ha_dataspacepv").asText());
			// ha_caappv
			request.setAttribute("ha_caappv", options.get("ha_caappv").asText());

			// ------VG创建方式-----
			// ha_db2homemode
			request.setAttribute("ha_db2homemode", options.get("ha_db2homemode").asText());
			// ha_db2logmode
			request.setAttribute("ha_db2logmode", options.get("ha_db2logmode").asText());
			// ha_db2archlogmode
			request.setAttribute("ha_db2archlogmode", options.get("ha_db2archlogmode").asText());
			// ha_dataspace1mode
			request.setAttribute("ha_dataspacemode", options.get("ha_dataspacemode").asText());
			// ha_caapmode
			request.setAttribute("ha_caapmode", options.get("ha_caapmode").asText());

			// ------HA切换配置-----
			// ha_startpolicy
			String ha_startpolicy = "";
			if (options.get("ha_startpolicy").asText().equals("OHN")) {
				ha_startpolicy = "Online On Home Node Only";
			} else if (options.get("ha_startpolicy").asText().equals("OFAN")) {
				ha_startpolicy = "Online On First Available Node";
			} else if (options.get("ha_startpolicy").asText().equals("OUDP")) {
				ha_startpolicy = "Online Using Node Distribution Policy";
			} else if (options.get("ha_startpolicy").asText().equals("OAAN")) {
				ha_startpolicy = "Online On All Available Nodes";
			}
			request.setAttribute("ha_startpolicy", ha_startpolicy);

			// ha_fopolicy
			String ha_fopolicy = "";
			if (options.get("ha_fopolicy").asText().equals("FNPN")) {
				ha_fopolicy = "Fallover To Next Priority Node In The List";
			} else if (options.get("ha_fopolicy").asText().equals("FUDNP")) {
				ha_fopolicy = "Fallover Using Dynamic Node Priority";
			} else if (options.get("ha_fopolicy").asText().equals("BO")) {
				ha_fopolicy = "Bring Offline (On Error Node Only)";
			}
			request.setAttribute("ha_fopolicy", ha_fopolicy);

			// ha_fbpolicy
			String ha_fbpolicy = "";
			if (options.get("ha_fbpolicy").asText().equals("FBHPN")) {
				ha_fbpolicy = "Fallback To Higher Priority Node In The List";
			} else if (options.get("ha_fbpolicy").asText().equals("NFB")) {
				ha_fbpolicy = "Never Fallback";
			}
			request.setAttribute("ha_fbpolicy", ha_fbpolicy);

			// --------NFS---------
			// ha_nfsON
			request.setAttribute("ha_nfsON", options.get("ha_nfsON").asText());
			request.setAttribute("ison", "yes");
			// ha_nfsIP1
			request.setAttribute("ha_nfsIP1", options.get("ha_nfsIP1").asText());
			// ha_nfsSPoint1
			request.setAttribute("ha_nfsSPoint1", options.get("ha_nfsSPoint1").asText());
			// ha_nfsCPoint1
			request.setAttribute("ha_nfsCPoint1", options.get("ha_nfsCPoint1").asText());
			// ha_nfsIP2
			request.setAttribute("ha_nfsIP2", options.get("ha_nfsIP2").asText());
			// ha_nfsSPoint2
			request.setAttribute("ha_nfsSPoint2", options.get("ha_nfsSPoint2").asText());
			// ha_nfsCPoint2
			request.setAttribute("ha_nfsCPoint2", options.get("ha_nfsCPoint2").asText());
			// ha_nfsIP3
			request.setAttribute("ha_nfsIP3", options.get("ha_nfsIP3").asText());
			// ha_nfsSPoint3
			request.setAttribute("ha_nfsSPoint3", options.get("ha_nfsSPoint3").asText());
			// ha_nfsCPoint3
			request.setAttribute("ha_nfsCPoint3", options.get("ha_nfsCPoint3").asText());
			// ha_nfsIP4
			request.setAttribute("ha_nfsIP4", options.get("ha_nfsIP4").asText());
			// ha_nfsSPoint4
			request.setAttribute("ha_nfsSPoint4", options.get("ha_nfsSPoint4").asText());
			// ha_nfsCPoint4
			request.setAttribute("ha_nfsCPoint4", options.get("ha_nfsCPoint4").asText());
			// ha_nfsIP5
			request.setAttribute("ha_nfsIP5", options.get("ha_nfsIP5").asText());
			// ha_nfsSPoint5
			request.setAttribute("ha_nfsSPoint5", options.get("ha_nfsSPoint5").asText());
			// ha_nfsCPoint5
			request.setAttribute("ha_nfsCPoint5", options.get("ha_nfsCPoint5").asText());
			// ha_nfsIP6
			request.setAttribute("ha_nfsIP6", options.get("ha_nfsIP6").asText());
			// ha_nfsSPoint6
			request.setAttribute("ha_nfsSPoint6", options.get("ha_nfsSPoint6").asText());
			// ha_nfsCPoint6
			request.setAttribute("ha_nfsCPoint6", options.get("ha_nfsCPoint6").asText());
			// ha_nfsIP7
			request.setAttribute("ha_nfsIP7", options.get("ha_nfsIP7").asText());
			// ha_nfsSPoint7
			request.setAttribute("ha_nfsSPoint7", options.get("ha_nfsSPoint7").asText());
			// ha_nfsCPoint7
			request.setAttribute("ha_nfsCPoint7", options.get("ha_nfsCPoint7").asText());
			// ha_nfsIP8
			request.setAttribute("ha_nfsIP8", options.get("ha_nfsIP8").asText());
			// ha_nfsSPoint8
			request.setAttribute("ha_nfsSPoint8", options.get("ha_nfsSPoint8").asText());
			// ha_nfsCPoint8
			request.setAttribute("ha_nfsCPoint8", options.get("ha_nfsCPoint8").asText());
			// ha_nfsIP9
			request.setAttribute("ha_nfsIP9", options.get("ha_nfsIP9").asText());
			// ha_nfsSPoint9
			request.setAttribute("ha_nfsSPoint9", options.get("ha_nfsSPoint9").asText());
			// ha_nfsCPoint9
			request.setAttribute("ha_nfsCPoint9", options.get("ha_nfsCPoint9").asText());
			// ha_nfsIP10
			request.setAttribute("ha_nfsIP10", options.get("ha_nfsIP10").asText());
			// ha_nfsSPoint10
			request.setAttribute("ha_nfsSPoint10", options.get("ha_nfsSPoint10").asText());
			// ha_nfsCPoint10
			request.setAttribute("ha_nfsCPoint10", options.get("ha_nfsCPoint10").asText());
			// ha_nfsIP11
			request.setAttribute("ha_nfsIP11", options.get("ha_nfsIP11").asText());
			// ha_nfsSPoint11
			request.setAttribute("ha_nfsSPoint11", options.get("ha_nfsSPoint11").asText());
			// ha_nfsCPoint11
			request.setAttribute("ha_nfsCPoint11", options.get("ha_nfsCPoint11").asText());
			// ha_nfsIP12
			request.setAttribute("ha_nfsIP12", options.get("ha_nfsIP12").asText());
			// ha_nfsSPoint12
			request.setAttribute("ha_nfsSPoint12", options.get("ha_nfsSPoint12").asText());
			// ha_nfsCPoint12
			request.setAttribute("ha_nfsCPoint12", options.get("ha_nfsCPoint12").asText());
			// ha_nfsIP13
			request.setAttribute("ha_nfsIP13", options.get("ha_nfsIP13").asText());
			// ha_nfsSPoint13
			request.setAttribute("ha_nfsSPoint13", options.get("ha_nfsSPoint13").asText());
			// ha_nfsCPoint13
			request.setAttribute("ha_nfsCPoint13", options.get("ha_nfsCPoint13").asText());
			// ha_nfsIP14
			request.setAttribute("ha_nfsIP14", options.get("ha_nfsIP14").asText());
			// ha_nfsSPoint14
			request.setAttribute("ha_nfsSPoint14", options.get("ha_nfsSPoint14").asText());
			// ha_nfsCPoint14
			request.setAttribute("ha_nfsCPoint14", options.get("ha_nfsCPoint14").asText());
			// ha_nfsIP15
			request.setAttribute("ha_nfsIP15", options.get("ha_nfsIP15").asText());
			// ha_nfsSPoint15
			request.setAttribute("ha_nfsSPoint15", options.get("ha_nfsSPoint15").asText());
			// ha_nfsCPoint15
			request.setAttribute("ha_nfsCPoint15", options.get("ha_nfsCPoint15").asText());

			// -------基本信息------
			// db2_version
			request.setAttribute("db2_version", options.get("db2_version").asText());
			// db2_db2base
			request.setAttribute("db2_db2base", options.get("db2_db2base").asText());
			// db2_dbpath
			request.setAttribute("db2_dbpath", options.get("db2_dbpath").asText());
			// db2_svcename
			request.setAttribute("db2_svcename", options.get("db2_svcename").asText());
			// db2_db2insusr
			request.setAttribute("db2_db2insusr", options.get("db2_db2insusr").asText());
			// db2_dbname
			request.setAttribute("db2_dbname", options.get("db2_dbname").asText());
			// db2_codeset
			request.setAttribute("db2_codeset", options.get("db2_codeset").asText());
			// db2_dbdatapath
			request.setAttribute("db2_dbdatapath", options.get("db2_dbdatapath").asText());
			// ------实例高级属性-------
			// db2_db2insgrp
			request.setAttribute("db2_db2insgrp", options.get("db2_db2insgrp").asText());
			// db2_db2fenusr
			request.setAttribute("db2_db2fenusr", options.get("db2_db2fenusr").asText());
			// db2_db2fengrp
			request.setAttribute("db2_db2fengrp", options.get("db2_db2fengrp").asText());
			// db2_db2comm
			request.setAttribute("db2_db2comm", options.get("db2_db2comm").asText());
			// db2_db2codepage
			request.setAttribute("db2_db2codepage", options.get("db2_db2codepage").asText());
			// db2_initagents
			request.setAttribute("db2_initagents", options.get("db2_initagents").asText());
			// db2_max_coordagents
			request.setAttribute("db2_max_coordagents", options.get("db2_max_coordagents").asText());
			// db2_max_connectings
			request.setAttribute("db2_max_connectings", options.get("db2_max_connectings").asText());
			// db2_poolagents
			request.setAttribute("db2_poolagents", options.get("db2_poolagents").asText());
			// db2_diagsize
			request.setAttribute("db2_diagsize", options.get("db2_diagsize").asText());
			// db2_mon_buf
			request.setAttribute("db2_mon_buf", options.get("db2_mon_buf").asText());
			// db2_mon_lock
			request.setAttribute("db2_mon_lock", options.get("db2_mon_lock").asText());
			// db2_mon_sort
			request.setAttribute("db2_mon_sort", options.get("db2_mon_sort").asText());
			// db2_mon_stmt
			request.setAttribute("db2_mon_stmt", options.get("db2_mon_stmt").asText());
			// db2_mon_table
			request.setAttribute("db2_mon_table", options.get("db2_mon_table").asText());
			// db2_mon_uow
			request.setAttribute("db2_mon_uow", options.get("db2_mon_uow").asText());
			// db2_health_mon
			request.setAttribute("db2_health_mon", options.get("db2_health_mon").asText());
			// db2_mon_heap
			request.setAttribute("db2_mon_heap", options.get("db2_mon_heap").asText());

			// -------数据库高级属性------
			// db2_db2log
			request.setAttribute("db2_db2log", options.get("db2_db2log").asText());
			// db2_backuppath
			request.setAttribute("db2_backuppath", options.get("db2_backuppath").asText());
			// db2_logarchpath
			request.setAttribute("db2_logarchpath", options.get("db2_logarchpath").asText());
			// db2_stmm
			request.setAttribute("db2_stmm", options.get("db2_stmm").asText());
			// db2_locklist
			request.setAttribute("db2_locklist", options.get("db2_locklist").asText());
			// db2_maxlocks
			request.setAttribute("db2_maxlocks", options.get("db2_maxlocks").asText());
			// db2_locktimeout
			request.setAttribute("db2_locktimeout", options.get("db2_locktimeout").asText());
			// db2_sortheap
			request.setAttribute("db2_sortheap", options.get("db2_sortheap").asText());
			// db2_logfilesize
			request.setAttribute("db2_logfilesize", options.get("db2_logfilesize").asText());
			// db2_logprimary
			request.setAttribute("db2_logprimary", options.get("db2_logprimary").asText());
			// db2_logsecond
			request.setAttribute("db2_logsecond", options.get("db2_logsecond").asText());
			// db2_logbuff
			request.setAttribute("db2_logbuff", options.get("db2_logbuff").asText());
			// db2_softmax
			request.setAttribute("db2_softmax", options.get("db2_softmax").asText());
			// db2_trackmod
			request.setAttribute("db2_trackmod", options.get("db2_trackmod").asText());
			// db2_pagesize
			request.setAttribute("db2_pagesize", options.get("db2_pagesize").asText());

		}
		// List<ServerBean> listDetial = new ArrayList<ServerBean>();

		// request.setAttribute("servers", listDetial);

		// request.setAttribute("flavors", fList);
		// request.setAttribute("imageList", imageList);
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

	//	System.out.println("map:" + map);

		return "instance_aix_log_details2";
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

	public ObjectNode postDB2Run(ArrayNode hostnode, ObjectNode crednode, String info, String type, String nfsON) {
		serverlists = getServerList(); // 为获取serverlist 的加密认证信息

		// 设值是否立即执行标识
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
			names = AMS2KeyUtil.getArrayNodeByKey("db2_job_name"); // 自动执行,job名称的集合
		} else {
			names = AMS2KeyUtil.getArrayNodeByKey("db2_job_name_sub"); // 手动执行job名称的集合
		}
		ObjectNode stepobj = this.getDb2StepObjectNode();// step集合
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
					steps = getPutHosts(hostnode, crednode, jobname);
				} else if (sname.equals("put-hostname")) {
					steps = getPutHostname(hostnode, crednode, jobname);
				} else if (sname.equals("files")) {
					steps = getFiles(hostnode, crednode, jobname, "db2_file_name", "db2_file_path");
				} else if (sname.equals("prepare.db2.lst")) {
					steps = getPrepareDb2Lst(hostnode, crednode, jobname, info);
				} else if (sname.equals("chmod")) {
					steps = getChmod(hostnode, crednode, jobname);
				} else if (sname.equals("set-hostname")) {
					steps = getSetHostname(hostnode, crednode, jobname);
				} else if (flag && sname.equals("prepare.db2.ksh")) {
					steps = getPrepareDb2Ksh(hostnode, crednode, jobname);
				} else if (flag && sname.equals("install.db2.ksh")) {
					steps = getInstallDb2Ksh(hostnode, crednode, jobname);
				} else if (flag && sname.equals("mount.nfs.ksh")) {
					steps = getNFSDb2Ksh(hostnode, crednode, jobname);
				}
				jobnode.set("steps", steps);
				jobs.add(jobnode);

			}
		}
	//	System.out.println("jobs::" + jobs);
		logger.info("task::" + task);
	//	System.out.println("task::" + task);
		String response = "";
		try {
			response = HttpClientUtil.postMethod(strOrgUrl, task.toString());
			logger.info("aix db2ha response::"+response);
	//		System.out.println("response::" + response);
			return (ObjectNode) om.readTree(response);
		} catch (Exception e1) {
			e1.printStackTrace();
			logger.error("db2ha error::"+ e1.getMessage());
		}
		return null;
	}
	private ObjectNode getDb2StepObjectNode() {
		ObjectNode stepobj = om.createObjectNode();
		ArrayNode prepare_steps = AMS2KeyUtil.getArrayNodeByKey("db2_prepare_step_name");
		ArrayNode install_steps = AMS2KeyUtil.getArrayNodeByKey("db2_install_step_name");
		ArrayNode cluster_steps = AMS2KeyUtil.getArrayNodeByKey("db2_cluster_step_name");
		ArrayNode nfs_steps = AMS2KeyUtil.getArrayNodeByKey("db2_nfs_step_name");
		stepobj.set("prepare", prepare_steps);
		stepobj.set("install", install_steps);
		stepobj.set("cluster", cluster_steps);
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
	private ArrayNode getPutHosts(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "/script/hosts.helper.sh");
			steps.put("exec", amsprop.getProperty("script_path") + amsprop.getProperty("hosts_sh"));
			steps.put("type", "sput");
			// steps.put("file",
			// "/home/cloudm/works/tcloud2-ams/shell/hosts.helper.sh");
			steps.put("file", amsprop.getProperty("db2_helpers_file_path") + amsprop.getProperty("hosts_sh"));
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}
	private ArrayNode getPutHostname(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "/script/hostname.helper.sh");
			steps.put("exec", amsprop.getProperty("script_path") + amsprop.getProperty("hostname_sh"));
			steps.put("type", "sput");
			// steps.put("file",
			// "/home/cloudm/works/tcloud2-ams/shell/hostname.helper.sh");
			steps.put("file", amsprop.getProperty("db2_helpers_file_path") + amsprop.getProperty("hostname_sh"));
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
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
