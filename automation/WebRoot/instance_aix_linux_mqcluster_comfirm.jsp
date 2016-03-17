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
	float: left;}

.col-sm-1, .col-sm-2, .col-sm-3, .col-sm-4, .col-sm-5, .col-sm-6,
	.col-sm-7, .col-sm-8, .col-sm-9, .col-sm-10, .col-sm-11, .col-sm-12 {
	float: left;}

.col-md-3 {
	width: 25%;
	align: right;
}

.col-sm-12 { width: 100% }

.col-sm-11 { width: 91.66666667% }

.col-sm-10 { width: 83.33333333% }

.col-sm-9 { width: 75% }

.col-sm-8 { width: 45% }

.col-sm-7 { width: 58.33333333% }

.col-sm-6 { width: 50% }

.col-sm-5 { width: 41.66666667% }

.col-sm-4 { width: 33.33333333% }

.col-sm-3 { width: 23% }

.col-sm-2 { width: 16.66666667% }

.col-sm-1 { width: 8.33333333% }

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
.input140{font-size: 13px;}
.one{display:none;}

.tooltip-inner{color:black; background-color:#FFB073;}
.tooltip.right .tooltip-arrow{border-right-color:#FFB073;}
</style>
<script type="text/javascript">
	//操作
	function CheckInput() {
		ymPrompt.win({
			message : '&nbsp;提交任务后是否在目标主机立即运行脚本，创建环境？',
			title : '创建提示！',
			handler : function(tp) {
				if (tp == "no") {
					window.history.go(0);
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
			<a href="getAllServers" title="IBM MQCluster" class="tip-bottom"><i class="icon-home"></i>IBM MQCluster</a> 
			<a class="current">实例配置详细</a>
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
							<div class="widget-content">确认当前虚拟机的MQCluster参数信息</div>
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
							    <p class="twotit" style="padding-left: 0px;">
								       节点<c:out  value="${num.count }"/>
							    </p>						
								<div class="column">
									<div class="span12">
										<p>
											<b>主机名:</b><span class="column_txt"> ${ser.name } </span>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<b>IP地址:</b><span class="column_txt">${ser.IP}</span>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<b>操作系统:</b><span class="column_txt"><em>${ser.OS}</em></span>
										</p>
										<p>
											<b>系统配置:</b><span class="column_txt">${ser.HConf }</span>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;										
											<b>状态:</b><span class="column_txt"><em>${ser.status }</em></span>
										</p>
									</div>
								</div>
							</c:forEach>
						</div>
						
						<form action="installMqClusterInfo" method="post" id="submits" class="form-horizontal">
							<div class="form-horizontal">
								<input type="hidden" id="ptype" name="ptype" value="${ptype}">
								<input type="hidden" id="hostId" name="hostId" value="${hostId}">
								<input type="hidden" id="serId" name="serId" value="${serId}">
								<input type="hidden" id="mqfix" name="mqfix" value="${mqfix}"> 								
								 
								<input type="hidden" id="mq_version" name="mq_version" value="${mq_version}"> 
								<input type="hidden" id="mq_fp" name="mq_fp" value="${mq_fp}"> 								
								<input type="hidden" id="mq_inst_path" name="mq_inst_path" value="${mq_inst_path}"> 
								<input type="hidden" id="mq_user" name="mq_user" value="${mq_user}"> 
								
								<input type="hidden" id="qmgr_method" name="qmgr_method" value="${qmgr_method}">								
								<input type="hidden" id="allhostname" name="allhostname" value="${allhostname}"> 								
								<input type="hidden" id="allip" name="allip" value="${allip}"> 
								<input type="hidden" id="allqmgrname" name="allqmgrname" value="${allqmgrname}"> 
								<input type="hidden" id="allcompletesave" name="allcompletesave" value="${allcompletesave}">
								<input type="hidden" id="mq_mon_port" name="mq_mon_port" value="${mq_mon_port}">
								<input type="hidden" id="mq_qmgr_plog" name="mq_qmgr_plog" value="${mq_qmgr_plog}">
								<input type="hidden" id="mq_data_path" name="mq_data_path" value="${mq_data_path}"> 
								<input type="hidden" id="mq_qmgr_slog" name="mq_qmgr_slog" value="${mq_qmgr_slog}">
								<input type="hidden" id="mq_log_path" name="mq_log_path" value="${mq_log_path}">
								<input type="hidden" id="mq_log_psize" name="mq_log_psize" value="${mq_log_psize}">
								<input type="hidden" id="mq_chl_kalive" name="mq_chl_kalive" value="${mq_chl_kalive}">
								<input type="hidden" id="mq_chl_max" name="mq_chl_max" value="${mq_chl_max}">
								
								<input type="hidden" id="lin_semmsl" name="lin_semmsl" value="${lin_semmsl}">
								<input type="hidden" id="lin_semmns" name="lin_semmns" value="${lin_semmns}">
								<input type="hidden" id="lin_semopm" name="lin_semopm" value="${lin_semopm}">
								<input type="hidden" id="lin_semmni" name="lin_semmni" value="${lin_semmni}">
								<input type="hidden" id="lin_shmax" name="lin_shmax" value="${lin_shmax}">
								<input type="hidden" id="lin_shmni" name="lin_shmni" value="${lin_shmni}">
								<input type="hidden" id="lin_shmall" name="lin_shmall" value="${lin_shmall}">
								<input type="hidden" id="lin_filemax" name="lin_filemax" value="${lin_filemax}">
								<input type="hidden" id="lin_nofile" name="lin_nofile" value="${lin_nofile}">
								<input type="hidden" id="lin_nproc" name="lin_nproc" value="${lin_nproc}">
								<input type="hidden" id="lin_tcptime" name="lin_tcptime" value="${lin_tcptime}">
																							
								<div class="mainmodule">
									<h5 class="swapcon">
										<i class="icon-chevron-down icon-chevron-right"></i>基本属性
									</h5>
									<div class="form-horizontal">
										<div class="control-group">
											<label class="control-label">MQ安装版本</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${mq_version }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">MQ安装补丁</span> 													
													<span class="graytxt">${mqfix }</span>
												</div>
											</div>
										</div>
										<div class="control-group">
											<label class="control-label">MQ安装路径</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${mq_inst_path }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">MQ管理用户</span> <span
														class="graytxt">${mq_user }</span>
												</div>
											</div>
										</div>
									</div>
								</div>

								<div class="mainmodule">
									<h5 class="swapcon">
										<i class="icon-chevron-down icon-chevron-right"></i>队列管理器属性
									</h5>
									<div class="form-horizontal">
										<div class="control-group">
											<label class="control-label">队列管理器创建方式</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${qmgr_method }</span>
												</div>
											</div>
										</div>									
								<c:forEach items="${allservers }" var="sers">								
									<div class="control-group" style="padding-bottom: 9px;padding-top:9px;">									  
										<div class="col-md-3" style="margin-left:135px;">
											<div class="form-group">
												<label class="col-sm-2" style="padding-top: 2.5px;">主机名</label>
												<div class="col-sm-8" style="margin-left:12px;">
													<span class="graytxt">${sers.name }</span> 
												</div>
											</div>
										</div>										
										<div class="col-md-3" style="margin-left:-80px;">
											<div class="form-group">
												<label class="col-sm-2" style="padding-top: 2.5px;">IP地址</label>
												<div class="col-sm-8" style="padding-left:-10px;">
													<span class="graytxt">${sers.ip }</span>
												</div>
											</div>
										</div>																		  
										<div class="col-md-3" style="margin-left:-90px;">
											<div class="form-group">
												<label class="col-sm-3" style="padding-top: 2.5px;">QMGR名称</label>
												<div class="col-sm-8">
													<span class="graytxt">${sers.qmgrname }</span>
												</div>
											</div>
										</div>
										<div class="col-md-3" style="margin-left:-70px;">
											<div class="form-group">
												<label class="col-sm-3" style="padding-top: 2.5px;">完全存储库</label>
												<div style="float:left;">
													<span class="graytxt">${sers.completesave }</span>
												</div>
											</div>
										</div>									 										
									</div> 
								</c:forEach>	
							<c:if test="${qmgr_method eq '默认命令方式' }">							
								<div class="control-group">
									<label class="control-label">监听端口</label>
									<div class="controls">
										<div class="inputb2l">
											<span class="graytxt">${mq_mon_port }</span>
										</div>
										<div class="inputb2l">
											<span class="input140 mr20">主日志文件数</span> 
											<span class="graytxt">${mq_qmgr_plog }</span>
										</div>
									</div>
								</div>										
								<div class="control-group">
									<label class="control-label">QMGR数据路径</label>
									<div class="controls">
										<div class="inputb2l">
											<span class="graytxt">${mq_data_path }</span>
										</div>
										<div class="inputb2l">
											<span class="input140 mr20">次日志文件数</span> 
											<span class="graytxt">${mq_qmgr_slog }</span>
										</div>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label">QMGR日志路径</label>
									<div class="controls">
										<div class="inputb2l">
											<span class="graytxt">${mq_log_path }</span>
										</div>
										<div class="inputb2l">
											<span class="input140 mr20">日志文件页数(4KB)</span> 
											<span class="graytxt">${mq_log_psize }</span>
										</div>
									</div>
								</div>
								<div class="control-group">
									<label class="control-label">通道连接是否保持</label>
									<div class="controls">
										<div class="inputb2l">
											<span class="graytxt">${mq_chl_kalive }</span>
										</div>
										<div class="inputb2l">
											<span class="input140 mr20">最大通道数</span> 
											<span class="graytxt">${mq_chl_max }</span>
										</div>
									</div>
								</div>
					         </c:if>
							</div>
						</div>


									<div class="mainmodule">
										<h5 class="swapcon">
											<i class="icon-chevron-down icon-chevron-right"></i>系统属性
										</h5>
										<div class="form-horizontal" style="display: none;">
											<div class="control-group">
												<label class="control-label">semmsl</label>
												<div class="controls">
													<div class="inputb2l">
														<span class="graytxt">${lin_semmsl }</span>
													</div>
													<div class="inputb2l">
														<span class="input140 mr20">semmns</span> 
														<span class="graytxt">${lin_semmns }</span>
													</div>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label">semopm</label>
												<div class="controls">
													<div class="inputb2l">
														<span class="graytxt">${lin_semopm }</span>
													</div>
													<div class="inputb2l">
														<span class="input140 mr20">semmni</span> 
														<span class="graytxt">${lin_semmni }</span>
													</div>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label">shmmax</label>
												<div class="controls">
													<div class="inputb2l">
														<span class="graytxt">${lin_shmax }</span>
													</div>
													<div class="inputb2l">
														<span class="input140 mr20">shmmni</span> 
														<span class="graytxt">${lin_shmni }</span>
													</div>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label">shmall</label>
												<div class="controls">
													<div class="inputb2l">
														<span class="graytxt">${lin_shmall }</span>
													</div>
													<div class="inputb2l">
														<span class="input140 mr20">file-max</span> 
														<span class="graytxt">${lin_filemax }</span>
													</div>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label">nofile</label>
												<div class="controls">
													<div class="inputb2l">
														<span class="graytxt">${lin_nofile }</span>
													</div>
													<div class="inputb2l">
														<span class="input140 mr20">nproc</span> 
														<span class="graytxt">${lin_nproc }</span>
													</div>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label">tcptime</label>
												<div class="controls">
													<div class="inputb2l">
														<span class="graytxt">${lin_tcptime }</span>
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
		</a> 
		<a class="btn btn-info fr btn-down" onclick="CheckInput();"> 
		    <span>创建</span> <i class="icon-btn-next"></i>
		</a>
	</div>
</body>
</html>
