<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@include file="loginCheck.jsp" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.fasterxml.jackson.databind.*"%>
<%@ page import="com.fasterxml.jackson.databind.node.*"%>
<!DOCTYPE HTML>
<html>
<head>
<meta name="renderer" content="webkit|ie-comp|ie-stand">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<jsp:include page="header.jsp" flush="true" />
<title>自动化部署平台</title>
<style type="text/css">
.input140{font-size:13px;}
</style>
<script language="javascript" type="text/javascript">
	//操作
	function CheckInput() {
		ymPrompt.win({
			message : '&nbsp;提交任务后是否在目标主机立即运行脚本？',  
			title : '创建提示！',
			handler : function(tp) {
				if (tp == "no") {
					window.history.go(0);
					//alert("已经创建脚本！");
					var type = document.getElementById("type");
					type.setAttribute("value", "no");
				}
				else{
					$("#submits").submit();
				}				
			},
			btn : [ [ '是', 'yes' ] , [ '否', 'no' ] ],
			icoCls : 'ymPrompt_alert'
		});
	}
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
			<a href="getAllServers" title="IBM WAS" class="tip-bottom"><i
				class="icon-home"></i>IBM WAS</a> <a class="current">实例配置详细</a>
		</div>

		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span12">
					<div class="widget-box collapsible">
						<div class="widget-title">
							<a data-toggle="collapse" href="#collapseOne"> <span
								class="icon"> <i class="icon-arrow-right"></i></span>
								<h5>说明：</h5>
							</a>
						</div>
						<div id="collapseOne" class="collapse in">
							<div class="widget-content">确认当前虚拟机的WAS参数信息</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span12">
					<div class="columnauto">
					<div class="mainmodule">
							<h5 class="stairtit swapcon">
								<!-- <i class="icon-chevron-down"></i> -->
								拓扑结构
							</h5>
							<p class="twotit" style="padding-left:0;">
								<em class="majornode">单</em>节点1
							</p>
							<c:forEach items="${servers }" var="ser" varStatus="num"
								begin="0" end="0">
								<div class="column">
									<div class="span12">
										<p>
											<b>主机名:</b> <span class="column_txt"> ${ser.name } </span> 
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<b>IP地址:</b><span class="column_txt">${ser.IP}</span>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
											<b>操作系统:</b><span class="column_txt"><em>${ser.OS}</em></span>
										</p>
										<p>
											<b>系统配置:</b><span class="column_txt">${ser.HConf }</span> 
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<!-- <b>Hypervisor:</b><span class="column_txt">${ser.HVisor }</span> --> 
											<b>状态:</b><span class="column_txt"><em>${ser.status }</em></span>
										</p>
									</div>
								</div>
							</c:forEach>
						</div>
						<form action="installWasStandAloneInfo" method="post" id="submits" class="form-horizontal">
							<div class="form-horizontal">
							    <input type="hidden" id="ptype" name="ptype" value="${ptype}">
							    <input type="hidden" id="hostId" name="hostId" value="${hostId}">
							    <input type="hidden" id="serId" name="serId" value="${serId}">
							    <input type="hidden" id="wasfix" name="wasfix" value="${wasfix}">
							    
							    <input type="hidden" id="was_user" name="was_user" value="${was_user}">
							    <input type="hidden" id="was_jdk7" name="was_jdk7" value="${was_jdk7}"> 
							    <input type="hidden" id="was_version" name="was_version" value="${was_version}">
							    <input type="hidden" id="was_fp" name="was_fp" value="${was_fp}">
							    <input type="hidden" id="was_im_path" name="was_im_path" value="${was_im_path}">
							    <input type="hidden" id="was_inst_path" name="was_inst_path" value="${was_inst_path}"> 							    
								<input type="hidden" id="was_hostname" name="was_hostname" value="${was_hostname}"> 
																										 								
								<input type="hidden" id="was_ip" name="was_ip" value="${was_ip}">								
								<input type="hidden" id="was_profile_type" name="was_profile_type" value="${was_profile_type}">
								<input type="hidden" id="was_profile_name" name="was_profile_name" value="${was_profile_name}">  								
								<input type="hidden" id="was_profile_path" name="was_profile_path" value="${was_profile_path}"> 								
								<input type="hidden" id="was_security" name="was_security" value="${was_security}">								
								<input type="hidden" id="was_userid" name="was_userid" value="${was_userid}"> 
								<input type="hidden" id="was_password" name="was_password" value="${was_password}">
								
								<input type="hidden" id="was_nofile_soft" name="was_nofile_soft" value="${was_nofile_soft}"> 
								<input type="hidden" id="was_nofile_hard" name="was_nofile_hard" value="${was_nofile_hard}"> 
								<input type="hidden" id="was_fsize_soft" name="was_fsize_soft" value="${was_fsize_soft}"> 
								<input type="hidden" id="was_fsize_hard" name="was_fsize_hard" value="${was_fsize_hard}"> 
								<input type="hidden" id="was_core_soft" name="was_core_soft" value="${was_core_soft}"> 
								<input type="hidden" id="was_core_hard" name="was_core_hard" value="${was_core_hard}"> 
																
								<div class="mainmodule">
									<h5 class="swapcon">
										<i class="icon-chevron-down icon-chevron-right"></i>WAS 基本信息
									</h5>
									<div class="form-horizontal">
										<div class="control-group">
											<label class="control-label">主机名</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${was_hostname }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">安装用户</span> <span
														class="graytxt">${was_user }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">WAS安装版本</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${was_version }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">WAS安装补丁</span> <span
														class="graytxt">${wasfix }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">IM安装路径</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${was_im_path }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">WAS安装路径</span> <span
														class="graytxt">${was_inst_path }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">JDK7</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${was_jdk7 }</span>
												</div>
											</div>
										</div>
									</div>
								</div>

								<div class="mainmodule">
									<h5 class="swapcon">
										<i class="icon-chevron-down icon-chevron-right"></i>WAS 概要属性
									</h5>
									<div class="form-horizontal">
										<div class="control-group">
											<label class="control-label">IP地址</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${was_ip }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">Profile类型</span> <span
														class="graytxt">${was_profile_type }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">Profile名称</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${was_profile_name }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">Profile路径</span> <span
														class="graytxt">${was_profile_path }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">全局安全性</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${was_security }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">管理员用户</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${was_userid }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">管理员密码</span> <span
														class="graytxt">${was_password }</span>
												</div>
											</div>
										</div>
									</div>
								</div>

								<div class="mainmodule">
									<h5 class="swapcon">
										<i class="icon-chevron-down icon-chevron-right"></i>WAS 系统属性
									</h5>
									<div class="form-horizontal" style="display: none;">
										<div class="control-group">
											<label class="control-label">nofile_soft</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${was_nofile_soft }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">nofile_hard</span> <span
														class="graytxt">${was_nofile_hard }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">fsize_soft</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${was_fsize_soft }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">fsize_hard</span> <span
														class="graytxt">${was_fsize_hard }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">core_soft</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${was_core_soft }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">core_hard</span> <span
														class="graytxt">${was_core_hard }</span>
												</div>
											</div>
										</div>
									</div>
								</div>

							</div>

						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!--content end-->
	<!--footer start-->
	<div class="columnfoot">
		<a class="btn btn-info btn-up" onclick="javascript:history.go(-1);">
			<i class="icon-btn-up"></i> <span>上一页</span>
		</a> <a class="btn btn-info fr btn-down" onclick="CheckInput();"> <span>创建</span>
			<i class="icon-btn-next"></i>
		</a>
	</div>
</body>
</html>
