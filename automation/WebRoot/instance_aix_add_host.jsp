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
<title>自动化部署平台——手动添加</title>
<style type="text/css">
#left {
	margin-left: 475px;
}

.move {
	position: relative;
	top: -10px;
}
</style>


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
			<a href="#" title="既有虚机+DB2" class="tip-bottom"><i
				class="icon-home"></i>既有虚机+DB2</a> <a class="current">手工添加</a>
		</div>

		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span12">
					<div class="widget-box collapsible">
						<div class="widget-title">
							<a data-toggle="collapse" href="#collapseOne"> <span
								class="icon"> <i class="icon-arrow-right"></i>
							</span>
								<h5>说明：</h5>
							</a>

						</div>
						<div id="collapseOne" class="collapse in">
							<div class="widget-content">手动添加部署的目标系统.</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!--  		<div>
			<form action="importExcel" method="post" id="fileUpload" name="fileUpload"
				enctype="multipart/form-data"> 
				<input type="file" name="file" id="file"> 
				<input type="button" value="提交" onclick="submitExcel()"></input>
			</form>
		</div>
		-->




		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span12">
					<div class="columnauto">
						<div class="mainmodule">
							<h5 class="stairtit">添加信息</h5>
							
							<button class="btn btn-primary btn-lg" data-toggle="modal"
								data-target="#add_single" style="background: rgb(248, 251, 253)">
								<h5 class="stairtit swapcon">手工添加</h5>
							</button>
							<button class="btn btn-primary btn-lg" data-toggle="modal"
								data-target="#add_more" style="background: rgb(248, 251, 253)">
								<h5 class="stairtit swapcon">批量添加</h5>
							</button>
							<form action="addHost" method="post" id="submits">
								<div class="mainmodule" id="div1">
									<div class="control-group" style="height: 30px">
										<div class="controls" align="center">
											<div class="inputb2l">
												<span class="input140 mr20">名称：</span> <input
													class="form-control" type="text" id="Name" name="Name">
												&nbsp;&nbsp;&nbsp;<span style="color: #727272">*注：用于命名要添加的系统</span>
											</div>
										</div>
									</div>
									<hr>

									<div class="control-group" style="height: 12px">
										<div class="controls">
											<div class="inputb2l move" id="left">
												<span class="input140 mr20">IP：</span> <input
													class="form-control" type="text" id="IP" name="IP">
												<span hidden="" id="ipshow" name="ipshow">IP已经存在，请更换IP</span>
												<span hidden="" id="ipshow1" name="ipshow1">恭喜，IP可用</span> <input
													type="hidden" id="msgflag" name="msgflag" value="">
											</div>
										</div>
									</div>
									<hr>

									<div class="control-group" style="height: 12px">
										<div class="controls">
											<div class="inputb2l move" id="left">
												<span class="input140 mr20">用户名：</span> <input
													class="form-control" type="text" id="UserID" name="UserID">
											</div>
										</div>
									</div>
									<hr>

									<div class="control-group" style="height: 12px">
										<div class="controls">
											<div class="inputb2l move" id="left">
												<span class="input140 mr20">密码：</span> <input
													class="form-control" type="password" id="Password"
													name="Password">
											</div>
										</div>
									</div>
									<hr>

									<div class="control-group" style="height: 12px">
										<div class="controls">
											<div class="inputb2l move" id="left">
												<span class="input140 mr20">操作系统：</span> <select id="OS"
													class="w85" style="width: 32%" name="OS">

													<option>AIX</option>
													<option>Solaris</option>
													<option>Linux</option>
													<option>Windows</option>
												</select>
											</div>
										</div>
									</div>
									<hr>
									<div class="control-group" style="height: 12px">
										<div class="controls">
											<div class="inputb2l move" id="left">
												<span class="input140 mr20">HVisor：</span> <select
													id="HVisor" class="w85" style="width: 32%" name="HVisor">

													<option>PowerVC</option>
													<option>PowerVM</option>
													<option>Vmware</option>
													<option>KVM</option>
												</select>
											</div>
										</div>
									</div>
									<hr>
									<div>
										<input type="hidden" name="type" id="type" value="create">
									</div>
									<div class="control-group" style="height: 12px">
										<div class="controls" align="center">
											<div class="inputb2l move">
												<button class="btn btn-primary" type="button"
													onclick="CheckInput();">添加</button>
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

	</div>
	<script type="text/javascript">
		$("#IP")
				.blur(
						function() { //发送请求看是否存在IP

							var re = /^([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])$/;
							if (!re.test($("#IP").val().trim())) {
								ymPrompt.alert("ip地址格式不正确，请修改");
								$("#ipshow").hide();
								$("#ipshow1").hide();
								return;
							}

							var txt = $("#IP").val();
							$.ajax({
								url : "/automation/IPCheck",
								data : {
									IP : txt
								},
								type : 'post',
								dataType : 'json',
								success : function(result) {
									if (result.msg == 1) {
										$("#msgflag").val(1);
										$("#ipshow1").hide();
										$("#ipshow").show()
									}
									if (result.msg == 0) {
										$("#msgflag").val(0);
										$("#ipshow").hide();
										$("#ipshow1").show()
									}
								}
							});
						});
		function CheckInput() {

			if ($("#Name").val().trim() == "") {
				ymPrompt.alert("请输入名称!");
				return;
			}
			if ($("#IP").val().trim() == "") {
				ymPrompt.alert("请输入IP!");
				return;
			}
			if ($("#UserID").val().trim() == "") {
				ymPrompt.alert("请输入用户名!");
				return;
			}
			if ($("#Password").val().trim() == "") {
				ymPrompt.alert("请输入密码!");
				return;
			}

			if ($("#msgflag").val() == 1) {
				ymPrompt.alert("IP已经存在，无法提交，请更换IP!");
				return;
			}

			$
					.ajax({
						url : "/automation/addHost",
						data : $('#submits').serialize(),
						type : 'post',
						dataType : 'json',
						success : function(result) {
							//alert(result.msg);
							if (result.msg == 'failure') {
								ymPrompt
										.win({
											message : '创建主机失败，继续添加主机？',
											title : 'yes->重新添加,no->返回主机列表',
											handler : function(tp) {
												if (tp == "no")//返回主页
												{
													window.location.href = "instance_aix_add_newlist.jsp";
												} else {
													window.location.href = "instance_aix_add_host.jsp";
												}
											},
											btn : [ [ '是', 'yes' ],
													[ '否', 'no' ] ]
										})
							}
							if (result.msg == 'success') {
								ymPrompt
										.win({
											message : '创建主机成功，继续添加主机？',
											title : 'yes->继续添加,no->返回主机列表',
											handler : function(tp) {
												if (tp == "no")//返回主页
												{
													window.location.href = "getAllServers";
												} else {
													window.location.href = "instance_aix_add_host.jsp";
												}
											},
											btn : [ [ '是', 'yes' ],
													[ '否', 'no' ] ]
										})
							}
						}

					});
			//	$("#submits").submit();
		}

		function submitExcel() {
			var excelFile = $("#file").val();
			if (excelFile == '') {
				alert("请选择需上传的文件!");
				return false;
			}
			//     if(excelFile.indexOf('.xls')==-1){alert("文件格式不正确，请选择正确的Excel文件(后缀名.xls)！");return false;}
			$("#fileUpload").submit();
		}
	</script>
</body>
</html>