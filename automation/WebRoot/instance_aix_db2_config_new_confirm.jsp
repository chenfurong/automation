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
			message : '&nbsp;提交任务后是否在目标主机立即运行脚本，创建环境？',
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
			btn : [ [ '是', 'yes' ], [ '否', 'no' ] ],
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
			<a href="getAllServers" title="IBM DB2" class="tip-bottom"><i
				class="icon-home"></i>IBM DB2</a> <a class="current">实例配置详细</a>
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
							<div class="widget-content">确认当前虚拟机的DB2参数信息</div>
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
						<form action="installDb2StandAloneInfo" method="post" id="submits"
							class="form-horizontal">
							<input type="hidden" id="serId" name="serId" value="${serId }">

							<!-- 是否立即执行标识 -->
							<input type="hidden" id="type" name="type" value="yes">


							<!-- IP Begin -->
							<input type="hidden" id="hostname" name="hostname"
								value="${hostname}"> <input type="hidden" id="ip"
								name="ip" value="${ip}"> <input type="hidden"
								id="bootip" name="bootip" value="${bootip}"> <input
								type="hidden" id="hostId" name="hostId" value="${hostId}">
							<input type="hidden" id="ptype" name="ptype" value="${ptype }">
							
							<!-- IP End -->
							<input type="hidden" id="db2logpv" name="db2logpv"
								value="${db2logpv }"> <input type="hidden"
								id="db2archlogpv" name="db2archlogpv" value="${db2archlogpv }">
							<input type="hidden" id="dataspacepv" name="dataspacepv"
								value="${dataspacepv }">
							<!--old VG BEGIN -->
							<input type="hidden" id="hdiskname" name="hdiskname"
								value="${hdiskname }"> <input type="hidden" id="hdiskid"
								name="hdiskid" value="${hdiskid }"> <input type="hidden"
								id="vgtype" name="vgtype" value="${vgtype }">
							<!--old VG END -->

							<!-- VG BEGIN -->
							<input type="hidden" id="vgdb2home" name="vgdb2home"
								value="${vgdb2home }"> <input type="hidden"
								id="vgdb2log" name="vgdb2log" value="${vgdb2log }"> <input
								type="hidden" id="vgdb2archlog" name="vgdb2archlog"
								value="${vgdb2archlog }"> <input type="hidden"
								id="vgdataspace" name="vgdataspace" value="${vgdataspace }">
							<!-- VG END -->

							<!-- PV BEGIN -->
							<input type="hidden" id="db2homepv" name="db2homepv"
								value="${db2homepv }"> <input type="hidden"
								id="db2logpv" name="db2logpv" value="${db2logpv }"> <input
								type="hidden" id="db2archlogpv" name="db2archlogpv"
								value="${db2archlogpv }"> <input type="hidden"
								id="db2backuppv" name="db2backuppv" value="${db2backuppv }">
							<input type="hidden" id="dataspacepv" name="dataspacepv"
								value="${dataspacepv }"> <input type="hidden"
								id="dataspace2pv" name="dataspace2pv" value="${dataspace2pv }">
							<input type="hidden" id="dataspace3pv" name="dataspace3pv"
								value="${dataspace3pv }"> <input type="hidden"
								id="dataspace4pv" name="dataspace4pv" value="${dataspace4pv }">

							<!-- PV END -->

							<!-- VG 创建方式  BEGIN -->
							<input type="hidden" id="db2homemode" name="db2homemode"
								value="${db2homemode }"> <input type="hidden"
								id="db2logmode" name="db2logmode" value="${db2logmode }">
							<input type="hidden" id="db2archlogmode" name="db2archlogmode"
								value="${db2archlogmode }">
							<%-- <input type="hidden" id="db2backupmode" name="db2backupmode" value="${db2backupmode }"> --%>
							<input type="hidden" id="dataspace1mode" name="dataspacemode"
								value="${dataspacemode }">

							<!-- VG 创建方式 END -->



							<!-- NFS BEGIN -->
							<input type="hidden" id="nfsON" name="nfsON" value="${nfsON}">

							<input type="hidden" id="nfsIP1" name="nfsIP1" value="${nfsIP1}">
							<input type="hidden" id="nfsSPoint1" name="nfsSPoint1"
								value="${nfsSPoint1}"> <input type="hidden"
								id="nfsCPoint1" name="nfsCPoint1" value="${nfsCPoint1}">
							<input type="hidden" id="nfsIP2" name="nfsIP2" value="${nfsIP2}">
							<input type="hidden" id="nfsSPoint2" name="nfsSPoint2"
								value="${nfsSPoint2}"> <input type="hidden"
								id="nfsCPoint2" name="nfsCPoint2" value="${nfsCPoint2}">
							<input type="hidden" id="nfsIP3" name="nfsIP3" value="${nfsIP3}">
							<input type="hidden" id="nfsSPoint3" name="nfsSPoint3"
								value="${nfsSPoint3}"> <input type="hidden"
								id="nfsCPoint3" name="nfsCPoint3" value="${nfsCPoint3}">
							<input type="hidden" id="nfsIP4" name="nfsIP4" value="${nfsIP4}">
							<input type="hidden" id="nfsSPoint4" name="nfsSPoint4"
								value="${nfsSPoint4}"> <input type="hidden"
								id="nfsCPoint4" name="nfsCPoint4" value="${nfsCPoint4}">
							<input type="hidden" id="nfsIP5" name="nfsIP5" value="${nfsIP5}">
							<input type="hidden" id="nfsSPoint5" name="nfsSPoint5"
								value="${nfsSPoint5}"> <input type="hidden"
								id="nfsCPoint5" name="nfsCPoint5" value="${nfsCPoint5}">

							<input type="hidden" id="nfsIP6" name="nfsIP6" value="${nfsIP6}">
							<input type="hidden" id="nfsSPoint6" name="nfsSPoint6"
								value="${nfsSPoint6}"> <input type="hidden"
								id="nfsCPoint6" name="nfsCPoint6" value="${nfsCPoint6}">
							<input type="hidden" id="nfsIP7" name="nfsIP7" value="${nfsIP7}">
							<input type="hidden" id="nfsSPoint7" name="nfsSPoint7"
								value="${nfsSPoint7}"> <input type="hidden"
								id="nfsCPoint7" name="nfsCPoint7" value="${nfsCPoint7}">
							<input type="hidden" id="nfsIP8" name="nfsIP8" value="${nfsIP8}">
							<input type="hidden" id="nfsSPoint8" name="nfsSPoint8"
								value="${nfsSPoint8}"> <input type="hidden"
								id="nfsCPoint8" name="nfsCPoint8" value="${nfsCPoint8}">
							<input type="hidden" id="nfsIP9" name="nfsIP9" value="${nfsIP9}">
							<input type="hidden" id="nfsSPoint9" name="nfsSPoint9"
								value="${nfsSPoint9}"> <input type="hidden"
								id="nfsCPoint9" name="nfsCPoint9" value="${nfsCPoint9}">
							<input type="hidden" id="nfsIP10" name="nfsIP10"
								value="${nfsIP10}"> <input type="hidden"
								id="nfsSPoint10" name="nfsSPoint10" value="${nfsSPoint10}">
							<input type="hidden" id="nfsCPoint10" name="nfsCPoint10"
								value="${nfsCPoint10}"> <input type="hidden"
								id="nfsIP11" name="nfsIP11" value="${nfsIP11}"> <input
								type="hidden" id="nfsSPoint11" name="nfsSPoint11"
								value="${nfsSPoint11}"> <input type="hidden"
								id="nfsCPoint11" name="nfsCPoint11" value="${nfsCPoint11}">
							<input type="hidden" id="nfsIP12" name="nfsIP12"
								value="${nfsIP12}"> <input type="hidden"
								id="nfsSPoint12" name="nfsSPoint12" value="${nfsSPoint12}">
							<input type="hidden" id="nfsCPoint12" name="nfsCPoint12"
								value="${nfsCPoint12}"> <input type="hidden"
								id="nfsIP13" name="nfsIP13" value="${nfsIP13}"> <input
								type="hidden" id="nfsSPoint13" name="nfsSPoint13"
								value="${nfsSPoint13}"> <input type="hidden"
								id="nfsCPoint13" name="nfsCPoint13" value="${nfsCPoint13}">
							<input type="hidden" id="nfsIP14" name="nfsIP14"
								value="${nfsIP14}"> <input type="hidden"
								id="nfsSPoint14" name="nfsSPoint14" value="${nfsSPoint14}">
							<input type="hidden" id="nfsCPoint14" name="nfsCPoint14"
								value="${nfsCPoint14}"> <input type="hidden"
								id="nfsIP15" name="nfsIP15" value="${nfsIP15}"> <input
								type="hidden" id="nfsSPoint15" name="nfsSPoint15"
								value="${nfsSPoint15}"> <input type="hidden"
								id="nfsCPoint15" name="nfsCPoint15" value="${nfsCPoint15}">
							<!-- NFS END -->

							<!-- DB2 Begin -->
							<!-- 基本信息 -->
							<input type="hidden" id="db2_version" name="db2_version"
								value="${db2_version }"> <input type="hidden"
								id="db2_db2base" name="db2_db2base" value="${db2_db2base }">
							
							<input type="hidden" id="db2_fixpack" name="db2_fixpack"
								value="${db2_fixpack }"> 
							
							<input type="hidden" id="db2_dbpath" name="db2_dbpath"
								value="${db2_dbpath }"> <input type="hidden"
								id="db2_svcename" name="db2_svcename" value="${db2_svcename }">
							<input type="hidden" id="db2_db2insusr" name="db2_db2insusr"
								value="${db2_db2insusr }"> <input type="hidden"
								id="db2_dbname" name="db2_dbname" value="${db2_dbname }">
							<input type="hidden" id="db2_codeset" name="db2_codeset"
								value="${db2_codeset }"> <input type="hidden"
								id="db2_dbdatapath" name="db2_dbdatapath"
								value="${db2_dbdatapath }">
							<!-- 实例高级属性 -->
							<input type="hidden" id="db2_db2insgrp" name="db2_db2insgrp"
								value="${db2_db2insgrp }"> <input type="hidden"
								id="vgdb2_db2fenusrtype" name="db2_db2fenusr"
								value="${db2_db2fenusr }"> <input type="hidden"
								id="db2_db2fengrp" name="db2_db2fengrp"
								value="${db2_db2fengrp }"> <input type="hidden"
								id="db2_db2comm" name="db2_db2comm" value="${db2_db2comm }">
							<input type="hidden" id="db2_db2codepage" name="db2_db2codepage"
								value="${db2_db2codepage }"> <input type="hidden"
								id="db2_initagents" name="db2_initagents"
								value="${db2_initagents }"> <input type="hidden"
								id="db2_max_coordagents" name="db2_max_coordagents"
								value="${db2_max_coordagents }"> <input type="hidden"
								id="db2_max_connectings" name="db2_max_connectings"
								value="${db2_max_connectings }"> <input type="hidden"
								id="db2_poolagents" name="db2_poolagents"
								value="${db2_poolagents }"> <input type="hidden"
								id="db2_diagsize" name="db2_diagsize" value="${db2_diagsize }">
							<input type="hidden" id="db2_mon_buf" name="db2_mon_buf"
								value="${db2_mon_buf }"> <input type="hidden"
								id="db2_mon_lock" name="db2_mon_lock" value="${db2_mon_lock }">
							<input type="hidden" id="db2_mon_sort" name="db2_mon_sort"
								value="${db2_mon_sort }"> <input type="hidden"
								id="db2_mon_stmt" name="db2_mon_stmt" value="${db2_mon_stmt }">
							<input type="hidden" id="db2_mon_table" name="db2_mon_table"
								value="${db2_mon_table }"> <input type="hidden"
								id="db2_mon_uow" name="db2_mon_uow" value="${db2_mon_uow }">
							<input type="hidden" id="db2_health_mon" name="db2_health_mon"
								value="${db2_health_mon }"> <input type="hidden"
								id="db2_mon_heap" name="db2_mon_heap" value="${db2_mon_heap }">
							<!-- 数据库高级属性 -->
							<input type="hidden" id="db2_db2log" name="db2_db2log"
								value="${db2_db2log }"> <input type="hidden"
								id="db2_backuppath" name="db2_backuppath"
								value="${db2_backuppath }"> <input type="hidden"
								id="db2_logarchpath" name="db2_logarchpath"
								value="${db2_logarchpath }"> <input type="hidden"
								id="db2_stmm" name="db2_stmm" value="${db2_stmm }"> <input
								type="hidden" id="db2_locklist" name="db2_locklist"
								value="${db2_locklist }"> <input type="hidden"
								id="db2_maxlocks" name="db2_maxlocks" value="${db2_maxlocks }">
							<input type="hidden" id="db2_locktimeout" name="db2_locktimeout"
								value="${db2_locktimeout }"> <input type="hidden"
								id="db2_sortheap" name="db2_sortheap" value="${db2_sortheap }">
							<input type="hidden" id="db2_logfilesize" name="db2_logfilesize"
								value="${db2_logfilesize }"> <input type="hidden"
								id="db2_logprimary" name="db2_logprimary"
								value="${db2_logprimary }"> <input type="hidden"
								id="db2_logsecond" name="db2_logsecond"
								value="${db2_logsecond }"> <input type="hidden"
								id="db2_logbuff" name="db2_logbuff" value="${db2_logbuff }">
							<input type="hidden" id="db2_softmax" name="db2_softmax"
								value="${db2_softmax }"> <input type="hidden"
								id="db2_trackmod" name="db2_trackmod" value="${db2_trackmod }">
							<input type="hidden" id="db2_pagesize" name="db2_pagesize"
								value="${db2_pagesize }">
							<!-- DB2 end -->

							<div class="mainmodule">
								<h5 class="swapcon">
									<i class="icon-chevron-down"></i>基本信息
								</h5>
								<!-- <form action="#" method="get" class="form-horizontal"> -->
								<div class="form-horizontal">
									<div class="control-group">
										<label class="control-label">DB2安装版本</label>
										<div class="controls">
											<span class="graytxt">${db2_version }</span>
										</div>
									</div>
									
									<div class="control-group">
										<label class="control-label">DB2版本补丁</label>
										<div class="controls">
											<span class="graytxt">${db2_fixpack }</span>
										</div>
									</div>
									
									
									<div class="control-group">
										<label class="control-label">DB2安装路径</label>
										<div class="controls">
											<span class="graytxt">${db2_db2base }</span>
										</div>
									</div>

									<div class="control-group">
										<label class="control-label">DB2实例目录</label>
										<div class="controls">
											<%-- <input type="text" id="dbpath" name="dbpath" value="<%=dbpath %>" readonly="readonly"/> --%>
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
												<span class="input140 mr20">字符集</span> <span class="graytxt">${db2_codeset }</span>
											</div>
										</div>
									</div>
									<div class="control-group">
										<label class="control-label">DB2数据目录</label>
										<div class="controls">
											<span class="graytxt">${db2_dbdatapath }</span>
										</div>
									</div>
									<!--  </form> -->
								</div>
							</div>

							<div class="mainmodule">
								<h5 class="swapcon">
									<i class="icon-chevron-down icon-chevron-right"></i>实例高级属性
								</h5>
								<!--  <form action="#" method="get" class="form-horizontal" style="display:none;"> -->
								<div class="form-horizontal" style="display: none;">

									<div class="control-group">
										<label class="control-label">实例用户</label>
										<div class="controls">
											<div class="inputb2l">
												<!-- <input type="text" class="w45" value="db2insist1" readonly/> -->
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
								<!-- <h5 class="swapcon"><i class="icon-chevron-right"></i>数据库高级属性</h5> -->
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
