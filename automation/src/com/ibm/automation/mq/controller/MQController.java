package com.ibm.automation.mq.controller;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.automation.ams.constants.AMS2KeyUtil;
import com.ibm.automation.ams.service.AmsRestService;
import com.ibm.automation.ams.service.impl.AmsRestServiceImpl;
import com.ibm.automation.ams.util.AmsClient;
import com.ibm.automation.ams.util.AmsV2ClientHttpClient4Impl;
import com.ibm.automation.core.bean.AddHostBean;
import com.ibm.automation.core.constants.OPSTPropertyKeyConst;
import com.ibm.automation.core.exception.ParamErrorException;
import com.ibm.automation.core.util.EncodeUtil;
import com.ibm.automation.core.util.FormatUtil;
import com.ibm.automation.core.util.HttpClientUtil;
import com.ibm.automation.core.util.PropertyUtil;

/**
 * MQ单节点的逻辑
 */
@Controller
public class MQController {
	@Autowired
	private AmsRestService amsRestService;
	AmsClient amsClient = new AmsV2ClientHttpClient4Impl();
	ObjectMapper om = new ObjectMapper();
	private static Logger logger = Logger.getLogger(MQController.class);
	Properties p = PropertyUtil.getResourceFile("config/properties/cloud.properties");
	Properties amsprop = PropertyUtil.getResourceFile("config/properties/ams2.properties");

	private ArrayNode serverlists; // 放入认证信息

	public ArrayNode getServerList() // 获取servers 的列表
	{
		ArrayNode lists = getList(null, null, "odata/servers"); // 获取server表数据
		
		logger.info("调用MQController::获取servers表数据。");
		return lists;
	}

	public ArrayNode getList(ArrayNode sort, JsonNode query, String url) {
		try {
			return amsClient.list(sort, query, url);
		} catch (Exception e) {
			logger.error("调用MQController::getList 出错，出错信息为："+e.getMessage());
			//e.printStackTrace();
		}
		return null;
	}

	/**
	 * 拼接MQ 产生的prepare.mq.lst文件
	 */
	private String setMQStandAloneConfigMsg(HttpServletRequest request) {
		String os = request.getParameter("os");// 获取是linux 还是aix
		String lineSep = "\n";
		if (os.equals("linux")) {
			String retStr = "mq_version=" + request.getParameter("mq_version") + lineSep 
					      + "mq_fp=" + request.getParameter("mq_fp") + lineSep 
					      + "mq_inst_path=" + request.getParameter("mq_inst_path") + lineSep 
					      + "mq_user=" + request.getParameter("mq_user") + lineSep 
					      + "mq_hostname=" + request.getParameter("mq_hostname") + lineSep 
					      + "mq_ip=" + request.getParameter("mq_ip") + lineSep
					      + "qmgr_method=" + request.getParameter("qmgr_method") + lineSep 
					      + "qmgr_script=" + request.getParameter("qmgr_script") + lineSep 
					      + "mq_qmgr_name=" + request.getParameter("mq_qmgr_name") + lineSep 
					      + "mq_data_path=" + request.getParameter("mq_data_path") + lineSep 
					      + "mq_log_path=" + request.getParameter("mq_log_path") + lineSep 
					      + "mq_qmgr_plog=" + request.getParameter("mq_qmgr_plog") + lineSep 
					      + "mq_qmgr_slog=" + request.getParameter("mq_qmgr_slog") + lineSep 
					      + "mq_log_psize=" + request.getParameter("mq_log_psize") + lineSep 
					      + "mq_chl_max=" + request.getParameter("mq_chl_max") + lineSep 
					      + "mq_chl_kalive=" + request.getParameter("mq_chl_kalive") + lineSep 
					      + "lin_semmsl=" + request.getParameter("lin_semmsl") + lineSep 
					      + "lin_semmns=" + request.getParameter("lin_semmns") + lineSep 
					      + "lin_semopm=" + request.getParameter("lin_semopm") + lineSep 
					      + "lin_semmni=" + request.getParameter("lin_semmni") + lineSep 
					      + "lin_shmax=" + request.getParameter("lin_shmax") + lineSep 
					      + "lin_shmni=" + request.getParameter("lin_shmni") + lineSep 
					      + "lin_shmall=" + request.getParameter("lin_shmall") + lineSep 
					      + "lin_filemax=" + request.getParameter("lin_filemax") + lineSep 
					      + "lin_nofile=" + request.getParameter("lin_nofile") + lineSep 
					      + "lin_nproc=" + request.getParameter("lin_nproc") + lineSep 
					      + "lin_tcptime=" + request.getParameter("lin_tcptime") + lineSep 
					      + "product=" + amsprop.getProperty("mq_standalone_product") + lineSep 
					      + "ftphost=" + amsprop.getProperty("mq_standalone_ftphost") + lineSep 
					      + "ftppath=" + amsprop.getProperty("mq_standalone_ftppath") + lineSep 
					      + "ftpuser=" + amsprop.getProperty("mq_standalone_ftpuser") + lineSep 
					      + "ftppass=" + amsprop.getProperty("mq_standalone_ftppass");
			return retStr;
		} else {
			String retStr = "mq_version=" + request.getParameter("mq_version") + lineSep 
						  + "mq_fp=" + request.getParameter("mq_fp") + lineSep 
						  + "mq_inst_path=" + request.getParameter("mq_inst_path") + lineSep 
						  + "mq_user=" + request.getParameter("mq_user") + lineSep 
						  + "mq_hostname=" + request.getParameter("mq_hostname") + lineSep 
						  + "mq_ip=" + request.getParameter("mq_ip") + lineSep
					      + "qmgr_method=" + request.getParameter("qmgr_method") + lineSep 
					      + "qmgr_script=" + request.getParameter("qmgr_script") + lineSep 
					      + "mq_qmgr_name=" + request.getParameter("mq_qmgr_name") + lineSep 
					      + "mq_data_path=" + request.getParameter("mq_data_path") + lineSep 
					      + "mq_log_path=" + request.getParameter("mq_log_path") + lineSep 
					      + "mq_qmgr_plog=" + request.getParameter("mq_qmgr_plog") + lineSep 
					      + "mq_qmgr_slog=" + request.getParameter("mq_qmgr_slog") + lineSep 
					      + "mq_log_psize=" + request.getParameter("mq_log_psize") + lineSep 
					      + "mq_chl_max=" + request.getParameter("mq_chl_max") + lineSep 
					      + "mq_chl_kalive=" + request.getParameter("mq_chl_kalive") + lineSep 
					      + "aix_semmni=" + request.getParameter("aix_semmni") + lineSep 
					      + "aix_semmns=" + request.getParameter("aix_semmns") + lineSep 
					      + "aix_shmni=" + request.getParameter("aix_shmni") + lineSep 
					      + "aix_maxuproc=" + request.getParameter("aix_maxuproc") + lineSep 
					      + "aix_nofiles=" + request.getParameter("aix_nofiles") + lineSep 
					      + "aix_data=" + request.getParameter("aix_data") + lineSep 
					      + "aix_stack=" + request.getParameter("aix_stack") + lineSep 
					      + "product=" + amsprop.getProperty("mq_standalone_product") + lineSep 
					      + "ftphost=" + amsprop.getProperty("mq_standalone_ftphost") + lineSep 
					      + "ftppath=" + amsprop.getProperty("mq_standalone_ftppath") + lineSep 
					      + "ftpuser=" + amsprop.getProperty("mq_standalone_ftpuser") + lineSep 
					      + "ftppass=" + amsprop.getProperty("mq_standalone_ftppass");
			return retStr;
		}
	}

	@RequestMapping("/toMqNextPage")
	public String toMqNextPage(HttpServletRequest request, HttpSession session) throws Exception {
		String os = request.getParameter("os");// 判断是AIX 还是linux 平台
		String status = request.getParameter("status");// 得到哪个页面的状态
		String hostId = request.getParameter("hostId");
		String ptype = request.getParameter("ptype");
		String mqfix = request.getParameter("mqfix");// mq 版本补丁的简单形式
		request.setAttribute("mqfix", mqfix);
		request.setAttribute("mq_version", request.getParameter("mq_version"));
		request.setAttribute("mq_fp", request.getParameter("mq_fp"));
		request.setAttribute("mq_inst_path", request.getParameter("mq_inst_path"));
		request.setAttribute("mq_user", request.getParameter("mq_user"));
		request.setAttribute("mq_hostname", request.getParameter("mq_hostname"));
		request.setAttribute("mq_ip", request.getParameter("mq_ip"));
		request.setAttribute("qmgr_method", request.getParameter("qmgr_method"));
		request.setAttribute("qmgr_script", request.getParameter("qmgr_script"));
		request.setAttribute("mq_qmgr_name", request.getParameter("mq_qmgr_name"));
		request.setAttribute("mq_data_path", request.getParameter("mq_data_path"));
		request.setAttribute("mq_log_path", request.getParameter("mq_log_path"));
		request.setAttribute("mq_qmgr_plog", request.getParameter("mq_qmgr_plog"));
		request.setAttribute("mq_qmgr_slog", request.getParameter("mq_qmgr_slog"));
		request.setAttribute("mq_log_psize", request.getParameter("mq_log_psize"));
		request.setAttribute("mq_chl_max", request.getParameter("mq_chl_max"));
		request.setAttribute("mq_chl_kalive", request.getParameter("mq_chl_kalive"));
		if (os.equals("aix")) {
			request.setAttribute("aix_semmni", request.getParameter("aix_semmni"));
			request.setAttribute("aix_semmns", request.getParameter("aix_semmns"));
			request.setAttribute("aix_shmni", request.getParameter("aix_shmni"));
			request.setAttribute("aix_maxuproc", request.getParameter("aix_maxuproc"));
			request.setAttribute("aix_nofiles", request.getParameter("aix_nofiles"));
			request.setAttribute("aix_data", request.getParameter("aix_data"));
			request.setAttribute("aix_stack", request.getParameter("aix_stack"));
			request.setAttribute("os", "aix");
		} else if (os.equals("linux")) {
			request.setAttribute("lin_semmsl", request.getParameter("lin_semmsl"));
			request.setAttribute("lin_semmns", request.getParameter("lin_semmns"));
			request.setAttribute("lin_semopm", request.getParameter("lin_semopm"));
			request.setAttribute("lin_semmni", request.getParameter("lin_semmni"));
			request.setAttribute("lin_shmax", request.getParameter("lin_shmax"));
			request.setAttribute("lin_shmni", request.getParameter("lin_shmni"));
			request.setAttribute("lin_shmall", request.getParameter("lin_shmall"));
			request.setAttribute("lin_filemax", request.getParameter("lin_filemax"));
			request.setAttribute("lin_nofile", request.getParameter("lin_nofile"));
			request.setAttribute("lin_nproc", request.getParameter("lin_nproc"));
			request.setAttribute("lin_tcptime", request.getParameter("lin_tcptime"));
			request.setAttribute("os", "linux");
		} 
		
		if(logger.isDebugEnabled())
		{
			logger.debug("调用MQController::toMqNextPage 所有的参数信息为:os = "+ os + " status=" + status );
		}
		
		String serId = request.getParameter("serId");
		// ObjectMapper om = new ObjectMapper();

		List serIds = new ArrayList();
		if (serId != null && serId != "") {
			String[] ss = serId.split(",");
			for (int i = 0; i < ss.length; i++) {
				serIds.add(ss[i]);
			}
		}
		ArrayNode lists = amsRestService.getList(null, null, "odata/servers");
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
	
		request.setAttribute("serId", serId);
		request.setAttribute("total", lahb.size());
		request.setAttribute("ptype", ptype);
		request.setAttribute("hostId", hostId);
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

		if (status.equals("confirm")) {
			return "instance_aix_linux_mq_comfirm";
		}
		return null;
	}

	@RequestMapping("/installMqStandAloneInfo")
	public String installMqStandAloneInfo(HttpServletRequest request, HttpSession session) {
		String retStr = setMQStandAloneConfigMsg(request);
		ObjectMapper om = new ObjectMapper();
		ArrayNode hosts = om.createArrayNode();
		ArrayNode pvcclusternodes = om.createArrayNode();
		// String mqfix=request.getParameter("mqfix");//得到这个参数是为了日志这页能显示版本补丁
		String mq_hostname = request.getParameter("mq_hostname");
		String mq_ip = request.getParameter("mq_ip");
		String hostId = request.getParameter("hostId");// hostId
		String ptype = request.getParameter("ptype");
		String autoorno = "yes";

		ObjectNode host = createHostObjNode("mqstandalone", mq_hostname, mq_ip);
		hosts.add(host);

		String pvcnodeId = createPvcNodeObjNode(mq_hostname, mq_ip, hostId, "MQ", String.valueOf(1));
		ObjectNode clusterNode = createPvcClusterNode(mq_hostname, mq_ip, hostId, pvcnodeId, String.valueOf(1));
		pvcclusternodes.add(clusterNode);
		ObjectNode run = postMQStandAloneRun(hosts, null, retStr, autoorno);
		logger.info("installMqStandAloneInfo::postMQStandAloneRun finished");
		if (run != null && run.get("uuid") != null) {
			ObjectNode optionsInfo = om.createObjectNode();
			setMQStandAloneOptionsInfo(request, optionsInfo);
			createPvcClustersObjNode(run.get("uuid").asText(), pvcclusternodes, optionsInfo, ptype);
		}
		return "redirect:/getLogInfo";
	
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
		ObjectNode obj=amsRestService.savePVCNode(pvcnode, "odata/pvcnodes");
		logger.info("mq pvcnode::"+obj);
		
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
		host.set("conf", om.createObjectNode().put("mq", type));
		return host;
	}

	/*
	 * 设置mq 单节点属性
	 */
	private void setMQStandAloneOptionsInfo(HttpServletRequest request, ObjectNode optionsInfo) {
		optionsInfo.put("mq_version", request.getParameter("mq_version"));
		optionsInfo.put("mq_fp", request.getParameter("mq_fp"));
		optionsInfo.put("mq_inst_path", request.getParameter("mq_inst_path"));
		optionsInfo.put("mq_user", request.getParameter("mq_user"));
		optionsInfo.put("mq_hostname", request.getParameter("mq_hostname"));
		optionsInfo.put("mq_ip", request.getParameter("mq_ip"));
		optionsInfo.put("qmgr_method", request.getParameter("qmgr_method"));
		optionsInfo.put("qmgr_script", request.getParameter("qmgr_script"));
		optionsInfo.put("mq_qmgr_name", request.getParameter("mq_qmgr_name"));
		optionsInfo.put("mq_data_path", request.getParameter("mq_data_path"));
		optionsInfo.put("mq_log_path", request.getParameter("mq_log_path"));
		optionsInfo.put("mq_qmgr_plog", request.getParameter("mq_qmgr_plog"));
		optionsInfo.put("mq_qmgr_slog", request.getParameter("mq_qmgr_slog"));
		optionsInfo.put("mq_log_psize", request.getParameter("mq_log_psize"));
		optionsInfo.put("mq_chl_max", request.getParameter("mq_chl_max"));
		optionsInfo.put("mq_chl_kalive", request.getParameter("mq_chl_kalive"));
		optionsInfo.put("lin_semmsl", request.getParameter("lin_semmsl"));
		optionsInfo.put("lin_semmns", request.getParameter("lin_semmns"));
		optionsInfo.put("lin_semopm", request.getParameter("lin_semopm"));
		optionsInfo.put("lin_semmni", request.getParameter("lin_semmni"));
		optionsInfo.put("lin_shmax", request.getParameter("lin_shmax"));
		optionsInfo.put("lin_shmni", request.getParameter("lin_shmni"));
		optionsInfo.put("lin_shmall", request.getParameter("lin_shmall"));
		optionsInfo.put("lin_filemax", request.getParameter("lin_filemax"));
		optionsInfo.put("lin_nofile", request.getParameter("lin_nofile"));
		optionsInfo.put("lin_nproc", request.getParameter("lin_nproc"));
		optionsInfo.put("lin_tcptime", request.getParameter("lin_tcptime"));
		optionsInfo.put("aix_semmni", request.getParameter("aix_semmni"));
		optionsInfo.put("aix_semmns", request.getParameter("aix_semmns"));
		optionsInfo.put("aix_shmni", request.getParameter("aix_shmni"));
		optionsInfo.put("aix_maxuproc", request.getParameter("aix_maxuproc"));
		optionsInfo.put("aix_nofiles", request.getParameter("aix_nofiles"));
		optionsInfo.put("aix_data", request.getParameter("aix_data"));
		optionsInfo.put("aix_stack", request.getParameter("aix_stack"));
		optionsInfo.put("mqfix", request.getParameter("mqfix"));
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

	private ArrayNode getMQ_StandAlone_Scripts(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			steps.put("exec", "mkdir -p " + amsprop.getProperty("mq_standalone_script_path"));
			steps.put("type", "scmd");
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}

	public ObjectNode postMQStandAloneRun(ArrayNode hostnode, ObjectNode crednode, String info, String types) {
		// TODO Auto-generated method stub
		serverlists = getServerList(); // 为获取serverlist 的加密认证信息
		// 获取参数信息
		String hdiskHost = p.getProperty(OPSTPropertyKeyConst.AMS2_HOST);
		String hdiskApi = p.getProperty(OPSTPropertyKeyConst.POST_ams2_service_run);
		// 拼接URL
		String strOrgUrl = hdiskHost + hdiskApi;
		logger.info("url::" + strOrgUrl);
		// 构建json
		ObjectNode task = om.createObjectNode();
		ArrayNode jobs = om.createArrayNode();
		task.put("name", "build-mq");
		task.put("immediate", "true");
		task.set("jobs", jobs);
		ArrayNode names = AMS2KeyUtil.getArrayNodeByKey("mq_standalone_job_name"); // job名称的集合
		ObjectNode stepobj = this.getMqStandAloneStepObject();// step集合
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
				if (sname.equals("make-directory")) 
				{
					steps = getMQ_StandAlone_Scripts(hostnode, crednode, jobname);
				} 
				else if (sname.equals("propagate-host-script")) 
				{
					steps = getMQFiles(hostnode, crednode, jobname, "mq_standalone_hostname_sh", "mq_standalone_file_path");
				} 
				else if (sname.equals("propagate-prepare-script")) 
				{
					steps = getMQFiles(hostnode, crednode, jobname, "mq_standalone_download_file", "mq_standalone_file_path");
				} 
				else if (sname.equals("propagate-install-script")) 
				{
					steps = getMQFiles(hostnode, crednode, jobname, "mq_standalone_setup", "mq_standalone_file_path");
				} 
				else if (sname.equals("propagate-build-script")) 
				{
					steps = getMQFiles(hostnode, crednode, jobname, "mq_standalone_build", "mq_standalone_file_path");
				} 
				else if (sname.equals("manipulate-mq-config")) 
				{
					steps = getPrepareMQ_standAlone_Lst(hostnode, crednode, jobname, info);
				} 
				else if (sname.equals("prepare-chmod")) 
				{
					steps = getMQ_standalone_Chmod(hostnode, crednode, jobname);
				} 
				else if (sname.equals("prepare-set-hostname")) 
				{
					steps = getSetMQStandAloneHostname(hostnode, crednode, jobname);
				} 
				else if (sname.equals("download-files")) 
				{
					steps = getMq_DownLoadFiles(hostnode, crednode, jobname);
				} 
				else if (sname.equals("install-mq")) 
				{
					steps = getSetMqStandAloneMq(hostnode, crednode, jobname, "-b");//
				} 
				else if (sname.equals("update-mq")) 
				{
					steps = getSetMqStandAloneMq(hostnode, crednode, jobname, "-f");// 安装was
																					// 补丁
				} 
				else if (sname.equals("build-mq")) 
				{
					steps = getSetMqStandAloneBuildMq(hostnode, crednode, jobname, "-b");//
				} 
				else if (sname.equals("start-mq")) 
				{
					steps = getSetMqStandAloneBuildMq(hostnode, crednode, jobname, "-s");//
				}
				jobnode.set("steps", steps);
				jobs.add(jobnode);
			}
		}

		logger.info("MQ 单节点 task::" + task);
		String response = "";
		try 
		{
			response = HttpClientUtil.postMethod(strOrgUrl, task.toString());
			logger.info("MQ 单节点response::" + response);
			return (ObjectNode) om.readTree(response);
		} 
		catch (ParamErrorException opst) 
		{
			logger.error("调用MQ单节点http异常，异常为" + opst.getMessage());			
		} 
		catch (JsonProcessingException json) 
		{			
			logger.debug("调用MQ单节点json处理异常，异常为" + json.getMessage());
		} 
		catch (IOException io) 
		{
			logger.debug("调用MQ单节点IO处理异常，异常为" + io.getMessage());
		}
		return null;
	}

	private ObjectNode getMqStandAloneStepObject() {
		ObjectNode stepobj = om.createObjectNode();
		ArrayNode prepare_steps = AMS2KeyUtil.getArrayNodeByKey("mq_standalone_prepare_step_name");
		ArrayNode install_steps = null;

		install_steps = AMS2KeyUtil.getArrayNodeByKey("mq_standalone_install_step_name");

		stepobj.set("prepare", prepare_steps);
		stepobj.set("install", install_steps);
		return stepobj;
	}

	private ArrayNode getMQFiles(ArrayNode hostnode, ObjectNode crednode, String name, String filenamekey, String filepath) {
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
				step.put("exec", amsprop.getProperty("mq_standalone_script_path_sub") + filename);
				step.put("type", "sput");
				step.put("file", filepath1 + filename);
				step.put("name", name + "#" + filename + "#" + rs.get("node").get("host").asText());
				filesteps.add(step);
			}
		}
		return filesteps;
	}

	private ArrayNode getPrepareMQ_standAlone_Lst(ArrayNode hostnode, ObjectNode crednode, String name, String info) {
		ArrayNode filesteps = om.createArrayNode();
		ArrayNode raws = getraws(hostnode, crednode);
		String encodestr = EncodeUtil.encode(info.trim());
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "/script/prepare.db2.lst");
			steps.put("exec", amsprop.getProperty("mq_standalone_script_path_sub")
					+ amsprop.getProperty("prepare_mq_standalone_lst"));
			steps.put("type", "scat");
			steps.put("text", "data:text/plain;base64," + encodestr);
			steps.put("name", name + "#" + amsprop.getProperty("prepare_mq_standalone_lst") + "#"
					+ rs.get("node").get("host").asText());
			filesteps.add(steps);
		}
		return filesteps;
	}

	private ArrayNode getMQ_standalone_Chmod(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "chmod +x /script/*.ksh && chmod +x
			// /script/*.sh");
			steps.put("exec", "chmod +x /script/mq/*.sh");
			steps.put("type", "scmd");
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}

	private ArrayNode getSetMQStandAloneHostname(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "cd /script && sh ./hostname.helper.sh
			// "+rs.get("node").get("host").asText());
			steps.put("exec", "cd " + amsprop.getProperty("mq_standalone_script_path") + " &&  ./"
					+ amsprop.getProperty("mq_standalone_hostname_sh") + " -a");
			steps.put("type", "scmd");
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}

	private ArrayNode getMq_DownLoadFiles(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "mkdir -p /script");
			// steps.put("exec", amsprop.getProperty("script_path") +
			// amsprop.getProperty("hostname_sh"));

			steps.put("exec", "cd " + amsprop.getProperty("mq_standalone_script_path") + " && ./"
					+ amsprop.getProperty("mq_standalone_download_file") + " -a");

			steps.put("type", "scmd");
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}

	private ArrayNode getSetMqStandAloneMq(ArrayNode hostnode, ObjectNode crednode, String name, String type) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "cd /script && sh ./hostname.helper.sh
			// "+rs.get("node").get("host").asText());
			steps.put("exec", "cd " + amsprop.getProperty("mq_standalone_script_path") + " &&  ./"
					+ amsprop.getProperty("mq_standalone_setup") + " " + type);
			steps.put("type", "scmd");
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}

	private ArrayNode getSetMqStandAloneBuildMq(ArrayNode hostnode, ObjectNode crednode, String name, String type) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			// steps.put("exec", "cd /script && sh ./hostname.helper.sh
			// "+rs.get("node").get("host").asText());
			steps.put("exec", "cd " + amsprop.getProperty("mq_standalone_script_path") + " &&  ./"
					+ amsprop.getProperty("mq_standalone_build") + " " + type);
			steps.put("type", "scmd");
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}

	/**
	 * 
	 * 获取mq 单节点日志情况
	 * 
	 */
	@RequestMapping("/getmqLogInfoDetial")
	public String getMqStandAloneInfoDetail(HttpServletRequest request, HttpSession session) {
		String uuid = request.getParameter("uuid");
	//	 String serid=request.getParameter("serids");
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

			request.setAttribute("mq_version", options.get("mq_version").asText());
			request.setAttribute("mq_fp", options.get("mq_fp").asText());
			request.setAttribute("mq_inst_path", options.get("mq_inst_path").asText());
			request.setAttribute("mq_user", options.get("mq_user").asText());
			request.setAttribute("mq_hostname", options.get("mq_hostname").asText());
			request.setAttribute("mq_ip", options.get("mq_ip").asText());
			request.setAttribute("qmgr_method", options.get("qmgr_method").asText());
			request.setAttribute("qmgr_script", options.get("qmgr_script").asText());
			request.setAttribute("mq_qmgr_name", options.get("mq_qmgr_name").asText());
			request.setAttribute("mq_data_path", options.get("mq_data_path").asText());
			request.setAttribute("mq_log_path", options.get("mq_log_path").asText());
			request.setAttribute("mq_qmgr_plog", options.get("mq_qmgr_plog").asText());
			request.setAttribute("mq_qmgr_slog", options.get("mq_qmgr_slog").asText());
			request.setAttribute("mq_log_psize", options.get("mq_log_psize").asText());
			request.setAttribute("mq_chl_max", options.get("mq_chl_max").asText());
			request.setAttribute("mq_chl_kalive", options.get("mq_chl_kalive").asText());
			request.setAttribute("mqfix", options.get("mqfix").asText());
			String linuxoraix = options.get("aix_semmni").asText();// 用于判断这是linux
																	// 还是aix的mq
			if (linuxoraix == null || linuxoraix.equals("null")) {
				request.setAttribute("lin_semmsl", options.get("lin_semmsl").asText());
				request.setAttribute("lin_semmns", options.get("lin_semmns").asText());
				request.setAttribute("lin_semopm", options.get("lin_semopm").asText());
				request.setAttribute("lin_semmni", options.get("lin_semmni").asText());
				request.setAttribute("lin_shmax", options.get("lin_shmax").asText());
				request.setAttribute("lin_shmni", options.get("lin_shmni").asText());
				request.setAttribute("lin_shmall", options.get("lin_shmall").asText());
				request.setAttribute("lin_filemax", options.get("lin_filemax").asText());
				request.setAttribute("lin_nofile", options.get("lin_nofile").asText());
				request.setAttribute("lin_nproc", options.get("lin_nproc").asText());
				request.setAttribute("lin_tcptime", options.get("lin_tcptime").asText());
			} else {
				request.setAttribute("aix_semmni", options.get("aix_semmni").asText());
				request.setAttribute("aix_semmns", options.get("aix_semmns").asText());
				request.setAttribute("aix_shmni", options.get("aix_shmni").asText());
				request.setAttribute("aix_maxuproc", options.get("aix_maxuproc").asText());
				request.setAttribute("aix_nofiles", options.get("aix_nofiles").asText());
				request.setAttribute("aix_data", options.get("aix_data").asText());
				request.setAttribute("aix_stack", options.get("aix_stack").asText());
			}
		}
		ArrayNode lists = amsRestService.getList(null, null, "odata/servers");
	
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
						//	System.out.println((name + "_" + i).replace('-', '_').replace('.', '_'));
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

		return "instance_aix_linux_mq_log_details";

	}
/*
 * 得到mq 的小版本
 * */
	@RequestMapping("/getmqfixver")
	@ResponseBody
	public String getMQVer(HttpServletRequest request, HttpSession session) {
		String vsion = request.getParameter("version");
		if(vsion.equals("-1"))//表示选了请选择...
		{
			return "{\"key\":-1,\"value\":-1}";
		}
		if (vsion.equals("") || vsion == null)
			return "error";
		ObjectMapper om = new ObjectMapper();
		ObjectNode on = amsRestService.getVersion("MQ", "linux");
		if (on == null)

		{
			return "error";
		}
		JsonNode fixpack = on.get("fixpack");
		JsonNode package1 = on.get("package");

		JsonNode needVersion = fixpack.get(vsion);// 所需要的版本
		if(needVersion==null)//表示这里的版本包含补丁返回个无就行
		{
			return "{\"key\":-2,\"value\":\"无\"}";
		}
		ArrayNode lastarr = om.createArrayNode();
		if (needVersion instanceof ArrayNode) // 如果是个数组
		{
			ArrayNode an = (ArrayNode) needVersion;// 转换为数组json
			int anlen = an.size();// 获取数组大小
			for (int i = 0; i < anlen; i++) {
				ObjectNode lastobj = om.createObjectNode();
				lastobj.put("key", an.get(i).asText().toString());
				lastobj.put("value", package1.get(an.get(i).asText().toString()).asText().toString());
				lastarr.add(lastobj);
			}
		} else {
			ObjectNode lastobj = om.createObjectNode();

			lastobj.put("key", needVersion.asText().toString());
			lastobj.put("value", package1.get(needVersion.asText().toString()).asText().toString());
			lastarr.add(lastobj);
		}
		String s = lastarr.toString();
		if (s == null || s.equals(""))
			return "error";
		else
			return s;
	}
	/* @author hujin
	 * 
	 * 得到mq 的大版本
	 * */
	@RequestMapping("/getmqversion")
	@ResponseBody
	public String getMQversion(HttpServletRequest request, HttpSession session) {
		String product = request.getParameter("product");
		String platform = request.getParameter("platform");
		
		ObjectMapper om = new ObjectMapper();
		ArrayNode lastarr = om.createArrayNode();
		AmsRestServiceImpl ams = new AmsRestServiceImpl();
		ObjectNode on = ams.getVersion(product.toUpperCase(), platform);
	
		JsonNode fixpack = on.get("version");
		if(fixpack instanceof ObjectNode)
		{
			ObjectNode temp = (ObjectNode) fixpack;	
			Iterator<JsonNode> iter = temp.iterator();
			while(iter.hasNext())
			{
				ObjectNode oe = om.createObjectNode();
				String sTemp=iter.next().asText().toString();
				oe.put("key", sTemp);
				oe.put("value", sTemp);
				
				lastarr.add(oe);
			}
			
			
		}
		String s = lastarr.toString();
		if (s == null || s.equals(""))
			return "error";
		else
			return s;
	}
	public void test()
	{
		String product = "MQ";
		String platform = "linux";
		
		ObjectMapper om = new ObjectMapper();
		ArrayNode lastarr = om.createArrayNode();
		AmsRestServiceImpl ams = new AmsRestServiceImpl();
		ObjectNode on = ams.getVersion(product.toUpperCase(), platform);
	
		JsonNode fixpack = on.get("version");
		if(fixpack instanceof ObjectNode)
		{
			ObjectNode temp = (ObjectNode) fixpack;	
			Iterator<JsonNode> iter = temp.iterator();
			while(iter.hasNext())
			{
				ObjectNode oe = om.createObjectNode();
				String sTemp=iter.next().asText().toString();
				oe.put("key", sTemp);
				oe.put("value", sTemp);
				
				lastarr.add(oe);
			}
			
			
		}
		String s = lastarr.toString();
		/*if (s == null || s.equals(""))
			return "error";
		else
			return s;*/
	}
	public static void main(String[] args) {
		MQController mqc = new MQController();
		mqc.test();
	}
}
