package com.ibm.automation.wascluster.controller;

import java.io.IOException;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.automation.ams.constants.AMS2KeyUtil;
import com.ibm.automation.ams.service.AmsRestService;
import com.ibm.automation.ams.util.AmsClient;
import com.ibm.automation.ams.util.AmsV2ClientHttpClient4Impl;
import com.ibm.automation.core.bean.AddHostBean;
import com.ibm.automation.core.bean.WasClusterBean;
import com.ibm.automation.core.bean.WasClusterLogBean;
import com.ibm.automation.core.constants.OPSTPropertyKeyConst;
import com.ibm.automation.core.controller.InstanceController;
import com.ibm.automation.core.exception.ParamErrorException;
import com.ibm.automation.core.util.EncodeUtil;
import com.ibm.automation.core.util.FormatUtil;
import com.ibm.automation.core.util.HttpClientUtil;
import com.ibm.automation.core.util.PropertyUtil;


@Controller
public class WASCLUSTERController {
	@Autowired
	private AmsRestService amsRestService;
	AmsClient amsClient = new AmsV2ClientHttpClient4Impl();
	private static final Logger logger = Logger.getLogger(WASCLUSTERController.class);
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
		logger.info("pvcnode:::" + obj);
		return pvcnodeId;
	}
	// 创建一个host
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
	}
	@RequestMapping("/toWasClusterNextPage")
	public String toWasCluterNextPage(HttpServletRequest request, HttpSession session) {
		String serId = request.getParameter("serId");

		String ptype = request.getParameter("ptype");

		String[] allips = request.getParameterValues("all_ips");
		String[] allhostnames = request.getParameterValues("all_hostnames");
		String[] allprofiletypes = request.getParameterValues("all_profile_types");
		String[] allprofilenames = request.getParameterValues("all_profile_names");

		for (int i = 0; i < allprofiletypes.length; i++) {
			if (allprofiletypes[i].equals("cell")) {
				if (i == 0) {
					break;
				} else {
					String typetemp = allprofiletypes[0];
					String nametemp = allprofilenames[0];
					String hosttemp = allhostnames[0];
					String iptemp = allips[0];

					allprofiletypes[0] = allprofiletypes[i];
					allprofilenames[0] = allprofilenames[i];
					allhostnames[0] = allhostnames[i];
					allips[0] = allips[i];

					allprofiletypes[i] = typetemp;
					allprofilenames[i] = nametemp;
					allhostnames[i] = hosttemp;
					allips[i] = iptemp;

				}
			}
		}

		List<WasClusterBean> arrList = new ArrayList<WasClusterBean>();
		for (int i = 0; i < allips.length; i++) {
			WasClusterBean bean = new WasClusterBean();
			bean.setIp(allips[i]);
			bean.setName(allhostnames[i]);
			bean.setProfilename(allprofilenames[i]);
			bean.setProfiletype(allprofiletypes[i]);
			arrList.add(bean);
		}

		request.setAttribute("allservers", arrList); // 显示多个IP 主机名 概要情况
		// String str1 = String.Join(",", allips);

		StringBuffer sbhostnames = new StringBuffer();
		StringBuffer sbhostips = new StringBuffer();
		StringBuffer sbprofiletypes = new StringBuffer();
		StringBuffer sballprofilenames = new StringBuffer();
		for (int i = 0; i < allhostnames.length; i++) {
			sbhostnames.append(allhostnames[i]).append(",");
			sbhostips.append(allips[i]).append(",");
			sbprofiletypes.append(allprofiletypes[i]).append(",");
			sballprofilenames.append(allprofilenames[i]).append(",");
		}
		String allhostname = sbhostnames.substring(0, sbhostnames.length() - 1);// 截取末尾的,
		String allip = sbhostips.substring(0, sbhostips.length() - 1);// 截取获取ip
		String allprofiletype = sbprofiletypes.substring(0, sbprofiletypes.length() - 1);// 截取profiletype
		String allprofilename = sballprofilenames.substring(0, sballprofilenames.length() - 1);// 截取profilename

		request.setAttribute("allip", allip);
		request.setAttribute("allhostname", allhostname);
		request.setAttribute("allprofiletype", allprofiletype);
		request.setAttribute("allprofilename", allprofilename);

		List serIds = new ArrayList();
		if (serId != null && serId != "") {
			String[] ss = serId.split(",");
			for (int i = 0; i < ss.length; i++) {
				serIds.add(ss[i]);
			}
		}
		ArrayNode lists = amsRestService.getList(null, null, "odata/servers");
		// System.out.println(lists);
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
		// System.out.println(lahb);

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
		Iterator<AddHostBean> iter = listDetial.iterator();
		AddHostBean sb = null;
		while (iter.hasNext()) {
			sb = (AddHostBean) iter.next();
		}
		request.setAttribute("total", lahb.size());
		request.setAttribute("serId", serId);
		request.setAttribute("servers", listDetial);
		request.setAttribute("ptype", ptype);							
		request.setAttribute("hostId", request.getParameter("hostId"));
		request.setAttribute("wasfix", request.getParameter("wasfix"));

		String status = request.getParameter("status");
		// was参数
		request.setAttribute("was_user", request.getParameter("was_user"));
		request.setAttribute("was_jdk7", request.getParameter("was_jdk7"));	
		request.setAttribute("was_version", request.getParameter("was_version"));
		request.setAttribute("was_fp", request.getParameter("was_fp"));
		request.setAttribute("was_im_path", request.getParameter("was_im_path"));
		request.setAttribute("was_inst_path", request.getParameter("was_inst_path"));
		
		request.setAttribute("was_hostname", sb.getName());	
		request.setAttribute("was_ip", request.getParameter("was_ip"));				
		request.setAttribute("was_profile_type", request.getParameter("was_profile_type"));
		request.setAttribute("was_profile_name", request.getParameter("was_profile_name"));
		request.setAttribute("was_profile_path", request.getParameter("was_profile_path"));	
		request.setAttribute("was_security", request.getParameter("was_security"));
		request.setAttribute("was_userid", request.getParameter("was_userid"));
		request.setAttribute("was_password", request.getParameter("was_password"));

		request.setAttribute("was_nofile_soft", request.getParameter("was_nofile_soft"));
		request.setAttribute("was_nofile_hard", request.getParameter("was_nofile_hard"));
		request.setAttribute("was_fsize_soft", request.getParameter("was_fsize_soft"));
		request.setAttribute("was_fsize_hard", request.getParameter("was_fsize_hard"));
		request.setAttribute("was_core_soft", request.getParameter("was_core_soft"));
		request.setAttribute("was_core_hard", request.getParameter("was_core_hard"));
				
		if (status.equals("confirm")) {
			return "instance_linux_wascluster_confirm";
		}
		return null;
	}
	/**
	 * 设置was cluster 的属性
	 */
	private void setWasClusterOptionsInfo(HttpServletRequest request, ObjectNode optionsInfo) {
		optionsInfo.put("was_hostname", request.getParameter("allhostname"));
		optionsInfo.put("was_ip", request.getParameter("allip"));
		optionsInfo.put("was_profile_type", request.getParameter("allprofiletype"));
		optionsInfo.put("was_profile_name", request.getParameter("allprofilename"));
		
		optionsInfo.put("was_user", request.getParameter("was_user"));
		optionsInfo.put("was_jdk7", request.getParameter("was_jdk7"));		
		optionsInfo.put("was_version", request.getParameter("was_version"));
		optionsInfo.put("was_fp", request.getParameter("was_fp"));
		optionsInfo.put("wasfix", request.getParameter("wasfix"));		
		optionsInfo.put("was_im_path", request.getParameter("was_im_path"));
		optionsInfo.put("was_inst_path", request.getParameter("was_inst_path"));		

		optionsInfo.put("was_profile_path", request.getParameter("was_profile_path"));
		optionsInfo.put("was_security", request.getParameter("was_security"));
		optionsInfo.put("was_userid", request.getParameter("was_userid"));
		optionsInfo.put("was_password", request.getParameter("was_password"));
		
		optionsInfo.put("was_nofile_soft", request.getParameter("was_nofile_soft"));
		optionsInfo.put("was_nofile_hard", request.getParameter("was_nofile_hard"));
		optionsInfo.put("was_fsize_soft", request.getParameter("was_fsize_soft"));
		optionsInfo.put("was_fsize_hard", request.getParameter("was_fsize_hard"));
		optionsInfo.put("was_core_soft", request.getParameter("was_core_soft"));
		optionsInfo.put("was_core_hard", request.getParameter("was_core_hard"));
	}
	@RequestMapping("/installWasClusterInfo")
	public String installWasClusterInfo(HttpServletRequest request, HttpSession session) {
		String retStr = setWasClusterConfigMsg(request);
		logger.info(retStr);
		String allip = request.getParameter("allip");
		String allhostname = request.getParameter("allhostname");
		String allprofiletype = request.getParameter("allprofiletype");
		String allprofilename = request.getParameter("allprofilename");

		String[] allips = InstanceController.convertStrToArray(allip);
		String[] allhostnames = InstanceController.convertStrToArray(allhostname);
		// String[] allprofiletypes=
		// InstanceController.convertStrToArray(allprofiletype);
		// String[] allprofilenames=
		// InstanceController.convertStrToArray(allprofilename);

		String hostIds = request.getParameter("serId");
		String[] hostIdss = hostIds.split(",");
		ObjectMapper om = new ObjectMapper();
		ArrayNode hosts = om.createArrayNode();
		ArrayNode pvcclusternodes = om.createArrayNode();

		// String hostId = request.getParameter("hostId");// hostId
		String ptype = request.getParameter("ptype");
		String autoorno = "yes";
		String wasjdk7 = request.getParameter("was_jdk7"); // 是否安装jdk7
		for (int i = 0; i < allips.length; i++) {
			ObjectNode host = createHostObjNode("wascluster", allhostnames[i], allips[i]);
			hosts.add(host);
			String pvcnodeId = createPvcNodeObjNode(allhostnames[i], allips[i], hostIdss[i], "wascluster",
					String.valueOf(1));
			ObjectNode clusterNode = createPvcClusterNode(allhostnames[i], allips[i], hostIdss[i], pvcnodeId,
					String.valueOf(1));
			pvcclusternodes.add(clusterNode);
		}

		ObjectNode run = this.postWASClusterRun(hosts, null, retStr, autoorno, wasjdk7);

		if (run != null && run.get("uuid") != null) {
			ObjectNode optionsInfo = om.createObjectNode();
			setWasClusterOptionsInfo(request, optionsInfo);
			// setWasOptionsInfo(request, optionsInfo);
			createPvcClustersObjNode(run.get("uuid").asText(), pvcclusternodes, optionsInfo, "WAS Cluster");
		}
		return "redirect:/getLogInfo";
	}
	private String setWasClusterConfigMsg(HttpServletRequest request) {

		/*
		 * String[] allips = request.getParameterValues("all_ips"); String[]
		 * allhostnames = request.getParameterValues("all_hostnames"); String[]
		 * allprofiletypes = request.getParameterValues("all_profile_types");
		 * String[] allprofilenames =
		 * request.getParameterValues("all_profile_names"); StringBuffer
		 * sbhostnames = new StringBuffer(); StringBuffer sbhostips = new
		 * StringBuffer(); StringBuffer sbprofiletypes = new StringBuffer();
		 * StringBuffer sballprofilenames = new StringBuffer(); for (int i = 0;
		 * i < allhostnames.length; i++) {
		 * sbhostnames.append(allhostnames[i]).append(",");
		 * sbhostips.append(allips[i]).append(",");
		 * sbprofiletypes.append(allprofiletypes[i]).append(",");
		 * sballprofilenames.append(allprofilenames[i]).append(","); } String
		 * sbhostname = sbhostnames.substring(0, sbhostnames.length() - 1);//
		 * 截取末尾的, String sbhostip = sbhostips.substring(0, sbhostips.length() -
		 * 1);// 截取获取ip String sbprofiletype = sbprofiletypes.substring(0,
		 * sbprofiletypes.length() - 1);// 截取profiletype String sballprofilename
		 * = sballprofilenames.substring(0, sballprofilenames.length() - 1);//
		 * 截取profilename
		 */
		String sbhostip = request.getParameter("allip");
		String sbhostname = request.getParameter("allhostname");
		String sbprofiletype = request.getParameter("allprofiletype");
		String sballprofilename = request.getParameter("allprofilename");

		String lineSep = "\n";
		String retStr = "was_hostname=" + sbhostname + lineSep 
				+ "was_version=" + request.getParameter("was_version") + lineSep
				+ "was_fp=" + request.getParameter("was_fp") + lineSep 
				+ "was_user=" + request.getParameter("was_user") + lineSep 
				+ "was_im_path=" + request.getParameter("was_im_path") + lineSep 
				+ "was_inst_path=" + request.getParameter("was_inst_path") + lineSep 
				+ "was_jdk7=" + request.getParameter("was_jdk7") + lineSep 
				+ "was_ip=" + sbhostip + lineSep 
				+ "was_profile_type=" + sbprofiletype + lineSep 
				+ "was_profile_path=" + request.getParameter("was_profile_path") + lineSep
				+ "was_profile_name=" + sballprofilename + lineSep 
				+ "was_security=" + request.getParameter("was_security") + lineSep 
				+ "was_userid=" + request.getParameter("was_userid") + lineSep 
				+ "was_password=" + request.getParameter("was_password") + lineSep 
				+ "was_nofile_soft=" + request.getParameter("was_nofile_soft") + lineSep 
				+ "was_nofile_hard=" + request.getParameter("was_nofile_hard") + lineSep 
				+ "was_fsize_soft=" + request.getParameter("was_fsize_soft") + lineSep 
				+ "was_fsize_hard=" + request.getParameter("was_fsize_hard") + lineSep 
				+ "was_core_soft=" + request.getParameter("was_core_soft") + lineSep 
				+ "was_core_hard=" + request.getParameter("was_core_hard") + lineSep 
				+ "product="
				+ amsprop.getProperty("was_standalone_product") + lineSep + "ftphost="
				+ amsprop.getProperty("was_standalone_ftphost") + lineSep + "ftppath="
				+ amsprop.getProperty("was_standalone_ftppath") + lineSep + "ftpuser="
				+ amsprop.getProperty("was_standalone_ftpuser") + lineSep + "ftppass="
				+ amsprop.getProperty("was_standalone_ftppass");
		return retStr;

	}
	public void setWasClusterLogAddressDetail(WasClusterLogBean wclb, String name, int i, String address) {
		if ("make_directory_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setMake_directory_addr_0(address);
		} 
		else if ("propagate_scripts_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setPropagate_scripts_addr_0(address);
		} 
		else if ("propagate_scripts_addr_1".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setPropagate_scripts_addr_1(address);
		} 
		else if ("propagate_scripts_addr_2".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setPropagate_scripts_addr_2(address);
		} 
		else if ("propagate_scripts_addr_3".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setPropagate_scripts_addr_3(address);
		} 
		else if ("manipulate_was_config_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setManipulate_was_config_addr_0(address);
		} 
		else if ("prepare_chmod_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setPrepare_chmod_addr_0(address);
		} 
		else if ("prepare_set_hostname_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setPrepare_set_hostname_addr_0(address);
		} 
		else if ("download_files_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setDownload_files_addr_0(address);
		} 
		else if ("install_im_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setInstall_im_addr_0(address);
		} 
		else if ("install_was_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setInstall_was_addr_0(address);
		}
		else if("install_jdk_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setInstall_jdk_addr_0(address);
		} 
		else if ("update_was_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setUpdate_was_addr_0(address);
		} 
		else if ("build_was_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setBuild_was_addr_0(address);
		} 
		else if ("start_was_addr_0".equals((name + "_addr_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setStart_was_addr_0(address);
		}
	}

	public void setWasClusterLogDetail(WasClusterLogBean wclb, String name, int i, String status) {
		if ("make_directory_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setMake_directory_0(status);
		} else if ("propagate_scripts_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setPropagate_scripts_0(status);
		} else if ("propagate_scripts_1".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setPropagate_scripts_1(status);
		} 
		else if ("propagate_scripts_2".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setPropagate_scripts_2(status);
		} 
		else if ("propagate_scripts_3".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setPropagate_scripts_3(status);
		} 
		else if ("manipulate_was_config_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setManipulate_was_config_0(status);
		} 
		else if ("prepare_chmod_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setPrepare_chmod_0(status);
		} 
		else if ("prepare_set_hostname_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setPrepare_set_hostname_0(status);
		} 
		else if ("download_files_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setDownload_files_0(status);
		} 
		else if ("install_im_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setInstall_im_0(status);
		} 
		else if ("install_was_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setInstall_was_0(status);
		} 
		else if ("update_was_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setUpdate_was_0(status);
		}
		else if ("install_jdk_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setInstall_jdk_0(status);
		} 
		else if ("build_was_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setBuild_was_0(status);
		} 
		else if ("start_was_0".equals((name + "_" + i).replace('-', '_').replace('.', '_'))) 
		{
			wclb.setStart_was_0(status);
		}
	}
	
	
	
	/**
	 * 获取was集群日志情况
	 */
	@RequestMapping("/getwasclusterLogInfoDetial")
	public String getWasClusterInfoDetail(HttpServletRequest request, HttpSession session) {
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
			// 单节点IP

			request.setAttribute("was_core_hard", options.get("was_core_hard").asText());
			request.setAttribute("was_core_soft", options.get("was_core_soft").asText());
			request.setAttribute("was_fsize_hard", options.get("was_fsize_hard").asText());
			request.setAttribute("was_fsize_soft", options.get("was_fsize_soft").asText());
			request.setAttribute("was_nofile_hard", options.get("was_nofile_hard").asText());
			request.setAttribute("was_nofile_soft", options.get("was_nofile_soft").asText());
			request.setAttribute("was_password", options.get("was_password").asText());
			request.setAttribute("was_userid", options.get("was_userid").asText());
			request.setAttribute("was_security", options.get("was_security").asText());
			request.setAttribute("was_profile_name", options.get("was_profile_name").asText());
			request.setAttribute("was_profile_path", options.get("was_profile_path").asText());
			request.setAttribute("was_profile_type", options.get("was_profile_type").asText());
			request.setAttribute("was_ip", options.get("was_ip").asText());
			request.setAttribute("was_jdk7", options.get("was_jdk7").asText());
			request.setAttribute("was_inst_path", options.get("was_inst_path").asText());
			request.setAttribute("was_im_path", options.get("was_im_path").asText());
			request.setAttribute("was_user", options.get("was_user").asText());
			request.setAttribute("was_fp", options.get("was_fp").asText());
			request.setAttribute("was_version", options.get("was_version").asText());
			request.setAttribute("was_hostname", options.get("was_hostname").asText());
			request.setAttribute("wasfix", options.get("wasfix").asText());
		}
		ArrayNode lists = amsRestService.getList(null, null, "odata/servers");
		// System.out.println(lists);

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
			List<WasClusterLogBean> wasclusterlist = new ArrayList<WasClusterLogBean>();
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
							String successstep = (name + "_" + i).replace('-', '_').replace('.', '_');
							map.put((name + "_" + i).replace('-', '_').replace('.', '_'), "成功");

							setWasClusterLogDetail(wclb, name, i, "成功");

						} else {
							// 失败
							// request.setAttribute((name + "_" +
							// i).replace('-', '_').replace('.', '_'), "失败");
							map.put((name + "_" + i).replace('-', '_').replace('.', '_'), "失败");
							setWasClusterLogDetail(wclb, name, i, "失败");
						}
					} else if ("1".equals(script_status)) {
						// request.setAttribute((name + "_" + i).replace('-',
						// '_').replace('.', '_'), "执行中");
						map.put((name + "_" + i).replace('-', '_').replace('.', '_'), "执行中");
						setWasClusterLogDetail(wclb, name, i, "执行中");
					} else {
						// 未执行
						/// request.setAttribute((name + "_" + i).replace('-',
						// '_').replace('.', '_'), "未开始");
						map.put((name + "_" + i).replace('-', '_').replace('.', '_'), "未开始");
						setWasClusterLogDetail(wclb, name, i, "未执行");
					}
					String script_addr = step.get("node").get("addr").asText();
					// request.setAttribute((name + "_addr_" + i).replace('-',
					// '_').replace('.', '_'), script_addr);
					// System.out.println((name + "_addr_" + i).replace('-',
					// '_').replace('.', '_'));
					map.put((name + "_addr_" + i).replace('-', '_').replace('.', '_'), script_addr);
					setWasClusterLogAddressDetail(wclb, name, i, script_addr);

				}
				if ("start-was".equals(name))
					wasclusterlist.add(wclb);
			}
			request.setAttribute("wasclusterservers", wasclusterlist);
		}
		return "instance_linux_wascluster_log_details";
	}
	
	
	private ObjectNode getWasClusterStepObject(String jdk7) {
		ObjectNode stepobj = om.createObjectNode();
		ArrayNode prepare_steps = AMS2KeyUtil.getArrayNodeByKey("was_cluster_prepare_step_name");
		ArrayNode install_steps = null;
		if (jdk7.equals("yes")) // 如果存在jdk7
		{
			install_steps = AMS2KeyUtil.getArrayNodeByKey("was_cluster_install_step_name");
		} else {
			install_steps = AMS2KeyUtil.getArrayNodeByKey("was_cluster_install_withoutjdk7_step_name");
		}
		stepobj.set("prepare", prepare_steps);
		stepobj.set("install", install_steps);
		return stepobj;
	}
	private ArrayNode getWas_Cluster_Scripts(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "mkdir -p /script");
			steps.put("exec", "mkdir -p " + amsprop.getProperty("was_cluster_script_path"));
			steps.put("type", "scmd");
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}
	private ArrayNode getWasClusterFiles(ArrayNode hostnode, ObjectNode crednode, String name, String filenamekey,
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
				step.put("exec", amsprop.getProperty("was_cluster_script_path_sub") + filename);
				step.put("type", "sput");
				step.put("file", filepath1 + filename);
				step.put("name", name + "#" + filename + "#" + rs.get("node").get("host").asText());
				filesteps.add(step);
			}
		}
		return filesteps;
	}
	public ObjectNode postWASClusterRun(ArrayNode hostnode, ObjectNode crednode, String info, String type,
			String jdk7) {
		// TODO Auto-generated method stub
		serverlists = getServerList(); // 为获取serverlist 的加密认证信息

		boolean flag = false; // 手工或者自动
		if ("yes".equals(jdk7)) {
			flag = true;
		} else if ("no".equals(jdk7)) {
			flag = false;
		}

		// 获取参数信息
		String hdiskHost = p.getProperty(OPSTPropertyKeyConst.AMS2_HOST);
		String hdiskApi = p.getProperty(OPSTPropertyKeyConst.POST_ams2_service_run);
		// 拼接URL
		String strOrgUrl = hdiskHost + hdiskApi;
		// strOrgUrl="http://9.77.179.211:5000/api/v2/run";
		System.out.println("url::" + strOrgUrl);
		// 构建json
		ObjectNode task = om.createObjectNode();
		ArrayNode jobs = om.createArrayNode();
		task.put("name", "build-wascluster");
		task.put("immediate", "false");
		task.set("jobs", jobs);
		ArrayNode names = AMS2KeyUtil.getArrayNodeByKey("was_cluster_job_name"); // job名称的集合
		ObjectNode stepobj = this.getWasClusterStepObject(jdk7);// step集合
		for (JsonNode hnode : hostnode) {
			ArrayNode an = om.createArrayNode(); // 将数组拆成单节点
			// ArrayNode myNode = om.createArrayNode();
			an.add(hnode);
			for (JsonNode jnode : names) { // prepare,install
				ObjectNode job = (ObjectNode) jnode;
				String name = job.get("name").asText();
				ArrayNode stepnames = (ArrayNode) stepobj.get(name);
				for (JsonNode sn : stepnames) {
					ObjectNode jobnode = om.createObjectNode();
					ArrayNode steps = null;
					String sname = sn.get("name").asText();
					String jobname = sname;
					jobnode.put("name", jobname);
					jobnode.put("type", "series");
					if (sname.equals("make-directory")) {
						steps = getWas_Cluster_Scripts(an, crednode, jobname);
					} else if (sname.equals("propagate-scripts")) {
						steps = getWasClusterFiles(an, crednode, jobname, "was_cluster_file_name",
								"was_cluster_file_path");
					} else if (sname.equals("manipulate-was-config")) {
						steps = getPrepareWas_cluster_Lst(an, crednode, jobname, info);
					} else if (sname.equals("prepare-chmod")) {
						steps = getWas_cluster_Chmod(an, crednode, jobname);
					} else if (sname.equals("prepare-set-hostname")) {
						steps = getSetWasClusterHostname(an, crednode, jobname);
					} else if (sname.equals("download-files")) {
						steps = getWasCluster_DownLoadFiles(an, crednode, jobname);
					} else if (sname.equals("install-im")) {
						steps = getSetWasClusterWas(an, crednode, jobname, "im"); // 安装im
					} else if (sname.equals("install-was")) {
						steps = getSetWasClusterWas(an, crednode, jobname, "was");// 安装was
					} else if (sname.equals("update-was")) {
						steps = getSetWasClusterWas(an, crednode, jobname, "fp");// 安装was
																					// 补丁
					} else if (flag && sname.equals("install-jdk")) {
						steps = getSetWasClusterWas(an, crednode, jobname, "jdk7");// 安装jdk
					} else if (sname.equals("build-was")) {
						steps = getSetWasClusterBuildWas(an, crednode, jobname, "build");// 创建概要
					} else if (sname.equals("start-was")) {
						steps = getSetWasClusterBuildWas(an, crednode, jobname, "start");// 启动服务
					}
					jobnode.set("steps", steps);
					jobs.add(jobnode);
					// myNode.add(jobnode); //变数组
				}
			}
			// jobs.add(myNode);
		}

		logger.info("wascluster task::" + task);
		String response = "";
		try {
			response = HttpClientUtil.postMethod(strOrgUrl, task.toString());
			logger.info("response::" + response);
			return (ObjectNode) om.readTree(response);
		} catch (ParamErrorException opst) {
			logger.debug("调用wascluster http异常，异常为" + opst.getMessage());
		} catch (JsonProcessingException json) {
			logger.debug("调用wasclusterjson处理异常，异常为" + json.getMessage());

		} catch (IOException io) {
			logger.debug("调用wasclusterIO处理异常，异常为" + io.getMessage());

		}

		return null;
	}
	private ArrayNode getWasCluster_DownLoadFiles(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "mkdir -p /script");
			// steps.put("exec", amsprop.getProperty("script_path") +
			// amsprop.getProperty("hostname_sh"));

			steps.put("exec", "cd " + amsprop.getProperty("was_cluster_script_path") + " && ./"
					+ amsprop.getProperty("was_cluster_download_file"));

			steps.put("type", "scmd");
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}
	private ArrayNode getSetWasClusterHostname(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "cd /script && sh ./hostname.helper.sh
			// "+rs.get("node").get("host").asText());
			steps.put("exec", "cd " + amsprop.getProperty("was_cluster_script_path") + " &&  ./"
					+ amsprop.getProperty("was_cluster_hostname_sh"));
			steps.put("type", "scmd");
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}

	private ArrayNode getWas_cluster_Chmod(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "chmod +x /script/*.ksh && chmod +x
			// /script/*.sh");
			steps.put("exec", "chmod +x /script/wascluster/*.sh");
			steps.put("type", "scmd");
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}
	private ArrayNode getPrepareWas_cluster_Lst(ArrayNode hostnode, ObjectNode crednode, String name, String info) {
		ArrayNode filesteps = om.createArrayNode();
		ArrayNode raws = getraws(hostnode, crednode);
		String encodestr = EncodeUtil.encode(info.trim());
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "/script/prepare.db2.lst");
			steps.put("exec",
					amsprop.getProperty("was_cluster_script_path_sub") + amsprop.getProperty("prepare_was_cluster_lst"));
			steps.put("type", "scat");
			steps.put("text", "data:text/plain;base64," + encodestr);
			steps.put("name", name + "#" + amsprop.getProperty("prepare_was_cluster_lst") + "#"
					+ rs.get("node").get("host").asText());
			filesteps.add(steps);
		}
		return filesteps;
	}
	private ArrayNode getSetWasClusterWas(ArrayNode hostnode, ObjectNode crednode, String name, String type) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "cd /script && sh ./hostname.helper.sh
			// "+rs.get("node").get("host").asText());
			steps.put("exec", "cd " + amsprop.getProperty("was_cluster_script_path") + " &&  ./"
					+ amsprop.getProperty("was_cluster_setup") + " " + type);
			steps.put("type", "scmd");
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}
	private ArrayNode getSetWasClusterBuildWas(ArrayNode hostnode, ObjectNode crednode, String name, String type) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "cd /script && sh ./hostname.helper.sh
			// "+rs.get("node").get("host").asText());
			steps.put("exec", "cd " + amsprop.getProperty("was_cluster_script_path") + " &&  ./"
					+ amsprop.getProperty("was_cluster_build") + " " + type);
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

		/*ObjectNode cred = crednode == null ? (ObjectNode) default_cred.get("default")
				: (ObjectNode) crednode.get("host");*/
		return default_cred;
	}


}
