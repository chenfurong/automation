package com.ibm.automation.ams.constants;

import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.automation.core.util.PropertyUtil;



public class AMS2KeyUtil {
    static final Properties p = PropertyUtil.getResourceFile("config/properties/ams2.properties");
    static ObjectMapper om = new ObjectMapper();
    
    public static void main(String[] args) {
    	ArrayNode arrayNode = getArrayNodeByKey("db2_job_name_sub");
    	System.out.println(arrayNode);
    	for (JsonNode jsonNode : arrayNode) {
    		ObjectNode job = (ObjectNode) jsonNode;
			String name = job.get("name").asText();
			System.out.println(name);			
		}
	}
    
	public static ArrayNode getArrayNodeByKey(String key){
		String prepare_step_name = p.getProperty(key);
		String[] strings = prepare_step_name.split(",");
				
		ArrayNode steps = om.createArrayNode();
		for (String string : strings) {
			ObjectNode step = om.createObjectNode();
			step.put("name", string);
			steps.add(step);
		}
		return steps;
	}
	
	public static ObjectNode getCredDefaultUserNode(){
		String username = p.getProperty("cred_default_username");
		String password = p.getProperty("cred_default_password");
		
		ObjectNode udefault = om.createObjectNode();
		udefault.put("user", username);
		udefault.put("pass", password);
		
		ObjectNode creds = om.createObjectNode();
		creds.set("default", udefault);
		
		return creds;
	}
	
	public static String getFTPNode(){
		String addr = p.getProperty("ftp_addr");
		String username = p.getProperty("ftp_username");
		String password = p.getProperty("ftp_password");
		
		String ftp = addr+" "+username+" "+password;		
		return ftp;
	}
	
	/**
	 * 基本信息默认值
	 */
	public static ObjectNode getBasicInfo(){
		
		ObjectNode basicInfo = om.createObjectNode();
		//db2base=/opt/ibm/db2/$version
		basicInfo.put("db2_db2base", p.getProperty("db2_db2base"));
		//dbpath=/db2home
		basicInfo.put("db2_dbpath", p.getProperty("db2_dbpath"));
		//db2insusr=db2inst1
		basicInfo.put("db2_db2insusr", p.getProperty("db2_db2insusr"));
		//svcename=60000
		basicInfo.put("db2_svcename", p.getProperty("db2_svcename"));
		//dbname=sample
		basicInfo.put("db2_dbname", p.getProperty("db2_dbname"));
		// datapath=/db2space1, /db2space2, /db2space3, /db2space4
		basicInfo.put("db2_dbdatapath", p.getProperty("db2_dbdatapath"));
		
		return basicInfo;
	}
	
	/**
	 * 实例高级属性默认值
	 */
	public static ObjectNode getInstAdvProp() {
		
		ObjectNode instProp = om.createObjectNode();

		// db2insusr=db2inst1
		instProp.put("db2_db2insusr", p.getProperty("db2_db2insusr"));
		// db2insgrp=db2igrp
		instProp.put("db2_db2insgrp", p.getProperty("db2_db2insgrp"));
		// db2fenusr=db2fenc
		instProp.put("db2_db2fenusr", p.getProperty("db2_db2fenusr"));
		// db2fengrp=db2fgrp
		instProp.put("db2_db2fengrp", p.getProperty("db2_db2fengrp"));
		// db2comm=tcpip
		instProp.put("db2_db2comm", p.getProperty("db2_db2comm"));
		// db2codepage=1386
		instProp.put("db2_db2codepage", p.getProperty("db2_db2codepage"));
		// initagents=0
		instProp.put("db2_initagents", p.getProperty("db2_initagents"));
		// poolagents=Automatic
		instProp.put("db2_poolagents", p.getProperty("db2_poolagents"));
		// db2_max_coordagents=Automatic
		instProp.put("db2_max_coordagents", p.getProperty("db2_max_coordagents"));
		// db2_max_connectings=Automatic
		instProp.put("db2_max_connectings", p.getProperty("db2_max_connectings"));
		// diagsize=50
		instProp.put("db2_diagsize", p.getProperty("db2_diagsize"));
		// mon_heap=Automatic(500)
		instProp.put("db2_mon_heap", p.getProperty("db2_mon_heap"));

		return instProp;
	}
	
	/**
	 * 数据库高级属性默认值
	 */
	public static ObjectNode getDB2AdvProp(){
		
		ObjectNode dbProp = om.createObjectNode();
		
		// db2log=/db2log
		dbProp.put("db2_db2log", p.getProperty("db2_db2log"));
		// logarchpath=/db2archlog
		dbProp.put("db2_logarchpath", p.getProperty("db2_logarchpath"));
		// backuppath=/db2backup
		dbProp.put("db2_backuppath", p.getProperty("db2_backuppath"));
		// locklist=Automatic
		dbProp.put("db2_locklist", p.getProperty("db2_locklist"));
		// maxlocks=Automatic
		dbProp.put("db2_maxlocks", p.getProperty("db2_maxlocks"));
		// maxlocks=Automatic
		dbProp.put("db2_maxlocks", p.getProperty("db2_maxlocks"));
		// locktimeout=60
		dbProp.put("db2_locktimeout", p.getProperty("db2_locktimeout"));
		// sortheap=Automatic
		dbProp.put("db2_sortheap", p.getProperty("db2_sortheap"));
		// logprimary=30
		dbProp.put("db2_logprimary", p.getProperty("db2_logprimary"));
		// logsecond=20
		dbProp.put("db2_logsecond", p.getProperty("db2_logsecond"));
		// logbuff=200
		dbProp.put("db2_logbuff", p.getProperty("db2_logbuff"));
		// softmax=100
		dbProp.put("db2_softmax", p.getProperty("db2_softmax"));
		
		return dbProp;
	}
	
	
	
	public static ObjectNode getDb2ConfigInfo(){
		
		ObjectNode db2config = om.createObjectNode();

		db2config.put("db2_path", p.getProperty("db2_path"));
		db2config.put("db2_data_path", p.getProperty("db2_data_path"));
		db2config.put("db2_user_path", p.getProperty("db2_user_path"));
		db2config.put("db2_dataname", p.getProperty("db2_dataname"));
		db2config.put("db2_install_username", p.getProperty("db2_install_username"));
		db2config.put("db2_dataname", p.getProperty("db2_dataname"));
		db2config.put("db2_install_username", p.getProperty("db2_install_username"));
		db2config.put("db2_dasgrp", p.getProperty("db2_dasgrp"));
		db2config.put("db2_port", p.getProperty("db2_port"));
		
		return db2config;
	}
	

}
