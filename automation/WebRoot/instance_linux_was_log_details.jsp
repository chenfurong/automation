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
<title>云计算基础架构平台</title>
<style type="text/css">
  span{font-size:13px;}
</style>
<style>
.bcg {
	background: #00ec00;
}

.bcr {
	background: #ff8080;
}

.bcb {
	background: #8080ff;
}

.bcy {
	background: yellow;
}

.input200 {
	display: inline-block;
	width: 200px;
	margin-top: 2px;
	text-align: right;
}
#logTable3 tr{height:25px;}
body{font-size:13px;}
.input140{font-size:13px;}
</style>
<script  type="text/javascript">
	$(document).ready(function() {
						//给td添加颜色
						trObject = $("#logTable3").find("tr");
						for (var i = 0; i < trObject.length; i++) {
							var tdText = $(trObject[i]).find("td:last");
							if (tdText.html().trim() == "成功") {
								tdText.addClass("bcg");
								$("#progress_my")
								.attr("class","progress progress-s progress-striped progress-success ine-block w100");								
							}
							if (tdText.html().trim() == "失败") {
								tdText.addClass("bcr");
								$("#progress_my")
								.attr("class","progress progress-s progress-striped progress-danger ine-block w100");
							}
							if (tdText.html().trim() == "执行中") {
								tdText.addClass("bcb");
								$("#progress_my")
								.attr("class","progress progress-s progress-striped progress-primary ine-block w100 active");

				//				tdText.append("<img src='img/loading14.gif' />");

							}
							if (tdText.html().trim() == "手工运行") {
								tdText.addClass("bcy");
							}
						}  
		//giveTdColor();
					});
</script>
<script type="text/javascript">
function giveTdColor()
{
	//给td添加颜色
	trObject = $("#logTable3").find("tr");
	for (var i = 0; i < trObject.length; i++) {
		var tdText = $(trObject[i]).find("td:last");
		if (tdText.html().trim() == "成功") {
			tdText.removeClass();
			tdText.addClass("bcg");
			$("#progress_my")
			.attr("class","progress progress-s progress-striped progress-success ine-block w100");			
		}
		if (tdText.html().trim() == "失败") {
			tdText.removeClass();
			tdText.addClass("bcr");
			$("#progress_my")
			.attr("class","progress progress-s progress-striped progress-danger ine-block w100");
		}
		if (tdText.html().trim() == "执行中") {
			tdText.removeClass();
			tdText.addClass("bcb");
			$("#progress_my")
			.attr("class","progress progress-s progress-striped progress-primary ine-block w100 active");
	//		tdText.append("<img src='img/loading14.gif' />");
		}
		if (tdText.html().trim() == "手工运行") {
			tdText.removeClass();
			tdText.addClass("bcy");
		}
	}
}


function getInstallMsg() 
{
	var uuid = $("#uuid").val().trim();
	$.ajax({
		url : '/automation/nodeInstall',
		type : 'post',
		data : {
			uuid:uuid
		},
		dataType : "json",
		error : function() {
			//alert("获取安装信息异常！");
		},
		success : function(data) {
			$("#progress_my").prev().html("主机安装进度 :&nbsp;&nbsp;&nbsp;"+data.progress);//安装进度
			$(".bar").attr('style','width: '+data.percent+';') //percent
			$("#percent").val(data.percent);
			$("#status").val(data.status);
			
			$("#make_directory_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在目标系统"+data.make_directory_addr_0+"上生成脚本存放目录");
			$("#make_directory_0").text(data.make_directory_0);
			
			$("#propagate_scripts_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向目标系统"+data.propagate_scripts_addr_0+"推送脚本文件hostname.setup.sh");
			$("#propagate_scripts_0").text(data.propagate_scripts_0);
			
			$("#propagate_scripts_addr_1").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向目标系统"+data.propagate_scripts_addr_1+"推送脚本文件prepare.was.sh");
			$("#propagate_scripts_1").text(data.propagate_scripts_1);
			
			$("#propagate_scripts_addr_2").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向目标系统"+data.propagate_scripts_addr_2+"推送脚本文件install.was.sh");
			$("#propagate_scripts_2").text(data.propagate_scripts_2);
			
			$("#propagate_scripts_addr_3").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向目标系统"+data.propagate_scripts_addr_3+"推送脚本文件build.was.sh");
			$("#propagate_scripts_3").text(data.propagate_scripts_3);
			
			$("#manipulate_was_config_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;目标系统"+data.manipulate_was_config_addr_0+"生成参数文件prepare.was.lst");
			$("#manipulate_was_config_0").text(data.manipulate_was_config_0);
			
			$("#prepare_chmod_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;为目标系统"+data.prepare_chmod_addr_0+"脚本文件添加执行权限");
			$("#prepare_chmod_0").text(data.prepare_chmod_0);
			
			$("#prepare_set_hostname_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在目标系统"+data.prepare_set_hostname_addr_0+"设置主机名");
			$("#prepare_set_hostname_0").text(data.prepare_set_hostname_0);
			
			$("#download_files_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在目标系统"+data.download_files_addr_0+"下载安装软件与补丁");
			$("#download_files_0").text(data.download_files_0);
			
			$("#install_im_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在目标系统"+data.install_im_addr_0+"安装Installation Manager");
			$("#install_im_0").text(data.install_im_0);
			
			$("#install_was_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在目标系统"+data.install_was_addr_0+"安装WebSphere Application Server");
			$("#install_was_0").text(data.install_was_0);
			
			$("#update_was_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在目标系统"+data.update_was_addr_0+"安装WebSphere Application Server补丁");
			$("#update_was_0").text(data.update_was_0);
			
			if($("#was_jdk7").val().trim() == "yes")
			{
				$("#install_jdk_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在目标系统"+data.install_jdk_addr_0+"安装JDK 7");
				$("#install_jdk_0").text(data.install_jdk_0);
			}
			
			$("#build_was_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在目标系统"+data.build_was_addr_0+"创建概要文件");
			$("#build_was_0").text(data.build_was_0);
			
			$("#start_was_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在目标系统"+data.start_was_addr_0+"启动WAS Server");
			$("#start_was_0").text(data.start_was_0);
			giveTdColor();//给TD上颜色
		}
	});
}

function myrefresh() 
{
	if (($("#logmsg").attr("class") == 'tabcontent tabnow') && ($("#status").val().trim() != '2')) 
	{
		//window.location.reload();
		getInstallMsg();
	}
}   

window.setInterval('myrefresh()',10000);  //每隔10秒自动刷新一次
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
			<a href="getLogInfo" title="历史执行日志" class="tip-bottom">
			  <i class="icon-home"></i>历史执行日志</a> 
			<a href="#" class="current">任务信息</a>
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
							<div class="widget-content">详细信息.</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span12">
					<div class="columnauto">
						<div class="tabtitle">
							<ul class="tabnav">
								<li>主机节点</li>
								<li>环境参数</li>
								<li class="active">执行日志</li>
							</ul>
						</div>

						<div class="tabcontent">
							<!-- <div class="mainmodule"> -->
								<div class="mainmodule">									
									<h5 class="stairtit swapcon">拓扑结构</h5>
									<p class="twotit" style="padding-left:0;">
										<em class="majornode">单</em>节点1
									</p>

									<c:forEach items="${servers }" var="ser" varStatus="num" begin="0" end="0">
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
							<!-- 	<div class="mainmodule">
									<h5 class="swapcon">
										脚本已生成并上传至服务器路径：
									</h5>
									<div class="form-horizontal processInfo">
										<textarea readonly="readonly" rows="6"
											style="background: #f7f7f7; font-size: 13px; overflow-y: hidden;width:1490px;">
[username@hostname]# cd /script/was
[username@hostname was]# ls
hostname.setup.sh  prepare.was.sh  install.was.sh  build.was.sh  prepare.was.lst
            
                      </textarea>
									</div>
								</div> -->

						<%-- 		<div class="mainmodule">
									<h5 class="swapcon">
										<!-- <i class="icon-chevron-down"></i> -->
										手工执行脚本方法如下：
									</h5>
									<div class="form-horizontal processInfo">
										<textarea readonly="readonly" rows="7"
											style="background: #f7f7f7; font-size: 13px; overflow-y: hidden;">
1.使用root用户登录主机
2.进入目标主机的目录/script中
3.首先在主机端执行hostname.helper.sh ${hostname }
4.其次在主机端执行./prepare.was.sh ${ip}
5.若挂载了NFS文件系统，执行mount.nfs.ksh挂载NFS文件系统
                      </textarea>
									</div>
								</div> --%>

							<!-- </div> -->
						</div>

						<div class="tabcontent">
							<div class="mainmodule">


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
													<span class="input140 mr20">安装用户</span> 
													<span class="graytxt">${was_user }</span>
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
													<span class="input140 mr20">WAS安装补丁</span> 
													<span class="graytxt">${wasfix }</span>
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
													<span class="input140 mr20">WAS安装路径</span> 
													<span class="graytxt">${was_inst_path }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">JDK7</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt" id="was_jdk7">${was_jdk7 }</span>
												</div>
											</div>
										</div>
									</div>
								</div>

								<div class="mainmodule">
									<h5 class="swapcon">
										<i class="icon-chevron-down icon-chevron-right"></i>WAS 概要属性
									</h5>
									<div class="form-horizontal" style="display: none;">
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
                            <input type="hidden" id="uuid" name="uuid" value="${uuid }">
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
						</div>
						
						<div id="logmsg" class="tabcontent tabnow">
							<p class="columntxt2">主机安装进度 :&nbsp;&nbsp;&nbsp;${progress }</p>
							<div id="progress_my"
								class="progress progress-s progress-striped progress-success ine-block w100">
								<div class="bar" name="progress" style="width: ${percent};"></div>
								<input type="hidden" id="percent" value="${percent}"> 
								<input type="hidden" id="status" value="${status}">
							</div>
							<h5>详细任务执行状态：</h5>
							<table id="logTable3" align="center" width="100%" cellspacing="0"
								cellpadding="0" border="1">
								<tr>
									<td colspan="2"><b>&nbsp;&nbsp;&nbsp;第一步：创建目录</b></td>
								</tr>
								<tr>
									<td id="make_directory_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在目标系统${make_directory_addr_0 }上生成脚本存放目录</td>
									<td id="make_directory_0" style="text-align: center;">${make_directory_0 }</td>
								</tr>
								<tr>
									<td colspan="2"><b>&nbsp;&nbsp;&nbsp;第二步：推送脚本</b></td>
								</tr>
								<tr>
									<td id="propagate_scripts_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向目标系统${propagate_scripts_addr_0 }
										推送脚本文件hostname.setup.sh</td>
									<td id="propagate_scripts_0" style="text-align: center;">${propagate_scripts_0 }</td>
								</tr>

								<tr>
									<td id="propagate_scripts_addr_1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向目标系统${propagate_scripts_addr_1 }
										推送脚本文件prepare.was.sh</td>
									<td id="propagate_scripts_1" style="text-align: center;">${propagate_scripts_1 }</td>
								</tr>

								<tr>
									<td id="propagate_scripts_addr_2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向目标系统${propagate_scripts_addr_2 }
										推送脚本文件install.was.sh</td>
									<td id="propagate_scripts_2" style="text-align: center;">${propagate_scripts_2 }</td>
								</tr>
								<tr>
									<td id="propagate_scripts_addr_3">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向目标系统${propagate_scripts_addr_3 }
										推送脚本文件build.was.sh</td>
									<td id="propagate_scripts_3" style="text-align: center;">${propagate_scripts_3}</td>
								</tr>


								<tr>
									<td colspan="2"><b>&nbsp;&nbsp;&nbsp;第三步：生成文件</b></td>
								</tr>

								<tr>
									<td id="manipulate_was_config_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;目标系统${manipulate_was_config_addr_0 }
										生成参数文件prepare.was.lst</td>
									<td id="manipulate_was_config_0" style="text-align: center;">${manipulate_was_config_0 }</td>
								</tr>

								<tr>
									<td colspan="2"><b>&nbsp;&nbsp;&nbsp;第四步：赋予权限</b></td>
								</tr>
								<tr>
									<td id="prepare_chmod_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;为目标系统${prepare_chmod_addr_0 }
										脚本文件添加执行权限</td>
									<td id="prepare_chmod_0" style="text-align: center;">${prepare_chmod_0 }</td>
								</tr>
								<tr>
									<td colspan="2"><b>&nbsp;&nbsp;&nbsp;第五步：安装软件</b></td>
								</tr>
								<tr>
									<td id="prepare_set_hostname_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在目标系统${prepare_set_hostname_addr_0 }
										设置主机名</td>
									<td id="prepare_set_hostname_0" style="text-align: center;">${prepare_set_hostname_0 }</td>
								</tr>
								<tr>
									<td id="download_files_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在目标系统${download_files_addr_0 }
										下载安装软件与补丁</td>
									<td id="download_files_0" style="text-align: center;">${download_files_0 }</td>
								</tr>
								<tr>
									<td id="install_im_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在目标系统${install_im_addr_0 }
										安装Installation Manager</td>
									<td id="install_im_0" style="text-align: center;">${install_im_0 }</td>
								</tr>
								<tr>
									<td id="install_was_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在目标系统${install_was_addr_0}
										安装WebSphere Application Server</td>
									<td id="install_was_0" style="text-align: center;">${install_was_0 }</td>
								</tr>
								
								<tr>
									<td id="update_was_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在目标系统${update_was_addr_0}
										安装WebSphere Application Server补丁</td>
									<td id="update_was_0" style="text-align: center;">${update_was_0 }</td>
								</tr>
								
								<c:if test="${was_jdk7 eq 'yes' }">
								<tr>
									<td id="install_jdk_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在目标系统${install_jdk_addr_0 }
										安装JDK 7</td>
									<td id="install_jdk_0" style="text-align: center;">${install_jdk_0 }</td>
								</tr>
								</c:if>
								<tr>
									<td colspan="2"><b>&nbsp;&nbsp;&nbsp;第六步：构建环境</b></td>
								</tr>
								<tr>
									<td id="build_was_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在目标系统${build_was_addr_0 }
										创建概要文件</td>
									<td id="build_was_0" style="text-align: center;">${build_was_0 }</td>
								</tr>
								<tr>
									<td colspan="2"><b>&nbsp;&nbsp;&nbsp;第七步：启动服务</b></td>
								</tr>								
								<tr>
									<td id="start_was_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在目标系统${start_was_addr_0 }
										启动WAS Server</td>
									<td id="start_was_0" style="text-align: center;">${start_was_0 }</td>
								</tr>
						
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
