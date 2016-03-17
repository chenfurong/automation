<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@include file="loginCheck.jsp" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.fasterxml.jackson.databind.*"%>
<%@ page import="com.fasterxml.jackson.databind.node.*"%>
<%@ page import="com.ibm.automation.core.util.*"%>
<!DOCTYPE HTML>
<html>
<head>
<meta name="renderer" content="webkit|ie-comp|ie-stand">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<jsp:include page="header.jsp" flush="true" />
<title>自动化部署平台</title>
<script type="text/javascript">
  //var item = $("tbody tr:eq(0)").find("td:eq(1)").val().trim();
  //alert(item);
 // reverse(item);
</script>
</head>

<body>
	<!--header start-->
	<div class="header">
		<jsp:include page="topinfo.jsp" flush="true" />
	</div>
	<!--header end-->

	<!--content start-->
	<div class="content">
		<div class="breadcrumb">
			<a href="getLogInfo" title="历史执行日志" class="tip-bottom"><i class="icon-home"></i>历史执行记录</a> 
			<a href="#" class="current">任务列表</a>
		</div>
		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span12">
					<div class="widget-box collapsible">
						<div class="widget-title">
							<a data-toggle="collapse" href="#collapseOne"> 
							   <span class="icon"> <i class="icon-arrow-right"></i></span>
							   <h5>说明：</h5>
							</a>
						</div>
						<div id="collapseOne" class="collapse in">
							<div class="widget-content">历史任务信息列表</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span12">
					<div class="columnauto">
						<div class="widget-box nostyle">
							<table class="table table-bordered data-table with-check table-hover no-search no-select">
								<thead>
									<tr>
										<th style="text-align: center;">序号</th>
										<th style="text-align: center;">主节点</th>
										<th style="text-align: center;">副节点</th>
										<th style="text-align: center;">任务</th>
										<th style="text-align: center;">时间</th>
										<th style="text-align: center;">结果</th>
										<th style="text-align: center;">操作</th>
									</tr>
								</thead>
								<tbody>
									<%
										String items = String.valueOf(request.getAttribute("items"));
										ObjectMapper om = new ObjectMapper();
										ArrayNode list = (ArrayNode) om.readTree(items);

										int i = 1;
										for (JsonNode jn : list) {
											ObjectNode node = (ObjectNode) jn;
											String uuid = node.get("uuid").asText();
											String mainNode = node.get("1").asText();
											String subNode = "";
											
											try {
												subNode = node.get("2").asText();
											} catch (Exception e) {
												subNode = "无";
											}

											String type = node.get("type").asText();
											String nodesnum = node.get("nodesnum").asText();
											String mainaddr = node.get("mainaddr").asText();
											String created_at = node.get("created_at").asText();
											String createdate = FormatUtil.dateTime(created_at);

											String status = node.get("status").asText().trim();
									%>
									<tr>
										<td style="text-align: center;"><%=i++%></td>
										<td style="text-align: center;"><%=mainNode%></td>
										<td style="text-align: center;"><%=subNode%></td>
										<td style="text-align: center;"><%=type%></td>
										<td style="text-align: center;"><%=createdate%></td>

										<%
											if ("0".equals(status)) {
										%>
										<td style="text-align: center;">&nbsp;&nbsp;&nbsp;&nbsp;
										  <img src="img/icons/common/icon_paused.png">
										  <b>未执行</b>
										</td>
										<%
											} else if ("1".equals(status)) {
										%>
										<td style="text-align: center;">&nbsp;&nbsp;&nbsp;&nbsp;
										  <img src="img/icons/common/icon_resized.png">
										  <b>执行中</b>
										</td>
										<%
											} else if ("2".equals(status)) {
										%>
										<td style="text-align: center;">
										  <img src="img/icon_success.png">
										  <b>成功</b>
										</td>
										<%
											} else {
										%>
										<td style="text-align: center;">
										  <img src="img/icon_error.png">
										  <b>失败</b>
										</td>
										<%
											}
										%>
										<td style="text-align: center;"><a
											href="getLogInfoDetial?uuid=<%=uuid%>&mainaddr=<%=mainaddr%>&type=<%=type%>">详细</a>
										</td>

									</tr>

									<%
										}
									%>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
