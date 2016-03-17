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
.input140 {
	font-size: 13px;
}
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
			<a href="getAllServers" title="IBM MQ" class="tip-bottom"><i
				class="icon-home"></i>IBM MQ</a> <a class="current">实例配置详细</a>
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
							<div class="widget-content">确认当前虚拟机的MQ参数信息</div>
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
							<p class="twotit" style="padding-left: 0px;">
								<em class="majornode">单</em>节点1
							</p>
							<c:forEach items="${servers }" var="ser" varStatus="num"
								begin="0" end="0">
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
											<!-- <b>Hypervisor:</b><span class="column_txt">${ser.HVisor }</span>&nbsp; -->											
											<b>状态:</b><span class="column_txt"><em>${ser.status }</em></span>
										</p>
									</div>
								</div>
							</c:forEach>
						</div>
						<form action="installMqStandAloneInfo" method="post" id="submits"
							class="form-horizontal">

							<div class="form-horizontal">
								<input type="hidden" id="ptype" name="ptype" value="${ptype}">
								<input type="hidden" id="hostId" name="hostId" value="${hostId}">
								<input type="hidden" id="serId" name="serId" value="${serId}">
								<input type="hidden" id="os" name="os" value="${os}"> 
								<input type="hidden" id="mq_version" name="mq_version" value="${mq_version}"> 
								<input type="hidden" id="mq_fp" name="mq_fp" value="${mq_fp}"> 
								<input type="hidden" id="mqfix" name="mqfix" value="${mqfix}"> 
								<input type="hidden" id="mq_inst_path" name="mq_inst_path" value="${mq_inst_path}"> 
								<input type="hidden" id="mq_user" name="mq_user" value="${mq_user}"> 
								<input type="hidden" id="mq_hostname" name="mq_hostname" value="${mq_hostname}"> 
								<input type="hidden" id="mq_ip" name="mq_ip" value="${mq_ip}"> 
								<input type="hidden" id="qmgr_method" name="qmgr_method" value="${qmgr_method}">
								<input type="hidden" id="qmgr_script" name="qmgr_script" value="${qmgr_script}"> 
								<input type="hidden" id="mq_qmgr_name" name="mq_qmgr_name" value="${mq_qmgr_name}">
								<input type="hidden" id="mq_data_path" name="mq_data_path" value="${mq_data_path}"> 
								<input type="hidden" id="mq_log_path" name="mq_log_path" value="${mq_log_path}">
								<input type="hidden" id="mq_qmgr_plog" name="mq_qmgr_plog" value="${mq_qmgr_plog}"> 
								<input type="hidden" id="mq_qmgr_slog" name="mq_qmgr_slog" value="${mq_qmgr_slog}">
								<input type="hidden" id="mq_log_psize" name="mq_log_psize" value="${mq_log_psize}"> 
								<input type="hidden" id="mq_chl_max" name="mq_chl_max" value="${mq_chl_max}">
								<input type="hidden" id="mq_chl_kalive" name="mq_chl_kalive" value="${mq_chl_kalive}">
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
										<div class="control-group">
											<label class="control-label">主机名</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${mq_hostname }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">IP地址</span> <span
														class="graytxt">${mq_ip }</span>
												</div>
											</div>
										</div>
<c:if test="${qmgr_method eq 'yes' }">
										<div class="control-group">
											<label class="control-label">QMGR名称</label>
											<div class="controls">
												<div class="inputb2l">
													<span class="graytxt">${mq_qmgr_name }</span>
												</div>
												<div class="inputb2l">
													<span class="input140 mr20">主日志文件数</span> <span
														class="graytxt">${mq_qmgr_plog }</span>
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
													<span class="input140 mr20">次日志文件数</span> <span
														class="graytxt">${mq_qmgr_slog }</span>
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
													<span class="input140 mr20">次日志文件数</span> <span
														class="graytxt">${mq_log_psize }</span>
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
													<span class="input140 mr20">最大通道数</span> <span
														class="graytxt">${mq_chl_max }</span>
												</div>
											</div>
										</div>
</c:if>
									</div>
								</div>

								<c:if test="${os eq 'linux' }">
									<input type="hidden" id="lin_semmsl" name="lin_semmsl"
										value="${lin_semmsl}">
									<input type="hidden" id="lin_semmns" name="lin_semmns"
										value="${lin_semmns}">
									<input type="hidden" id="lin_semopm" name="lin_semopm"
										value="${lin_semopm}">
									<input type="hidden" id="lin_semmni" name="lin_semmni"
										value="${lin_semmni}">
									<input type="hidden" id="lin_shmax" name="lin_shmax"
										value="${lin_shmax}">
									<input type="hidden" id="lin_shmni" name="lin_shmni"
										value="${lin_shmni}">
									<input type="hidden" id="lin_shmall" name="lin_shmall"
										value="${lin_shmall}">
									<input type="hidden" id="lin_filemax" name="lin_filemax"
										value="${lin_filemax}">
									<input type="hidden" id="lin_nofile" name="lin_nofile"
										value="${lin_nofile}">
									<input type="hidden" id="lin_nproc" name="lin_nproc"
										value="${lin_nproc}">
									<input type="hidden" id="lin_tcptime" name="lin_tcptime"
										value="${lin_tcptime}">
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
														<span class="input140 mr20">semmns</span> <span
															class="graytxt">${lin_semmns }</span>
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
														<span class="input140 mr20">semmni</span> <span
															class="graytxt">${lin_semmni }</span>
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
														<span class="input140 mr20">shmmni</span> <span
															class="graytxt">${lin_shmni }</span>
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
														<span class="input140 mr20">file-max</span> <span
															class="graytxt">${lin_filemax }</span>
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
														<span class="input140 mr20">nproc</span> <span
															class="graytxt">${lin_nproc }</span>
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
								</c:if>
								<!-- aix 参数的情况 -->

								<c:if test="${os eq 'aix' }">
									<input type="hidden" id="aix_semmns" name="aix_semmns"
										value="${aix_semmns}">


									<input type="hidden" id="aix_semmni" name="aix_semmni"
										value="${aix_semmni}">
									<input type="hidden" id="aix_shmni" name="aix_shmni"
										value="${aix_shmni}">

									<input type="hidden" id="aix_maxuproc" name="aix_maxuproc"
										value="${aix_maxuproc}">
									<input type="hidden" id="aix_stack" name="aix_stack"
										value="${aix_stack}">
									<input type="hidden" id="aix_nofiles" name="aix_nofiles"
										value="${aix_nofiles}">

									<input type="hidden" id="aix_data" name="aix_data"
										value="${aix_data}">
									<div class="mainmodule">
										<h5 class="swapcon">
											<i class="icon-chevron-down icon-chevron-right"></i>系统属性
										</h5>
										<div class="form-horizontal" style="display: none;">
											<div class="control-group">
												<label class="control-label">semmni</label>
												<div class="controls">
													<div class="inputb2l">
														<span class="graytxt">${aix_semmni }</span>
													</div>
													<div class="inputb2l">
														<span class="input140 mr20">semmns</span> <span
															class="graytxt">${aix_semmns }</span>
													</div>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label">shmmni</label>
												<div class="controls">
													<div class="inputb2l">
														<span class="graytxt">${aix_shmmni }</span>
													</div>
													<div class="inputb2l">
														<span class="input140 mr20">maxuproc</span> <span
															class="graytxt">${aix_maxuproc }</span>
													</div>
												</div>
											</div>
											<div class="control-group"></div>
											<div class="control-group">
												<label class="control-label">nofiles</label>
												<div class="controls">
													<div class="inputb2l">
														<span class="graytxt">${aix_nofiles }</span>
													</div>

												</div>
											</div>
											<div class="control-group">
												<label class="control-label">data</label>
												<div class="controls">
													<div class="inputb2l">
														<span class="graytxt">${aix_data }</span>
													</div>
													<div class="inputb2l">
														<span class="input140 mr20">stack</span> <span
															class="graytxt">${aix_stack }</span>
													</div>
												</div>
											</div>
										</div>
									</div>
								</c:if>
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
