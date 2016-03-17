package com.ibm.automation.mqcluster.controller;

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
import com.ibm.automation.core.bean.MqClusterBean;
import com.ibm.automation.core.bean.MqClusterLogBean;
import com.ibm.automation.core.bean.WasClusterBean;
import com.ibm.automation.core.bean.WasClusterLogBean;
import com.ibm.automation.core.constants.OPSTPropertyKeyConst;
import com.ibm.automation.core.controller.InstanceController;
import com.ibm.automation.core.exception.ParamErrorException;
import com.ibm.automation.core.util.EncodeUtil;
import com.ibm.automation.core.util.FormatUtil;
import com.ibm.automation.core.util.HttpClientUtil;
import com.ibm.automation.core.util.PropertyUtil;

/**
 * MQCluster的逻辑
 */
@Controller
public class MQCLUSTERController {
	@Autowired
	private AmsRestService amsRestService;
	AmsClient amsClient = new AmsV2ClientHttpClient4Impl();
	private static final Logger logger = Logger.getLogger(MQCLUSTERController.class);
	private ArrayNode serverlists; // 放入认证信息
	
	ObjectMapper om = new ObjectMapper();	
	Properties p = PropertyUtil.getResourceFile("config/properties/cloud.properties");
	Properties amsprop = PropertyUtil.getResourceFile("config/properties/ams2.properties");
	
	public ArrayNode getServerList() // 获取servers 的列表
	{
		ArrayNode lists = getList(null, null, "odata/servers"); // 获取server表数据		
		logger.info("调用MQClusterController::获取servers表数据。");
		return lists;
	}
	public ArrayNode getList(ArrayNode sort, JsonNode query, String url) {
		try {
			return amsClient.list(sort, query, url);
		} catch (Exception e) {
			logger.error("调用MQClusterController::getList 出错，出错信息为："+e.getMessage());
		}
		return null;
	}
	
	@RequestMapping("/toMqClusterNextPage")
	public String toMqClusterNextPage(HttpServletRequest request, HttpSession session) throws Exception{
		String os = request.getParameter("os");// 得到平台
		String status = request.getParameter("status");// 得到哪个页面的状态
		String serId = request.getParameter("serId");
		String hostId = request.getParameter("hostId");
		String ptype = request.getParameter("ptype");
		String mqfix = request.getParameter("mqfix");// mq 版本补丁的简单形式
				
		String[] allhostnames = request.getParameterValues("all_hostnames");
		String[] allips = request.getParameterValues("all_ips");
		String[] allqmgrnames = request.getParameterValues("all_qmgr_names");
		String[] allcompletesaves = request.getParameterValues("all_complete_saves");	
		
		for (int i = 0; i < allcompletesaves.length; i++) {
			if (allcompletesaves[i].equals("yes")) {
				if (i == 0) {
					break;
				} else {
					String typetemp = allcompletesaves[0];
					String nametemp = allqmgrnames[0];
					String hosttemp = allhostnames[0];
					String iptemp = allips[0];

					allcompletesaves[0] = allcompletesaves[i];
					allqmgrnames[0] = allqmgrnames[i];
					allhostnames[0] = allhostnames[i];
					allips[0] = allips[i];

					allcompletesaves[i] = typetemp;
					allqmgrnames[i] = nametemp;
					allhostnames[i] = hosttemp;
					allips[i] = iptemp;
				}
			}
		}
		
		List<MqClusterBean> arrList = new ArrayList<MqClusterBean>();
		for (int i = 0; i < allips.length; i++) {
			MqClusterBean bean = new MqClusterBean();
			bean.setIp(allips[i]);
			bean.setName(allhostnames[i]);
			bean.setQmgrname(allqmgrnames[i]);
			bean.setCompletesave(allcompletesaves[i]);
			arrList.add(bean);
		}
		
		request.setAttribute("allservers", arrList); // 显示多个IP 主机名 概要情况
		
		StringBuffer sbhostnames = new StringBuffer();
		StringBuffer sbhostips = new StringBuffer();
		StringBuffer sballqmgrnames = new StringBuffer();
		StringBuffer sbcompletesaves = new StringBuffer();
		
		for (int i = 0; i < allhostnames.length; i++) {
			sbhostnames.append(allhostnames[i]).append(",");
			sbhostips.append(allips[i]).append(",");
			sballqmgrnames.append(allqmgrnames[i]).append(",");
			sbcompletesaves.append(allcompletesaves[i]).append(",");
		}
		
		String allhostname = sbhostnames.substring(0, sbhostnames.length() - 1);// 截取末尾的,
		String allip = sbhostips.substring(0, sbhostips.length() - 1);// 截取获取ip
		String allqmgrname = sballqmgrnames.substring(0, sballqmgrnames.length() - 1);// 截取qmgrname
		String allcompletesave = sbcompletesaves.substring(0, sbcompletesaves.length() - 1);// 截取completesave
		
		request.setAttribute("allhostname", allhostname);
		request.setAttribute("allip", allip);		
		request.setAttribute("allqmgrname", allqmgrname);
		request.setAttribute("allcompletesave", allcompletesave);		
		
		if(logger.isDebugEnabled())
		{
			logger.debug("调用MQClusterController::toMqClusterNextPage 所有的参数信息为:os = "+ os + " status=" + status );
		}
		
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
		request.setAttribute("hostId", hostId);
		
		// mq cluster参数
		request.setAttribute("mq_version", request.getParameter("mq_version"));
		request.setAttribute("mq_fp", request.getParameter("mq_fp"));
		request.setAttribute("mq_inst_path", request.getParameter("mq_inst_path"));
		request.setAttribute("mq_user", request.getParameter("mq_user"));		
		
		request.setAttribute("qmgr_method", request.getParameter("qmgr_method"));
		request.setAttribute("mq_hostname", sb.getName());		
		request.setAttribute("mq_ip", request.getParameter("mq_ip"));				
		request.setAttribute("mq_qmgr_name", request.getParameter("mq_qmgr_name"));
		request.setAttribute("mq_complete_save", request.getParameter("mq_complete_save"));
		request.setAttribute("mq_mon_port", request.getParameter("mq_mon_port"));
		request.setAttribute("mq_qmgr_plog", request.getParameter("mq_qmgr_plog"));		
		request.setAttribute("mq_data_path", request.getParameter("mq_data_path"));
		request.setAttribute("mq_qmgr_slog", request.getParameter("mq_qmgr_slog"));		
		request.setAttribute("mq_log_path", request.getParameter("mq_log_path"));		
		request.setAttribute("mq_log_psize", request.getParameter("mq_log_psize"));		
		request.setAttribute("mq_chl_kalive", request.getParameter("mq_chl_kalive"));
		request.setAttribute("mq_chl_max", request.getParameter("mq_chl_max"));
		
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
		
		request.setAttribute("mqfix", mqfix);
		request.setAttribute("qmgr_script", request.getParameter("qmgr_script"));
		
		if (status.equals("confirm")) {
			return "instance_aix_linux_mqcluster_comfirm";
		}
		return null;
	}
	
	// 创建pvcclusters-nodes-node
	private ObjectNode createPvcClusterNode(String hostname, String addr, String hostId, String pvcnodeId, String type) {
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
		logger.info("mq cluster pvccluster:::" + obj);
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
		logger.info("mq cluster pvcnode::"+obj);		
		return pvcnodeId;
	}
	
	// 创建一个host
	private ObjectNode createHostObjNode(String type, String hostname, String addr) {
		ObjectMapper om = new ObjectMapper();
		ObjectNode host = om.createObjectNode();
		host.put("host", hostname);
		// ip地址可能有多网卡,现在暂时只取第一个
		addr = addr.split(";")[0];
		host.put("addr", addr);
		host.set("conf", om.createObjectNode().put("mq", type));
		return host;
	}
	
	/**
	 * 设置mq cluster 的属性
	 */
	private void setMQClusterOptionsInfo(HttpServletRequest request, ObjectNode optionsInfo){
		optionsInfo.put("mq_hostname", request.getParameter("allhostname"));
		optionsInfo.put("mq_ip", request.getParameter("allip"));
		optionsInfo.put("mq_qmgr_name", request.getParameter("allqmgrname"));
		optionsInfo.put("mq_complete_save", request.getParameter("allcompletesave"));
		
		optionsInfo.put("mq_version", request.getParameter("mq_version"));
		optionsInfo.put("mq_fp", request.getParameter("mq_fp"));
		optionsInfo.put("mqfix", request.getParameter("mqfix"));		
		optionsInfo.put("mq_inst_path", request.getParameter("mq_inst_path"));
		optionsInfo.put("mq_user", request.getParameter("mq_user"));
		
		optionsInfo.put("qmgr_method", request.getParameter("qmgr_method"));
		optionsInfo.put("mq_mon_port", request.getParameter("mq_mon_port"));
		optionsInfo.put("mq_data_path", request.getParameter("mq_data_path"));
		optionsInfo.put("mq_log_path", request.getParameter("mq_log_path"));
		optionsInfo.put("mq_qmgr_plog", request.getParameter("mq_qmgr_plog"));
		optionsInfo.put("mq_qmgr_slog", request.getParameter("mq_qmgr_slog"));
		optionsInfo.put("mq_log_psize", request.getParameter("mq_log_psize"));
		optionsInfo.put("mq_chl_max", request.getParameter("mq_chl_max"));
		optionsInfo.put("mq_chl_kalive", request.getParameter("mq_chl_kalive"));
		
		optionsInfo.put("qmgr_script", request.getParameter("qmgr_script"));		
		
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
	}
	
	/**
	 * 拼接MQ 产生的prepare.mqcluster.lst文件
	 */
	private String setMQClusterConfigMsg(HttpServletRequest request){
		String sbhostname = request.getParameter("allhostname");
		String sbhostip = request.getParameter("allip");		
		String sballqmgrname = request.getParameter("allqmgrname");
		String sbcompletesave = request.getParameter("allcompletesave");
		
		String lineSep = "\n";
		String retStr = "mq_hostname=" + sbhostname + lineSep 
				      + "mq_version=" + request.getParameter("mq_version") + lineSep
				      + "mq_fp=" + request.getParameter("mq_fp") + lineSep 
				      + "mq_user=" + request.getParameter("mq_user") + lineSep
				      + "mq_inst_path=" + request.getParameter("mq_inst_path") + lineSep
				      + "mq_ip=" + sbhostip + lineSep
				      + "qmgr_method=" + request.getParameter("qmgr_method") + lineSep 
					  + "qmgr_script=" + request.getParameter("qmgr_script") + lineSep 
					  + "mq_qmgr_name=" + sballqmgrname + lineSep 
					  + "mq_complete_save=" + sbcompletesave + lineSep  //新加的
					  + "mq_mon_port=" + request.getParameter("mq_mon_port") + lineSep   //新加的
					  + "mq_data_path=" + request.getParameter("mq_data_path") + lineSep 
					  + "mq_log_path=" + request.getParameter("mq_log_path") + lineSep 
					  + "mq_qmgr_plog=" + request.getParameter("mq_qmgr_plog") + lineSep 
					  + "mq_qmgr_slog=" + request.getParameter("mq_qmgr_slog") + lineSep
					  + "mq_log_psize=" + request.getParameter("mq_log_psize") + lineSep 
					  + "mq_chl_max=" + request.getParameter("mq_chl_max") + lineSep 
					  + "mq_chl_kalive=" + request.getParameter("mq_chl_kalive") + lineSep 
					  + "lin_semmsl=" + request.getParameter("lin_semmsl") + lineSep 
					  + "lin_semmns=" + request.getParameter("lin_semmns")+ lineSep 
					  + "lin_semopm=" + request.getParameter("lin_semopm") + lineSep 
					  + "lin_semmni="+ request.getParameter("lin_semmni") + lineSep 
					  + "lin_shmax=" + request.getParameter("lin_shmax") + lineSep 
					  + "lin_shmni=" + request.getParameter("lin_shmni") + lineSep 
					  + "lin_shmall="+ request.getParameter("lin_shmall") + lineSep 
					  + "lin_filemax="+ request.getParameter("lin_filemax") + lineSep 
					  + "lin_nofile=" + request.getParameter("lin_nofile")+ lineSep 
					  + "lin_nproc=" + request.getParameter("lin_nproc") + lineSep 
					  + "lin_tcptime=" + request.getParameter("lin_tcptime") + lineSep 
					  + "product=" + amsprop.getProperty("mq_cluster_product") + lineSep 
					  + "ftphost=" + amsprop.getProperty("mq_cluster_ftphost") + lineSep 
					  + "ftppath=" + amsprop.getProperty("mq_cluster_ftppath") + lineSep 
					  + "ftpuser=" + amsprop.getProperty("mq_cluster_ftpuser") + lineSep 
					  + "ftppass=" + amsprop.getProperty("mq_cluster_ftppass");
		return retStr;
	}
	
	@RequestMapping("/installMqClusterInfo")
	public String installMqClusterInfo(HttpServletRequest request, HttpSession session){
		String retStr = setMQClusterConfigMsg(request);
		String allhostname = request.getParameter("allhostname");
		String allip = request.getParameter("allip");
		String allqmgrname = request.getParameter("allqmgrname");
		String allcompletesave = request.getParameter("allcompletesave");
		System.out.println(allqmgrname +  "---->"+ allcompletesave);
		String[] allips = InstanceController.convertStrToArray(allip);
		String[] allhostnames = InstanceController.convertStrToArray(allhostname);
		
		String hostIds = request.getParameter("serId");
		String[] hostIdss = hostIds.split(",");
		ObjectMapper om = new ObjectMapper();
		ArrayNode hosts = om.createArrayNode();
		ArrayNode pvcclusternodes = om.createArrayNode();
		
		String ptype = request.getParameter("ptype");
		String autoorno = "yes";
		for(int i=0; i<allips.length; i++){
			ObjectNode host = createHostObjNode("mqcluster", allhostnames[i], allips[i]);
			hosts.add(host);
			String pvcnodeId = createPvcNodeObjNode(allhostnames[i], allips[i], hostIdss[i], "mqcluster", String.valueOf(1));
			ObjectNode clusterNode = createPvcClusterNode(allhostnames[i], allips[i], hostIdss[i], pvcnodeId, String.valueOf(1));
			pvcclusternodes.add(clusterNode);
		}
		ObjectNode run = this.postMQClusterRun(hosts, null, retStr, autoorno);
		logger.info("installMqClusterInfo::postMQClusterRun finished");
		if (run != null && run.get("uuid") != null) {
			ObjectNode optionsInfo = om.createObjectNode();
			setMQClusterOptionsInfo(request, optionsInfo);
			createPvcClustersObjNode(run.get("uuid").asText(), pvcclusternodes, optionsInfo, "MQ Cluster");
		}
		return "redirect:/getLogInfo";
	}
	
	/** 提供封装json的拼装方法 开始 **/
	private ObjectNode getcred(ArrayNode an, ObjectNode crednode){
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
		return default_cred;
	}
	
	private ArrayNode getraws(ArrayNode hostnode, ObjectNode crednode) {
		ArrayNode stephosts = om.createArrayNode();
		for (JsonNode h : hostnode) {
			ObjectNode step = om.createObjectNode();
			step.set("cred", getcred(hostnode, crednode));
			step.set("node", om.createObjectNode().put("host", h.get("host").asText()).put("addr", h.get("addr").asText()));
			stephosts.add(step);
		}
		return stephosts;
	}
	
	private ArrayNode getMQ_Cluster_Scripts(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			steps.put("exec", "mkdir -p " + amsprop.getProperty("mq_cluster_script_path"));
			steps.put("type", "scmd");
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}
	
	public ObjectNode postMQClusterRun(ArrayNode hostnode, ObjectNode crednode, String info, String type){
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
		task.put("name", "build-mqcluster");
		task.put("immediate", "false");
		task.set("jobs", jobs);
		ArrayNode names = AMS2KeyUtil.getArrayNodeByKey("mq_cluster_job_name"); // job名称的集合
		ObjectNode stepobj = this.getMqClusterStepObject();// step集合
		for(JsonNode hnode : hostnode){
			ArrayNode an = om.createArrayNode();//// 将数组拆成单节点
			an.add(hnode);
			for (JsonNode jnode : names) { // prepare,install
				ObjectNode job = (ObjectNode) jnode;
				String name = job.get("name").asText();
				ArrayNode stepnames = (ArrayNode) stepobj.get(name);
				for (JsonNode sn : stepnames){
					ObjectNode jobnode = om.createObjectNode();
					ArrayNode steps = null;
					String sname = sn.get("name").asText();
					String jobname = sname;
					jobnode.put("name", jobname);
					jobnode.put("type", "series");
					if (sname.equals("make-directory")) 
					{
						steps = getMQ_Cluster_Scripts(an, crednode, jobname);
					} 
					else if (sname.equals("propagate-scripts")) 
					{
						steps = getMQClusterFiles(an, crednode, jobname, "mq_cluster_file_name", "mq_cluster_file_path");
					} 
					else if (sname.equals("manipulate-mq-config")) 
					{
						steps = getPrepareMQ_cluster_Lst(an, crednode, jobname, info);
					} 
					else if (sname.equals("prepare-chmod")) 
					{
						steps = getMQ_cluster_Chmod(an, crednode, jobname);
					} 
					else if (sname.equals("prepare-set-hostname")) 
					{
						steps = getSetMQClusterHostname(an, crednode, jobname);
					} 
					else if (sname.equals("download-files")) 
					{
						steps = getMqCluster_DownLoadFiles(an, crednode, jobname);
					} 
					else if (sname.equals("install-mq")) 
					{
						steps = getSetMqClusterMq(an, crednode, jobname, "mq");
					} 
					else if (sname.equals("update-mq")) 
					{
						steps = getSetMqClusterMq(an, crednode, jobname, "-f");// 安装was补丁
					} 
					else if (sname.equals("build-mq")) 
					{
						steps = getSetMqClusterBuildMq(an, crednode, jobname, "-b");//
					} 
					else if (sname.equals("start-mq")) 
					{
						steps = getSetMqClusterBuildMq(an, crednode, jobname, "-s");//
					}
					jobnode.set("steps", steps);
					jobs.add(jobnode);
				}
			}
		}
		logger.info("MQ Cluster task::" + task);
		String response = "";
		try {
			response = HttpClientUtil.postMethod(strOrgUrl, task.toString());
			logger.info("MQ Cluster response::" + response);
			return (ObjectNode) om.readTree(response);
		} catch (ParamErrorException opst) {
			logger.debug("调用MQCluster http异常，异常为" + opst.getMessage());			
		} catch (JsonProcessingException json) {			
			logger.debug("调用MQCluster json处理异常，异常为" + json.getMessage());
		} catch (IOException io) {
			logger.debug("调用MQCluster IO处理异常，异常为" + io.getMessage());
		}
		return null;
	}
	
	private ObjectNode getMqClusterStepObject() {
		ObjectNode stepobj = om.createObjectNode();
		ArrayNode prepare_steps = AMS2KeyUtil.getArrayNodeByKey("mq_cluster_prepare_step_name");
		ArrayNode install_steps = null;
		install_steps = AMS2KeyUtil.getArrayNodeByKey("mq_cluster_install_step_name");
		stepobj.set("prepare", prepare_steps);
		stepobj.set("install", install_steps);
		return stepobj;
	}
	
	private ArrayNode getMQClusterFiles(ArrayNode hostnode, ObjectNode crednode, String name, String filenamekey, String filepath) {
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
				step.put("exec", amsprop.getProperty("mq_cluster_script_path_sub") + filename);
				step.put("type", "sput");
				step.put("file", filepath1 + filename);
				step.put("name", name + "#" + filename + "#" + rs.get("node").get("host").asText());
				filesteps.add(step);
			}
		}
		return filesteps;
	}
	
	private ArrayNode getPrepareMQ_cluster_Lst(ArrayNode hostnode, ObjectNode crednode, String name, String info) {
		ArrayNode filesteps = om.createArrayNode();
		ArrayNode raws = getraws(hostnode, crednode);
		String encodestr = EncodeUtil.encode(info.trim());
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			steps.put("exec", amsprop.getProperty("mq_cluster_script_path_sub")
					+ amsprop.getProperty("prepare_mq_cluster_lst"));
			steps.put("type", "scat");
			steps.put("text", "data:text/plain;base64," + encodestr);
			steps.put("name", name + "#" + amsprop.getProperty("prepare_mq_cluster_lst") + "#"
					+ rs.get("node").get("host").asText());
			filesteps.add(steps);
		}
		return filesteps;
	}
	
	private ArrayNode getMQ_cluster_Chmod(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			steps.put("exec", "chmod +x /script/mqcluster/*.sh");
			steps.put("type", "scmd");
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}
	
	private ArrayNode getSetMQClusterHostname(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			steps.put("exec", "cd " + amsprop.getProperty("mq_cluster_script_path") + " &&  ./"
					+ amsprop.getProperty("mq_cluster_hostname_sh") + " -a");
			steps.put("type", "scmd");
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}
	
	private ArrayNode getMqCluster_DownLoadFiles(ArrayNode hostnode, ObjectNode crednode, String name) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			steps.put("exec", "cd " + amsprop.getProperty("mq_cluster_script_path") + " && ./"
					+ amsprop.getProperty("mq_cluster_download_file") + " -a");
			steps.put("type", "scmd");
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}
	
	private ArrayNode getSetMqClusterMq(ArrayNode hostnode, ObjectNode crednode, String name, String type) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			steps.put("exec", "cd " + amsprop.getProperty("mq_cluster_script_path") + " &&  ./"
					+ amsprop.getProperty("mq_cluster_setup") + " " + type);
			steps.put("type", "scmd");
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}
	
	private ArrayNode getSetMqClusterBuildMq(ArrayNode hostnode, ObjectNode crednode, String name, String type) {
		ArrayNode raws = getraws(hostnode, crednode);
		for (JsonNode rs : raws) {
			ObjectNode steps = (ObjectNode) rs;
			steps.put("async", true);
			steps.put("exec", "cd " + amsprop.getProperty("mq_cluster_script_path") + " &&  ./"
					+ amsprop.getProperty("mq_cluster_build") + " " + type);
			steps.put("type", "scmd");
			steps.put("name", name + "#" + rs.get("node").get("host").asText());
		}
		return raws;
	}
	
	
	 /* 
	 * 获取mq cluster 日志情况
	 */
	
	@RequestMapping("/getmqclusterLogInfoDetial")
	public String getMqClusterInfoDetail(HttpServletRequest request, HttpSession session){
		String uuid = request.getParameter("uuid");
		ArrayNode pvcclusters = amsRestService.getList(null, null, "odata/pvcclusters?uuid=" + uuid);
		ObjectNode cluster = null;
		if (pvcclusters != null && pvcclusters.size() > 0) 
		{
			cluster = (ObjectNode) pvcclusters.get(0);
		}
		List<String> serIds = new ArrayList<String>();
		if (cluster != null && cluster.get("nodes") != null) {
			ArrayNode nodes = (ArrayNode) cluster.get("nodes");
			for (JsonNode jn : nodes) {
				serIds.add(jn.get("pvcid") == null ? "" : jn.get("pvcid").asText());
			}
		}
		if (cluster != null && cluster.get("options") != null){
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
			List<MqClusterLogBean> mqclusterlist = new ArrayList<MqClusterLogBean>();
			MqClusterLogBean mclb = null;
			
			ArrayNode jobsNode = (ArrayNode) task.get("jobs");
			for (JsonNode jsonNode : jobsNode) {
				ObjectNode jobnode = (ObjectNode) jsonNode;
				String name = jobnode.get("name").asText();
				if(name.equals("make-directory")){
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
							// 成功
							String successstep = ((name + "_" + i).replace('-', '_').replace('.', '_'));
							map.put((name + "_" + i).replace('-', '_').replace('.', '_'), "成功");
							setMqClusterLogDetail(mclb, name, i, "成功");
						} 
						else 
						{
							// 失败
							map.put((name + "_" + i).replace('-', '_').replace('.', '_'), "失败");
							setMqClusterLogDetail(mclb, name, i, "失败");
						}
					} 
					else if ("1".equals(script_status)) 
					{
						map.put((name + "_" + i).replace('-', '_').replace('.', '_'), "执行中");
						setMqClusterLogDetail(mclb, name, i, "执行中");
					} 
					else 
					{
						// 未执行
						map.put((name + "_" + i).replace('-', '_').replace('.', '_'), "未开始");
						setMqClusterLogDetail(mclb, name, i, "未执行");
					}
					String script_addr = step.get("node").get("addr").asText();				
					map.put((name + "_addr_" + i).replace('-', '_').replace('.', '_'), script_addr);
					setMqClusterLogAddressDetail(mclb, name, i, script_addr);
				}
				if("start-mq".equals(name))
					mqclusterlist.add(mclb);
			}
			request.setAttribute("mqclusterservers",mqclusterlist);
		}
		return "instance_aix_linux_mqcluster_log_details";
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
	
}
