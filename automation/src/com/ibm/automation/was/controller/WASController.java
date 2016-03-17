package com.ibm.automation.was.controller;

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



@Controller
public class WASController {
		
		@Autowired
		private AmsRestService amsRestService;
		ObjectMapper om = new ObjectMapper();
		Properties p = PropertyUtil.getResourceFile("config/properties/cloud.properties");
		Properties amsprop = PropertyUtil.getResourceFile("config/properties/ams2.properties");
		
		AmsClient amsClient = new AmsV2ClientHttpClient4Impl();
		private static Logger logger = Logger.getLogger(WASController.class);
		private  ArrayNode serverlists; //放入认证信息
		
		public ArrayNode getServerList()  //获取servers 的列表
		{
			ArrayNode lists = getList(null,null, "odata/servers");  //获取server表数据
			return lists;
		}
		public ArrayNode getList(ArrayNode sort, JsonNode query, String url) {
			try {
				return amsClient.list(sort, query, url);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("getList 获取数据失败");
			}
			return null;
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
		
		@RequestMapping("/toWasNextPage")
		public String toWasNextPage(HttpServletRequest request, HttpSession session) throws Exception {
			

			String serId = request.getParameter("serId");

			String ptype = request.getParameter("ptype");// db2 -db2ha -was - mq

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
			request.setAttribute("hostId", sb.get_id());

			String status = request.getParameter("status");
			// was参数
			request.setAttribute("was_hostname", sb.getName());
			request.setAttribute("was_version", request.getParameter("was_version"));
			request.setAttribute("was_fp", request.getParameter("was_fp"));
			request.setAttribute("was_user", request.getParameter("was_user"));
			request.setAttribute("was_im_path", request.getParameter("was_im_path"));
			request.setAttribute("was_inst_path", request.getParameter("was_inst_path"));
			request.setAttribute("was_jdk7", request.getParameter("was_jdk7"));
			request.setAttribute("was_ip", request.getParameter("was_ip"));
			request.setAttribute("was_profile_type", request.getParameter("was_profile_type"));
			request.setAttribute("was_profile_path", request.getParameter("was_profile_path"));
			request.setAttribute("was_profile_name", request.getParameter("was_profile_name"));
			request.setAttribute("was_security", request.getParameter("was_security"));
			request.setAttribute("was_userid", request.getParameter("was_userid"));
			request.setAttribute("was_password", request.getParameter("was_password"));
			request.setAttribute("was_nofile_soft", request.getParameter("was_nofile_soft"));
			request.setAttribute("was_nofile_hard", request.getParameter("was_nofile_hard"));
			request.setAttribute("was_fsize_soft", request.getParameter("was_fsize_soft"));
			request.setAttribute("was_fsize_hard", request.getParameter("was_fsize_hard"));
			request.setAttribute("was_core_soft", request.getParameter("was_core_soft"));
			request.setAttribute("was_core_hard", request.getParameter("was_core_hard"));
			request.setAttribute("hostId", request.getParameter("hostId"));
			request.setAttribute("wasfix", request.getParameter("wasfix"));
			
			if (status.equals("confirm")) {
				return "instance_linux_was_confirm";
			}
			return null;
		}
		// 拼接was单节点参数
		private String setWasStandAloneConfigMsg(HttpServletRequest request) {
			String lineSep = "\n";
			String retStr = "was_hostname=" + request.getParameter("was_hostname") + lineSep 
					      + "was_version=" + request.getParameter("was_version") + lineSep 
					      + "was_fp=" + request.getParameter("was_fp") + lineSep
					      + "was_user=" + request.getParameter("was_user") + lineSep 
					      + "was_im_path=" + request.getParameter("was_im_path") + lineSep 
					      + "was_inst_path=" + request.getParameter("was_inst_path") + lineSep 
					      + "was_jdk7=" + request.getParameter("was_jdk7") + lineSep 
					      + "was_ip=" + request.getParameter("was_ip") + lineSep 
					      + "was_profile_type=default" + lineSep
					      + "was_profile_path=" + request.getParameter("was_profile_path") + lineSep 
					      + "was_profile_name=" + request.getParameter("was_profile_name") + lineSep 
					      + "was_security=" + request.getParameter("was_security") + lineSep 
					      + "was_userid=" + request.getParameter("was_userid") + lineSep 
					      + "was_password=" + request.getParameter("was_password") + lineSep 
					      + "was_nofile_soft=" + request.getParameter("was_nofile_soft") + lineSep 
					      + "was_nofile_hard=" + request.getParameter("was_nofile_hard") + lineSep 
					      + "was_fsize_soft=" + request.getParameter("was_fsize_soft") + lineSep 
					      + "was_fsize_hard=" + request.getParameter("was_fsize_hard") + lineSep 
					      + "was_core_soft=" + request.getParameter("was_core_soft") + lineSep 
					      + "was_core_hard=" + request.getParameter("was_core_hard") + lineSep 
					      + "product=" + amsprop.getProperty("was_standalone_product") + lineSep 
					      + "ftphost=" + amsprop.getProperty("was_standalone_ftphost") + lineSep 
					      + "ftppath=" + amsprop.getProperty("was_standalone_ftppath") + lineSep 
					      + "ftpuser=" + amsprop.getProperty("was_standalone_ftpuser") + lineSep 
					      + "ftppass=" + amsprop.getProperty("was_standalone_ftppass");
			return retStr;

		}
		@RequestMapping("/installWasStandAloneInfo")
		public String installWasStandAloneInfo(HttpServletRequest request, HttpSession session) {
			String retStr = setWasStandAloneConfigMsg(request);
			logger.info(retStr);
			ObjectMapper om = new ObjectMapper();
			ArrayNode hosts = om.createArrayNode();
			ArrayNode pvcclusternodes = om.createArrayNode();
			String was_hostname = request.getParameter("was_hostname");
			String was_ip = request.getParameter("was_ip");
			String hostId = request.getParameter("hostId");// hostId
			String ptype = request.getParameter("ptype");
			String autoorno = "yes";
			String wasjdk7 = request.getParameter("was_jdk7"); // 是否安装jdk7
			ObjectNode host = createHostObjNode("wasstandalone", was_hostname, was_ip);
			hosts.add(host);

			String pvcnodeId = createPvcNodeObjNode(was_hostname, was_ip, hostId, "WAS", String.valueOf(1));
			ObjectNode clusterNode = createPvcClusterNode(was_hostname, was_ip, hostId, pvcnodeId, String.valueOf(1));
			pvcclusternodes.add(clusterNode);
			ObjectNode run = postWASStandAloneRun(hosts, null, retStr, autoorno, wasjdk7);
			
			logger.info("postWASStandAloneRun finished");

			if (run != null && run.get("uuid") != null) {
				ObjectNode optionsInfo = om.createObjectNode();
				setWasOptionsInfo(request, optionsInfo);
				createPvcClustersObjNode(run.get("uuid").asText(), pvcclusternodes, optionsInfo, ptype);
			}
			
			return "redirect:/getLogInfo";
			
		}
		/**
		 * 设置was 单节点的数据
		 */
		private void setWasOptionsInfo(HttpServletRequest request, ObjectNode optionsInfo) {
			optionsInfo.put("was_hostname", request.getParameter("was_hostname"));
			optionsInfo.put("was_version", request.getParameter("was_version"));
			optionsInfo.put("was_fp", request.getParameter("was_fp"));
			optionsInfo.put("was_user", request.getParameter("was_user"));
			optionsInfo.put("was_im_path", request.getParameter("was_im_path"));
			optionsInfo.put("was_inst_path", request.getParameter("was_inst_path"));
			optionsInfo.put("was_jdk7", request.getParameter("was_jdk7"));
			optionsInfo.put("was_ip", request.getParameter("was_ip"));
			optionsInfo.put("was_profile_type", request.getParameter("was_profile_type"));
			optionsInfo.put("was_profile_path", request.getParameter("was_profile_path"));
			optionsInfo.put("was_profile_name", request.getParameter("was_profile_name"));
			optionsInfo.put("was_security", request.getParameter("was_security"));
			optionsInfo.put("was_userid", request.getParameter("was_userid"));
			optionsInfo.put("was_password", request.getParameter("was_password"));
			optionsInfo.put("was_nofile_soft", request.getParameter("was_nofile_soft"));
			optionsInfo.put("was_nofile_hard", request.getParameter("was_nofile_hard"));
			optionsInfo.put("was_fsize_soft", request.getParameter("was_fsize_soft"));
			optionsInfo.put("was_fsize_hard", request.getParameter("was_fsize_hard"));
			optionsInfo.put("was_core_soft", request.getParameter("was_core_soft"));
			optionsInfo.put("was_core_hard", request.getParameter("was_core_hard"));
			optionsInfo.put("wasfix", request.getParameter("wasfix"));			
		}
		
		
		
		/**
		 * 
		 * 获取was 单节点日志情况
		 * 
		 */
		@RequestMapping("/getwasLogInfoDetial")
		public String getWasStandAloneInfoDetail(HttpServletRequest request, HttpSession session) {
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
	//		System.out.println(lahb);

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
								// System.out.println((name + "_" + i).replace('-',
								// '_').replace('.', '_'));
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
						// System.out.println((name + "_addr_" + i).replace('-',
						// '_').replace('.', '_'));
						map.put((name + "_addr_" + i).replace('-', '_').replace('.', '_'), script_addr);

					}
				}
			}

			return "instance_linux_was_log_details";
		}
		
		
		public ObjectNode postWASStandAloneRun(ArrayNode hostnode, ObjectNode crednode, String info, String type,String jdk7) {
			serverlists =getServerList(); //为获取serverlist 的加密认证信息
						
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
			logger.info("strOrgUrl::"+strOrgUrl);
			// 构建json
			ObjectNode task = om.createObjectNode();
			ArrayNode jobs = om.createArrayNode();
			task.put("name", "build-was");
			task.put("immediate", "true");
			task.set("jobs", jobs);
			ArrayNode names = AMS2KeyUtil.getArrayNodeByKey("was_standalone_job_name"); // job名称的集合
			ObjectNode stepobj = this.getWasStandAloneStepObject(jdk7);// step集合
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
						steps = getWas_StandAlone_Scripts(hostnode, crednode, jobname);
					} else if (sname.equals("propagate-scripts")) {
						steps = getWasFiles(hostnode, crednode, jobname, "was_standalone_file_name",
								"was_standalone_file_path");
					} else if (sname.equals("manipulate-was-config")) {
						steps = getPrepareWas_standAlone_Lst(hostnode, crednode, jobname, info);
					} else if (sname.equals("prepare-chmod")) {
						steps = getWas_standalone_Chmod(hostnode, crednode, jobname);
					} else if (sname.equals("prepare-set-hostname")) {
						steps = getSetWasStandAloneHostname(hostnode, crednode, jobname);
					} else if (sname.equals("download-files")) {
						steps = getWas_DownLoadFiles(hostnode, crednode, jobname);
					} else if ( sname.equals("install-im")) {
						steps = getSetWasStandAloneWas(hostnode, crednode, jobname, "im"); // 安装im
					} else if ( sname.equals("install-was")) {
						steps = getSetWasStandAloneWas(hostnode, crednode, jobname, "was");// 安装was
					} else if ( sname.equals("update-was")) {
						steps = getSetWasStandAloneWas(hostnode, crednode, jobname, "fp");// 安装was
																							// 补丁
					} else if (flag &&  sname.equals("install-jdk")) {
						steps = getSetWasStandAloneWas(hostnode, crednode, jobname, "jdk7");// 安装jdk
					} else if ( sname.equals("build-was")) {
						steps = getSetWasStandAloneBuildWas(hostnode, crednode, jobname, "build");// 创建概要
					} else if ( sname.equals("start-was")) {
						steps = getSetWasStandAloneBuildWas(hostnode, crednode, jobname, "start");// 启动服务
					}
					jobnode.set("steps", steps);
					jobs.add(jobnode);

				}
			}
			logger.info("task::" + task);
		//	System.out.println("task::" + task);
			String response = "";
			try 
			{
				response = HttpClientUtil.postMethod(strOrgUrl, task.toString());
				logger.info("response::"+response);
				return (ObjectNode) om.readTree(response);
			} 
			catch (ParamErrorException opst) 
			{
				logger.error("调用was单节点http异常，异常为" + opst.getMessage());
			} 
			catch (JsonProcessingException json) 
			{
				logger.error("调用was单节点json处理异常，异常为" + json.getMessage());
			} 
			catch (IOException io) 
			{
				logger.error("调用was单节点IO处理异常，异常为" + io.getMessage());
			}
			return null;
		}
		
		
		/** 提供封装json的拼装方法 开始 **/
		private ObjectNode getcred(ArrayNode an , ObjectNode crednode) {
			//ObjectNode default_cred = AMS2KeyUtil.getCredDefaultUserNode();
			
			ObjectNode default_cred= om.createObjectNode();
			
			for(JsonNode jn : serverlists)
			{
				JsonNode ip = an.get(0).get("addr");//addr
				String ip1 = ip.asText().toString();
				if(ip1.equals(jn.get("IP").asText()))
				{
					default_cred.put("user", jn.get("UserID").asText());
					default_cred.put("pass", jn.get("Password").asText());
					break;
				}
			}	
			
			ObjectNode cred = crednode == null ? (ObjectNode) default_cred.get("default")
					: (ObjectNode) crednode.get("host");
			return default_cred;
		}
		private ArrayNode getraws(ArrayNode hostnode, ObjectNode crednode) {
			ArrayNode stephosts = om.createArrayNode();
			for (JsonNode h : hostnode) {
				ObjectNode step = om.createObjectNode();
				step.set("cred", getcred(hostnode,crednode));
				step.set("node",
						om.createObjectNode().put("host", h.get("host").asText()).put("addr", h.get("addr").asText()));

				stephosts.add(step);
			}
			return stephosts;
		}
		private ArrayNode getWas_StandAlone_Scripts(ArrayNode hostnode, ObjectNode crednode, String name) {
			ArrayNode raws = getraws(hostnode, crednode);
			for (JsonNode rs : raws) {
				ObjectNode steps = (ObjectNode) rs;
				steps.put("async", true);
				// steps.put("exec", "mkdir -p /script");
				steps.put("exec", "mkdir -p " + amsprop.getProperty("was_standalone_script_path"));
				steps.put("type", "scmd");
				steps.put("name", name + "#" + rs.get("node").get("host").asText());
			}
			return raws;
		}
		private ArrayNode getSetWasStandAloneHostname(ArrayNode hostnode, ObjectNode crednode, String name) {
			ArrayNode raws = getraws(hostnode, crednode);
			for (JsonNode rs : raws) {
				ObjectNode steps = (ObjectNode) rs;
				steps.put("async", true);
				// steps.put("exec", "cd /script && sh ./hostname.helper.sh
				// "+rs.get("node").get("host").asText());
				steps.put("exec", "cd " + amsprop.getProperty("was_standalone_script_path") + " &&  ./"
						+ amsprop.getProperty("was_standalone_hostname_sh"));
				steps.put("type", "scmd");
				steps.put("name", name + "#" + rs.get("node").get("host").asText());
			}
			return raws;
		}
		private ObjectNode getWasStandAloneStepObject(String jdk7) {
			ObjectNode stepobj = om.createObjectNode();
			ArrayNode prepare_steps = AMS2KeyUtil.getArrayNodeByKey("was_standalone_prepare_step_name");
			ArrayNode install_steps = null;
			if(jdk7.equals("yes"))  //如果存在jdk7
			{
			 install_steps = AMS2KeyUtil.getArrayNodeByKey("was_standalone_install_step_name");
			}else
			{
			 install_steps = AMS2KeyUtil.getArrayNodeByKey("was_standalone_install_withoutjdk7_step_name");
			}
			stepobj.set("prepare", prepare_steps);
			stepobj.set("install", install_steps);
			return stepobj;
		}
		private ArrayNode getWasFiles(ArrayNode hostnode, ObjectNode crednode, String name, String filenamekey,
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
					step.put("exec", amsprop.getProperty("was_standalone_script_path_sub") + filename);
					step.put("type", "sput");
					step.put("file", filepath1 + filename);
					step.put("name", name + "#" + filename + "#" + rs.get("node").get("host").asText());
					filesteps.add(step);
				}
			}
			return filesteps;
		}
		private ArrayNode getSetWasStandAloneWas(ArrayNode hostnode, ObjectNode crednode, String name, String type) {
			ArrayNode raws = getraws(hostnode, crednode);
			for (JsonNode rs : raws) {
				ObjectNode steps = (ObjectNode) rs;
				steps.put("async", true);
				// steps.put("exec", "cd /script && sh ./hostname.helper.sh
				// "+rs.get("node").get("host").asText());
				steps.put("exec", "cd " + amsprop.getProperty("was_standalone_script_path") + " &&  ./"
						+ amsprop.getProperty("was_standalone_setup") + " " + type);
				steps.put("type", "scmd");
				steps.put("name", name + "#" + rs.get("node").get("host").asText());
			}
			return raws;
		}
		private ArrayNode getWas_DownLoadFiles(ArrayNode hostnode, ObjectNode crednode, String name) {
			ArrayNode raws = getraws(hostnode, crednode);
			for (JsonNode rs : raws) {
				ObjectNode steps = (ObjectNode) rs;
				steps.put("async", true);
				// steps.put("exec", "mkdir -p /script");
				// steps.put("exec", amsprop.getProperty("script_path") +
				// amsprop.getProperty("hostname_sh"));

				steps.put("exec", "cd " + amsprop.getProperty("was_standalone_script_path") + " && ./"
						+ amsprop.getProperty("was_standalone_download_file"));

				steps.put("type", "scmd");
				steps.put("name", name + "#" + rs.get("node").get("host").asText());
			}
			return raws;
		}
		private ArrayNode getPrepareWas_standAlone_Lst(ArrayNode hostnode, ObjectNode crednode, String name, String info) {
			ArrayNode filesteps = om.createArrayNode();
			ArrayNode raws = getraws(hostnode, crednode);
			String encodestr = EncodeUtil.encode(info.trim());
			for (JsonNode rs : raws) {
				ObjectNode steps = (ObjectNode) rs;
				steps.put("async", true);
				// steps.put("exec", "/script/prepare.db2.lst");
				steps.put("exec", amsprop.getProperty("was_standalone_script_path_sub")
						+ amsprop.getProperty("prepare_was_standalone_lst"));
				steps.put("type", "scat");
				steps.put("text", "data:text/plain;base64," + encodestr);
				steps.put("name", name + "#" + amsprop.getProperty("prepare_was_standalone_lst") + "#"
						+ rs.get("node").get("host").asText());
				filesteps.add(steps);
			}
			return filesteps;
		}
		private ArrayNode getWas_standalone_Chmod(ArrayNode hostnode, ObjectNode crednode, String name) {
			ArrayNode raws = getraws(hostnode, crednode);
			for (JsonNode rs : raws) {
				ObjectNode steps = (ObjectNode) rs;
				steps.put("async", true);
				// steps.put("exec", "chmod +x /script/*.ksh && chmod +x
				// /script/*.sh");
				steps.put("exec", "chmod +x /script/was/*.sh");
				steps.put("type", "scmd");
				steps.put("name", name + "#" + rs.get("node").get("host").asText());
			}
			return raws;
		}
		private ArrayNode getSetWasStandAloneBuildWas(ArrayNode hostnode, ObjectNode crednode, String name, String type) {
			ArrayNode raws = getraws(hostnode, crednode);
			for (JsonNode rs : raws) {
				ObjectNode steps = (ObjectNode) rs;
				steps.put("async", true);
				// steps.put("exec", "cd /script && sh ./hostname.helper.sh
				// "+rs.get("node").get("host").asText());
				steps.put("exec", "cd " + amsprop.getProperty("was_standalone_script_path") + " &&  ./"
						+ amsprop.getProperty("was_standalone_build") + " " + type);
				steps.put("type", "scmd");
				steps.put("name", name + "#" + rs.get("node").get("host").asText());
			}
			return raws;
		}
		
		/* @author hujin
		 * 
		 * 得到mq 的大版本
		 * */
		@RequestMapping("/getwasversion")
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
		/*
		 * 得到mq 的小版本
		 * */
			@RequestMapping("/getwasfixver")
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
				ObjectNode on = amsRestService.getVersion("WAS", "linux");
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
}
