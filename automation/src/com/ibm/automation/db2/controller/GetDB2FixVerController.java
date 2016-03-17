package com.ibm.automation.db2.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.automation.ams.service.AmsRestService;
import com.ibm.automation.core.controller.BaseController;


@Controller
public class GetDB2FixVerController extends BaseController {
	@Autowired
	private AmsRestService amsRestService;

	@RequestMapping("/getdb2fixver")
	@ResponseBody
	public String getDb2Ver(HttpServletRequest request, HttpSession session) {
		String vsion = request.getParameter("version");
		if (vsion.equals("") || vsion == null)
			return "error";
		ObjectMapper om = new ObjectMapper();
		ObjectNode on = amsRestService.getVersion("DB2", "aix");
		if (on == null)

		{
			return "error";
		}
		JsonNode fixpack = on.get("fixpack");
		JsonNode package1 = on.get("package");

		JsonNode needVersion = fixpack.get(vsion);// 所需要的版本
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
