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
.col-md-1, .col-md-2, .col-md-3, .col-md-4, .col-md-5, .col-md-6,
	.col-md-7, .col-md-8, .col-md-9, .col-md-10, .col-md-11, .col-md-12 {
	float: left;
}

.col-sm-1, .col-sm-2, .col-sm-3, .col-sm-4, .col-sm-5, .col-sm-6,
	.col-sm-7, .col-sm-8, .col-sm-9, .col-sm-10, .col-sm-11, .col-sm-12 {
	float: left;
}

.col-md-3 {
	width: 25%;
	align: right;
}

.col-sm-12 {
	width: 100%
}

.col-sm-11 {
	width: 91.66666667%
}

.col-sm-10 {
	width: 83.33333333%
}

.col-sm-9 {
	width: 75%
}

.col-sm-8 {
	width: 45%
}

.col-sm-7 {
	width: 58.33333333%
}

.col-sm-6 {
	width: 50%
}

.col-sm-5 {
	width: 41.66666667%
}

.col-sm-4 {
	width: 33.33333333%
}

.col-sm-3 {
	width: 23%
}

.col-sm-2 {
	width: 16.66666667%
}

.col-sm-1 {
	width: 8.33333333%
}

.form-group {
	margin-right: -15px;
	margin-left: -15px
}

.container {
	padding-right: 15px;
	padding-left: 15px;
	margin-right: auto;
	margin-left: auto
}

@media ( min-width :768px) {
	.container {
		width: 750px
	}
}

@media ( min-width :992px) {
	.container {
		width: 970px
	}
}

@media ( min-width :1200px) {
	.container {
		width: 1170px
	}
}
label{font-size:13px;}
.input140{
	font-size: 13px;
}

</style>
<script type="text/javascript">

	//根据全局安全性判断是否可编辑管理员用户和管理员密码
	function sortRadio(val) //0代表否，1代表是
	{
		if (val == 0) {
			$("#was_userid").val("");
			$("#was_password").val("");

			$("#was_userid").attr("readonly", "disabled");
			$("#was_password").attr("readonly", "disabled");
			//	$("#was_password").Attr("disabled");
		} else {
			$("#was_userid").val("admin");
			$("#was_userid").removeAttr("readonly");
			$("#was_password").val("admin");
			$("#was_password").removeAttr("readonly");

		}
	}

	function CheckInput() {
		if($("#was_version").prev().find('a').find('span').text()=='请选择...')
		{
			ymPrompt.alert("请选择WAS安装版本!");
			return;
		}
	 
		var str = $(".all_profile_class").find("option:selected").text();
		
		var reg = /DMGR/g;
	    var arr  = str.match(reg);
	    if(arr === null  || typeof(arr) == "undefined")
	    {
	    	ymPrompt.alert("profile类型有且仅有一个DMGR+AppServer!");
			return;
	    }
	    if((arr.length < 1) || (arr.length >= 2))
	    {
	    	ymPrompt.alert("profile类型有且仅有一个DMGR+AppServer!");
			return;
	    }
		
		$("#submits").submit();

		//校验是否必选了dmgr

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
			<a href="getAllServers" title="IBM WAS" class="tip-bottom"><i class="icon-home"></i>IBM WAS</a> 
			<a class="current">实例配置详细</a>
		</div>

		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span12">
					<div class="widget-box collapsible">
						<div class="widget-title">
							<a data-toggle="collapse" href="#collapseOne"> 
							  <span class="icon"><i class="icon-arrow-right"></i></span>
							  <h5>说明：</h5>
							</a>
						</div>
						<div id="collapseOne" class="collapse in">
							<div class="widget-content">配置当前虚拟机的WAS Cluster参数信息</div>
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
							<h5 class="stairtit swapcon">拓扑结构</h5>
							<c:forEach items="${servers }" var="ser" varStatus="num">
								<p class="twotit" style="padding-left:0;">
									节点 <c:out value="${num.count }" />
								</p>
								<div class="column">
									<div class="span12">
										<p>
											<b>主机名:</b> <span class="column_txt"> ${ser.name } </span>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
											<b>IP地址:</b><span class="column_txt">${ser.IP}</span> 
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
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
						<form action="toWasClusterNextPage?status=confirm" method="post"
							id="submits" name="submits" class="form-horizontal">
							<div class="mainmodule">
								<h5 class="swapcon">
									<i class="icon-chevron-down"></i>基本信息
								</h5>
								<div class="form-horizontal">
									<input type="hidden" id="serId" name="serId" value="${serId}">
									<input type="hidden" id="ptype" name="ptype" value="${ptype}">
									<input type="hidden" id="hostId" name="hostId"	value="${hostId}">
									<div class="control-group">
										<label class="control-label">安装用户</label>
										<div class="controls">
											<div class="inputb2l">
												<input type="text" class="w45" id="was_user" name="was_user"
													value="root" />
											</div>
											<div class="inputb2l">
												<span class="input140 mr20">是否安装JDK7</span>
												<div class="inblock mr20">
													<label><input type="radio" id="was_jdk7"
														name="was_jdk7" value="yes" />是</label>
												</div>
												<div class="inblock mr20">
													<label><input type="radio" id="was_jdk7"
														name="was_jdk7" value="no" checked />否</label>
												</div>
												<div class="inblock mr20">
													<span class="input140 mr20" style="color: red">* 提示：默认为JDK6</span>
												</div>


											</div>
										</div>
									</div>
									<div class="control-group">
										<label class="control-label">WAS安装版本</label>
										<div class="controls">
											<div class="inputb2l">
												<select style="width: 47.5%; font-size: 13px;"
													id="was_version" class="w48" name="was_version"
													onchange="showfix(this);">
												<option value="-1" selected="selected">请选择...</option>
													<!-- <option value="8.5" selected="selected">8.5</option> -->
												</select>
											</div>
											<div class="inputb2l">
												<span class="input140 mr20">WAS安装补丁</span> <select
													style="width: 47.5%; font-size: 13px;" id="was_fp"
													class="w48" name="was_fp" onchange="getVer(this)">
													<option value="-1" selected="selected">请选择...</option>
												
												</select> <input type="hidden" id="wasfix" name="wasfix" value="-">
											</div>
										</div>
									</div>
									<div class="control-group">
										<label class="control-label">IM安装路径</label>
										<div class="controls">
											<div class="inputb2l">
												<input type="text" class="w45" id="was_im_path"
													name="was_im_path" readonly="readonly"
													value="/opt/IBM/InstallationManager" />
											</div>
											<div class="inputb2l">
												<span class="input140 mr20">WAS安装路径</span> <input
													type="text" class="w45" id="was_inst_path"
													name="was_inst_path" value="/opt/IBM/WebSphere/AppServer" />
											</div>

										</div>
									</div>




								</div>
							</div>
							<div class="mainmodule">
								<h5 class="swapcon">
									<i class="icon-chevron-down icon-chevron-right"></i>概要属性
								</h5>
								<div class="form-horizontal">
										<c:forEach items="${servers }" var="ser">
											<div class="control-group" style="padding-bottom: 15px;padding-top:10px;">
												<!-- 第一行 -->
												<div class="col-md-3" style="margin-left:135px;">
													<div class="form-group">
														<label class="col-sm-2" style="padding-top: 2.5px;">IP地址</label>
														<div class="col-sm-8">
															<input readonly="readonly" name="all_ips" type="text"
																class="form-control" value="${ser.IP }">
														</div>
													</div>
												</div>
												<div class="col-md-3" style="margin-left:-80px;">
													<div class="form-group">
														<label class="col-sm-2" style="padding-top: 2.5px;">主机名</label>
														<div class="col-sm-8">
															<input type="text" name="all_hostnames"
																class="form-control" value="${ser.name }">
														</div>
													</div>
												</div>
												<div class="col-md-3" style="margin-left:-80px;">
													<div class="form-group">
														<label class="col-sm-3" style="padding-top: 2.5px;">Profile类型</label>
														<div class="col-sm-8">
															<select style="width: 180px;" name="all_profile_types"
																class="all_profile_class">
																<option value="cell">DMGR+AppServer</option>
																<option value="default" selected="selected">AppServer</option>

															</select>
														</div>
													</div>
												</div>
												<div class="col-md-3" style="margin-left:-30px;">
													<div class="form-group">
														<label class="col-sm-3" style="padding-top: 2.5px;">Profile名称</label>
														<div class="col-sm-8">
															<input type="text" class="form-control"
																name="all_profile_names" value="AppSrv01">
														</div>
													</div>
												</div>
											</div>
										
										</c:forEach>

										<div  class="control-group">
											<label class="control-label">Profile路径</label>
											<div class="controls">
												<div style="margin-left: -10px;">
													<input type="text" class="w45" id="was_profile_path"
														   name="was_profile_path" style="width:265px;"
														   value="/opt/IBM/WebSphere/AppServer/profiles" />
												</div>

											</div>
										</div>
                                        
										<div  class="control-group">
											<!-- 第三行 -->
											<label class="control-label">是否开启安全性</label>
											<div class="controls">
												<div class="inblock mr20">
													<label><input type="radio" id="was_security"
														name="was_security" value="yes" onClick="sortRadio(1)"
														checked />是</label>
												</div>
												<div class="inblock mr20">
													<label><input type="radio" id="was_security"
														name="was_security" value="no" onClick="sortRadio(0)" />否</label>
												</div>
											</div>
										</div>

										<div  class="control-group">
											<label class="control-label">管理员用户</label>
											<div class="controls">
												<div class="inputb2l" style="margin-left:-10px;">
													<input type="text" class="w45" id="was_userid" style="width:265px;"
														name="was_userid" value="admin" />
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">管理员密码</span>
													&nbsp;&nbsp;
													<input type="text" class="w45" id="was_password"
														name="was_password" value="admin" />
												</div>
											</div>
										</div>
																		
								</div>
							</div>




							<div class="mainmodule">
								<h5 class="swapcon">
									<i class="icon-chevron-down icon-chevron-right"></i>系统属性
								</h5>
								<div class="form-horizontal" style="display: none;">
									<div class="control-group">
										<label class="control-label">nofile_soft</label>
										<div class="controls">
											<div class="inputb2l">
												<input type="text" class="w45" id="was_nofile_soft"
													name="was_nofile_soft" value="20480" />
											</div>
											<div class="inputb2l">
												<span class="input140 mr20">nofile_hard</span> <input
													type="text" class="w45" id="was_nofile_hard"
													name="was_nofile_hard" value="20480" />
											</div>
										</div>
									</div>
									<div class="control-group">
										<label class="control-label">fsize_soft</label>
										<div class="controls">
											<div class="inputb2l">
												<input type="text" class="w45" id="was_fsize_soft"
													name="was_fsize_soft" value="unlimited" />
											</div>
											<div class="inputb2l">
												<span class="input140 mr20">fsize_hard</span> <input
													type="text" class="w45" id="was_fsize_hard"
													name="was_fsize_hard" value="unlimited" />
											</div>
										</div>
									</div>
									<div class="control-group">
										<label class="control-label">core_soft</label>
										<div class="controls">
											<div class="inputb2l">
												<input type="text" class="w45" id="was_core_soft"
													name="was_core_soft" value="unlimited" />
											</div>
											<div class="inputb2l">
												<span class="input140 mr20">core_hard</span> <input
													type="text" class="w45" id="was_core_hard"
													name="was_core_hard" value="unlimited" />
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
		</a> <a class="btn btn-info fr btn-down" onclick="CheckInput();"> <span>下一页</span>
			<i class="icon-btn-next"></i>
		</a>
	</div>
	<!--footer end-->

	<script type="text/javascript">
		$(document)
				.ready(
						function() {
							$("#was_user")
									.blur(
											function() {

												if ($("#was_user").val() != 'root') {
													$("#was_im_path")
															.val(
																	'/home/'
																			+ $(
																					"#was_user")
																					.val()
																			+ '/IBM/InstallationManager');
												}
												if ($("#was_user").val() == 'root') {
													$("#was_im_path")
															.val(
																	'/opt/IBM/InstallationManager');
												}

											})
						});
	</script>
	<script type="text/javascript">
	//显示was版本补丁
	function showfix(obj) {

		var ver = obj.value;//获取版本
		$
				.ajax({
					url : "/automation/getwasfixver",
					data : {
						version : ver
					},
					type : 'post',
					dataType : 'json',
					success : function(result) {
						
						$("#was_fp").empty();
						var str="<option value='-1'>"+ "请选择..." + "</option>";;
						for (var i = 0; i < result.length; i++) {
							if (result[i].key == '-10')// -10表示只显示请选择...
							{
								$("#wasfix").val('请选择...');
								break;
							} if(result[i].key=='-12')//表示是版本包含补丁
							{
								$("#wasfix").val('-12');
								str += "<option value='" + result[i].value + "'>"
								+ result[i].key + "</option>";
							}else {
								
								str += "<option value='" + result[i].value + "'>"
										+ result[i].key + "</option>";
								
							}
							
						}
						$("#was_fp").append(str);
						
					}
				});
	}

	//自动加载
function getVer(obj) {

	if ($("#was_fp").find("option:selected").text() == '请选择...') {
		$("#wasfix").val('-');
	} else
		$("#wasfix").val($("#was_fp").find("option:selected").text());
}
window.onload = function() {
	$.ajax({
		url : '/automation/getwasversion',
		data : {
			product : "was",
			platform : 'linux'
		},
		type : 'post',
		dataType : 'json',
		success : function(result) {
			$("#wasfix").val('-');
			var str;
			for (var i = 0; i < result.length; i++) {
				if (result[i].key == '.')// .号表示没有安装文件
				{
					break;
				} else {
					str += "<option value='" + result[i].key + "'>"
							+ result[i].value + "</option>";
				}
			}
			$("#was_version").append(str);
		}
	})
}
	</script>
</body>
</html>
