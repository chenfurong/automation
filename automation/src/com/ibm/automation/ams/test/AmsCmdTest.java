package com.ibm.automation.ams.test;

import java.util.List;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class AmsCmdTest {
	
	ObjectMapper om = new ObjectMapper();
	String endpoint = "http://10.28.0.231:3000/api/v2/cmd";
	RestTemplate tpl = new RestTemplate();
	
	
	public JsonNode buildShellCmd(String exec, String host, String addr, String user, String pass) {
		ObjectNode cmd = om.createObjectNode();
		cmd.put("name", Joiner.on("-").join(new String[]{ host, exec }));
		cmd.put("type", "shell");
		cmd.put("exec", exec);
		cmd.putObject("node").put("host", host).put("addr", addr);
		cmd.putObject("cred").put("user", user).put("pass", pass);
		return cmd;
	}
	

	public JsonNode buildTouchCmd(String exec, String text, String host, String addr, String user, String pass) {
		ObjectNode cmd = om.createObjectNode();
		cmd.put("name", Joiner.on("-").join(new String[]{ host, exec }));
		cmd.put("type", "touch");
		cmd.put("exec", exec);
		cmd.put("text", text);
		cmd.putObject("node").put("host", host).put("addr", addr);
		cmd.putObject("cred").put("user", user).put("pass", pass);
		return cmd;
	}
	
	public String getText() {
		String[] lines = new String[] {
			"CLUSTER;;cl_test1_lr001",
			"NODE;;AIX71_HA2,AIX71_HA1",
			"ENET;;net_ether_01;;255.255.255.0;;yes",
			"REPDEV;;hdisk2",
			"HAMODE;;AA",
			"RGNUM;;1",
			"APSERV1;;ap01;;/script/start.sh;;/script/stop.sh",
			"SERVIP1;;aix7svc;;net_ether_01;;20.211.30.245",
			"PERIP1;;perip1;;net_ether_01;;255.255.255.0",
			"RGROUP1;;rg_hln01;;AIX71_HA2,AIX71_HA1;;normal;;aix7svc;;ap01;;datavg;;fsck;;sequential;;true"
		};
		return Joiner.on("\n").join(lines);
	}

	@Test
	public void test() {
		this.testLspv();
	}
	

	public void testLspv() {
		String text = getText();
		System.out.println(text);
		JsonNode cmd = buildTouchCmd("/tmp/lrtest.txt", text, "AIX71_HA1", "10.58.0.101", "root", "passw0rd");
		// JsonNode cmd = buildShellCmd("lspv", "AIX71_HA1", "10.58.0.101", "root", "passw0rd");
		System.out.println(cmd);
		HttpEntity he =new HttpEntity(cmd, new HttpHeaders());
		ResponseEntity<JsonNode> resp = tpl.postForEntity(endpoint, he, JsonNode.class);
		System.out.println(resp);
		if(resp.getStatusCode().is2xxSuccessful()) {
			JsonNode result = resp.getBody();
			System.out.println(result);
			String msg = result.get("msg").textValue();
			List<List<String>> rows = Lists.newLinkedList();
			String[] lines = msg.split("\\\n");
			for(String line: lines) {
				String[] cells = line.split("\\s+");
				List<String> cols = Lists.newLinkedList();
				for(String cell: cells) {
				//	System.out.println("cell: " + cell.trim());
					cols.add(cell.trim());
				}
				rows.add(cols);
			}
			System.out.println("rows: " + rows);
		}
	}
	

}
