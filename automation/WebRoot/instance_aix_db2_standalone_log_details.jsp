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
	$(document).ready(
					function() {
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
							//	tdText.append("<img src='img/loading14.gif' />");
							}
							if (tdText.html().trim() == "手工运行") {
								tdText.addClass("bcy");
							}
						}
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
	var test1 = ${install_prepare_db2_ksh_0};//获取install_prepare_db2_ksh_0
	var test2 = ${nfs_mount_nfs_ksh_0};//获取nfs_mount_nfs_ksh_0
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
			
			$("#prepare_scripts_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向虚拟机"+data.prepare_scripts_addr_0+"上生成脚本存放目录");
			$("#prepare_scripts_0").text(data.prepare_scripts_0);
			
			$("#prepare_put_hosts_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向虚拟机"+data.prepare_put_hosts_addr_0+"推送脚本文件hosts.helper.sh");
			$("#prepare_put_hosts_0").text(data.prepare_put_hosts_0);
			
			$("#prepare_put_hostname_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向虚拟机"+data.prepare_put_hostname_addr_0+"推送脚本文件hostname.helper.sh");
			$("#prepare_put_hostname_0").text(data.prepare_put_hostname_0);
			
			$("#prepare_files_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向虚拟机"+data.prepare_files_addr_0+"推送脚本文件audb.sh");
			$("#prepare_files_0").text(data.prepare_files_0);
			
			$("#prepare_files_addr_1").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向虚拟机"+data.prepare_files_addr_1+"推送脚本文件mkvg.sh");
			$("#prepare_files_1").text(data.prepare_files_1);
			
			$("#prepare_files_addr_2").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向虚拟机"+data.prepare_files_addr_2+"推送脚本文件mount.nfs.ksh");
			$("#prepare_files_2").text(data.prepare_files_2);
			
			$("#prepare_files_addr_3").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向虚拟机"+data.prepare_files_addr_3+"推送脚本文件prepare.db2.sh");
			$("#prepare_files_3").text(data.prepare_files_3);
			
			$("#prepare_files_addr_4").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向虚拟机"+data.prepare_files_addr_4+"推送脚本文件update.db2.sh");
			$("#prepare_files_4").text(data.prepare_files_4);
			
			$("#prepare_prepare_db2_lst_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;虚拟机"+data.prepare_prepare_db2_lst_addr_0+"生成参数文件prepare.db2.lst");
			$("#prepare_prepare_db2_lst_0").text(data.prepare_prepare_db2_lst_0);
			
			$("#prepare_chmod_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;为虚拟机"+data.prepare_chmod_addr_0+"脚本文件添加执行权限");
			$("#prepare_chmod_0").text(data.prepare_chmod_0);
			
			$("#prepare_set_hostname_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;运行虚拟机"+data.prepare_set_hostname_addr_0+"设置主机名");
			$("#prepare_set_hostname_0").text(data.prepare_set_hostname_0);
			
			$("#install_prepare_db2_ksh_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;为虚拟机"+data.install_prepare_db2_ksh_addr_0+"上运行脚本文件prepare.db2.ksh，创建VG、安装DB2、配置数据库参数等");
			if(test1 != null)
			{
				$("#install_prepare_db2_ksh_0").text(data.install_prepare_db2_ksh_0);
			}
			else
			{
				$("#install_prepare_db2_ksh_0").text("手工运行");
			}
			
			$("#nfs_mount_nfs_ksh_addr_0").html("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;为虚拟机"+data.nfs_mount_nfs_ksh_addr_0+"上运行脚本文件mount.nfs.ksh,挂载NFS文件系统");
			if(test2 != null)
			{
				$("#nfs_mount_nfs_ksh_0").text(data.nfs_mount_nfs_ksh_0);
			}
			else
			{
				$("#nfs_mount_nfs_ksh_0").text("手工运行");
			}
			
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
			<a href="getLogInfo" title="历史执行日志" class="tip-bottom"><i
				class="icon-home"></i>历史执行日志</a> <a href="#" class="current">任务信息</a>
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
								<li >主机节点</li>
								<li>环境参数</li>
								<li class="active">执行日志</li>
							</ul>
						</div>

						<div class="tabcontent">
							<!-- <div class="mainmodule"> -->
								<div class="mainmodule">
									<!-- <h5 class="stairtit" style="margin-left:10px;"><i class="icon-chevron-down"></i>拓扑结构</h5> -->
									<h5 class="stairtit swapcon">
										<!-- <i class="icon-chevron-down"></i> -->
										拓扑结构
									</h5>
									<p class="twotit" style="padding-left: 0px;">
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
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<b>操作系统:</b><span class="column_txt"><em>${ser.OS}</em></span>
										</p>
										<p>
											<b>系统配置:</b><span class="column_txt">${ser.HConf }</span> 
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<!-- <b>挂卷:</b><span class="column_txt">volume_HB ,volume_Data</span>  -->
											<b>状态:</b><span class="column_txt"><em>${ser.status }</em></span>
										</p>
									</div>
								</div>
							</c:forEach>
								</div>
							<!-- 	<div class="mainmodule">
									<h5 class="swapcon">
										<i class="icon-chevron-down"></i>
										脚本已生成并上传至服务器路径：
									</h5>
									<div class="form-horizontal processInfo">
										<textarea readonly="readonly" rows="6"
											style="background: #f7f7f7; font-size: 13px; overflow-y: hidden;width:1490px;">
[username@hostname]# cd /script
[username@hostname script]# ls
audb.sh    hostname.helper.sh  hosts.helper.sh    mkvg.ksh  prepare.db2.ksh  prepare.db2.lst  update.db2.ksh  mount.nfs.ksh
            
                      </textarea>
									</div>
								</div> -->

<%-- 								<div class="mainmodule">
									<h5 class="swapcon">
										<!-- <i class="icon-chevron-down"></i> -->
										手工执行脚本方法如下：
									</h5>
									<div class="form-horizontal processInfo">
										<textarea readonly="readonly" rows="7"
											style="background: #f7f7f7; font-size: 13px; overflow-y: hidden;width:1490px;">
1.使用root用户登录主机
2.进入目标主机的目录/script中
3.首先在主机端执行hostname.helper.sh ${hostname }
4.其次在主机端执行./prepare.db2.ksh ${ip}
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
										<i class="icon-chevron-down"></i>系统参数
									</h5>
									<div class="form-horizontal">



										<div class="control-group">
											<label class="control-label">IP</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${ip }</span>
												</div>
												<div class="inputb2l">
													<span class="input200 mr20"> 主机名</span> <span
														class="graytxt">${hostname }</span>
												</div>
											</div>
										</div>

										<div class="control-group groupborder divnfs">
											<div class="controls controls-mini"
												style="margin-left: 0.7%;">
												<span class="input140 mr20" style="width: 142px;">VG名称</span>
												<div class="inputb4" style="width: 20%;">
													<span class="graytxt">${vgdb2home }</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">包含PV</span>
												<div class="inputb4" style="width: 20%;">
													<span class="graytxt">${db2homepv }</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">VG创建方式</span>
												<div class="inputb4" style="width: 20%;">
													<span class="graytxt">${db2homemode }</span>
												</div>
											</div>
										</div>

										<div class="control-group groupborder divnfs">
											<div class="controls controls-mini"
												style="margin-left: 0.7%;">
												<span class="input140 mr20" style="width: 142px;">VG名称</span>
												<div class="inputb4" style="width: 20%;">
													<span class="graytxt">${vgdb2log }</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">包含PV</span>
												<div class="inputb4" style="width: 20%;">
													<span class="graytxt">${db2logpv }</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">VG创建方式</span>
												<div class="inputb4" style="width: 20%;">
													<span class="graytxt">${db2logmode }</span>
												</div>
											</div>
										</div>

										<div class="control-group groupborder divnfs">
											<div class="controls controls-mini"
												style="margin-left: 0.7%;">
												<span class="input140 mr20" style="width: 142px;">VG名称</span>
												<div class="inputb4" style="width: 20%;">
													<span class="graytxt">${vgdb2archlog }</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">包含PV</span>
												<div class="inputb4" style="width: 20%;">
													<span class="graytxt">${db2archlogpv }</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">VG创建方式</span>
												<div class="inputb4" style="width: 20%;">
													<span class="graytxt">${db2archlogmode }</span>
												</div>
											</div>
										</div>

										<div class="control-group groupborder divnfs">
											<div class="controls controls-mini"
												style="margin-left: 0.7%;">
												<span class="input140 mr20" style="width: 142px;">VG名称</span>
												<div class="inputb4" style="width: 20%;">
													<span class="graytxt">${vgdataspace }</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">包含PV</span>
												<div class="inputb4" style="width: 20%;">
													<span class="graytxt">${dataspacepv }</span>
												</div>
												<span class="input140 mr20" style="width: 128px;">VG创建方式</span>
												<div class="inputb4" style="width: 20%;">
													<span class="graytxt">${dataspacemode }</span>
												</div>
											</div>
										</div>

										<c:if test="${nfsON ne ison }">
											<div class="controls controls-mini"
												style="margin-left: 0.7%;">
												<span class="input140 mr20" style="width: 142px;">是否挂载NFS文件系统</span>
												<div class="inputb4" style="width: 20%;">
													<span class="graytxt">${nfsON }</span>
												</div>
											</div>
										</c:if>

										<c:if test="${nfsON eq ison }">
											<c:if test="${nfsIP1 != '' }">
												<div class="controls controls-mini"
													style="margin-left: 0.7%;">
													<span class="input140 mr20" style="width: 142px;">NFS服务端
														IP地址1</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsIP1 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">服务端共享目录1</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsSPoint1 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">NFS客户端挂载点1</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsCPoint1 }</span>
													</div>
												</div>
											</c:if>
											<c:if test="${nfsIP2 != '' }">
												<div class="controls controls-mini"
													style="margin-left: 0.7%;">
													<span class="input140 mr20" style="width: 142px;">NFS服务端
														IP地址2</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsIP2 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">服务端共享目录2</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsSPoint2 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">NFS客户端挂载点2</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsCPoint2 }</span>
													</div>
												</div>
											</c:if>
											<c:if test="${nfsIP3 != '' }">
												<div class="controls controls-mini"
													style="margin-left: 0.7%;">
													<span class="input140 mr20" style="width: 142px;">NFS服务端
														IP地址3</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsIP3 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">服务端共享目录3</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsSPoint3 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">NFS客户端挂载点3</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsCPoint3 }</span>
													</div>
												</div>
											</c:if>
											<c:if test="${nfsIP4 != '' }">
												<div class="controls controls-mini"
													style="margin-left: 0.7%;">
													<span class="input140 mr20" style="width: 142px;">NFS服务端
														IP地址4</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsIP4 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">服务端共享目录4</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsSPoint4 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">NFS客户端挂载点4</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsCPoint4 }</span>
													</div>
												</div>
											</c:if>
											<c:if test="${nfsIP5 != '' }">
												<div class="controls controls-mini"
													style="margin-left: 0.7%;">
													<span class="input140 mr20" style="width: 142px;">NFS服务端
														IP地址5</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsIP5 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">服务端共享目录5</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsSPoint5 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">NFS客户端挂载点5</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsCPoint5 }</span>
													</div>
												</div>
											</c:if>
											<c:if test="${nfsIP6 != '' }">
												<div class="controls controls-mini"
													style="margin-left: 0.7%;">
													<span class="input140 mr20" style="width: 142px;">NFS服务端
														IP地址6</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsIP6 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">服务端共享目录6</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsSPoint6 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">NFS客户端挂载点6</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsCPoint6 }</span>
													</div>
												</div>
											</c:if>
											<c:if test="${nfsIP7 != '' }">
												<div class="controls controls-mini"
													style="margin-left: 0.7%;">
													<span class="input140 mr20" style="width: 142px;">NFS服务端
														IP地址7</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsIP7 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">服务端共享目录7</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsSPoint7 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">NFS客户端挂载点7</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsCPoint7 }</span>
													</div>
												</div>
											</c:if>
											<c:if test="${nfsIP8 != '' }">
												<div class="controls controls-mini"
													style="margin-left: 0.7%;">
													<span class="input140 mr20" style="width: 142px;">NFS服务端
														IP地址8</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsIP8 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">服务端共享目录8</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsSPoint8 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">NFS客户端挂载点8</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsCPoint8 }</span>
													</div>
												</div>
											</c:if>
											<c:if test="${nfsIP9 != '' }">
												<div class="controls controls-mini"
													style="margin-left: 0.7%;">
													<span class="input140 mr20" style="width: 142px;">NFS服务端
														IP地址9</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsIP9 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">服务端共享目录9</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsSPoint9 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">NFS客户端挂载点9</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsCPoint9 }</span>
													</div>
												</div>
											</c:if>
											<c:if test="${nfsIP10 != '' }">
												<div class="controls controls-mini"
													style="margin-left: 0.7%;">
													<span class="input140 mr20" style="width: 142px;">NFS服务端
														IP地址10</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsIP10 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">服务端共享目录10</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsSPoint10 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">NFS客户端挂载点10</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsCPoint10 }</span>
													</div>
												</div>
											</c:if>
											<c:if test="${nfsIP11 != '' }">
												<div class="controls controls-mini"
													style="margin-left: 0.7%;">
													<span class="input140 mr20" style="width: 142px;">NFS服务端
														IP地址11</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsIP11 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">服务端共享目录11</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsSPoint11 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">NFS客户端挂载点11</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsCPoint11 }</span>
													</div>
												</div>
											</c:if>
											<c:if test="${nfsIP12 != '' }">
												<div class="controls controls-mini"
													style="margin-left: 0.7%;">
													<span class="input140 mr20" style="width: 142px;">NFS服务端
														IP地址12</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsIP12 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">服务端共享目录12</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsSPoint12 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">NFS客户端挂载点12</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsCPoint12 }</span>
													</div>
												</div>
											</c:if>
											<c:if test="${nfsIP13 != '' }">
												<div class="controls controls-mini"
													style="margin-left: 0.7%;">
													<span class="input140 mr20" style="width: 142px;">NFS服务端
														IP地址13</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsIP13 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">服务端共享目录13</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsSPoint13 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">NFS客户端挂载点13</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsCPoint13 }</span>
													</div>
												</div>
											</c:if>
											<c:if test="${nfsIP14 != '' }">
												<div class="controls controls-mini"
													style="margin-left: 0.7%;">
													<span class="input140 mr20" style="width: 142px;">NFS服务端
														IP地址14</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsIP14 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">服务端共享目录14</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsSPoint14 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">NFS客户端挂载点14</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsCPoint14 }</span>
													</div>
												</div>
											</c:if>
											<c:if test="${nfsIP15 != '' }">
												<div class="controls controls-mini"
													style="margin-left: 0.7%;">
													<span class="input140 mr20" style="width: 142px;">NFS服务端
														IP地址15</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsIP15 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">服务端共享目录15</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsSPoint15 }</span>
													</div>
													<span class="input140 mr20" style="width: 128px;">NFS客户端挂载点15</span>
													<div class="inputb4" style="width: 20%;">
														<span class="graytxt">${nfsCPoint15 }</span>
													</div>
												</div>
											</c:if>
										</c:if>
									</div>
								</div>

								<div class="mainmodule">
									<h5 class="swapcon">
										<i class="icon-chevron-down icon-chevron-right"></i>基本信息
									</h5>
									<div class="form-horizontal" style="display: none;">
										<div class="control-group">
											<label class="control-label">DB2安装版本</label>
											<div class="controls">
												<span class="graytxt">${db2_version }</span>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">DB2安装版本</label>
											<div class="controls">
												<span class="graytxt">${db2_db2base }</span>
											</div>
										</div>

										<div class="control-group">
											<label class="control-label">DB2实例目录</label>
											<div class="controls">
												<span class="graytxt">${db2_dbpath }</span>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">DB2实例名</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${db2_db2insusr }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">监听端口</span> <span
														class="graytxt">${db2_svcename }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">DB2数据库名</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${db2_dbname }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">字符集</span> <span
														class="graytxt">${db2_codeset }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">DB2数据目录</label>
											<div class="controls">
												<span class="graytxt">${db2_dbdatapath }</span>
											</div>
										</div>
									</div>
								</div>

								<div class="mainmodule">
									<h5 class="swapcon">
										<i class="icon-chevron-down icon-chevron-right"></i>实例高级属性
									</h5>
									<div class="form-horizontal" style="display: none;">

										<div class="control-group">
											<label class="control-label">实例用户</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${db2_db2insusr }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">实例用户组</span> <span
														class="graytxt">${db2_db2insgrp }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">fence用户</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${db2_db2fenusr }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">fence用户组</span> <span
														class="graytxt">${db2_db2fengrp }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">DB2COMM</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${db2_db2comm }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">DB2CODEPAGE</span> <span
														class="graytxt">${db2_db2codepage }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">NUM_INITAGENI</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${db2_initagents }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">NUM_POOLAGENTS</span> <span
														class="graytxt">${db2_poolagents }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">MAX_COORDAGENTS</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${db2_max_coordagents }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">MAX_CONNECTIONS</span> <span
														class="graytxt">${db2_max_connectings }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">DIAGSIZE</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${db2_diagsize }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">DFT_MON_BUFPOOL</span> <span
														class="graytxt">${db2_mon_buf }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">DFT_MON_LOCK</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${db2_mon_lock }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">DFT_MON_SORT</span> <span
														class="graytxt">${db2_mon_sort }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">DFT_MON_STMT</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${db2_mon_stmt }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">DFT_MON_TABLE</span> <span
														class="graytxt">${db2_mon_table }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">DFT_MON_UOW</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${db2_mon_uow }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">HEACTH_MON</span> <span
														class="graytxt">${db2_health_mon }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">MON_HEAP_SZ</label>
											<div class="controls">
												<span class="graytxt">${db2_mon_heap }</span>
											</div>
										</div>
									</div>
								</div>

								<div class="mainmodule">
									<h5 class="swapcon">
										<i class="icon-chevron-down icon-chevron-right"></i>数据库高级属性
									</h5>
									<div class="form-horizontal" style="display: none;">
										<div class="control-group">
											<label class="control-label">DB2日志路径</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${db2_db2log }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">归档日志路径</span> <span
														class="graytxt">${db2_logarchpath }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">DB2备份路径</label>
											<div class="controls">
												<span class="graytxt">${db2_backuppath }</span>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">SELF_TUNING_MEM</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${db2_stmm }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">LOCKLIST</span> <span
														class="graytxt">${db2_locklist }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">MAXLOCKS</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${db2_maxlocks }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">LOCKTIMEOUT</span> <span
														class="graytxt">${db2_locktimeout }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">SORTHEAP</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${db2_sortheap }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">LOGFILESIZ</span> <span
														class="graytxt">${db2_logfilesize } MB</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">LOGPRIMARY</label>
											<div class="controls">
												<div class="inputb2l">
													<!-- <input type="text" class="w45" value="30" /> -->
													<span class="graytxt">${db2_logprimary }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">LOGSECOND</span> <span
														class="graytxt">${db2_logsecond }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">LOGBUFSZ</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${db2_logbuff } MB</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">SOFTMAX</span> <span
														class="graytxt">${db2_softmax }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">TRACKMOD</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${db2_trackmod }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">PAGESIZE</span> <span
														class="graytxt">${db2_pagesize }</span>
												</div>
											</div>
										</div>

									</div>
								</div>



							</div>
						</div>
						<div id="logmsg" class="tabcontent tabnow">
							<input type="hidden" id="uuid" name="uuid" value="${uuid }">
							<p class="columntxt2">主机安装进度 :&nbsp;&nbsp;&nbsp;${progress }</p>
							<div id="progress_my"
								class="progress progress-s progress-striped progress-success ine-block w100">
								<div class="bar" name="progress" style="width: ${percent};"></div>
								<input type="hidden" id="percent" value="${percent}"> 
								<input type="hidden" id="status" value="${status}">
							</div>
							<h5>详细任务执行状态：</h5>
							<!-- <div class="column installMsg"style="height: 500px; background: #ccc; overflow: scroll;"> -->
							<%--  <center>  --%>
							<table id="logTable3" align="center" width="100%" cellspacing="0"
								cellpadding="0" border="1">
								<tr>
									<td colspan="2"><b>&nbsp;&nbsp;&nbsp;第一步：创建目录</b></td>
								</tr>
								<tr>
									<td id="prepare_scripts_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向虚拟机${prepare_scripts_addr_0 }上生成脚本存放目录</td>
									<td id="prepare_scripts_0" style="text-align: center;">${prepare_scripts_0 }</td>
								</tr>
								<tr>
									<td colspan="2"><b>&nbsp;&nbsp;&nbsp;第二步：推送脚本</b></td>
								</tr>
								<tr>
									<td id="prepare_put_hosts_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向虚拟机${prepare_put_hosts_addr_0 }
										推送脚本文件hosts.helper.sh</td>
									<td id="prepare_put_hosts_0" style="text-align: center;">${prepare_put_hosts_0 }</td>
								</tr>

								<tr>
									<td id="prepare_put_hostname_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向虚拟机${prepare_put_hostname_addr_0 }
										推送脚本文件hostname.helper.sh</td>
									<td id="prepare_put_hostname_0" style="text-align: center;">${prepare_put_hostname_0 }</td>
								</tr>


								<tr>
									<td id="prepare_files_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向虚拟机${prepare_files_addr_0 }
										推送脚本文件audb.sh</td>
									<td id="prepare_files_0" style="text-align: center;">${prepare_files_0 }</td>
								</tr>
								<tr>
									<td id="prepare_files_addr_1">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向虚拟机${prepare_files_addr_1 }
										推送脚本文件mkvg.sh</td>
									<td id="prepare_files_1" style="text-align: center;">${prepare_files_1 }</td>
								</tr>
								<tr>
									<td id="prepare_files_addr_2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向虚拟机${prepare_files_addr_2 }
										推送脚本文件mount.nfs.ksh</td>
									<td id="prepare_files_2" style="text-align: center;">${prepare_files_2 }</td>
								</tr>
								<tr>
									<td id="prepare_files_addr_3">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向虚拟机${prepare_files_addr_3 }
										推送脚本文件prepare.db2.sh</td>
									<td id="prepare_files_3" style="text-align: center;">${prepare_files_3 }</td>
								</tr>
								<tr>
									<td id="prepare_files_addr_4">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;向虚拟机${prepare_files_addr_4 }
										推送脚本文件update.db2.sh</td>
									<td id="prepare_files_4" style="text-align: center;">${prepare_files_4 }</td>
								</tr>
								
								<tr>
									<td colspan="2"><b>&nbsp;&nbsp;&nbsp;第三步：目标虚拟机上生成参数文件</b></td>
								</tr>
								<tr>
									<td id="prepare_prepare_db2_lst_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;虚拟机${prepare_prepare_db2_lst_addr_0 }
										生成参数文件prepare.db2.lst</td>
									<td id="prepare_prepare_db2_lst_0" style="text-align: center;">${prepare_prepare_db2_lst_0 }</td>
								</tr>

								<tr>
									<td colspan="2"><b>&nbsp;&nbsp;&nbsp;第四步：赋予脚本执行权限</b></td>
								</tr>
								<tr>
									<td id="prepare_chmod_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;为虚拟机${prepare_chmod_addr_0 }
										脚本文件添加执行权限</td>
									<td id="prepare_chmod_0" style="text-align: center;">${prepare_chmod_0 }</td>
								</tr>
								
								<tr>
									<td colspan="2"><b>&nbsp;&nbsp;&nbsp;第五步：安装软件</b></td>
								</tr>
								<tr>
									<td id="prepare_set_hostname_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;运行虚拟机${prepare_set_hostname_addr_0 }
										设置主机名</td>
									<td id="prepare_set_hostname_0" style="text-align: center;">${prepare_set_hostname_0 }</td>
								</tr>
								
								<tr>
									<td colspan="2"><b>&nbsp;&nbsp;&nbsp;第六步：在虚拟机上执行文件</b></td>
								</tr>
								<tr>
									<td id="install_prepare_db2_ksh_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;为虚拟机${install_prepare_db2_ksh_addr_0 }上运行脚本文件prepare.db2.ksh，创建VG、安装DB2、配置数据库参数等</td>
									<td id="install_prepare_db2_ksh_0" style="text-align: center;">
									   <c:if test="${install_prepare_db2_ksh_0 != null}">
											${install_prepare_db2_ksh_0 }
									   </c:if> 
									   <c:if test="${install_prepare_db2_ksh_0 == null}">
											手工运行
										</c:if>
								    </td>
								</tr>
								<tr>
										<td id="nfs_mount_nfs_ksh_addr_0">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;为虚拟机${nfs_mount_nfs_ksh_addr_0 }上运行脚本文件mount.nfs.ksh,挂载NFS文件系统</td>
										<td id="nfs_mount_nfs_ksh_0" style="text-align:center;">
										  <c:if test="${nfs_mount_nfs_ksh_0 != null}">
											 ${nfs_mount_nfs_ksh_0 }
										  </c:if>
										  <c:if test="${nfs_mount_nfs_ksh_0 == null}">
											 手工运行
										  </c:if>
										</td>
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
