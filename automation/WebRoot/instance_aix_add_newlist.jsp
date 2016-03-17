<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@include file="loginCheck.jsp" %>
<!DOCTYPE HTML>
<html>
<head>
<meta name="renderer" content="webkit|ie-comp|ie-stand">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<jsp:include page="header.jsp" flush="true" />
<title>自动化部署平台——主机列表</title>

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
			<a href="getAllServers" class="current"><i class="icon-home"></i>实例一览</a>
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
							<div class="widget-content">所有实例信息.</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span12">
					<div class="columnauto">
						<div class="widget-box nostyle ">
							<div class="col-sm-6 form-inline">

								<div class="dt-buttons btn-group">
									<button class="btn btn-sm" data-toggle="modal" style="background-color:rgb(68,143,200);"
										data-target="#add_single"><font color="white">手工添加</font></button>
									<span style="margin-right: 5px;">&nbsp;&nbsp;&nbsp;</span>
									<button class="btn btn-sm" data-toggle="modal" style="background-color:rgb(68,143,200);"
										data-target="#add_more"><font color="white">批量导入</font></button>
								</div>
								<h5 style="weight: 200px; float: right">共有 ${total }台主机</h5>

							</div>
							<div style="margin-bottom: 5px"></div>
				<!-- data-table -->				<table name="data-table"
							class="table table-bordered data-table  with-check table-hover no-search no-select">
								<thead>
									<tr>
										<th style="text-align: center;">序号</th>
										<th style="text-align: center;">主机名</th>
										<th style="text-align: center;">IP地址</th>
										<th style="text-align: center;">主机配置</th>
										<th style="text-align: center;">操作系统</th>
										<!-- <th style="text-align: center;">HyperVisor</th> -->
										<th style="text-align: center;">健康状态</th>
									<!-- 	<th style="text-align: center;">编辑</th> -->
									</tr>
								</thead>
								<%
									int i = 1;
								%>
								<tbody>


									<c:forEach items="${servers }" var="ser">
										<tr>
											<td style="text-align: center;"><%=i++%></td>
											<td style="text-align: center;">${ser.name }</td>
											<td style="text-align: center;">${ser.IP }</td>

											<td style="text-align: center;">${ser.HConf}</td>
											<td style="text-align: center;">${ser.OS}</td>
											<!-- <td style="text-align: center;">${ser.HVisor}</td> -->
											<td style="text-align: center;" name="state"><c:if
													test="${ser.status eq 'Active' }">
													&nbsp;&nbsp;<img src="img/icon_success.png"></img>&nbsp;
													<b>${ser.status}</b>
												</c:if> <c:if test="${ser.status eq 'Error' }">
													<img src="img/icon_error.png"></img>
													&nbsp;<b>${ser.status}</b>
												</c:if></td>
                                         <!--    <td style="text-align: center;"><img src="img/edit.png"></td> -->
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>

					<div class="columnfoot"></div>



					<div class="modal fade" id="add_single" tabindex="-1" role="dialog"
						aria-labelledby="myModalLabel" aria-hidden="true">
						<div class="modal-dialog">
							<div class="modal-content">
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h5 class="modal-title" id="myModalLabel">
										<img src="<%=path%>/img/plus15.png">&nbsp;&nbsp;添加信息
									</h5>
								</div>

								<form action="addHost" method="post" id="submits">

									<div class="modal-body">
										<div class="control-group">
											<div class="controls">
												<!-- <span class="input140 mr20">名称：</span> -->
												 <input class="form-control" type="hidden" id="Name" name="Name" value="DefaultName">
											</div>
										</div>

										<div class="control-group">
											<div class="controls">
												<span class="input140 mr20">IP：</span> 
												<input class="form-control" type="text" id="IP" name="IP">
												<div style="margin-top:3px;float:right;margin-right:20px;"><span hidden="" id="ipshow" name="ipshow">
												    <img alt="" src="img/icon_error.png">&nbsp;
												    <span style="font-size: 13px">此IP地址已注册</span>
												</span></div> 
												<div style="float:right;margin-top:4px;"><span hidden="" id="ipshow1" name="ipshow1">
												    <img align="top" alt="" src="img/icon_success.png">&nbsp;
												    <span style="font-size: 13px">此IP地址未注册</span>
												</span></div> 
												<input type="hidden" id="msgflag" name="msgflag" value="">
											</div>
										</div>

										<div class="control-group">
											<div class="controls">
												<span class="input140 mr20">用户名：</span> <input
													class="form-control" type="text" id="UserID" name="UserID">
													<span style="font-size: 13px;color:red;">*&nbsp;需管理员用户</span>
											</div>
										</div>

										<div class="control-group">
											<div class="controls">
												<span class="input140 mr20">密码：</span> <input
													class="form-control" type="password" id="Password"
													name="Password">
											</div>
										</div>
                                      <!--  
										<div class="control-group">
											<div class="controls">
												<span class="input140 mr20">操作系统：</span> 
												<select id="OS" class="w85" style="width: 220px;" name="OS">
													<option>AIX</option>
													<option>RedHat</option>
													<option>Suse</option>
													<option>CenterOS</option>
													<option>Other</option>
												</select>
											</div>
										</div>  -->
										
									</div>
                                    <!--  
									<div class="control-group">
										<div class="controls">
											<span class="input140 mr20" style="padding-left: 16px;">Hypervisor：</span>
											<select id="HVisor" class="w85" style="width: 220px;"
												name="HVisor">
												<option>PowerVM</option>
												<option>Vmware</option>
												<option>KVM</option>
												<option>None</option>
											</select>
										</div>
									</div>-->
									<input type="hidden" name="OS" id="OS" value="DefaultOS">
									<input type="hidden" name="HVisor" id="HVisor" value="DefaultHVisor">
									<input type="hidden" name="type" id="type" value="createServer">
								</form>
							</div>

							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal" onclick="closeModal();">关闭</button>
								<button type="button" class="btn" style="background-color:rgb(68,143,200);"
									onclick="CheckInput();"><font color="white">保存</font></button>

							</div>
						</div>
					</div>
				</div>

				<div class="modal fade" id="add_more" tabindex="-1" role="dialog"
					aria-labelledby="myModalLabel" aria-hidden="true"
					style="width: 400px; height: 183px;">
					<div class="modal-dialog">
						<div class="modal-content">
							<form action="importExcel" method="post" id="fileUpload"
								name="fileUpload" enctype="multipart/form-data">
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-hidden="true">&times;</button>
									<h5 class="modal-title" id="myModalLabel">
										<img src="<%=path%>/img/plus15.png">&nbsp;&nbsp;批量导入
									</h5>
								</div>
								<div class="modal-body" align="center">
									<div class="modal-body">
										<div>
											<input type="file" name="file" id="file"><span
												id="wait_tip" style="display: none;"><img
												src="img/spinner.gif" id="loading_img" />请等待...</span> <span
												id="wait_success" style="display: none;"><img
												src="img/check_a.png" id="loading_img1" /> 导入成功</span> <span
												id="wait_failure" style="display: none;"><img
												src="img/noadd.png" id="loading_img2" /> 导入失败</span>
										</div>

										<div style="margin-top:8px;">
											<a href="xls/machine.rar" target="_self">示例文件</a>
										</div>

									</div>
								</div>
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal" onclick="hidespinner();">关闭</button>
									<input type="submit" class="btn" value="保存" 
									       style="background-color:rgb(68,143,200);color:white;">
								</div>

							</form>
						</div>
					</div>
				</div>

			</div>




		</div>
	</div>
	</div>
	</div>
</body>
<script type="text/javascript">
	function checkFile() {
		//alert("aaaa");
		var excelFile = $("#file").val();
		if (excelFile == '') {
			getId("wait_tip").style.display = "none";
			alert("请选择需上传的文件!");

			return false;
		}
		if (excelFile.indexOf('.xls') == -1) {
			getId("wait_tip").style.display = "none";
			alert("文件格式不正确，请选择正确的Excel文件(后缀名.xls)！");
			return false;
		}
	}
	$("#IP")
			.blur(
					function() { //发送请求看是否存在IP

						var re = /^([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.([0-9]|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])$/;
						if (!re.test($("#IP").val().trim())) {
							ymPrompt.alert("IP地址格式不正确或数值超过了255，请修改!");
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

	/* 	if ($("#Name").val().trim() == "") {
			ymPrompt.alert("请输入名称!");
			return;
		} */
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
			ymPrompt.alert("IP已经存在，请更换IP!");
			return;
		}

		$.ajax({
			url : "/automation/addHost",
			data : $('#submits').serialize(),
			type : 'post',
			dataType : 'json',
			success : function(result) {
				//alert(result.msg);
				if (result.msg == 'failure') {
					ymPrompt.win({
						message : '是->重新添加，否->返回主机列表',
						title : '创建失败，继续添加？',
						handler : function(tp) {
							if (tp == "no")//返回主页
							{
								window.location.href = "getAllServers";
							} else {
								$("#Name").val("");
								$("#IP").val("");
								$("#ipshow").hide();
								$("#ipshow1").hide();
								$("#UserID").val("");
								$("#Password").val("");
							}
						},
						btn : [ [ '是', 'yes' ], [ '否', 'no' ] ]
					})
				}
				if (result.msg == 'success') {
					ymPrompt.win({
						message : '创建成功，是否继续添加？',
						//title : '创建成功，继续添加？',
						handler : function(tp) {
							if (tp == "no")//返回主页
							{
								window.location.href = "getAllServers";
							} else {
								$("#Name").val("");
								$("#IP").val("");
								$("#ipshow").hide();
								$("#ipshow1").hide();
								$("#UserID").val("");
								$("#Password").val("");
							}
						},
						btn : [ [ '是', 'yes' ], [ '否', 'no' ] ]
					})
				}
			}
		});
		//	$("#submits").submit();
	}

	function getId(id) {
		return document.getElementById(id);
	}
	function validation() {
		getId("submits").style.display = "none";
		getId("wait_tip").style.display = "";
		return true;
	}

	function closeModal() {
		//window.location.href="getAllServers";
		//alert("1111");
		window.location.href = "getAllServers";
		//	window.location.reload();
	}
	function hidespinner() {
		getId("wait_tip").style.display = "none";
		window.location.href = "getAllServers";

	}
</script>
<script type="text/javascript">
	(function() {

		//	var bar = $('.bar');
		//	var percent = $('.percent');
		//	var status = $('#status');
		//  alert("ssss");

		$('#fileUpload').ajaxForm({
			dataType : 'json',
			beforeSubmit : function() {
				//	alert("aaaa");
				var excelFile = $("#file").val();
				if (excelFile == '') {
					getId("wait_tip").style.display = "none";
					alert("请选择需上传的文件!");

					return false;
				}
				if (excelFile.indexOf('.xls') == -1) {
					getId("wait_tip").style.display = "none";
					alert("文件格式不正确，请选择正确的Excel文件(后缀名.xls)！");

					return false;
				}
			},
			beforeSend : function() {
				//   status.empty();
				//   var percentVal = '0%';
				//    bar.width(percentVal)
				//   percent.html(percentVal);

				getId("wait_success").style.display = "none";
				getId("wait_failure").style.display = "none";
				//getId("wait_tip").style.display = "none";
			},
			uploadProgress : function(event, position, total, percentComplete) {
				// var percentVal = percentComplete + '%';
				// bar.width(percentVal)
				// percent.html(percentVal);
			},

			success : function(result) {
				//     var percentVal = '100%';
				//     bar.width(percentVal)
				//     percent.html(percentVal);

				if (result.msg == 'success') {
					/*		ymPrompt.win({
									message : '导入成功!',
									title : '提示',
								
									btn : [ [ '是', 'yes' ] ]
								});
					 */
					getId("wait_success").style.display = "";
					getId("submits").style.display = "";
				}
				if (result.msg == 'failure') //{
					/*			ymPrompt.win({
									message : '导入失败！',
									title : '提示',
									btn : [ [ '是', 'yes' ] ]
								});
								getId("submits").style.display = "";
							}
					 */
					getId("wait_failure").style.display = "";
				getId("wait_tip").style.display = "none";
				return true;

			},
			complete : function(xhr) {
				//status.html(xhr.responseText);
				//	alert('111'+xhr.responseText);
			}
		});
 
	})();
</script>

</html>
